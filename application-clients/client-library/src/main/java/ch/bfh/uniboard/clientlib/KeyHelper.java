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
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.DSAPrivateKeySpec;
import java.security.spec.DSAPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 * Helper class allowing to transform BigInteger keys into PublicKey and PrivateKey objects
 *
 * @author Philémon von Bergen
 */
public class KeyHelper {

	public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
	private static final String PRIVATE_KEY_PREFIX = "=====BEGIN_UNICERT_PRIVATE_KEY=====";
	private static final String PRIVATE_KEY_POSTFIX = "=====END_UNICERT_PRIVATE_KEY=====";
	private static final String ENC_PRIVATE_KEY_PREFIX = "-----BEGIN ENCRYPTED UNICERT KEY-----";
	private static final String ENC_PRIVATE_KEY_POSTFIX = "-----END ENCRYPTED UNICERT KEY-----";
	private static final int KEY_SIZE = 128;
	private static final int ITERATIONS = 1000;

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

	/**
	 * Encrypts (using AES) the given private key with a key derived from the password.
	 *
	 * @param password password used to derive key
	 * @param privateKey Key to encrypt
	 * @return encrypted key with pre- and postfix
	 */
	public static String encryptPrivateKey(String password, byte[] privateKey) {

		String b16Message = DatatypeConverter.printHexBinary(privateKey);

		byte[] salt, iv, enc;
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			salt = sr.generateSeed(16);

			String toEncrypt = PRIVATE_KEY_PREFIX + b16Message + PRIVATE_KEY_POSTFIX;

			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			SecretKey sk = skf.generateSecret(spec);
			SecretKey aesSk = new SecretKeySpec(sk.getEncoded(), "AES");

			iv = sr.generateSeed(16);

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, aesSk, ivspec);

			enc = cipher.doFinal(toEncrypt.getBytes("UTF-8"));
		} catch (Exception e) {
			throw new RuntimeException("Error occured during encryption: " + e.getMessage());
		}
		return ENC_PRIVATE_KEY_PREFIX + DatatypeConverter.printBase64Binary(salt)
				+ DatatypeConverter.printBase64Binary(iv) + DatatypeConverter.printBase64Binary(enc)
				+ ENC_PRIVATE_KEY_POSTFIX;
	}

	/**
	 * Decrpyt the encrypted private key with a key derived from the password.
	 *
	 * @param password password to use to derive key
	 * @param cipherText encrypted private key with pre- and postfix
	 * @return the byte array representing the private key
	 */
	public static byte[] decryptPrivateKey(String password, String cipherText) {

		//Remove prefix and postfix if exists and all special chars
		String key;
		key = cipherText.replaceAll("[-]*", "");
		key = key.replace(ENC_PRIVATE_KEY_PREFIX.replaceAll("[-]*", ""), "");
		key = key.replace(ENC_PRIVATE_KEY_POSTFIX.replaceAll("[-]*", ""), "");
		key = key.replaceAll("[^\\w=\\+/\\-]", "");

		byte[] salt = DatatypeConverter.parseBase64Binary(key.substring(0, 24));
		byte[] iv = DatatypeConverter.parseBase64Binary(key.substring(24, 48));
		byte[] enc = DatatypeConverter.parseBase64Binary(key.substring(48));

		String dec = "";
		try {
			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_SIZE);
			SecretKeyFactory skf = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
			SecretKey sk = skf.generateSecret(spec);
			SecretKey sk2 = new SecretKeySpec(sk.getEncoded(), "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			IvParameterSpec ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE, sk2, ivspec);

			dec = new String(cipher.doFinal(enc), Charset.forName("UTF-8"));
		} catch (BadPaddingException e) {
			throw new RuntimeException("Wrong password");
		} catch (Exception e) {
			throw new RuntimeException("Invalid key");
		}

		if (!dec.contains(PRIVATE_KEY_PREFIX) || !dec.contains(PRIVATE_KEY_POSTFIX)) {
			throw new RuntimeException("Wrong password");
		}

		dec = dec.replace(PRIVATE_KEY_PREFIX, "");
		dec = dec.replace(PRIVATE_KEY_POSTFIX, "");
		dec = dec.replace("\n", "");

		return DatatypeConverter.parseHexBinary(dec);
	}
}
