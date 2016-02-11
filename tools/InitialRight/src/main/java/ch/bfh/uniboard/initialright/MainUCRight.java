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

import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigInteger;
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
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class MainUCRight {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String keyStorePath = "../../../unicert/demo/UniCert.jks";
		String keyStorePass = "12345678";
		String boardAlias = "uniboardcert";
		String boardPKPass = "12345678";
		String certAlias = "unicertbfh";
		String certPKPass = "12345678";
		String section = "unicert";

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
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", "accessRight"));
		Element ubMsgSig = PostCreator.createAlphaSignatureWithDL(authorization, alpha, dsaPrivKey);
		alpha.add(new Attribute("signature", ubMsgSig.convertToBigInteger().toString(10)));
		alpha.add(new Attribute("publickey", uniboardPublicKey.toString(10)));

		Attributes beta = new Attributes();
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		String dateTime = dateFormat.format(new Date());
		beta.add(new Attribute("timestamp", dateTime, DataType.DATE));
		beta.add(new Attribute("rank", "0", DataType.INTEGER));
		Element initMsgBetaSig = PostCreator.createBetaSignature(authorization, alpha, beta, dsaPrivKey);
		beta.add(new Attribute("boardSignature", initMsgBetaSig.convertToBigInteger().toString(10)));

		String post = PostCreator.createMessage(authorization, alpha, beta);
		System.out.println(post);

		//create accessRight message
		byte[] authorization2 = ("{\"group\":\"certificate\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ unicertPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha2 = new Attributes();
		alpha2.add(new Attribute("section", section));
		alpha2.add(new Attribute("group", "accessRight"));
		Element ucMsgSig = PostCreator.createAlphaSignatureWithRSA(authorization2, alpha2, rsaPrivKey);
		alpha2.add(new Attribute("signature", ucMsgSig.convertToBigInteger().toString(10)));
		alpha2.add(new Attribute("publickey", unicertPublicKey.toString(10)));

		Attributes beta2 = new Attributes();
		String dateTime2 = dateFormat.format(new Date());
		beta2.add(new Attribute("timestamp", dateTime2, DataType.DATE));
		beta2.add(new Attribute("rank", "1", DataType.INTEGER));
		Element acMsgSig = PostCreator.createBetaSignature(authorization2, alpha2, beta2, dsaPrivKey);
		beta2.add(new Attribute("boardSignature", acMsgSig.convertToBigInteger().toString(10)));

		//output post as json
		String post2 = PostCreator.createMessage(authorization2, alpha2, beta2);
		System.out.println(post2);

	}

}
