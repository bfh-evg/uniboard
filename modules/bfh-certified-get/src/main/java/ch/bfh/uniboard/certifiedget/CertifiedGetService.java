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

import ch.bfh.uniboard.service.data.In;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.Order;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.Less;
import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.data.Greater;
import ch.bfh.uniboard.service.data.Identifier;
import ch.bfh.uniboard.service.data.GreaterEqual;
import ch.bfh.uniboard.service.data.NotEqual;
import ch.bfh.uniboard.service.data.Post;
import ch.bfh.uniboard.service.data.Between;
import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.data.LessEqual;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.*;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.MessageIdentifier;
import ch.bfh.uniboard.service.data.PropertyIdentifier;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class CertifiedGetService extends GetComponent implements GetService {

	private static final String ATTRIBUTE_NAME = "boardSignature";
	private static final String CONFIG_NAME = "bfh-certified-get";
	private static final String CONFIG_KEYSTORE_PATH = "keystore-path";
	private static final String CONFIG_KEYSTORE_PASS = "keystore-pass";
	private static final String CONFIG_ID = "id";
	private static final String CONFIG_PRIVATEKEY_PASS = "privatekey-pass";

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(HashAlgorithm.SHA256);
	protected static final ConvertMethod CONVERT_METHOD = ConvertMethod.getInstance(
			BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
			StringToByteArray.getInstance(Charset.forName("UTF-8")));

	private static final StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.UNICODE_BMP);

	private static final Logger logger = Logger.getLogger(CertifiedGetService.class.getName());

	private SigningHelper signer = null;

	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	@EJB
	GetService getSuccessor;

	@EJB
	ConfigurationManager configurationManager;

	@Override
	protected GetService getGetSuccessor() {
		return this.getSuccessor;
	}

	@Override
	protected Attributes afterGet(Query query, ResultContainer resultContainer) {
		Attributes gamma = resultContainer.getGamma();
		if (this.signer == null) {
			logger.log(Level.SEVERE,
					"Signer is not set. Check the configuration.");
			gamma.add(new Attribute(Attributes.ERROR, "BCG-001 Internal server error."));
			return gamma;
		}
		Element messageElement = this.createMessageElement(query, resultContainer);
		logger.log(Level.FINE, "message: {0}", messageElement.toString());
		logger.log(Level.FINE, "complete: {0}",
				bytesToHex(messageElement.getHashValue(CONVERT_METHOD, HASH_METHOD).getBytes()));
		String signatureString = this.signer.sign(messageElement).toString(10);
		gamma.add(new Attribute(ATTRIBUTE_NAME, signatureString));
		return gamma;

	}

	protected Element createMessageElement(Query query, ResultContainer resultContainer) {

		Element queryElement = this.createQueryElement(query);
		logger.log(Level.FINE, "query: {0}",
				bytesToHex(queryElement.getHashValue(CONVERT_METHOD, HASH_METHOD).getBytes()));
		Element resultContainerElement = this.createResultContainerElement(resultContainer);
		logger.log(Level.FINE, "result container: {0}",
				bytesToHex(resultContainerElement.getHashValue(CONVERT_METHOD, HASH_METHOD).getBytes()));
		return Tuple.getInstance(queryElement, resultContainerElement);
	}

	protected Element createAttributeElement(Attribute attribute) {
		List<Element> attributeElements = new ArrayList<>();
		attributeElements.add(stringSpace.getElement(attribute.getKey()));
		attributeElements.add(stringSpace.getElement(attribute.getValue()));
		if (attribute.getDataType() != null) {
			attributeElements.add(stringSpace.getElement(attribute.getDataType().value()));
		}

		DenseArray attributeDenseElements = DenseArray.getInstance(attributeElements);
		return Tuple.getInstance(attributeDenseElements);
	}

	protected Element createIdentifierElement(Identifier identifier) {
		List<Element> identifierElements = new ArrayList<>();

		char c[] = identifier.getClass().getSimpleName().toCharArray();
		c[0] = Character.toLowerCase(c[0]);
		String strIdentifier = new String(c);

		identifierElements.add(stringSpace.getElement(strIdentifier));

		if (identifier instanceof PropertyIdentifier) {
			PropertyIdentifier pIdent = (PropertyIdentifier) identifier;
			identifierElements.add(stringSpace.getElement(pIdent.getType().value()));
			identifierElements.add(stringSpace.getElement(pIdent.getKeyPath()));
		} else if (identifier instanceof MessageIdentifier) {
			MessageIdentifier mIdent = (MessageIdentifier) identifier;
			identifierElements.add(stringSpace.getElement(mIdent.getKeyPath()));
		}

		DenseArray identifierDenseElements = DenseArray.getInstance(identifierElements);
		return Tuple.getInstance(identifierDenseElements);
	}

	protected Element createConstraintElement(Constraint constraint) {
		List<Element> constraintElements = new ArrayList<>();
		//Type
		XmlType type = constraint.getClass().getAnnotation(XmlType.class);
		constraintElements.add(stringSpace.getElement(type.name()));
		//Identifier
		constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
		//DataType
		if (constraint.getDataType() != null) {
			constraintElements.add(stringSpace.getElement(constraint.getDataType().value()));
		}
		//Values
		if (constraint instanceof Between) {
			Between between = (Between) constraint;
			constraintElements.add(stringSpace.getElement(between.getLowerBound()));
			constraintElements.add(stringSpace.getElement(between.getUpperBound()));
		} else if (constraint instanceof Equal) {
			Equal equal = (Equal) constraint;
			constraintElements.add(stringSpace.getElement(equal.getValue()));
		} else if (constraint instanceof Greater) {
			Greater greater = (Greater) constraint;
			constraintElements.add(stringSpace.getElement(greater.getValue()));
		} else if (constraint instanceof GreaterEqual) {
			GreaterEqual greaterEqual = (GreaterEqual) constraint;
			constraintElements.add(stringSpace.getElement(greaterEqual.getValue()));
		} else if (constraint instanceof In) {
			In in = (In) constraint;
			List<Element> inELements = new ArrayList<>();
			for (String v : in.getSet()) {
				inELements.add(stringSpace.getElement(v));
			}
			DenseArray inDenseElements = DenseArray.getInstance(inELements);
			constraintElements.add(Tuple.getInstance(inDenseElements));
		} else if (constraint instanceof Less) {
			Less less = (Less) constraint;
			constraintElements.add(stringSpace.getElement(less.getValue()));
		} else if (constraint instanceof LessEqual) {
			LessEqual lessEqual = (LessEqual) constraint;
			constraintElements.add(stringSpace.getElement(lessEqual.getValue()));
		} else if (constraint instanceof NotEqual) {
			NotEqual notEqual = (NotEqual) constraint;
			constraintElements.add(stringSpace.getElement(notEqual.getValue()));
		} else {
			logger.log(Level.SEVERE, "Unsupported constraint type.");
		}
		DenseArray constraintDenseElements = DenseArray.getInstance(constraintElements);
		return Tuple.getInstance(constraintDenseElements);
	}

	protected Element createOrderElement(Order order) {
		List<Element> orderElements = new ArrayList<>();
		orderElements.add(this.createIdentifierElement(order.getIdentifier()));
		orderElements.add(stringSpace.getElement(Boolean.toString(order.isAscDesc())));
		DenseArray orderDenseElements = DenseArray.getInstance(orderElements);
		return Tuple.getInstance(orderDenseElements);
	}

	protected Element createQueryElement(Query query) {

		List<Element> constraintsElements = new ArrayList<>();
		for (Constraint c : query.getConstraints()) {
			constraintsElements.add(this.createConstraintElement(c));
		}
		DenseArray constraintsDenseElements = DenseArray.getInstance(constraintsElements);
		Element contraints = Tuple.getInstance(constraintsDenseElements);

		List<Element> orderElements = new ArrayList<>();
		for (Order o : query.getOrder()) {
			orderElements.add(this.createOrderElement(o));
		}
		DenseArray orderDenseElements = DenseArray.getInstance(orderElements);
		Element orders = Tuple.getInstance(orderDenseElements);

		Z z = Z.getInstance();
		Element limit = z.getElement(query.getLimit());

		return Tuple.getInstance(contraints, orders, limit);
	}

	protected Element createPostElement(Post post) {
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(post.getMessage());

		List<Element> alphaElements = new ArrayList<>();
		for (Map.Entry<String, Attribute> e : post.getAlpha().getEntries()) {
			Element element = this.createAttributeElement(e.getValue());
			if (element != null) {
				alphaElements.add(element);

			}
		}
		DenseArray alphaDenseElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(alphaDenseElements);

		List<Element> betaElements = new ArrayList<>();
		for (Map.Entry<String, Attribute> e : post.getBeta().getEntries()) {
			Element element = this.createAttributeElement(e.getValue());
			if (element != null) {
				betaElements.add(element);
			}
		}
		DenseArray beteDenseElements = DenseArray.getInstance(betaElements);
		Element betaElement = Tuple.getInstance(beteDenseElements);

		return Tuple.getInstance(messageElement, alphaElement, betaElement);
	}

	protected Element createResultContainerElement(ResultContainer resultContainer) {

		List<Element> postElements = new ArrayList<>();
		for (Post p : resultContainer.getResult()) {
			postElements.add(this.createPostElement(p));
		}
		DenseArray postDenseElements = DenseArray.getInstance(postElements);
		Element postElement = Tuple.getInstance(postDenseElements);

		List<Element> gammaElements = new ArrayList<>();
		for (Map.Entry<String, Attribute> e : resultContainer.getGamma().getEntries()) {
			Element element = this.createAttributeElement(e.getValue());
			if (element != null) {
				gammaElements.add(element);
			}
		}
		DenseArray gammaDenseElements = DenseArray.getInstance(gammaElements);
		Element gammaElement = Tuple.getInstance(gammaDenseElements);

		return Tuple.getInstance(postElement, gammaElement);
	}

	@PostConstruct
	private void init() {

		Configuration configuration = configurationManager.getConfiguration(CONFIG_NAME);
		if (configuration == null) {
			logger.log(Level.SEVERE, "Could not load configuration: " + CONFIG_NAME);
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
			logger.log(Level.SEVERE, "Could not create keystore type.");
			logger.log(Level.SEVERE, ex.getMessage());
			return;
		}
		InputStream in;
		try {
			File file = new File(keyStorePath);
			in = new FileInputStream(file);
		} catch (FileNotFoundException | RuntimeException ex) {
			logger.log(Level.SEVERE, "Could not load keystore: {0}", keyStorePath);
			return;
		}
		try {
			caKs.load(in, keyStorePass.toCharArray());
		} catch (IOException | NoSuchAlgorithmException | CertificateException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
			return;
		}

		// load the key entry from the keystore
		Key key;
		try {
			key = caKs.getKey(id, privateKeyPass.toCharArray());
		} catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException ex) {
			logger.log(Level.SEVERE, ex.getMessage());
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
				logger.log(Level.SEVERE, ex.getMessage());
			}
		} else if (algo.equals("DSA")) {
			try {
				DSAPrivateKey dsaPrivKey = (DSAPrivateKey) key;
				this.signer = new SchnorrSigningHelper(dsaPrivKey);
			} catch (RuntimeException ex) {
				logger.log(Level.SEVERE, ex.getMessage());
			}
		} else if (algo.equals("EC")) {
			try {
				ECPrivateKey ecPrivKey = (ECPrivateKey) key;
				//TODO
			} catch (RuntimeException ex) {
				logger.log(Level.SEVERE, ex.getMessage());
			}
		}
	}
}
