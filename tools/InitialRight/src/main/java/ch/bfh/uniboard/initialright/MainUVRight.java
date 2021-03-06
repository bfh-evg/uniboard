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
import java.util.Date;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class MainUVRight {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String keyStorePath = "/home/phil/UniVote.jks";
		String keyStorePass = "123456";
		String boardAlias = "uniboardvote";
		String boardPKPass = "123456";
		String ecAlias = "ec-demo";
		String ecPKPass = "123456";
		String eaAlias = "ea-demo";
		String section = "test-2015";

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

		//Load keypair from ec
		Key ecKey = caKs.getKey(ecAlias, ecPKPass.toCharArray());
		DSAPrivateKey ecPrivKey = (DSAPrivateKey) ecKey;

		Certificate ecCert = caKs.getCertificate(ecAlias);
		DSAPublicKey ecPubKey = (DSAPublicKey) ecCert.getPublicKey();
		BigInteger electionCoordinatorPublicKey = ecPubKey.getY();

		//Load publickey of ea
		Certificate eaCert = caKs.getCertificate(eaAlias);
		DSAPublicKey eaPubKey = (DSAPublicKey) eaCert.getPublicKey();
		BigInteger electionAdministrationPublicKey = eaPubKey.getY();

		//Create correct json message
		byte[] message1 = ("{\"group\":\"accessRight\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ ecPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + ecPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + ecPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionCoordinatorPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue(section));
		alpha.add("group", new StringValue("accessRight"));
		Element ubMsgSig = PostCreator.createAlphaSignatureWithDL(message1, alpha, dsaPrivKey);
		alpha.add("signature", new StringValue(ubMsgSig.convertToBigInteger().toString(10)));
		alpha.add("publickey", new StringValue(uniboardPublicKey.toString(10)));

		Attributes beta = new Attributes();
		beta.add("timestamp", new DateValue(new Date()));
		beta.add("rank", new IntegerValue(0));
		Element initMsgBetaSig = PostCreator.createBetaSignature(message1, alpha, beta, dsaPrivKey);
		beta.add("boardSignature", new StringValue(initMsgBetaSig.convertToBigInteger().toString(10)));

		//output post as json
		String post = PostCreator.createMessage(message1, alpha, beta);

		System.out.println(post);

		//create accessRight message
		byte[] message2 = ("{\"group\":\"electionDefinition\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ eaPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + eaPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + eaPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionAdministrationPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha2 = new Attributes();
		alpha2.add("section", new StringValue(section));
		alpha2.add("group", new StringValue("accessRight"));
		Element ucMsgSig = PostCreator.createAlphaSignatureWithDL(message2, alpha2, dsaPrivKey);
		alpha2.add("signature", new StringValue(ucMsgSig.convertToBigInteger().toString(10)));
		alpha2.add("publickey", new StringValue(electionCoordinatorPublicKey.toString(10)));

		Attributes beta2 = new Attributes();
		beta2.add("timestamp", new DateValue(new Date()));
		beta2.add("rank", new IntegerValue(1));
		Element acMsgSig = PostCreator.createBetaSignature(message1, alpha2, beta2, dsaPrivKey);
		beta2.add("boardSignature", new StringValue(acMsgSig.convertToBigInteger().toString(10)));

		String post2 = PostCreator.createMessage(message2, alpha2, beta2);
		System.out.println(post2);

	}

}
