/* global leemon, CryptoJS, ubConfig */

////////////////////////////////////////////////////////////////////////
// Post signatures crypto
////////////////////////////////////////////////////////////////////////

(function (window) {

	var UBClient = new function () {

		this.post = function (message, sk, p, q, g, successCB, errorCB) {

			//TODO Create post
			var post = {
				message: message,
				alpha: {}
			};

			//For IE
			$.support.cors = true;
			//Ajax request
			$.ajax({
				url: ubConfig.URL_UNIBOARD_POST,
				type: 'POST',
				contentType: "application/json",
				accept: "application/json",
				cache: false,
				dataType: 'json',
				data: JSON.stringify(post),
				timeout: 10000,
				crossDomain: true,
				success: function (data) {
					UBClient.processPost(data, successCB, errorCB);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					var errorObj = {
						message: textStatus,
						code: errorThrown,
						type: "serverError"
					};
					errorCB(errorObj);
				}
			});
			;

		};

		this.get = function (query, successCB, errorCB) {
			//For IE
			$.support.cors = true;
			//Ajax request
			$.ajax({
				url: ubConfig.URL_UNIBOARD_GET,
				type: 'POST',
				contentType: "application/json",
				accept: "application/json",
				cache: false,
				dataType: 'json',
				data: JSON.stringify(query),
				timeout: 10000,
				crossDomain: true,
				success: function (data) {
					UBClient.processGet(data, successCB, errorCB);
				},
				error: function (jqXHR, textStatus, errorThrown) {
					var errorObj = {
						message: textStatus,
						code: errorThrown,
						type: "serverError"
					};
					errorCB(errorObj);
				}

			});
		}

		this.procsessGet = function (data, successCB, errorCB) {
			//TODO Validate gamma signature
			//TODO Check gamma timestamp

			//TODO for each post
			//TODO Check beta signature
			//TODO Check signature of poster if available

		};


		this.processPost = function (data, successCB, errorCB) {
			//TODO Validate beta signature

		};

		/**
		 * Signs the post that will be posted on UniBoard using the given generator (Schnorr signature)
		 * @param post Message to be signed
		 * @param generator Generator to be used in the signature
		 * @param sk Private key used for signature
		 * @returns Signature as object containing the paired value as
		 * bigInt (sig) and its base {base} string representation (sigString)
		 */
		this.signPost = function (post, generator, sk) {

			//Hash post
			var postHash = this.hashPost(post, false, false);
			var paired = this.createSchnorrSignature(postHash, sk, this.signatureSetting.p, this.signatureSetting.q, generator);

			return {sig: paired, sigString: leemon.bigInt2str(paired, ubConfig.BASE)};
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
					leemon.str2bigInt(resultContainer.gamma.attribute[1].value.value, ubConfig.BASE),
					hash,
					leemon.str2bigInt(sk, ubConfig.BASE),
					leemon.str2bigInt(p, ubConfig.BASE),
					leemon.str2bigInt(q, ubConfig.BASE),
					leemon.str2bigInt(g, ubConfig.BASE))) {
				return "Wrong board signature for GET";
			}

			// 2. Verify board signature of each Post
			var posts = resultContainer.result.post;
			for (var i = 0; i < posts.length; i++) {
				var post = posts[i];
				var postHash = this.hashPost(post, true, false);
				if (!this.verifySchnorrSignature(
						leemon.str2bigInt(post.beta.attribute[2].value.value, ubConfig.BASE),
						postHash,
						leemon.str2bigInt(sk, ubConfig.BASE),
						leemon.str2bigInt(p, ubConfig.BASE),
						leemon.str2bigInt(q, ubConfig.BASE),
						leemon.str2bigInt(g, ubConfig.BASE))) {
					return "Wrong board signature in post " + i;
				}
			}

			// 3. Verify poster signature of each Post contained in the result
			if (verifyPosterSignature == true) {
				for (var i = 0; i < posts.length; i++) {
					var post = posts[i];
					var postHash = this.hashPost(post, false, false);
					if (!this.verifySchnorrSignature(
							leemon.str2bigInt(post.alpha.attribute[2].value.value, ubConfig.BASE),
							postHash,
							leemon.str2bigInt(posterSetting.PK, ubConfig.BASE),
							leemon.str2bigInt(posterSetting.P, ubConfig.BASE),
							leemon.str2bigInt(posterSetting.Q, ubConfig.BASE),
							leemon.str2bigInt(posterSetting.G, ubConfig.BASE))) {
						return "Wrong poster signature in post " + i;
					}
				}
			}
			return true;
		};

		/**
		 * Verify a Schnorr signature
		 * @param signature The paired value of the signature (s,e)
		 * @param messageHash Hash of message to sign encoded in base 16
		 * @param publicKey Public key to use for the verification in leemon big int format
		 * @param p Prime p in leemon big int format
		 * @param q Prime q in leemon big int format
		 * @param g Generator g in leemon big int format
		 * @returns true if signature is valid, false otherwise
		 */
		this.verifySchnorrSignature = function (signature, messageHash, publicKey, p, q, g) {

			var signatureValues = this.unpair(signature);

			var a = signatureValues[0];
			var b = signatureValues[1];

			var c = leemon.powMod(g, b, p);
			var d = leemon.powMod(publicKey, a, p);

			var a2Verif = leemon.multMod(c, leemon.inverseMod(d, p), p);
			var bVerif = Hash.doHexStr(messageHash + Hash.doBigInt(a2Verif));

			return leemon.equals(a, leemon.mod(leemon.str2bigInt(bVerif, 16), q));
		};

		/**
		 * Create a Schnorr signature
		 * @param messageHash Hash of message to sign encoded in base 16
		 * @param privateKey Private key to use to sign in leemon big int format
		 * @param p Prime p in leemon big int format
		 * @param q Prime q in leemon big int format
		 * @param g Generator g in leemon big int format
		 * @returns the paired value of the two signature elements (s,e)
		 */
		this.createSchnorrSignature = function (messageHash, privateKey, p, q, g) {
			// 1. Choose r at random from Zq and calculate g^r
			var r = leemon.randBigIntInZq(q);
			var a2 = leemon.powMod(g, r, p);

			// 2. Hash and calculate second part of signature
			var a2Hash = Hash.doBigInt(a2);
			var aStr = Hash.doHexStr(messageHash + a2Hash);
			var a = leemon.mod(leemon.str2bigInt(aStr, 16), q);

			var b = leemon.add(r, leemon.mult(a, privateKey));
			b = leemon.mod(b, q);

			return this.pair(a, b);
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
		/**
		 * Computes the elegant pairing function for two non-negative BigInteger values.
		 * @see http://szudzik.com/ElegantPairing.pdf
		 * @param bigInt1 The first value
		 * @param bigInt2 The second value
		 * @return The result of applying the elegant pairing function
		 */
		this.pair = function (bigInt1, bigInt2) {
			if (leemon.negative(bigInt1) || leemon.negative(bigInt2)) {
				throw Error("Cannot be negative");
			}
			if (leemon.greater(bigInt2, bigInt1) || leemon.equals(bigInt2, bigInt1)) {
				return leemon.add(leemon.mult(bigInt2, bigInt2), bigInt1);
			} else {
				return leemon.add(leemon.add(leemon.mult(bigInt1, bigInt1), bigInt1), bigInt2);
			}
		};

		/**
		 * Computes the inverse of the binary elegant pairing function for a given non-negative BigInteger value.
		 * @see http://szudzik.com/ElegantPairing.pdf
		 * @param bigInt The input value
		 * @return An array containing the two resulting values
		 */
		this.unpair = function (bigInt) {
			var x1 = this.isqrt(bigInt);
			var x2 = leemon.sub(bigInt, leemon.mult(x1, x1));

			if (leemon.greater(x1, x2)) {
				return [x2, x1];
			} else {
				return [x1, leemon.sub(x2, x1)];
			}
		};
	}
	window.UBClient = UBClient;
})(window);
(function (window) {

	var Hash = new function () {

		// Default hash method
		var hashMethod = CryptoJS.SHA256;

		this.setHashMethod = function (method) {
			switch (method) {
				case 'SHA-1':
					hashMethod = CryptoJS.SHA1;
					break;
				case 'SHA-224':
					hashMethod = CryptoJS.SHA224;
					break;
				case 'SHA-256':
					hashMethod = CryptoJS.SHA256;
					break;
				case 'SHA-384':
					hashMethod = CryptoJS.SHA384;
					break;
				case 'SHA-512':
					hashMethod = CryptoJS.SHA512;
					break;
				default:
					// Unsupported!
					console.log("Unsupported hash algorithm: '" + method + "'");
			}
		};

		/*
		 * Hashes a UTF-8 string
		 * returns a hex representation of the hash
		 */
		this.doString = function (msg) {
			var hash = hashMethod(msg);
			return hash.toString(CryptoJS.enc.Hex).toUpperCase();
		};

		/*
		 * Hashes a leemon BigInteger
		 * returns a hex representation of the hash
		 */
		this.doBigInt = function (bigInteger) {

			//In UniCrypt (Java) the BigInteger as considered as positive before being hashed (0s are added in front of the byte
			//array in case of a negative big int. So, we do the same here
			var hexStr = leemon.bigInt2str(bigInteger, 16);

			if (parseInt(hexStr.substr(0, 2), 16) > 127) {
				hexStr = "0" + hexStr;
			}

			return this.doHexStr(hexStr);
		};

		/*
		 * Hashes an integer
		 * returns a hex representation of the hash
		 */
		this.doInt = function (long) {

			var bigint = leemon.int2bigInt(long, 1);
			return this.doBigInt(bigint);
		};

		/*
		 * Hashes a date
		 * Computes the hash of the ISO format without milliseconds
		 * returns a hex representation of the hash
		 */
		this.doDate = function (date) {

			var dateFormatted = date.toISOString();

			//Workaround because current Java implementation does not includes milliseconds
			dateFormatted = dateFormatted.substring(0, dateFormatted.length - 5) + "Z";

			return this.doString(dateFormatted);
		};

		/*
		 * Hashes a base 64 representation of a byte array
		 * returns a hex representation of the hash
		 */
		this.doByteArray = function (base64ByteArray) {

			var byteArray = B64.decode(base64ByteArray);
			return this.doBigInt(byteArray);
		};

		/*
		 * Hashes a hexadecimal representation
		 * returns a hex representation of the hash
		 */
		this.doHexStr = function (hexStr) {

			// If the length of the string is not a multiple of 2, "0" is added at the
			// beginning of the string.
			// Reason: CryptoJS.enc.Hex.parse('ABC').toString() results in 'AB0C'!
			if (hexStr.length % 2 != 0) {
				hexStr = "0" + hexStr;
			}
			var hash = hashMethod(CryptoJS.enc.Hex.parse(hexStr));
			return hash.toString(CryptoJS.enc.Hex).toUpperCase();
		};
	};

	window.Hash = Hash;

})(window);