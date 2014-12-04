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
package ch.bfh.uniboard.initialright;

import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.Alphabet;
import ch.bfh.unicrypt.helper.MathUtil;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.helper.converter.classes.ConvertMethod;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.BigIntegerToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.ByteArrayToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.StringToByteArray;
import ch.bfh.unicrypt.helper.hash.HashAlgorithm;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String keyStorePath = "/home/hss3/Documents/UniCert.jks";
		String keyStorePass = "123456";
		String boardAlias = "uniboardcert";
		String boardPKPass = "123456";
		String certAlias = "unicertbfh";
		String certPKPass = "123456";

		KeyStore caKs = KeyStore.getInstance(System.getProperty("javax.net.ssl.keyStoreType", "jks"));

		InputStream in;
		File file = new File(keyStorePath);
		in = new FileInputStream(file);

		caKs.load(in, keyStorePass.toCharArray());

		// Load uniboard key and cert
		Key boardKey = caKs.getKey(boardAlias, boardPKPass.toCharArray());
		DSAPrivateKey dsaPrivKey = (DSAPrivateKey) boardKey;

		Certificate boardCert = caKs.getCertificate(boardAlias);
		DSAPublicKey boardPubKey = (DSAPublicKey) boardCert.getPublicKey();
		BigInteger uniboardPublicKey = boardPubKey.getY();
		//Load keypair from unicert

		Key certKey = caKs.getKey(certAlias, certPKPass.toCharArray());
		RSAPrivateCrtKey rsaPrivKey = (RSAPrivateCrtKey) certKey;

		Certificate certCert = caKs.getCertificate(certAlias);
		RSAPublicKey certPubKey = (RSAPublicKey) certCert.getPublicKey();
		BigInteger unicertPublicKey = MathUtil.pair(certPubKey.getPublicExponent(), certPubKey.getModulus());

		//Create correct json message
		byte[] authorization = ("{\"group\":\"accessRight\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ unicertPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Map<String, String> alpha = new LinkedHashMap();
		alpha.put("section", "unicert");
		alpha.put("group", "accessRight");
		Element ubMsgSig = createSignatureInitialMsg(authorization, alpha, dsaPrivKey);
		alpha.put("signature", ubMsgSig.getBigInteger().toString(10));
		alpha.put("key", uniboardPublicKey.toString(10));

		Map<String, String> beta = new LinkedHashMap();
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(timeZone);
		beta.put("timestamp", dateFormat.format(new Date()));
		beta.put("rank", "0");
		Element initMsgBetaSig = createBetaSignature(authorization, alpha, beta, dsaPrivKey);
		beta.put("boardSignature", initMsgBetaSig.getBigInteger().toString(10));

		//output post as json
		String output = "";
		output += "{";
		output += "\"message\":";
		output += new String(authorization);
		output += ",";
		output += "\"alpha\": {";
		for (Map.Entry<String, String> e : alpha.entrySet()) {
			output += "\"" + e.getKey() + "\": \"" + e.getValue() + "\",";
		}
		output = output.substring(0, output.length() - 1);
		output += "},";
		output += "\"beta\": {";
		for (Map.Entry<String, String> e : beta.entrySet()) {
			output += "\"" + e.getKey() + "\": \"" + e.getValue() + "\",";
		}
		output = output.substring(0, output.length() - 1);
		output += "}}";
		System.out.println(output);

		//create accessRight message
		byte[] authorization2 = ("{\"group\":\"certificate\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ unicertPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Map<String, String> alpha2 = new LinkedHashMap();
		alpha2.put("section", "unicert");
		alpha2.put("group", "accessRight");
		Element ucMsgSig = createSignatureInitialMsg(authorization2, alpha2, dsaPrivKey);
		alpha2.put("signature", ucMsgSig.getBigInteger().toString(10));
		alpha2.put("key", uniboardPublicKey.toString(10));

		Map<String, String> beta2 = new LinkedHashMap();
		TimeZone timeZone2 = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat2.setTimeZone(timeZone2);
		beta2.put("timestamp", dateFormat2.format(new Date()));
		beta2.put("rank", "1");
		Element acMsgSig = createBetaSignature(authorization2, alpha2, beta2, dsaPrivKey);
		beta2.put("boardSignature", acMsgSig.getBigInteger().toString(10));

		//output post as json
		String output2 = "";
		output2 += "{";
		output2 += "\"message\":";
		output2 += new String(authorization2);
		output2 += ",";
		output2 += "\"alpha\": {";
		for (Map.Entry<String, String> e : alpha2.entrySet()) {
			output2 += "\"" + e.getKey() + "\": \"" + e.getValue() + "\",";
		}
		output2 = output2.substring(0, output2.length() - 1);
		output2 += "},";
		output2 += "\"beta\": {";
		for (Map.Entry<String, String> e : beta2.entrySet()) {
			output2 += "\"" + e.getKey() + "\": \"" + e.getValue() + "\",";
		}
		output2 = output2.substring(0, output2.length() - 1);
		output2 += "}}";
		System.out.println(output2);

	}

	public static Element createSignatureInitialMsg(byte[] message, Map<String, String> alpha, DSAPrivateKey dsaPrivKey) {

		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (String value : alpha.values()) {
			Element tmp = stringSpace.getElement(value);
			alphaElements.add(tmp);
		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);
		Element toSign = Pair.getInstance(messageElement, alphaElement);

		GStarModPrime g_q = GStarModPrime.getInstance(dsaPrivKey.getParams().getP(), dsaPrivKey.getParams().getQ());
		GStarModElement g = g_q.getElement(dsaPrivKey.getParams().getG());
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(toSign.getSet(), g, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(dsaPrivKey.getX());
		return schnorr.sign(privateKeyElement, toSign);
	}

	public static Element createSignatureACMsg(byte[] message, Map<String, String> alpha, RSAPrivateCrtKey rsaPrivKey) {

		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (String value : alpha.values()) {
			Element tmp = stringSpace.getElement(value);
			alphaElements.add(tmp);
		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);
		Element toSign = Pair.getInstance(messageElement, alphaElement);

		RSASignatureScheme rsaScheme
				= RSASignatureScheme.getInstance(toSign.getSet(), ZMod.getInstance(rsaPrivKey.getModulus()), HASH_METHOD);
		Element privateKeyElement = rsaScheme.getSignatureKeySpace().getElement(rsaPrivKey.getPrivateExponent());
		return rsaScheme.sign(privateKeyElement, toSign);
	}

	public static Element createBetaSignature(byte[] message,
			Map<String, String> alpha, Map<String, String> beta, DSAPrivateKey dsaPrivKey) {

		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (String value : alpha.values()) {
			Element tmp = stringSpace.getElement(value);
			alphaElements.add(tmp);
		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);

		List<Element> betaElements = new ArrayList<>();
		for (String value : beta.values()) {
			Element tmp = stringSpace.getElement(value);
			betaElements.add(tmp);
		}
		DenseArray immuElements2 = DenseArray.getInstance(betaElements);
		Element betaElement = Tuple.getInstance(immuElements2);
		Element toSign = Tuple.getInstance(messageElement, alphaElement, betaElement);

		GStarModPrime g_q = GStarModPrime.getInstance(dsaPrivKey.getParams().getP(), dsaPrivKey.getParams().getQ());
		GStarModElement g = g_q.getElement(dsaPrivKey.getParams().getG());
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(toSign.getSet(), g, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(dsaPrivKey.getX());
		return schnorr.sign(privateKeyElement, toSign);
	}

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(
			HashAlgorithm.SHA256,
			ConvertMethod.getInstance(
					BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
					ByteArrayToByteArray.getInstance(false),
					StringToByteArray.getInstance(Charset.forName("UTF-8"))),
			HashMethod.Mode.RECURSIVE);

}
