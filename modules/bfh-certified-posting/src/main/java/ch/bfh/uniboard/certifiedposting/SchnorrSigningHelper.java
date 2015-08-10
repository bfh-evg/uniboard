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
package ch.bfh.uniboard.certifiedposting;

import static ch.bfh.uniboard.certifiedposting.CertifiedPostingService.CONVERT_METHOD;
import static ch.bfh.uniboard.certifiedposting.CertifiedPostingService.HASH_METHOD;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import java.math.BigInteger;
import java.security.interfaces.DSAPrivateKey;

public class SchnorrSigningHelper implements SigningHelper {

	private final BigInteger modulus;
	private final BigInteger orderFactor;
	private final BigInteger generator;
	private final BigInteger privateKey;

	public SchnorrSigningHelper(DSAPrivateKey dsaPrivKey) {
		privateKey = dsaPrivKey.getX();
		modulus = dsaPrivKey.getParams().getP();
		orderFactor = dsaPrivKey.getParams().getQ();
		generator = dsaPrivKey.getParams().getG();
	}

	@Override
	public Element sign(Element message) {
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		GStarModElement g = g_q.getElement(generator);
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(message.getSet(), g, CONVERT_METHOD,
				HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(privateKey);
		return schnorr.sign(privateKeyElement, message);
	}

}
