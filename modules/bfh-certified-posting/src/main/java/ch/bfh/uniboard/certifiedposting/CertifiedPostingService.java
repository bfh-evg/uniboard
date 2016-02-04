/*
 * Uniboard
 *
 *  Copyright (c) 2015 Bern University of Applied Sciences (BFH),
 *  Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *  Quellgasse 21, CH-2501 Biel, Switzerland
 *
 *  Licensed under Dual License consisting of:
 *  1. GNU Affero General Public License (AGPL) v3
 *  and
 *  2. Commercial license
 *
 *
 *  1. This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU Affero General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Affero General Public License for more details.
 *
 *   You should have received a copy of the GNU Affero General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 *
 *  2. Licensees holding valid commercial licenses for UniVote2 may use this file in
 *   accordance with the commercial license agreement provided with the
 *   Software or, alternatively, in accordance with the terms contained in
 *   a written agreement between you and Bern University of Applied Sciences (BFH),
 *   Research Institute for Security in the Information Society (RISIS), E-Voting Group (EVG),
 *   Quellgasse 21, CH-2501 Biel, Switzerland.
 *
 *
 *   For further information contact <e-mail: severin.hauser@bfh.ch>
 *
 *
 * Redistributions of files must retain the above copyright notice.
 */
package ch.bfh.uniboard.certifiedposting;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.PostComponent;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.helper.converter.classes.ConvertMethod;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.BigIntegerToByteArray;
import ch.bfh.unicrypt.helper.converter.classes.bytearray.StringToByteArray;
import ch.bfh.unicrypt.helper.hash.HashAlgorithm;
import ch.bfh.unicrypt.helper.hash.HashMethod;
import ch.bfh.unicrypt.helper.math.Alphabet;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.Z;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class CertifiedPostingService extends PostComponent implements PostService {

	private static final String ATTRIBUTE_NAME = "boardSignature";
	private static final String CONFIG_NAME = "bfh-certified-posting";
	private static final String CONFIG_KEYSTORE_PATH = "keystore-path";
	private static final String CONFIG_KEYSTORE_PASS = "keystore-pass";
	private static final String CONFIG_ID = "id";
	private static final String CONFIG_PRIVATEKEY_PASS = "privatekey-pass";

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(HashAlgorithm.SHA256);
	protected static final ConvertMethod CONVERT_METHOD = ConvertMethod.getInstance(
			BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
			StringToByteArray.getInstance(Charset.forName("UTF-8")));

	private static final Logger logger = Logger.getLogger(CertifiedPostingService.class.getName());

	private SigningHelper signer = null;

	@EJB
	PostService postSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	protected PostService getPostSuccessor() {
		return this.postSuccessor;
	}

	@Override
	protected Attributes beforePost(byte[] message, Attributes alpha, Attributes beta) {
		if (this.signer == null) {
			logger.log(Level.SEVERE,
					"Signer is not set. Check the configuration.");
			beta.add(Attributes.ERROR,
					new StringValue("BCP-001 Internal server error."));
			return beta;
		}
		Element messageElement = this.createMessageElement(message, alpha, beta);
		String signatureString = this.signer.sign(messageElement).toString(10);
		beta.add(ATTRIBUTE_NAME, new StringValue(signatureString));
		return beta;
	}

	protected Element createMessageElement(byte[] message, Attributes alpha, Attributes beta) {
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : alpha.getEntries()) {
			Element element = this.createValueElement(e.getValue());
			if (element != null) {
				alphaElements.add(element);

			}
		}
		DenseArray alphaDenseElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(alphaDenseElements);

		List<Element> betaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : beta.getEntries()) {
			Element element = this.createValueElement(e.getValue());
			if (element != null) {
				betaElements.add(element);
			}
		}
		DenseArray beteDenseElements = DenseArray.getInstance(betaElements);
		Element betaElement = Tuple.getInstance(beteDenseElements);

		return Tuple.getInstance(messageElement, alphaElement, betaElement);
	}

	protected Element createValueElement(Value value) {
		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.UNICODE_BMP);
		Z z = Z.getInstance();
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		if (value instanceof ByteArrayValue) {
			return byteSpace.getElement(((ByteArrayValue) value).getValue());
		} else if (value instanceof DateValue) {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormat.setTimeZone(timeZone);
			String stringDate = dateFormat.format(((DateValue) value).getValue());
			return stringSpace.getElement(stringDate);

		} else if (value instanceof IntegerValue) {
			return z.getElement(((IntegerValue) value).getValue());
		} else if (value instanceof StringValue) {
			return stringSpace.getElement(((StringValue) value).getValue());
		} else {
			logger.log(Level.SEVERE, "Unsupported Value type.");
			return null;
		}
	}

	@PostConstruct
	private void init() {

		Configuration configuration = configurationManager.getConfiguration(CONFIG_NAME);
		if (configuration == null) {
			return;
		}

		String keyStorePath = configuration.getEntries().get(CONFIG_KEYSTORE_PATH);
		String keyStorePass = configuration.getEntries().get(CONFIG_KEYSTORE_PASS);
		String privateKeyPass = configuration.getEntries().get(CONFIG_PRIVATEKEY_PASS);
		String id = configuration.getEntries().get(CONFIG_ID);

		KeyStore caKs;

		try {
			caKs = KeyStore.getInstance(System.getProperty("javax.net.ssl.keyStoreType", "jks"));
		} catch (KeyStoreException ex) {
			return;
		}
		InputStream in;
		try {
			File file = new File(keyStorePath);
			in = new FileInputStream(file);
		} catch (FileNotFoundException | RuntimeException ex) {
			return;
		}
		try {
			caKs.load(in, keyStorePass.toCharArray());
		} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
			return;
		}

		// load the key entry from the keystore
		Key key;
		try {
			key = caKs.getKey(id, privateKeyPass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
			return;
		}

		if (key == null) {
			return;
		}
		String algo = key.getAlgorithm();
		if (algo.equals("RSA")) {
			try {
				RSAPrivateCrtKey rsaPrivKey = (RSAPrivateCrtKey) key;
				this.signer = new RSASigningHelper(rsaPrivKey);

			} catch (RuntimeException ex) {
			}
		} else if (algo.equals("DSA")) {
			try {
				DSAPrivateKey dsaPrivKey = (DSAPrivateKey) key;
				this.signer = new SchnorrSigningHelper(dsaPrivKey);
			} catch (RuntimeException ex) {
			}
		} else if (algo.equals("EC")) {
			try {
				ECPrivateKey ecPrivKey = (ECPrivateKey) key;
				//TODO
			} catch (RuntimeException ex) {
			}
		}
	}
}
