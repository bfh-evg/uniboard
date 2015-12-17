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

import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import java.math.BigInteger;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Helper class generating and verifying Schnorr signature of UniCrypt elements
 *
 * @author Philémon von Bergen
 */
public class SchnorrSignatureHelper extends SignatureHelper {

	private final BigInteger modulus;
	private final BigInteger orderFactor;
	private final BigInteger generator;
	private BigInteger privateKey = null;
	private BigInteger publicKey = null;
	private static final Logger logger = Logger.getLogger(SchnorrSignatureHelper.class.getName());

	/**
	 * Create a SignatureHelper for generating Schnorr signatures
	 *
	 * @param dsaPrivKey private key used to sign
	 */
	public SchnorrSignatureHelper(DSAPrivateKey dsaPrivKey) {
		privateKey = dsaPrivKey.getX();
		modulus = dsaPrivKey.getParams().getP();
		orderFactor = dsaPrivKey.getParams().getQ();
		generator = dsaPrivKey.getParams().getG();
	}

	/**
	 * Create a SignatureHelper for verifying Schnorr signatures
	 *
	 * @param dsaPublicKey public key used to verify
	 */
	public SchnorrSignatureHelper(DSAPublicKey dsaPublicKey) {
		logger.log(Level.FINE, "New SchnorrSignatureHelper");
		modulus = dsaPublicKey.getParams().getP();
		logger.log(Level.FINE, "Modulus {0}", modulus);
		orderFactor = dsaPublicKey.getParams().getQ();
		logger.log(Level.FINE, "Order {0}", orderFactor);
		generator = dsaPublicKey.getParams().getG();
		logger.log(Level.FINE, "Generator {0}", generator);
		publicKey = dsaPublicKey.getY();
		logger.log(Level.FINE, "PublicKey {0}", publicKey);
	}

	@Override
	protected BigInteger sign(Element element) throws SignatureException {
		if (privateKey == null) {
			throw new SignatureException("No private key provided in constructor");
		}
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		GStarModElement g = g_q.getElement(generator);
		SchnorrSignatureScheme<?> schnorr = SchnorrSignatureScheme.getInstance(element.getSet(), g,
				CONVERT_METHOD, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(privateKey);
		Pair signature = schnorr.sign(privateKeyElement, element);
		return MathUtil.pair(signature.getFirst().convertToBigInteger(), signature.getSecond().convertToBigInteger());
	}

	@Override
	protected boolean verify(Element element, BigInteger signatureBI) throws SignatureException {
		if (publicKey == null) {
			throw new SignatureException("No public key indicated in constructor");
		}
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		GStarModElement g = g_q.getElement(generator);
		SchnorrSignatureScheme<?> schnorr = SchnorrSignatureScheme.getInstance(element.getSet(), g,
				CONVERT_METHOD, HASH_METHOD);
		BigInteger[] schnorrSignature = MathUtil.unpair(signatureBI);
		logger.log(Level.FINE, "Signature Value 1: {0}", schnorrSignature[0]);
		logger.log(Level.FINE, "Signature Value 2: {0}", schnorrSignature[1]);
		Tuple signature = schnorr.getSignatureSpace().getElementFrom(schnorrSignature[0], schnorrSignature[1]);
		if (signature == null) {
			return false;
		}

		Element publicKeyElement = schnorr.getVerificationKeySpace().getElement(publicKey);

		return schnorr.verify(publicKeyElement, element, signature).getValue();

	}

}
