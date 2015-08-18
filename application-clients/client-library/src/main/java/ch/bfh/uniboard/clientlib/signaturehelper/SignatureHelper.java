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
package ch.bfh.uniboard.clientlib.signaturehelper;

import ch.bfh.uniboard.clientlib.UniBoardAttributesName;
import ch.bfh.uniboard.data.AlphaIdentifierDTO;
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.AttributesDTO.AttributeDTO;
import ch.bfh.uniboard.data.BetaIdentifierDTO;
import ch.bfh.uniboard.data.BetweenDTO;
import ch.bfh.uniboard.data.ByteArrayValueDTO;
import ch.bfh.uniboard.data.ConstraintDTO;
import ch.bfh.uniboard.data.DateValueDTO;
import ch.bfh.uniboard.data.EqualDTO;
import ch.bfh.uniboard.data.GreaterDTO;
import ch.bfh.uniboard.data.GreaterEqualDTO;
import ch.bfh.uniboard.data.IdentifierDTO;
import ch.bfh.uniboard.data.InDTO;
import ch.bfh.uniboard.data.IntegerValueDTO;
import ch.bfh.uniboard.data.LessDTO;
import ch.bfh.uniboard.data.LessEqualDTO;
import ch.bfh.uniboard.data.MessageIdentifierDTO;
import ch.bfh.uniboard.data.NotEqualDTO;
import ch.bfh.uniboard.data.OrderDTO;
import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.StringValueDTO;
import ch.bfh.uniboard.data.ValueDTO;
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
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class encapsulating code for generating poster signature and verifying signature of UniBoard
 *
 * @author Philémon von Bergen
 */
public abstract class SignatureHelper {

	private static final Logger logger = Logger.getLogger(SignatureHelper.class.getName());

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(HashAlgorithm.SHA256);
	protected static final ConvertMethod CONVERT_METHOD = ConvertMethod.getInstance(
			BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
			StringToByteArray.getInstance(Charset.forName("UTF-8")));
	private static final StringMonoid STRING_SPACE = StringMonoid.getInstance(Alphabet.UNICODE_BMP);

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

	/**
	 * Signs an UniCrypt element
	 *
	 * @param element element to sign
	 * @return the signature of element
	 * @throws SignatureException thrown when an error occured during signing process
	 */
	protected abstract BigInteger sign(Element element) throws SignatureException;

	/**
	 * Verifies signature of an UniCrypt element
	 *
	 * @param element element that was signed
	 * @param signatureBI signature of that element
	 * @return true if signature was correct, false otherwise
	 * @throws SignatureException thrown when an error occured during signing process
	 */
	protected abstract boolean verify(Element element, BigInteger signatureBI) throws SignatureException;

	/**
	 * Generate poster signature. It consists of signature of message and alpha attributes
	 *
	 * @param message message to post
	 * @param alpha user attributes that must also be signed
	 * @return the signature as big integer
	 * @throws SignatureException when error occured during signing
	 */
	public BigInteger sign(byte[] message, AttributesDTO alpha) throws SignatureException {
		Element messageElement = prepareElement(message, alpha);
		return sign(messageElement);
	}

	/**
	 * Verify poster signature containing message and user attributes
	 *
	 * @param message message posted
	 * @param alpha user attributes that must also be signed
	 * @param signature signature to verify
	 * @return true if signature is valid, false otherwise
	 * @throws SignatureException when error occured during signature verification
	 */
	public boolean verify(byte[] message, AttributesDTO alpha, BigInteger signature) throws
			SignatureException {
		Element messageElement = prepareElement(message, alpha);
		return verify(messageElement, signature);
	}

	/**
	 * Verify board signature containing message, user attributes and board attributes
	 *
	 * @param message posted message
	 * @param alpha user attributes sent
	 * @param beta board attributes received after posting
	 * @param signature board signature to verify
	 * @return true if signature is correct, false otherwise
	 * @throws SignatureException when error occured during signing
	 */
	public boolean verify(byte[] message, AttributesDTO alpha, AttributesDTO beta, BigInteger signature) throws
			SignatureException {
		Element messageElement = prepareElement(message, alpha, beta);
		logger.log(Level.INFO, "Element Hash: {0}",
				bytesToHex(messageElement.getHashValue(CONVERT_METHOD, HASH_METHOD).getBytes()));
		return verify(messageElement, signature);
	}

	/**
	 * Verify board signature of a result container
	 *
	 * @param query the query that was sent to the board
	 * @param resultContainer the result container received from the board
	 * @param signature the signature to verify
	 * @return true if signature is correct, false otherwise
	 * @throws SignatureException when error occured during signing
	 */
	public boolean verify(QueryDTO query, ResultContainerDTO resultContainer, BigInteger signature) throws
			SignatureException {
		Element messageElement = prepareElement(query, resultContainer);
		return verify(messageElement, signature);
	}

	/**
	 * Helper method creating an UniCrypt element with the message and the alpha attributes, stopping before poster
	 * signature is met in the alpha attributes. This is the method that must be called when generating poster signature
	 *
	 * @param message message to include in element
	 * @param alpha alpha attributes to include in element. Poster signature and following attributes are ignored
	 * @return the element representing the message and alpha attributes (Poster signature and following attributes
	 * excluded)
	 */
	protected Element prepareElement(byte[] message, AttributesDTO alpha) {
		return this.prepareElement(message, alpha, UniBoardAttributesName.SIGNATURE.getName());
	}

	/**
	 * Helper method creating an UniCrypt element with the message and the alpha and beta attributes. This method must
	 * be called when verifying board signature after post
	 *
	 * @param message message to include in element
	 * @param alpha alpha attributes to include in element. All attributes are included
	 * @param beta beta attributes to include in element. Board signature and following attributes are ignored
	 * @return the element representing the message and alpha and beta attributes (Board signature and following
	 * attributes excluded)
	 */
	private Element prepareElement(byte[] message, AttributesDTO alpha, AttributesDTO beta) {
		return this.prepareElement(message, alpha, beta, UniBoardAttributesName.BOARD_SIGNATURE.getName());
	}

	/**
	 * Helper method creating an UniCrypt element with the message and the alpha attributes, stopping before attribute
	 * with name passed in "lastAttributeName" is met. This is the method that is called when generating poster
	 * signature (lastNameSignature must contain UniBoardAttributesName.SIGNATURE) and when verifying board signature
	 * (lastNameSignature must be null)
	 *
	 * @param message message to include in element
	 * @param alpha alpha attributes to include in element until "lastAttributeName" is met (lastAttributeName excluded)
	 * @param lastAttributeName when this attribute is met, no other alpha attributes is added anymore to the element
	 * (attribute with name "lastAttributeName" is also ignored)
	 * @return the element representing the message and alpha attributes minus the excluded attributes
	 */
	private Element prepareElement(byte[] message, AttributesDTO alpha, String lastAttributeName) {
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		Element messageElement = byteSpace.getElement(message);

		Element alphaElement = this.prepareAttributesElement(alpha, lastAttributeName);
		return Pair.getInstance(messageElement, alphaElement);
	}

	/**
	 * Helper method creating an UniCrypt element with the message and the alpha and beta attributes, stopping before
	 * attribute with name passed in "lastAttributeName" is met in beta attributes. This is the method that is called
	 * when verifiying board signature after post (lastNameSignature must contain UniBoardAttributesName.SIGNATURE) and
	 * when verifying board signature in posts returned in a result container (lastNameSignature must be null)
	 *
	 * @param message message posted
	 * @param alpha alpha attributes to include in element (all attributes are included)
	 * @param beta bate attributes to include in element until "lastAttributeName" is met (lastAttributeName excluded)
	 * @param lastAttributeName when this attribute is met, no other beta attributes is added anymore to the element
	 * (attribute with name "lastAttributeName" is also ignored)
	 * @return the element representing the message and alpha and beta attributes minus the excluded attributes
	 */
	private Element prepareElement(byte[] message, AttributesDTO alpha, AttributesDTO beta, String lastAttributeName) {

		Pair pair = (Pair) this.prepareElement(message, alpha, (String) null);

		Element betaElement = this.prepareAttributesElement(beta, lastAttributeName);
		return Tuple.getInstance(pair.getAt(0), pair.getAt(1), betaElement);
	}

	/**
	 * Helper method creating an UniCrypt element with the passed attributes, stopping before attribute with name passed
	 * in "lastAttributeName" is met.
	 *
	 * @param attributes attributes to include in element until "lastAttributeName" is met (lastAttributeName excluded)
	 * @param lastAttributeName when this attribute is met, no other attributes is added anymore to the element
	 * (attribute with name "lastAttributeName" is also ignored)
	 * @return the element representing attributes minus the excluded attributes
	 */
	private Element prepareAttributesElement(AttributesDTO attributes, String lastAttributeName) {

		List<Element> attributesElements = new ArrayList<>();
		//iterate over alpha until one reaches the property = signature
		for (AttributeDTO attr : attributes.getAttribute()) {
			if (lastAttributeName != null && attr.getKey().equals(lastAttributeName)) {
				break;
			}
			Element element = this.prepareValueElement(attr.getValue());
			if (element != null) {
				attributesElements.add(element);
			}
		}
		DenseArray immuElements = DenseArray.getInstance(attributesElements);
		return Tuple.getInstance(immuElements);
	}

	/**
	 * Helper method generating an UniCrypt element for each type of Value
	 *
	 * @param value Value object to convert to UniCrypt element
	 * @return the element representing the Value object
	 */
	private Element prepareValueElement(ValueDTO value) {
		Z z = Z.getInstance();
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		if (value instanceof ByteArrayValueDTO) {
			return byteSpace.getElement(((ByteArrayValueDTO) value).getValue());
		} else if (value instanceof DateValueDTO) {
			TimeZone timeZone = TimeZone.getTimeZone("UTC");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			dateFormat.setTimeZone(timeZone);
			String stringDate = dateFormat.format(((DateValueDTO) value).getValue().toGregorianCalendar().getTime());
			return STRING_SPACE.getElement(stringDate);
		} else if (value instanceof IntegerValueDTO) {
			return z.getElement(((IntegerValueDTO) value).getValue());
		} else if (value instanceof StringValueDTO) {
			return STRING_SPACE.getElement(((StringValueDTO) value).getValue());
		} else {
			logger.log(Level.SEVERE, "Unsupported Value type.");
			return null;
		}
	}

	/**
	 * Helper method generating an UniCrypt element for a query and its results
	 *
	 * @param query the query
	 * @param resultContainer its results
	 * @return the UniCrypt element reprensenting the query and its results
	 */
	private Element prepareElement(QueryDTO query, ResultContainerDTO resultContainer) {
		Element queryElement = this.prepareQueryElement(query);
		Element resultContainerElement = this.prepareResultContainerElement(resultContainer);
		return Tuple.getInstance(queryElement, resultContainerElement);
	}

	/**
	 * Helper method generating an UniCrypt element for an Identifier
	 *
	 * @param identifier the identifier object to represent
	 * @return the UniCrypt element reprensenting the identifier
	 */
	private Element prepareIdentifierElement(IdentifierDTO identifier) {
		List<Element> identifierElements = new ArrayList<>();

		if (identifier instanceof AlphaIdentifierDTO) {
			identifierElements.add(STRING_SPACE.getElement("alpha"));
		} else if (identifier instanceof BetaIdentifierDTO) {
			identifierElements.add(STRING_SPACE.getElement("beta"));
		} else if (identifier instanceof MessageIdentifierDTO) {
			identifierElements.add(STRING_SPACE.getElement("message"));
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier type.");
			return null;
		}

		for (String part : identifier.getPart()) {
			identifierElements.add(STRING_SPACE.getElement(part));
		}
		DenseArray identifierDenseElements = DenseArray.getInstance(identifierElements);
		return Tuple.getInstance(identifierDenseElements);
	}

	/**
	 * Helper method generating an UniCrypt element for all types of Constraints
	 *
	 * @param constraint the constraint to represent
	 * @return the UniCrypt element reprensenting the constraint
	 */
	private Element prepareConstraintElement(ConstraintDTO constraint) {
		List<Element> constraintElements = new ArrayList<>();
		if (constraint instanceof BetweenDTO) {
			constraintElements.add(STRING_SPACE.getElement("between"));
			BetweenDTO between = (BetweenDTO) constraint;
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			constraintElements.add(this.prepareValueElement(between.getLowerBound()));
			constraintElements.add(this.prepareValueElement(between.getUpperBound()));
		} else if (constraint instanceof EqualDTO) {
			constraintElements.add(STRING_SPACE.getElement("equal"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			EqualDTO equal = (EqualDTO) constraint;
			constraintElements.add(this.prepareValueElement(equal.getValue()));
		} else if (constraint instanceof GreaterDTO) {
			constraintElements.add(STRING_SPACE.getElement("greater"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			GreaterDTO greater = (GreaterDTO) constraint;
			constraintElements.add(this.prepareValueElement(greater.getValue()));
		} else if (constraint instanceof GreaterEqualDTO) {
			constraintElements.add(STRING_SPACE.getElement("greaterEqual"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			GreaterEqualDTO greaterEqual = (GreaterEqualDTO) constraint;
			constraintElements.add(this.prepareValueElement(greaterEqual.getValue()));
		} else if (constraint instanceof InDTO) {
			constraintElements.add(STRING_SPACE.getElement("in"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			InDTO in = (InDTO) constraint;
			List<Element> inELements = new ArrayList<>();
			for (ValueDTO v : in.getElement()) {
				inELements.add(this.prepareValueElement(v));
			}
			DenseArray inDenseElements = DenseArray.getInstance(inELements);
			constraintElements.add(Tuple.getInstance(inDenseElements));
		} else if (constraint instanceof LessDTO) {
			constraintElements.add(STRING_SPACE.getElement("less"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			LessDTO less = (LessDTO) constraint;
			constraintElements.add(this.prepareValueElement(less.getValue()));
		} else if (constraint instanceof LessEqualDTO) {
			constraintElements.add(STRING_SPACE.getElement("lessEqual"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			LessEqualDTO lessEqual = (LessEqualDTO) constraint;
			constraintElements.add(this.prepareValueElement(lessEqual.getValue()));
		} else if (constraint instanceof NotEqualDTO) {
			constraintElements.add(STRING_SPACE.getElement("notEqual"));
			constraintElements.add(this.prepareIdentifierElement(constraint.getIdentifier()));
			NotEqualDTO notEqual = (NotEqualDTO) constraint;
			constraintElements.add(this.prepareValueElement(notEqual.getValue()));
		} else {
			logger.log(Level.SEVERE, "Unsupported constraint type.");
		}
		DenseArray constraintDenseElements = DenseArray.getInstance(constraintElements);
		return Tuple.getInstance(constraintDenseElements);
	}

	/**
	 * Helper method generating an UniCrypt element for an Order object
	 *
	 * @param order the Order to represent
	 * @return the UniCrypt element reprensenting the Order
	 */
	private Element prepareOrderElement(OrderDTO order) {
		List<Element> orderElements = new ArrayList<>();
		orderElements.add(this.prepareIdentifierElement(order.getIdentifier()));
		orderElements.add(STRING_SPACE.getElement(Boolean.toString(order.isAscDesc())));
		DenseArray orderDenseElements = DenseArray.getInstance(orderElements);
		return Tuple.getInstance(orderDenseElements);
	}

	/**
	 * Helper method generating an UniCrypt element for a Query
	 *
	 * @param query the Query to represent
	 * @return the UniCrypt element reprensenting the Query
	 */
	private Element prepareQueryElement(QueryDTO query) {

		List<Element> constraintsElements = new ArrayList<>();
		for (ConstraintDTO c : query.getConstraint()) {
			constraintsElements.add(this.prepareConstraintElement(c));
		}
		DenseArray constraintsDenseElements = DenseArray.getInstance(constraintsElements);
		Element contraints = Tuple.getInstance(constraintsDenseElements);

		List<Element> orderElements = new ArrayList<>();
		for (OrderDTO o : query.getOrder()) {
			orderElements.add(this.prepareOrderElement(o));
		}
		DenseArray orderDenseElements = DenseArray.getInstance(orderElements);
		Element orders = Tuple.getInstance(orderDenseElements);

		Z z = Z.getInstance();
		Element limit = z.getElement(query.getLimit());

		return Tuple.getInstance(contraints, orders, limit);
	}

	/**
	 * Helper method generating an UniCrypt element for a ResultContainer
	 *
	 * @param resultContainer the ResultContainer to represent
	 * @return the UniCrypt element reprensenting the ResultContainer
	 */
	private Element prepareResultContainerElement(ResultContainerDTO resultContainer) {

		List<Element> postElements = new ArrayList<>();
		for (PostDTO p : resultContainer.getResult().getPost()) {
			postElements.add(this.prepareElement(p.getMessage(), p.getAlpha(), p.getBeta(), null));
		}
		DenseArray postDenseElements = DenseArray.getInstance(postElements);
		Element postElement = Tuple.getInstance(postDenseElements);

		Element gammaElement = this.prepareAttributesElement(resultContainer.getGamma(), UniBoardAttributesName.BOARD_SIGNATURE.getName());

		return Tuple.getInstance(postElement, gammaElement);
	}
}
