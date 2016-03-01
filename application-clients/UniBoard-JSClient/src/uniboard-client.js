/* global leemon, uvConfig */

////////////////////////////////////////////////////////////////////////
// Post signatures crypto
////////////////////////////////////////////////////////////////////////
function UBClient() {
	/**
	 * Signs the post that will be posted on UniBoard using hte given generator (Schnorr signature)
	 * @param post Message to be signed
	 * @param generator Generator to be used in the signature
	 * @param sk Private key used for signature
	 * @returns Signature as object containing the paired value as
	 * bigInt (sig) and its base {uvConfig.BASE} string representation (sigString)
	 */
	this.signPost = function (post, generator, sk) {

		//Hash post
		var postHash = this.hashPost(post, false, false);
		var paired = this.createSchnorrSignature(postHash, sk, this.signatureSetting.p, this.signatureSetting.q, generator);

		return {sig: paired, sigString: leemon.bigInt2str(paired, uvConfig.BASE)};
	};

	/**
	 * Verify the (Schnorr) signature of a result received from the board
	 * @param query The query sent to the board
	 * @param resultContainer The result received from the board
	 * @param posterSetting Crypto setting of the poster
	 * @param verifyPosterSignature True if signature of poster of the posts contained in the result, fals if not
	 * @returns True if signature is correct, error message otherwise
	 */
	this.verifyResultSignature = function (query, resultContainer, posterSetting, verifyPosterSignature) {
		var posts = resultContainer.result.post;

		// 1. Verify ResultContainer signature
		// Hash: [query,resultcontainer]
		//          query=...
		//          resultcontainer=[result,gamma]
		//                     result [p1,p2,...]
		//                              post=...
		//                     gamma=[timestamp]

		var queryHash = this.hashQuery(query);
		var resultHash = '';
		for (var i = 0; i < posts.length; i++) {
			resultHash += this.hashPost(posts[i], true, true);
		}
		var gamma = Hash.doDate(new Date(resultContainer.gamma.attribute[0].value.value));
		var resultContainerHash = Hash.doHexStr(Hash.doHexStr(resultHash) + Hash.doHexStr(gamma));
		var hash = Hash.doHexStr(queryHash + resultContainerHash);
		if (!this.verifySchnorrSignature(
				leemon.str2bigInt(resultContainer.gamma.attribute[1].value.value, uvConfig.BASE),
				hash,
				leemon.str2bigInt(uvConfig.BOARD_SETTING.PK, uvConfig.BASE),
				leemon.str2bigInt(uvConfig.BOARD_SETTING.P, uvConfig.BASE),
				leemon.str2bigInt(uvConfig.BOARD_SETTING.Q, uvConfig.BASE),
				leemon.str2bigInt(uvConfig.BOARD_SETTING.G, uvConfig.BASE))) {
			return "Wrong board signature for GET";
		}

		// 2. Verify board signature of each Post
		var posts = resultContainer.result.post;
		for (var i = 0; i < posts.length; i++) {
			var post = posts[i];
			var postHash = this.hashPost(post, true, false);
			if (!this.verifySchnorrSignature(
					leemon.str2bigInt(post.beta.attribute[2].value.value, uvConfig.BASE),
					postHash,
					leemon.str2bigInt(uvConfig.BOARD_SETTING.PK, uvConfig.BASE),
					leemon.str2bigInt(uvConfig.BOARD_SETTING.P, uvConfig.BASE),
					leemon.str2bigInt(uvConfig.BOARD_SETTING.Q, uvConfig.BASE),
					leemon.str2bigInt(uvConfig.BOARD_SETTING.G, uvConfig.BASE))) {
				return "Wrong board signature in post " + i;
			}
		}

		// 3. Verify poster signature of each Post contained in the result
		if (verifyPosterSignature == true) {
			for (var i = 0; i < posts.length; i++) {
				var post = posts[i];
				var postHash = this.hashPost(post, false, false);
				if (!this.verifySchnorrSignature(
						leemon.str2bigInt(post.alpha.attribute[2].value.value, uvConfig.BASE),
						postHash,
						leemon.str2bigInt(posterSetting.PK, uvConfig.BASE),
						leemon.str2bigInt(posterSetting.P, uvConfig.BASE),
						leemon.str2bigInt(posterSetting.Q, uvConfig.BASE),
						leemon.str2bigInt(posterSetting.G, uvConfig.BASE))) {
					return "Wrong poster signature in post " + i;
				}
			}
		}
		return true;
	};

	/**
	 * Hashes a typed value.
	 * @param {type} Typed value
	 * @returns Hash of the typed value
	 */
	var hashTypedValue = function (typed) {
		var aHash = '';
		var type = typed.type;
		var value = typed.value;
		if (type === "stringValue") {
			aHash = Hash.doString(value);
		} else if (type === "integerValue") {
			aHash = Hash.doInt(value);
		} else if (type === "dateValue") {
			aHash = Hash.doDate(new Date(value));
		} else {
			throw "Error: unknown type of typed value! ('" + type + "').";
		}
		return aHash;
	};

	/**
	 * Helper to comupte the hash of a query.
	 *
	 * @param {type} query
	 * @returns The hash of the query
	 */
	this.hashQuery = function (query) {
		// query=[contraints,order,limit]
		//          constraint=[type,identifier,value.value]
		//              identifier=[type,s1,s2,s3,...]
		//          order=[identifier,ascDesc]

		var hashConstraints = '';
		for (var i = 0; i < query.constraint.length; i++) {
			var constraint = query.constraint[i];
			var hashConstraint = Hash.doString(constraint.type);
			var hashIdentifier = Hash.doString(constraint.identifier.type);
			for (var j = 0; j < constraint.identifier.part.length; j++) {
				hashIdentifier += Hash.doString(constraint.identifier.part[j]);
			}
			hashConstraint += Hash.doHexStr(hashIdentifier);
			hashConstraint += hashTypedValue(constraint.value);
			hashConstraints += Hash.doHexStr(hashConstraint);
		}
		// @TODO hash order and limit properly!
		var hashOrder = Hash.doString("");
		var hashLimit = Hash.doInt(0);
		return Hash.doHexStr(Hash.doHexStr(hashConstraints) + hashOrder + hashLimit);
	};

	/**
	 * Helper method computing the hash value of a post
	 *
	 * @param post - The post to hash
	 * @param includeBeta - If the beta attributes must also be hashed
	 *	(true when checking signature of result container (CertifiedReading) or signature of board (CertifiedPosting))
	 * @param includeBetaSignature - If the board signature present in beta attibutes must also be hashed
	 *	(true when checking signature of result container (CertifiedReading) only)
	 * @return The hash value of the post.
	 */
	this.hashPost = function (post, includeBeta, includeBetaSignature) {
		//Get message and alpha attributes
		var message = post.message;
		var alpha = post.alpha.attribute;
		var messageHash = this.hashPostMessage(message);
		var concatenatedAlphaHashes = "";

		for (var i = 0; i < alpha.length; i++) {
			var attribute = alpha[i];
			if ((attribute.key === "signature" || attribute.key === "publickey") && includeBeta == false) {
				//If includeBeta==false, we are checking or generating signature of poster (AccessControlled),
				//thus signature and key of post itself must not be included
				//If includeBeta==true, then we are checking signature of board (CertifiedPosting or CertifiedReading),
				//thus signature and key must be included
				continue;
			}
			concatenatedAlphaHashes += hashTypedValue(attribute.value);
		}
		var alphaHash = Hash.doHexStr(concatenatedAlphaHashes);
		var betaHash = '';
		if (includeBeta) {
			var beta = JSON.parse(JSON.stringify(post.beta.attribute).replace(/@/g, ""));
			var concatenatedBetaHashes = ""
			for (var i = 0; i < beta.length; i++) {
				var attribute = beta[i];
				if (attribute.key === "boardSignature" && includeBetaSignature == false) {
					//If includeBetaSignature==false, we are checking signature of board (CertifiedPosting),
					//thus signature of post itself must not be included
					//If includeBeta==true, then we are checking signature whole board result (CertifiedReading),
					//thus signature must be included
					continue;
				}
				concatenatedBetaHashes += hashTypedValue(attribute.value);
			}
			betaHash = Hash.doHexStr(concatenatedBetaHashes);
		}
		return Hash.doHexStr(messageHash + alphaHash + betaHash);
	};

	/**
	 * Helper method computing the hash value of the post message
	 *
	 * @param message - The Base64 encoded message of the post
	 * @return The hash value of the message.
	 */
	this.hashPostMessage = function (message) {
		return Hash.doString(B64.decode(message));
	};
}