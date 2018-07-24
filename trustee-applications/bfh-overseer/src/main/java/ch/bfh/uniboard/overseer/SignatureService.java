/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.overseer;

import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class SignatureService {

	private static final String CONFIG_NAME = "bfh-signature-service";
	private static final String CONFIG_KEYSTORE_PATH = "keystore-path";
	private static final String CONFIG_KEYSTORE_PASS = "keystore-pass";
	private static final String CONFIG_ID = "id";
	private static final String CONFIG_PRIVATEKEY_PASS = "privatekey-pass";

	private static final Logger logger = Logger.getLogger(SignatureService.class.getName());

	private SigningHelper signer = null;

	@EJB
	ConfigurationManager configurationManager;

	public BigInteger sign(Element message) {
		return signer.sign(message);
	}

	public String getPublicKey() {
		return signer.getPublicKey();
	}

	@PostConstruct
	private void init() {

		Configuration configuration = configurationManager.getConfiguration(CONFIG_NAME);
		if (configuration == null) {
			logger.log(Level.SEVERE, "Could not load configuration: " + CONFIG_NAME);
			return;
		}

		String keyStorePath = configuration.getEntries().get(CONFIG_KEYSTORE_PATH);
		String keyStorePass = configuration.getEntries().get(CONFIG_KEYSTORE_PASS);
		String privateKeyPass = configuration.getEntries().get(CONFIG_PRIVATEKEY_PASS);
		String id = configuration.getEntries().get(CONFIG_ID);

		KeyStore caKs;

		try {
			caKs = KeyStore.getInstance(System.getProperty("javax.net.ssl.keyStoreType", "jks"));
		} catch (KeyStoreException ex) {
			logger.log(Level.SEVERE, "Could not create keystore type.");
			logger.log(Level.SEVERE, ex.getMessage());
			return;
		}
		InputStream in;
		try {
			File file = new File(keyStorePath);
			in = new FileInputStream(file);
		} catch (FileNotFoundException | RuntimeException ex) {
			logger.log(Level.SEVERE, "Could not load keystore: {0}", keyStorePath);
			return;
		}
		try {
			caKs.load(in, keyStorePass.toCharArray());
		} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
			return;
		}

		// load the key entry from the keystore
		Key key;
		try {
			key = caKs.getKey(id, privateKeyPass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
			return;
		}

		if (key == null) {
			return;
		}
		String algo = key.getAlgorithm();
		if (algo.equals("RSA")) {
			try {
				RSAPrivateCrtKey rsaPrivKey = (RSAPrivateCrtKey) key;
				RSAPublicKey publicKey = (RSAPublicKey) caKs.getCertificate(id).getPublicKey();
				this.signer = new RSASigningHelper(rsaPrivKey, publicKey);

			} catch (KeyStoreException | RuntimeException ex) {
				logger.log(Level.SEVERE, ex.getMessage());
			}
		} else if (algo.equals("DSA")) {
			try {
				DSAPrivateKey dsaPrivKey = (DSAPrivateKey) key;
				DSAPublicKey publicKey = (DSAPublicKey) caKs.getCertificate(id).getPublicKey();
				this.signer = new SchnorrSigningHelper(dsaPrivKey, publicKey);
			} catch (KeyStoreException | RuntimeException ex) {
				logger.log(Level.SEVERE, ex.getMessage());
			}
		}
	}
}
