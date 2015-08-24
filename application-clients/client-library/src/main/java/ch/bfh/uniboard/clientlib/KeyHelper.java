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

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Helper class allowing to transform BigInteger keys into PublicKey and PrivateKey objects
 *
 * @author Philémon von Bergen
 */
public class KeyHelper {

	/**
	 * Transform a BigInteger DSA private key into a DSAPrivateKey object
	 *
	 * @param p value of prime p
	 * @param q value of prime q
	 * @param g value of generator g
	 * @param x value of private key x
	 * @return the corresponding DSAPrivateKey object, null if an error occured
	 * @throws InvalidKeySpecException if values do not represent a valid private key
	 * @throws java.security.NoSuchAlgorithmException if the DSA algorithm is not supported
	 */
	public static DSAPrivateKey createDSAPrivateKey(BigInteger p, BigInteger q, BigInteger g, BigInteger x) throws
			InvalidKeySpecException, NoSuchAlgorithmException {

		DSAPrivateKeySpec keySpec = new DSAPrivateKeySpec(x, p, q, g);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");

		return (DSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}

	/**
	 * Transform a BigInteger DSA public key into a DSAPublicKey object
	 *
	 * @param p value of prime p
	 * @param q value of prime q
	 * @param g value of generator g
	 * @param y value of public key y
	 * @return the corresponding DSAPublicKey object, null if an error occured
	 * @throws InvalidKeySpecException if values do not represent a valid public key
	 * @throws java.security.NoSuchAlgorithmException if the DSA algorithm is not supported
	 */
	public static DSAPublicKey createDSAPublicKey(BigInteger p, BigInteger q, BigInteger g, BigInteger y) throws
			InvalidKeySpecException, NoSuchAlgorithmException {

		DSAPublicKeySpec keySpec = new DSAPublicKeySpec(y, p, q, g);
		KeyFactory keyFactory = KeyFactory.getInstance("DSA");

		return (DSAPublicKey) keyFactory.generatePublic(keySpec);
	}

	/**
	 * Transform a BigInteger RSA private key into a RSAPrivateKey object
	 *
	 * @param modulus value of RSA modulus
	 * @param d value of private key
	 * @return the corresponding RSAPrivateKey object, null if an error occured
	 * @throws InvalidKeySpecException if values do not represent a valid private key
	 * @throws java.security.NoSuchAlgorithmException if the RSA algorithm is not supported
	 */
	public static RSAPrivateKey createRSAPrivateKey(BigInteger modulus, BigInteger d) throws
			InvalidKeySpecException, NoSuchAlgorithmException {

		RSAPrivateKeySpec spec = new RSAPrivateKeySpec(modulus, d);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return (RSAPrivateKey) keyFactory.generatePrivate(spec);
	}

	/**
	 * Transform a BigInteger RSA public key into a RSAPublicKey object
	 *
	 * @param modulus value of RSA modulus
	 * @param e value of public key
	 * @return the corresponding RSAPublicKey object, null if an error occured
	 * @throws InvalidKeySpecException if values do not represent a valid public key
	 * @throws java.security.NoSuchAlgorithmException if the RSA algorithm is not supported
	 */
	public static RSAPublicKey createRSAPublicKey(BigInteger modulus, BigInteger e) throws
			InvalidKeySpecException, NoSuchAlgorithmException {

		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, e);
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");

		return (RSAPublicKey) keyFactory.generatePublic(spec);
	}
}
