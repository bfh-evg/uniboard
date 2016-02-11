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
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class MainGenericPostGenerator {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {

		String keyStorePath = "/home/phil/UniVote.jks";
		String keyStorePass = "123456";
		String boardAlias = "uniboardvote";
		String boardPKPass = "123456";
		String signerPKPass = "123456";
		String signerAlias = "ec-demo"; // "ea-demo", "ec-demo" -- ea-demo has no private key!
		String section = "test-2015";
		String group = "accessRight";
		int rank = 13;
		String message = "{\"group\":\"accessRight\",\"crypto\":{\"type\":\"DL\", \"p\":\""
				+ "89884656743115795386465259539451236680898848947115328636715040578866337902750481566354238661203768010560056939935696678829394884407208311246423715319737062188883946712432742638151109800623047059726541476042502884419075341171231440736956555270413618581675255342293149119973622969239858152417678164812113740223" + "\",\"q\":\"" + "44942328371557897693232629769725618340449424473557664318357520289433168951375240783177119330601884005280028469967848339414697442203604155623211857659868531094441973356216371319075554900311523529863270738021251442209537670585615720368478277635206809290837627671146574559986811484619929076208839082406056870111"
				+ "\",\"g\":\"" + "107109962631870964694631290572616741684259433534913193717696669627034744183712064532843948178840692685135901742106546031184882792684386296417476646866306748317314750581351545212887046296410227653636832554555991359342552427316273176036531855263497569544312481810013296540896767718156533429912241745106756662354"
				+ "\",\"publickey\":\""
				+ "" + "\"}}";

		KeyStore caKs = KeyStore.getInstance(System.getProperty("javax.net.ssl.keyStoreType", "jks"));

		InputStream in;
		File file = new File(keyStorePath);
		in = new FileInputStream(file);

		caKs.load(in, keyStorePass.toCharArray());

		// Load uniboard key and cert
		Key boardKey = caKs.getKey(boardAlias, boardPKPass.toCharArray());
		DSAPrivateKey boardPrivKey = (DSAPrivateKey) boardKey;

		//Load keypair from ec
		Key signerKey = caKs.getKey(signerAlias, signerPKPass.toCharArray());
		DSAPrivateKey signerPrivKey = (DSAPrivateKey) signerKey;

		Certificate signerCert = caKs.getCertificate(signerAlias);
		DSAPublicKey signerPubKey = (DSAPublicKey) signerCert.getPublicKey();
		BigInteger signerPublicKey = signerPubKey.getY();

		//Create correct json message
		byte[] message1 = message.getBytes(Charset.forName("UTF-8"));

		//Create alphas and betas
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		Element msgSig = null;
		try {
			msgSig = PostCreator.createAlphaSignatureWithDL(message1, alpha, signerPrivKey);
			alpha.add(new Attribute("signature", msgSig.convertToBigInteger().toString(10)));
			alpha.add(new Attribute("publickey", signerPublicKey.toString(10)));
		} catch (Exception e) {
			//dummy signature if exception (for example no private key)
			System.err.println("Exception occured while signing: " + e.getClass() + " - " + e.getMessage());
			alpha.add(new Attribute("signature", ""));
			alpha.add(new Attribute("publickey", signerPublicKey.toString(10)));
		}

		Attributes beta = new Attributes();
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		dateFormat.setTimeZone(timeZone);
		String dateTime = dateFormat.format(new Date());
		beta.add(new Attribute("timestamp", dateTime, DataType.DATE));
		beta.add(new Attribute("rank", Integer.toString(rank, 10), DataType.INTEGER));
		Element initMsgBetaSig = PostCreator.createBetaSignature(message1, alpha, beta, boardPrivKey);
		beta.add(new Attribute("boardSignature", initMsgBetaSig.convertToBigInteger().toString(10)));

		//output post as json
		String post = PostCreator.createMessage(message1, alpha, beta);

		System.out.println(post);

	}

}
