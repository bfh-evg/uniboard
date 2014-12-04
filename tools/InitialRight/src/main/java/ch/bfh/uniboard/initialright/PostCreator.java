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
package ch.bfh.uniboard.initialright;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.Alphabet;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.helper.converter.classes.ConvertMethod;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.BigIntegerToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.ByteArrayToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.StringToByteArray;
import ch.bfh.unicrypt.helper.hash.HashAlgorithm;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.Z;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZMod;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class PostCreator {

	public static Element createAlphaSignatureWithDL(byte[] message, Attributes alpha, DSAPrivateKey dsaPrivKey) {

		Element toSign = PostCreator.createELementMessageAlpha(message, alpha);

		GStarModPrime g_q = GStarModPrime.getInstance(dsaPrivKey.getParams().getP(), dsaPrivKey.getParams().getQ());
		GStarModElement g = g_q.getElement(dsaPrivKey.getParams().getG());
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(toSign.getSet(), g, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(dsaPrivKey.getX());
		return schnorr.sign(privateKeyElement, toSign);
	}

	public static Element createAlphaSignatureWithRSA(byte[] message, Attributes alpha, RSAPrivateCrtKey rsaPrivKey) {

		Element toSign = PostCreator.createELementMessageAlpha(message, alpha);

		RSASignatureScheme rsaScheme
				= RSASignatureScheme.getInstance(toSign.getSet(), ZMod.getInstance(rsaPrivKey.getModulus()), HASH_METHOD);
		Element privateKeyElement = rsaScheme.getSignatureKeySpace().getElement(rsaPrivKey.getPrivateExponent());
		return rsaScheme.sign(privateKeyElement, toSign);
	}

	public static Element createELementMessageAlpha(byte[] message, Attributes alpha) {
		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		Z z = Z.getInstance();

		Element messageElement = byteSpace.getElement(message);
		
		List<Element> alphaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : alpha.getEntries()) {
			Element tmp;
			if (e.getValue() instanceof ByteArrayValue) {
				tmp = byteSpace.getElement(((ByteArrayValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof DateValue) {
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(((DateValue) e.getValue()).getValue());
				tmp = stringSpace.getElement(stringDate);
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof IntegerValue) {
				tmp = z.getElement(((IntegerValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof StringValue) {
				tmp = stringSpace.getElement(((StringValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			}

		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);
				
		return Pair.getInstance(messageElement, alphaElement);
	}

	public static Element createBetaSignature(byte[] message,
			Attributes alpha, Attributes beta, DSAPrivateKey dsaPrivKey) {

		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		Z z = Z.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : alpha.getEntries()) {
			Element tmp;
			if (e.getValue() instanceof ByteArrayValue) {
				tmp = byteSpace.getElement(((ByteArrayValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof DateValue) {
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(((DateValue) e.getValue()).getValue());
				tmp = stringSpace.getElement(stringDate);
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof IntegerValue) {
				tmp = z.getElement(((IntegerValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			} else if (e.getValue() instanceof StringValue) {
				tmp = stringSpace.getElement(((StringValue) e.getValue()).getValue());
				alphaElements.add(tmp);
			}

		}
		DenseArray immuElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(immuElements);

		List<Element> betaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : beta.getEntries()) {
			Element tmp;
			if (e.getValue() instanceof ByteArrayValue) {
				tmp = byteSpace.getElement(((ByteArrayValue) e.getValue()).getValue());
				betaElements.add(tmp);
			} else if (e.getValue() instanceof DateValue) {
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(((DateValue) e.getValue()).getValue());
				tmp = stringSpace.getElement(stringDate);
				betaElements.add(tmp);
			} else if (e.getValue() instanceof IntegerValue) {
				tmp = z.getElement(((IntegerValue) e.getValue()).getValue());
				betaElements.add(tmp);
			} else if (e.getValue() instanceof StringValue) {
				tmp = stringSpace.getElement(((StringValue) e.getValue()).getValue());
				betaElements.add(tmp);
			}

		}
		DenseArray immuElements2 = DenseArray.getInstance(betaElements);
		Element betaElement = Tuple.getInstance(immuElements2);
		Element toSign = Tuple.getInstance(messageElement, alphaElement, betaElement);
		
		GStarModPrime g_q = GStarModPrime.getInstance(dsaPrivKey.getParams().getP(), dsaPrivKey.getParams().getQ());
		GStarModElement g = g_q.getElement(dsaPrivKey.getParams().getG());
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(toSign.getSet(), g, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(dsaPrivKey.getX());
		return schnorr.sign(privateKeyElement, toSign);
	}

	public static String createMessage(byte[] message, Attributes alpha, Attributes beta) {
		String output = "";
		output += "{";
		output += "\"message\": \"";
		output += Base64.encode(message);
		output += "\",";
		output += "\"searchable-message\":";
		output += new String(message);
		output += ",";
		output += "\"alpha\": {";
		for (Map.Entry<String, Value> e : alpha.getEntries()) {
			if (e.getValue() instanceof ByteArrayValue) {
				ByteArrayValue tmpValue = (ByteArrayValue) e.getValue();
				System.out.println("WARNING NOT SUPPORTED YET");
			} else if (e.getValue() instanceof DateValue) {
				DateValue tmpValue = (DateValue) e.getValue();
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(tmpValue.getValue());
				output += "\"" + e.getKey() + "\": ISODate(\"" + stringDate + "\"),";
			} else if (e.getValue() instanceof IntegerValue) {
				IntegerValue tmpValue = (IntegerValue) e.getValue();
				output += "\"" + e.getKey() + "\": NumberInt(" + tmpValue.getValue() + "),";
			} else if (e.getValue() instanceof StringValue) {
				StringValue tmpValue = (StringValue) e.getValue();
				output += "\"" + e.getKey() + "\": \"" + tmpValue.getValue() + "\",";
			}
		}
		output = output.substring(0, output.length() - 1);
		output += "},";
		output += "\"beta\": {";
		for (Map.Entry<String, Value> e : beta.getEntries()) {
			if (e.getValue() instanceof ByteArrayValue) {
				ByteArrayValue tmpValue = (ByteArrayValue) e.getValue();
				System.out.println("WARNING NOT SUPPORTED YET");
			} else if (e.getValue() instanceof DateValue) {
				DateValue tmpValue = (DateValue) e.getValue();
				TimeZone timeZone = TimeZone.getTimeZone("UTC");
				DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
				dateFormat.setTimeZone(timeZone);
				String stringDate = dateFormat.format(tmpValue.getValue());
				output += "\"" + e.getKey() + "\": ISODate(\"" + stringDate + "\"),";
			} else if (e.getValue() instanceof IntegerValue) {
				IntegerValue tmpValue = (IntegerValue) e.getValue();
				output += "\"" + e.getKey() + "\": NumberInt(" + tmpValue.getValue() + "),";
			} else if (e.getValue() instanceof StringValue) {
				StringValue tmpValue = (StringValue) e.getValue();
				output += "\"" + e.getKey() + "\": \"" + tmpValue.getValue() + "\",";
			}
		}
		output = output.substring(0, output.length() - 1);
		output += "}}";
		return output;
	}

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(
			HashAlgorithm.SHA256,
			ConvertMethod.getInstance(
					BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
					ByteArrayToByteArray.getInstance(false),
					StringToByteArray.getInstance(Charset.forName("UTF-8"))),
			HashMethod.Mode.RECURSIVE);
}
