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
package ch.bfh.uniboard.certifiedget;

import static ch.bfh.uniboard.certifiedget.CertifiedGetService.HASH_METHOD;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;
import java.security.interfaces.RSAPrivateCrtKey;

public class RSASigningHelper implements SigningHelper {

	private final BigInteger privateKey;
	private final BigInteger modulus;

	public RSASigningHelper(RSAPrivateCrtKey rsaPrivKey) {
		privateKey = rsaPrivKey.getPrivateExponent();
		modulus = rsaPrivKey.getModulus();
	}

	@Override
	public Element sign(Element message) {
		RSASignatureScheme rsaScheme
				= RSASignatureScheme.getInstance(message.getSet(), ZMod.getInstance(modulus), HASH_METHOD);
		Element privateKeyElement = rsaScheme.getSignatureKeySpace().getElement(privateKey);
		return rsaScheme.sign(privateKeyElement, message);
	}

}
