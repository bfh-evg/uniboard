/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.clientlib.signaturehelper;

import static ch.bfh.uniboard.clientlib.signaturehelper.SignatureHelper.CONVERT_METHOD;
import static ch.bfh.uniboard.clientlib.signaturehelper.SignatureHelper.HASH_METHOD;
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.StringValueDTO;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.RSASignatureScheme;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.ZModPrimePair;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class RSASignatureHelperTest {

	public RSASignatureHelperTest() {
	}

	@Test
	public void testSign() throws Exception {
		RSASignatureHelper rsaHelper = new RSASignatureHelper(new BigInteger("3233"), new BigInteger("17"));
		byte[] message = new byte[1];
		message[0] = 0x5;
		AttributesDTO alpha = new AttributesDTO();
		alpha.getAttribute().add(new AttributesDTO.AttributeDTO("section", new StringValueDTO("bfh-test")));
		alpha.getAttribute().add(new AttributesDTO.AttributeDTO("group", new StringValueDTO("accessRight")));

		Element messageElement = rsaHelper.prepareElement(message, alpha);
		BigInteger signature = rsaHelper.sign(message, alpha);

		BigInteger p = new BigInteger("61");
		BigInteger q = new BigInteger("53");

		RSASignatureScheme rsa = RSASignatureScheme.getInstance(messageElement.getSet(),
				ZModPrimePair.getInstance(p, q), CONVERT_METHOD, HASH_METHOD);

		Element prKey = rsa.getSignatureKeySpace().getElement(new BigInteger("17"));
		Element puKey = rsa.getVerificationKeySpace().getElement(new BigInteger("2753"));
		Element sigElement = rsa.getSignatureSpace().getElementFrom(signature);

		assertTrue(rsa.verify(puKey, messageElement, sigElement).getValue());
	}

	@Test
	public void testVerify() throws Exception {
	}

}
