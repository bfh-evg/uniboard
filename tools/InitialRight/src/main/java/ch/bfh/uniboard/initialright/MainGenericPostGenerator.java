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
		int rank = 6;
		String message = "{\n" +
"     \"group\": \"ballot\",\n" +
"     \"crypto\": {\n" +
"       \"type\": \"DL\",\n" +
"       \"p\": \"161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297\",\n" +
"       \"q\": \"65133683824381501983523684796057614145070427752690897588060462960319251776021\",\n" +
"       \"g\": \"109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709\",\n" +
"       \"publickey\": \"15641892999515409051645892665989999498899901311359621052218874972698884961931166421401266507166830362119419577615302761258093815117755455652515953200726224831618214161273312096569779805633031759531944502972351706600581005250227805850883923121018387737912514331178347493711146452529152367343876886067057669034\"\n" +
"    }} ";

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
		alpha.add("section", new StringValue(section));
		alpha.add("group", new StringValue(group));
		Element msgSig = null;
		try{
		    msgSig = PostCreator.createAlphaSignatureWithDL(message1, alpha, signerPrivKey);
		    alpha.add("signature", new StringValue(msgSig.getBigInteger().toString(10)));
		    alpha.add("publickey", new StringValue(signerPublicKey.toString(10)));
		} catch (Exception e){
		    //dummy signature if exception (for example no private key)
		    System.err.println("Exception occured while signing: "+e.getClass()+" - "+ e.getMessage());
		    alpha.add("signature", new StringValue(""));
		    alpha.add("publickey", new StringValue(signerPublicKey.toString(10)));
		}
		

		Attributes beta = new Attributes();
		beta.add("timestamp", new DateValue(new Date()));
		beta.add("rank", new IntegerValue(rank));
		Element initMsgBetaSig = PostCreator.createBetaSignature(message1, alpha, beta, boardPrivKey);
		beta.add("boardSignature", new StringValue(initMsgBetaSig.getBigInteger().toString(10)));

		//output post as json
		String post = PostCreator.createMessage(message1, alpha, beta);

		System.out.println(post);

		
	}

}
