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
		int rank = 13;
		String message = "{\n" +
"     \"group\": \"ballot\",\n" +
"     \"crypto\": {\n" +
"       \"type\": \"RSA\",\n" +
"       \"publickey\": \"8929678803623423234650140905091265782177126532343305799523432485570665609740329884480076503804986605694297331948117884984431668840534448371283314077165594253446986565036143090547558028970935871720449070776818246892672777057924429656563470724840116644921835408266013821963202977626142229339560522785458683198638821534098632412515810513567824004764914253850511348944964763235393741752795501427087917363229814795984531547233609108160018447001183051487905789717703926417994853605887955871482073516634531717539286611812230867280177947596336935844553438612714195242300295662878002444311601284284480826501904795473157468538\"\n" +
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
