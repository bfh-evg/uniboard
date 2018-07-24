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
package ch.bfh.uniboard.overseer;

import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;

public class RSASigningHelper implements SigningHelper {

	private final BigInteger privateKey;
	private final BigInteger modulus;
	private final String publicKey;

	public RSASigningHelper(RSAPrivateCrtKey rsaPrivKey, RSAPublicKey publicKey) {
		privateKey = rsaPrivKey.getPrivateExponent();
		modulus = rsaPrivKey.getModulus();
		this.publicKey = MathUtil.pair(publicKey.getPublicExponent(), publicKey.getModulus()).toString(10);
	}

	@Override
	public BigInteger sign(Element message) {
		RSASignatureScheme rsaScheme
				= RSASignatureScheme.getInstance(message.getSet(), ZMod.getInstance(modulus), CONVERT_METHOD,
						HASH_METHOD);
		Element privateKeyElement = rsaScheme.getSignatureKeySpace().getElement(privateKey);
		return rsaScheme.sign(privateKeyElement, message).convertToBigInteger();
	}

	@Override
	public String getPublicKey() {
		return this.publicKey;
	}

}
