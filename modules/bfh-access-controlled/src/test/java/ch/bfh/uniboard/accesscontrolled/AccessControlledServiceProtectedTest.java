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
package ch.bfh.uniboard.accesscontrolled;

import static ch.bfh.uniboard.accesscontrolled.AccessControlledService.HASH_METHOD;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.math.MathUtil;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrimePair;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import java.io.IOException;
import java.math.BigInteger;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Test the protected methods which don't require arquillian.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class AccessControlledServiceProtectedTest {

	public AccessControlledServiceProtectedTest() {
	}

	@Test
	public void testCreateMessageElement() {

		AccessControlledService service = new AccessControlledService();
		byte[] message = new byte[1];
		message[0] = 0x6;
		String string = "test";
		Attributes alpha = new Attributes();
		alpha.add("a1", string);
		Element result = service.createMessageElement(message, alpha);
		assertTrue(result instanceof Pair);
		Pair resultPair = (Pair) result;
		ByteArrayElement resultMessage = (ByteArrayElement) resultPair.getFirst();
		assertArrayEquals(resultMessage.getValue().getBytes(), message);

		assertTrue(resultPair.getSecond().isTuple());
		Tuple alphaResult = (Tuple) resultPair.getSecond();
		assertEquals(string, ((StringElement) alphaResult.getAt(0)).getValue());
	}

	@Test
	public void testCheckDLSignature() throws IOException {

		AccessControlledService service = new AccessControlledService();

		JsonNode key = JsonLoader.fromString("{\"type\":\"DL\",\"p\":\"1907\",\"q\":\"953\",\"g\":\"4\",\"publickey\":\"1662\"}");
		byte[] message = new byte[1];
		message[0] = 0x5;
		Attributes alpha = new Attributes();
		alpha.add("section", "bfh-test");
		alpha.add("group", "accessRight");

		BigInteger modulus = new BigInteger("1907");
		BigInteger orderFactor = new BigInteger("953");
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		BigInteger generator = new BigInteger("4");
		GStarModElement g = g_q.getElement(generator);

		Element messageElement = service.createMessageElement(message, alpha);

		SchnorrSignatureScheme<?> schnorr = SchnorrSignatureScheme.getInstance(
				messageElement.getSet(), g, AccessControlledService.CONVERT_METHOD, AccessControlledService.HASH_METHOD);

		Element privateKey = schnorr.getSignatureKeySpace().getElement(new BigInteger("78"));
		Pair signature = schnorr.sign(privateKey, messageElement, schnorr.getRandomizationSpace().getRandomElement());
		System.out.println(signature);
		String sigString = MathUtil.pair(signature.getFirst().convertToBigInteger(),
				signature.getSecond().convertToBigInteger()).toString(10);

		alpha.add("signature", sigString);

		assertTrue(service.checkDLSignature(key, message, alpha));

	}

	@Test
	public void testCheckRSASignature() throws IOException {

		AccessControlledService service = new AccessControlledService();

		JsonNode key = JsonLoader.fromString("{\"type\":\"RSA\",\"publickey\":\"10455042\"}");
		byte[] message = new byte[1];
		message[0] = 0x5;
		Attributes alpha = new Attributes();
		alpha.add("section", "bfh-test");
		alpha.add("group", "accessRight");

		Element messageElement = service.createMessageElement(message, alpha);

		BigInteger p = new BigInteger("61");
		BigInteger q = new BigInteger("53");

		RSASignatureScheme rsa = RSASignatureScheme.getInstance(messageElement.getSet(),
				ZModPrimePair.getInstance(p, q), AccessControlledService.CONVERT_METHOD, HASH_METHOD);

		Element prKey = rsa.getSignatureKeySpace().getElement(new BigInteger("17"));
		Element puKey = rsa.getVerificationKeySpace().getElement(new BigInteger("2753"));

		Element signature = rsa.sign(prKey, messageElement);

		System.out.println(MathUtil.pair(new BigInteger("2753"), p.multiply(q)).toString(10));

		String sigString = signature.convertToBigInteger().toString(10);
		alpha.add("signature", sigString);

		assertTrue(service.checkRSASignature(key, message, alpha));
	}

}
