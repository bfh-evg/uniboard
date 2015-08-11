/*
 * Copyright (c) 2014 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.AlphaIdentifier;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.BetaIdentifier;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.MessageIdentifier;
import ch.bfh.uniboard.service.Order;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.helper.converter.classes.ConvertMethod;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.BigIntegerToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.StringToByteArray;
import ch.bfh.unicrypt.helper.hash.HashAlgorithm;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.helper.math.Alphabet;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.Z;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 * Component that checks if the author of the message has the right to post it on this board.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class AccessControlledService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME_CRYPTO = "crypto";
	private static final String ATTRIBUTE_NAME_PUBLICKEY = "publickey";
	private static final String ATTRIBUTE_NAME_SIG = "signature";
	private static final String GROUPED = "group";
	private static final String SECTIONED = "section";
	private static final String CHRONOLOGICAL = "timestamp";
	private static final String STARTTIME = "startTime";
	private static final String ENDTIME = "endTime";
	private static final String AUTH = "accessRight";
	private static final String AMOUNT = "amount";

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(HashAlgorithm.SHA256);
	protected static final ConvertMethod CONVERT_METHOD = ConvertMethod.getInstance(
			BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
			StringToByteArray.getInstance(Charset.forName("UTF-8")));

	private static final Logger logger = Logger.getLogger(AccessControlledService.class.getName());

	@EJB
	PostService postSuccessor;

	@EJB
	GetService getService;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {

		//TODO Check if ATTRIBUTE_NAME_PUBLICKEY and ATTRIBUTE_NAME_SIG are set in alpha
		if (!alpha.containsKey(ATTRIBUTE_NAME_PUBLICKEY)) {
			logger.log(Level.INFO, "Publickey missing in alpha.");
			beta.add(Attributes.REJECTED,
					new StringValue("BAC-001 Publickey missing in alpha."));
			return beta;
		}
		//Get the latest authorization with key and group in the current section in the authorization group
		//Contraint for the publickey
		List<Constraint> constraints = new ArrayList<>();
		List<String> sListKey = new ArrayList<>();
		sListKey.add(ATTRIBUTE_NAME_CRYPTO);
		sListKey.add(ATTRIBUTE_NAME_PUBLICKEY);
		Constraint cKey = new Equal(new MessageIdentifier(sListKey), alpha.getValue(ATTRIBUTE_NAME_PUBLICKEY));
		constraints.add(cKey);

		//Contraint of the group in the message
		List<String> sListGroup = new ArrayList<>();
		sListGroup.add(GROUPED);
		Constraint cGroup = new Equal(new MessageIdentifier(sListGroup), alpha.getValue(GROUPED));
		constraints.add(cGroup);

		//Constraint of the section
		List<String> sListSection = new ArrayList<>();
		sListSection.add(SECTIONED);
		Constraint cSection = new Equal(new AlphaIdentifier(sListSection), alpha.getValue(SECTIONED));
		constraints.add(cSection);

		//Constraint of the group
		List<String> sListGroup2 = new ArrayList<>();
		sListGroup2.add(GROUPED);
		Constraint cGroup2 = new Equal(new AlphaIdentifier(sListGroup2), new StringValue(AUTH));
		constraints.add(cGroup2);

		//Sort by time of posting desc
		List<Order> orderBy = new ArrayList<>();
		Order byTime = new Order(new BetaIdentifier(CHRONOLOGICAL), false);
		orderBy.add(byTime);

		//Limit to one result
		Query q = new Query(constraints, orderBy, 1);

		ResultContainer rc = this.getService.get(q);
		if (rc.getResult().isEmpty() || rc.getResult().size() != 1) {
			logger.log(Level.INFO, "No authorization for publickey {0}" + " section {1}" + " group {2}",
					new Object[]{alpha.getValue(ATTRIBUTE_NAME_PUBLICKEY), alpha.getValue(SECTIONED),
						alpha.getValue(GROUPED)});
			beta.add(Attributes.REJECTED,
					new StringValue("BAC-002 No authorization for this publickey."));
			return beta;
		}
		Post authPost = rc.getResult().get(0);

		DateValue currentPostTime = (DateValue) beta.getValue(CHRONOLOGICAL);
		try {
			JsonNode data = JsonLoader.fromString(new String(authPost.getMessage(), Charset.forName("UTF-8")));
			ObjectMapper mapper = new ObjectMapper();

			//Check the signature
			JsonNode key = data.get(ATTRIBUTE_NAME_CRYPTO);

			String type = key.get("type").textValue();

			boolean signature = false;

			switch (type) {
				case "RSA":
					signature = this.checkRSASignature(key, message, alpha);
					break;
				case "DL":
					signature = this.checkDLSignature(key, message, alpha);
					break;
				case "ECDL":
					signature = this.checkECDLSignature(key, message, alpha);
					break;
				default:
					break;
			}

			if (!signature) {
				logger.log(Level.INFO,
						"Signature for group {0} and key  {1} is not valid.",
						new Object[]{alpha.getValue(GROUPED), key.get("publickey").asText()});
				beta.add(Attributes.REJECTED,
						new StringValue("BAC-003 Signature is not valid."));
				return beta;
			}

			//Check if startTime and endTime are set and if the post time is in between.
			if (data.has(STARTTIME)) {
				Date startDate = mapper.readValue(data.get(STARTTIME).traverse(), Date.class);
				if (startDate.after(currentPostTime.getValue())) {
					logger.log(Level.INFO,
							"Authorization for key {0}" + " section {1}" + " group {2} is not active yet. Start at {3}",
							new Object[]{alpha.getValue(ATTRIBUTE_NAME_CRYPTO), alpha.getValue(SECTIONED),
								alpha.getValue(GROUPED), startDate});
					beta.add(Attributes.REJECTED,
							new StringValue("BAC-004 Authorization is not active yet."));
					return beta;
				}
			}
			if (data.has(ENDTIME)) {
				Date endDate = mapper.readValue(data.get(ENDTIME).traverse(), Date.class);
				if (endDate.before(currentPostTime.getValue())) {
					logger.log(Level.INFO,
							"Authorization for key {0}" + " section {1}" + " group {2} has expired at {3}.",
							new Object[]{alpha.getValue(ATTRIBUTE_NAME_CRYPTO), alpha.getValue(SECTIONED),
								alpha.getValue(GROUPED), endDate});
					beta.add(Attributes.REJECTED,
							new StringValue("BAC-005 Authorization expired."));
					return beta;
				}
			}
			//Check if the amount of allowed posts is not exceeded
			if (data.has(AMOUNT)) {
				//If check the group for the amount of existing posts
				List<Constraint> constraintsAmount = new ArrayList<>();

				Constraint cASection = new Equal(new AlphaIdentifier(SECTIONED), alpha.getValue(SECTIONED));
				constraintsAmount.add(cASection);

				Constraint cAGroup = new Equal(new AlphaIdentifier(GROUPED), alpha.getValue(GROUPED));
				constraintsAmount.add(cAGroup);

				Constraint cAKey = new Equal(new AlphaIdentifier(ATTRIBUTE_NAME_PUBLICKEY),
						alpha.getValue(ATTRIBUTE_NAME_PUBLICKEY));
				constraintsAmount.add(cAKey);

				Query qAmount = new Query(constraintsAmount);
				ResultContainer rcAmount = this.getService.get(qAmount);

				if (rcAmount.getResult().size() >= data.get(AMOUNT).asInt()) {
					logger.log(Level.INFO,
							"Authorization for key {0}" + " section {1}" + " group {2} has used the allowed posts {3}.",
							new Object[]{alpha.getValue(ATTRIBUTE_NAME_CRYPTO), alpha.getValue(SECTIONED),
								alpha.getValue(GROUPED), data.get("amount").asInt()});
					beta.add(Attributes.REJECTED,
							new StringValue("BAC-007 Amount of allowed posts used up."));
					return beta;
				}
			}

		} catch (IOException ex) {
			logger.log(Level.SEVERE, "Error occoured while parsing authorization {0}", ex.getMessage());
			beta.add(Attributes.ERROR,
					new StringValue("BAC-006 Internal server error."));
			return beta;
		}

		//accept message
		return beta;
	}

	protected boolean checkRSASignature(JsonNode key, byte[] message, Attributes alpha) {
		String rsaPublicKey = key.get(ATTRIBUTE_NAME_PUBLICKEY).textValue();
		BigInteger[] rsaPublicKeyBI = MathUtil.unpair(new BigInteger(rsaPublicKey));
		if (rsaPublicKeyBI.length != 2) {
			logger.log(Level.INFO, "RSA public key does not consist of 2 big integers.");
			return false;
		}
		ZMod n = ZMod.getInstance(rsaPublicKeyBI[1]);

		Element messageElement = this.createMessageElement(message, alpha);

		RSASignatureScheme rsa
				= RSASignatureScheme.getInstance(messageElement.getSet(), n, CONVERT_METHOD, HASH_METHOD);
		Element rsaPublicKeyElement = rsa.getVerificationKeySpace().getElement(rsaPublicKeyBI[0]);

		String signature = ((StringValue) alpha.getValue(ATTRIBUTE_NAME_SIG)).getValue();
		BigInteger biSignature = new BigInteger(signature);
		Element signatureElement = rsa.getSignatureSpace().getElementFrom(biSignature);

		return rsa.verify(rsaPublicKeyElement, messageElement, signatureElement).getValue();
	}

	protected boolean checkDLSignature(JsonNode key, byte[] message, Attributes alpha) {
		BigInteger modulus = new BigInteger(key.get("p").textValue());
		BigInteger orderFactor = new BigInteger(key.get("q").textValue());
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		BigInteger generator = new BigInteger(key.get("g").textValue());
		GStarModElement g = g_q.getElement(generator);

		Element messageElement = this.createMessageElement(message, alpha);

		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(
				messageElement.getSet(), g, CONVERT_METHOD, HASH_METHOD);

		Element publicKey = schnorr.getVerificationKeySpace()
				.getElement(new BigInteger(key.get(ATTRIBUTE_NAME_PUBLICKEY).textValue()));

		String signature = ((StringValue) alpha.getValue(ATTRIBUTE_NAME_SIG)).getValue();
		BigInteger biSignature = new BigInteger(signature);
		Element signatureElement = schnorr.getSignatureSpace().getElementFrom(biSignature);

		return schnorr.verify(publicKey, messageElement, signatureElement).getValue();
	}

	protected boolean checkECDLSignature(JsonNode key, byte[] message, Attributes alpha) {
		return false;
	}

	protected Element createMessageElement(byte[] message, Attributes alpha) {
		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.UNICODE_BMP);
		Z z = Z.getInstance();
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		//itterate over alpha until one reaches the property = signature
		for (Map.Entry<String, Value> e : alpha.getEntries()) {
			if (e.getKey().equals(ATTRIBUTE_NAME_SIG)) {
				break;
			}
			Element tmp;
			if (e.getValue() instanceof ByteArrayValue) {
				tmp = byteSpace.getElement(((ByteArrayValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof DateValue) {
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(((DateValue) e.getValue()).getValue());
				tmp = stringSpace.getElement(stringDate);
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof IntegerValue) {
				tmp = z.getElement(((IntegerValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof StringValue) {
				tmp = stringSpace.getElement(((StringValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else {
				logger.log(Level.SEVERE, "Unsupported Value type.");
			}

		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);
		return Pair.getInstance(messageElement, alphaElement);
	}
}
