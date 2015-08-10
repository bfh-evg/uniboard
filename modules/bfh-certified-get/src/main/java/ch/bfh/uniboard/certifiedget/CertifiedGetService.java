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

import ch.bfh.uniboard.service.*;
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
import java.util.Properties;
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

	private static final StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);

	private static final Logger logger = Logger.getLogger(CertifiedGetService.class.getName());

	private SigningHelper signer = null;

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
			gamma.add(Attributes.ERROR,
					new StringValue("BCG-001 Internal server error."));
			return gamma;
		}
		Element messageElement = this.createMessageElement(query, resultContainer);
		Element signature = this.signer.sign(messageElement);
		String signatureString = signature.convertToBigInteger().toString(10);
		gamma.add(ATTRIBUTE_NAME, new StringValue(signatureString));
		return gamma;

	}

	protected Element createMessageElement(Query query, ResultContainer resultContainer) {

		Element queryElement = this.createQueryElement(query);
		Element resultContainerElement = this.createResultContainerElement(resultContainer);
		return Tuple.getInstance(queryElement, resultContainerElement);
	}

	protected Element createValueElement(Value value) {
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

	protected Element createIdentifierElement(Identifier identifier) {
		List<Element> identifierElements = new ArrayList<>();

		if (identifier instanceof AlphaIdentifier) {
			identifierElements.add(stringSpace.getElement("alpha"));
		} else if (identifier instanceof BetaIdentifier) {
			identifierElements.add(stringSpace.getElement("beta"));
		} else if (identifier instanceof MessageIdentifier) {
			identifierElements.add(stringSpace.getElement("message"));
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier type.");
			return null;
		}

		for (String part : identifier.getParts()) {
			identifierElements.add(stringSpace.getElement(part));
		}
		DenseArray identifierDenseElements = DenseArray.getInstance(identifierElements);
		return Tuple.getInstance(identifierDenseElements);
	}

	protected Element createConstraintElement(Constraint constraint) {
		List<Element> constraintElements = new ArrayList<>();
		if (constraint instanceof Between) {
			constraintElements.add(stringSpace.getElement("between"));
			Between between = (Between) constraint;
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			constraintElements.add(this.createValueElement(between.getStart()));
			constraintElements.add(this.createValueElement(between.getEnd()));
		} else if (constraint instanceof Equal) {
			constraintElements.add(stringSpace.getElement("equal"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			Equal equal = (Equal) constraint;
			constraintElements.add(this.createValueElement(equal.getValue()));
		} else if (constraint instanceof Greater) {
			constraintElements.add(stringSpace.getElement("greater"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			Greater greater = (Greater) constraint;
			constraintElements.add(this.createValueElement(greater.getValue()));
		} else if (constraint instanceof GreaterEqual) {
			constraintElements.add(stringSpace.getElement("greaterEqual"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			GreaterEqual greaterEqual = (GreaterEqual) constraint;
			constraintElements.add(this.createValueElement(greaterEqual.getValue()));
		} else if (constraint instanceof In) {
			constraintElements.add(stringSpace.getElement("in"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			In in = (In) constraint;
			List<Element> inELements = new ArrayList<>();
			for (Value v : in.getSet()) {
				inELements.add(this.createValueElement(v));
			}
			DenseArray inDenseElements = DenseArray.getInstance(inELements);
			constraintElements.add(Tuple.getInstance(inDenseElements));
		} else if (constraint instanceof Less) {
			constraintElements.add(stringSpace.getElement("less"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			Less less = (Less) constraint;
			constraintElements.add(this.createValueElement(less.getValue()));
		} else if (constraint instanceof LessEqual) {
			constraintElements.add(stringSpace.getElement("lessEqual"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			LessEqual lessEqual = (LessEqual) constraint;
			constraintElements.add(this.createValueElement(lessEqual.getValue()));
		} else if (constraint instanceof NotEqual) {
			constraintElements.add(stringSpace.getElement("notEqual"));
			constraintElements.add(this.createIdentifierElement(constraint.getIdentifier()));
			NotEqual notEqual = (NotEqual) constraint;
			constraintElements.add(this.createValueElement(notEqual.getValue()));
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
		for (Map.Entry<String, Value> e : post.getAlpha().getEntries()) {
			Element element = this.createValueElement(e.getValue());
			if (element != null) {
				alphaElements.add(element);

			}
		}
		DenseArray alphaDenseElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(alphaDenseElements);

		List<Element> betaElements = new ArrayList<>();
		for (Map.Entry<String, Value> e : post.getBeta().getEntries()) {
			Element element = this.createValueElement(e.getValue());
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
		for (Map.Entry<String, Value> e : resultContainer.getGamma().getEntries()) {
			Element element = this.createValueElement(e.getValue());
			if (element != null) {
				gammaElements.add(element);
			}
		}
		DenseArray gammaDenseElements = DenseArray.getInstance(gammaElements);
		Element gammeElement = Tuple.getInstance(gammaDenseElements);

		return Tuple.getInstance(postElement, gammeElement);
	}

	@PostConstruct
	private void init() {

		Properties configuration = configurationManager.getConfiguration(CONFIG_NAME);
		if (configuration == null) {
			return;
		}

		String keyStorePath = configuration.getProperty(CONFIG_KEYSTORE_PATH);
		String keyStorePass = configuration.getProperty(CONFIG_KEYSTORE_PASS);
		String privateKeyPass = configuration.getProperty(CONFIG_PRIVATEKEY_PASS);
		String id = configuration.getProperty(CONFIG_ID);

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
