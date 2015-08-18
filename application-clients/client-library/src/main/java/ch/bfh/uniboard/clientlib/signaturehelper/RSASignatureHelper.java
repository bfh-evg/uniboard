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
package ch.bfh.uniboard.clientlib.signaturehelper;

import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

/**
 * Helper class generating and verifying RSA signature of UniCrypt elements
 *
 * @author Philémon von Bergen
 */
public class RSASignatureHelper extends SignatureHelper {

	private BigInteger privateKey = null;
	private BigInteger publicKey = null;
	private final BigInteger modulus;

	/**
	 * Create a SignatureHelper for generating RSA signatures
	 *
	 * @param rsaPrivKey private key used to sign
	 */
	public RSASignatureHelper(RSAPrivateCrtKey rsaPrivKey) {
		privateKey = rsaPrivKey.getPrivateExponent();
		modulus = rsaPrivKey.getModulus();
	}

	/**
	 * Create a SignatureHelper for generating RSA signatures
	 *
	 * @param rsaPrivKey private key used to sign
	 */
	public RSASignatureHelper(RSAPrivateKey rsaPrivKey) {
		privateKey = rsaPrivKey.getPrivateExponent();
		modulus = rsaPrivKey.getModulus();
	}

	public RSASignatureHelper(BigInteger modulus, BigInteger privateKey) {
		this.modulus = modulus;
		this.privateKey = privateKey;
	}

	/**
	 * Create a SignatureHelper for verifying RSA signatures
	 *
	 * @param rsaPublicKey public key used to verify
	 */
	public RSASignatureHelper(RSAPublicKey rsaPublicKey) {
		publicKey = rsaPublicKey.getPublicExponent();
		modulus = rsaPublicKey.getModulus();
	}

	@Override
	protected BigInteger sign(Element element) throws SignatureException {
		if (privateKey == null) {
			throw new SignatureException("No private key provided in constructor");
		}
		RSASignatureScheme rsaScheme = RSASignatureScheme.getInstance(element.getSet(), ZMod.getInstance(modulus),
				CONVERT_METHOD, HASH_METHOD);
		Element privateKeyElement = rsaScheme.getSignatureKeySpace().getElement(privateKey);
		return rsaScheme.sign(privateKeyElement, element).convertToBigInteger();
	}

	@Override
	protected boolean verify(Element element, BigInteger signatureBI) throws SignatureException {
		if (publicKey == null) {
			throw new SignatureException("No public key indicated in constructor");
		}
		RSASignatureScheme rsaScheme = RSASignatureScheme.getInstance(element.getSet(), ZMod.getInstance(modulus),
				CONVERT_METHOD, HASH_METHOD);;

		Element signature = rsaScheme.getSignatureSpace().getElement(signatureBI);
		Element publicKeyElement = rsaScheme.getVerificationKeySpace().getElement(publicKey);

		return rsaScheme.verify(publicKeyElement, element, signature).getValue();

	}

}
