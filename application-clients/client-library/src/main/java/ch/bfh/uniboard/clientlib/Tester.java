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
package ch.bfh.uniboard.clientlib;

import ch.bfh.uniboard.data.AlphaIdentifierDTO;
import ch.bfh.uniboard.data.ConstraintDTO;
import ch.bfh.uniboard.data.EqualDTO;
import ch.bfh.uniboard.data.OrderDTO;
import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.StringValueDTO;
import ch.bfh.unicrypt.helper.MathUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tester for library
 *
 * @author Philémon von Bergen
 */
class Tester {

    private static final String uniBoardUrl = "http://urd.bfh.ch:10080/UniBoardService/UniBoardServiceImpl";
    private static final String uniBoardWSDLurl = "http://urd.bfh.ch:10080/UniBoardService/UniBoardServiceImpl?wsdl";
    private static final String section = "unicert";

    //keystore is available in SVN
    private static final String keystorePath = "/home/phil/UniVote.jks";
    private static final String keystorePass = "123456";
    private static final String privKeyPass = "123456";

    private static final String message
	    = "{\"encryptedVote\": {\"firstvalue\": \"1\",\"secondvalue\": \"2\"},\"proof\": {\"commitment\": \"3\",\"response\": \"4\"}}";

    /*
     * This program expects an access right as follows to be able to post:
     * {
	    "group": "ballot",
	    "crypto": {
		"type": "RSA",
		"publickey": "892967880362342323465014090509126578217712653234330579952343248557066560974032988448007650380498660569429733194811788498443166884053444837128331407716 [...]"
	    }
	}
     */
    public static void main(String[] args) throws Exception {

	/**************************************************************************************************************
	 *  POST
	 *************************************************************************************************************/
	KeyStore ks = loadKeyStore(keystorePath, keystorePass);

	PrivateKey privKey = (RSAPrivateCrtKey) ks.getKey("test", privKeyPass.toCharArray());
	RSAPublicKey pubKey = (RSAPublicKey)ks.getCertificate("test").getPublicKey();
	
	String posterPublicKey = MathUtil.pair(pubKey.getPublicExponent(), pubKey.getModulus()).toString(10);
		
	PostHelper ph = new PostHelper(pubKey, privKey, ks.getCertificate(
		"uniboardvote").getPublicKey(), uniBoardWSDLurl, uniBoardUrl);

	try {
	    ph.post(message, "test-2015", "ballot");
	    System.out.println("Post successful");
	} catch (PostException | BoardErrorException e) {
	    System.out.println("Error during posting: " + e.getMessage());
	}
	
	/**************************************************************************************************************
	 *  GET
	 *************************************************************************************************************/
	
	GetHelper gh = new GetHelper(ks.getCertificate(
		"uniboardvote").getPublicKey(), uniBoardWSDLurl, uniBoardUrl);
	
	List<ConstraintDTO> contraints = new ArrayList<>();
	contraints.add(new EqualDTO(new AlphaIdentifierDTO(Collections.singletonList(UniBoardAttributesName.GROUP
		.getName())), new StringValueDTO("ballot")));
	contraints.add(new EqualDTO(new AlphaIdentifierDTO(Collections.singletonList(UniBoardAttributesName.PUBLIC_KEY
		.getName())), new StringValueDTO(posterPublicKey)));
	
	List<OrderDTO> orders = new ArrayList<>();
	orders.add(new OrderDTO(new AlphaIdentifierDTO(Collections.singletonList("group")), true));
	
	try {
	    QueryDTO q = new QueryDTO(contraints, orders, 0);
	    ResultContainerDTO rc =  gh.get(q);
	    System.out.println("Query returned "+rc.getResult().getPost().size()+" posts");
	    
	    //VERIFICATION OF POSTER SIGNATURES
	    for(PostDTO p: rc.getResult().getPost()){
		if(!gh.verifyPosterSignature(p, pubKey)){
		    System.out.println("Poster signature for post "+ p+" is invalid");
		}
	    }
	    System.out.println("Poster signatures verified");
	} catch (GetException e) {
	    System.out.println("Error during get: " + e.getMessage());
	}
	
	
    }

    private static KeyStore loadKeyStore(String keyStorePath, String keyStorePass) throws KeyStoreException,
	    FileNotFoundException,
	    IOException, NoSuchAlgorithmException, CertificateException {
	//Load keystore with private key for the manager
	KeyStore ks = KeyStore.getInstance(System.getProperty("javax.net.ssl.keyStoreType", "jks"));

	File file = new File(keyStorePath);
	InputStream in = new FileInputStream(file);

	ks.load(in, keyStorePass.toCharArray());

	return ks;
    }
}
