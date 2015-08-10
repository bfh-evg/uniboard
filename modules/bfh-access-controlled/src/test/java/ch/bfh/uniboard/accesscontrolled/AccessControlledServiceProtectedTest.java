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
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayElement;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringElement;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZElement;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
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
		int integer = 10;
		byte[] byteA = new byte[1];
		byteA[0] = 0x7;
		Date date = new Date();
		Attributes alpha = new Attributes();
		alpha.add("a1", new StringValue(string));
		alpha.add("a2", new IntegerValue(integer));
		alpha.add("a3", new ByteArrayValue(byteA));
		alpha.add("a4", new DateValue(date));
		Element result = service.createMessageElement(message, alpha);
		assertTrue(result instanceof Pair);
		Pair resultPair = (Pair) result;
		ByteArrayElement resultMessage = (ByteArrayElement) resultPair.getFirst();
		assertArrayEquals(resultMessage.getValue().getBytes(), message);

		assertTrue(resultPair.getSecond().isTuple());
		Tuple alphaResult = (Tuple) resultPair.getSecond();
		assertEquals(string, ((StringElement) alphaResult.getAt(0)).getValue());
		assertEquals(integer, ((ZElement) alphaResult.getAt(1)).getValue().intValue());
		assertArrayEquals(byteA, ((ByteArrayElement) alphaResult.getAt(2)).getValue().getBytes());
		TimeZone timeZone = TimeZone.getTimeZone("UTC");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		dateFormat.setTimeZone(timeZone);
		String expectedDateString = dateFormat.format(date);
		assertEquals(expectedDateString, ((StringElement) alphaResult.getAt(3)).getValue());

	}

	@Test
	public void testCheckDLSignature() throws IOException {

		AccessControlledService service = new AccessControlledService();

		JsonNode key = JsonLoader.fromString("{\"type\":\"DL\",\"p\":\"1907\",\"q\":\"953\",\"g\":\"4\",\"publickey\":\"1662\"}");
		byte[] message = new byte[1];
		message[0] = 0x5;
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("bfh-test"));
		alpha.add("group", new StringValue("accessRight"));

		BigInteger modulus = new BigInteger("1907");
		BigInteger orderFactor = new BigInteger("953");
		GStarModPrime g_q = GStarModPrime.getInstance(modulus, orderFactor);
		BigInteger generator = new BigInteger("4");
		GStarModElement g = g_q.getElement(generator);

		Element messageElement = service.createMessageElement(message, alpha);

		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(
				messageElement.getSet(), g, AccessControlledService.CONVERT_METHOD, AccessControlledService.HASH_METHOD);

		Element privateKey = schnorr.getSignatureKeySpace().getElement(new BigInteger("78"));
		Element signature = schnorr.sign(privateKey, messageElement, schnorr.getRandomizationSpace().getRandomElement());

		String sigString = signature.convertToBigInteger().toString(10);
		alpha.add("signature", new StringValue(sigString));

		assertTrue(service.checkDLSignature(key, message, alpha));

	}

	@Test
	public void testCheckRSASignature() throws IOException {

		AccessControlledService service = new AccessControlledService();

		JsonNode key = JsonLoader.fromString("{\"type\":\"RSA\",\"publickey\":\"10455042\"}");
		byte[] message = new byte[1];
		message[0] = 0x5;
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("bfh-test"));
		alpha.add("group", new StringValue("accessRight"));

		Element messageElement = service.createMessageElement(message, alpha);

		BigInteger p = new BigInteger("61");
		BigInteger q = new BigInteger("53");

		RSASignatureScheme rsa = RSASignatureScheme.getInstance(messageElement.getSet(),
				ZModPrimePair.getInstance(p, q), AccessControlledService.CONVERT_METHOD, HASH_METHOD);

		Element prKey = rsa.getSignatureKeySpace().getElement(new BigInteger("17"));
		Element puKey = rsa.getVerificationKeySpace().getElement(new BigInteger("2753"));

		Element signature = rsa.sign(prKey, messageElement);

		String sigString = signature.convertToBigInteger().toString(10);
		alpha.add("signature", new StringValue(sigString));

		assertTrue(service.checkRSASignature(key, message, alpha));
	}

}
