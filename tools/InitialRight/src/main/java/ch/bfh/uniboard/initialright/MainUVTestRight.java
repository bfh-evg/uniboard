 &/*
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
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */


public class MainUVTestRight {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String keyStorePath = "/home/hss3/Documents/GIT/univote2/demo/UniVote.jks";
		String keyStorePass = "12345678";
		String boardAlias = "uniboardvote";
		String boardPKPass = "12345678";
		String ecAlias = "ec-demo";
		String ecPKPass = "12345678";
		String eaAlias = "ea-demo";
		String section = "sub-2015";

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
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", "accessRight"));
		Element ubMsgSig = PostCreator.createAlphaSignatureWithDL(message1, alpha, dsaPrivKey);
		alpha.add(new Attribute("signature", ubMsgSig.convertToBigInteger().toString(10)));
		alpha.add(new Attribute("publickey", uniboardPublicKey.toString(10)));

		Attributes beta = new Attributes();
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		String dateTime = dateFormat.format(new Date());
		beta.add(new Attribute("timestamp", dateTime, DataType.DATE));
		beta.add(new Attribute("rank", "0", DataType.INTEGER));
		Element initMsgBetaSig = PostCreator.createBetaSignature(message1, alpha, beta, dsaPrivKey);
		beta.add(new Attribute("boardSignature", initMsgBetaSig.convertToBigInteger().toString(10)));

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
		alpha2.add(new Attribute("section", section));
		alpha2.add(new Attribute("group", "accessRight"));
		Element ucMsgSig = PostCreator.createAlphaSignatureWithDL(message2, alpha2, dsaPrivKey);
		alpha2.add(new Attribute("signature", ucMsgSig.convertToBigInteger().toString(10)));
		alpha2.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta2 = new Attributes();
		String dateTime2 = dateFormat.format(new Date());
		beta2.add(new Attribute("timestamp", dateTime2, DataType.DATE));
		beta2.add(new Attribute("rank", "1", DataType.INTEGER));
		Element acMsgSig = PostCreator.createBetaSignature(message1, alpha2, beta2, dsaPrivKey);
		beta2.add(new Attribute("boardSignature", acMsgSig.convertToBigInteger().toString(10)));

		String post2 = PostCreator.createMessage(message2, alpha2, beta2);
		System.out.println(post2);

		//create accessRight message
		byte[] message3 = ("{\"group\":\"electionOptions\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ eaPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + eaPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + eaPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionAdministrationPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha3 = new Attributes();
		alpha3.add(new Attribute("section", section));
		alpha3.add(new Attribute("group", "accessRight"));
		Element ucMsgSig2 = PostCreator.createAlphaSignatureWithDL(message3, alpha3, dsaPrivKey);
		alpha3.add(new Attribute("signature", ucMsgSig2.convertToBigInteger().toString(10)));
		alpha3.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta3 = new Attributes();
		String dateTime3 = dateFormat.format(new Date());
		beta3.add(new Attribute("timestamp", dateTime3, DataType.DATE));
		beta3.add(new Attribute("rank", "2", DataType.INTEGER));
		Element acMsgSig2 = PostCreator.createBetaSignature(message1, alpha3, beta3, dsaPrivKey);
		beta3.add(new Attribute("boardSignature", acMsgSig2.convertToBigInteger().toString(10)));

		String post3 = PostCreator.createMessage(message3, alpha3, beta3);
		System.out.println(post3);

		//create accessRight message
		byte[] message4 = ("{\"group\":\"electoralRoll\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ eaPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + eaPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + eaPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionAdministrationPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha4 = new Attributes();
		alpha4.add(new Attribute("section", section));
		alpha4.add(new Attribute("group", "accessRight"));
		Element ucMsgSig3 = PostCreator.createAlphaSignatureWithDL(message4, alpha4, dsaPrivKey);
		alpha4.add(new Attribute("signature", ucMsgSig3.convertToBigInteger().toString(10)));
		alpha4.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta4 = new Attributes();
		String dateTime4 = dateFormat.format(new Date());
		beta4.add(new Attribute("timestamp", dateTime4, DataType.DATE));
		beta4.add(new Attribute("rank", "3", DataType.INTEGER));
		Element acMsgSig3 = PostCreator.createBetaSignature(message1, alpha4, beta4, dsaPrivKey);
		beta4.add(new Attribute("boardSignature", acMsgSig3.convertToBigInteger().toString(10)));

		String post4 = PostCreator.createMessage(message4, alpha4, beta4);
		System.out.println(post4);

		//create accessRight message
		byte[] message5 = ("{\"group\":\"trustees\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ eaPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + eaPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + eaPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionAdministrationPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha5 = new Attributes();
		alpha5.add(new Attribute("section", section));
		alpha5.add(new Attribute("group", "accessRight"));
		Element ucMsgSig4 = PostCreator.createAlphaSignatureWithDL(message5, alpha5, dsaPrivKey);
		alpha5.add(new Attribute("signature", ucMsgSig4.convertToBigInteger().toString(10)));
		alpha5.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta5 = new Attributes();
		String dateTime5 = dateFormat.format(new Date());
		beta5.add(new Attribute("timestamp", dateTime5, DataType.DATE));
		beta5.add(new Attribute("rank", "4", DataType.INTEGER));
		Element acMsgSig4 = PostCreator.createBetaSignature(message1, alpha5, beta5, dsaPrivKey);
		beta5.add(new Attribute("boardSignature", acMsgSig4.convertToBigInteger().toString(10)));

		String post5 = PostCreator.createMessage(message5, alpha5, beta5);
		System.out.println(post5);

		//create accessRight message
		byte[] message6 = ("{\"group\":\"securityLevel\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ eaPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + eaPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + eaPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionAdministrationPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha6 = new Attributes();
		alpha6.add(new Attribute("section", section));
		alpha6.add(new Attribute("group", "accessRight"));
		Element ucMsgSig5 = PostCreator.createAlphaSignatureWithDL(message6, alpha6, dsaPrivKey);
		alpha6.add(new Attribute("signature", ucMsgSig5.convertToBigInteger().toString(10)));
		alpha6.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta6 = new Attributes();
		String dateTime6 = dateFormat.format(new Date());
		beta6.add(new Attribute("timestamp", dateTime6, DataType.DATE));
		beta6.add(new Attribute("rank", "5", DataType.INTEGER));
		Element acMsgSig5 = PostCreator.createBetaSignature(message1, alpha6, beta6, dsaPrivKey);
		beta6.add(new Attribute("boardSignature", acMsgSig5.convertToBigInteger().toString(10)));

		String post6 = PostCreator.createMessage(message6, alpha6, beta6);
		System.out.println(post6);

		//create accessRight message
		byte[] message7 = ("{\"group\":\"votingData\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ ecPubKey.getParams().getP().toString(10)
				+ "\",\"q\":\"" + ecPubKey.getParams().getQ().toString(10)
				+ "\",\"g\":\"" + ecPubKey.getParams().getG().toString(10)
				+ "\",\"publickey\":\""
				+ electionCoordinatorPublicKey.toString(10) + "\"}}").getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha7 = new Attributes();
		alpha7.add(new Attribute("section", section));
		alpha7.add(new Attribute("group", "accessRight"));
		Element ucMsgSig7 = PostCreator.createAlphaSignatureWithDL(message7, alpha7, dsaPrivKey);
		alpha7.add(new Attribute("signature", ucMsgSig7.convertToBigInteger().toString(10)));
		alpha7.add(new Attribute("publickey", electionCoordinatorPublicKey.toString(10)));

		Attributes beta7 = new Attributes();
		String dateTime7 = dateFormat.format(new Date());
		beta2.add(new Attribute("timestamp", dateTime2, DataType.DATE));
		beta2.add(new Attribute("rank", "6", DataType.INTEGER));
		Element acMsgSig7 = PostCreator.createBetaSignature(message1, alpha7, beta7, dsaPrivKey);
		beta7.add(new Attribute("boardSignature", acMsgSig7.convertToBigInteger().toString(10)));

		String post7 = PostCreator.createMessage(message7, alpha7, beta7);
		System.out.println(post7);

	}

}
