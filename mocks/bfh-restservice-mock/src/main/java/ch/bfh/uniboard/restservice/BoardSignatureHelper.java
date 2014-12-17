/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.restservice;

import static ch.bfh.uniboard.restservice.UniBoardRestServiceImpl.HASH_METHOD;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import ch.bfh.unicrypt.crypto.schemes.signature.classes.SchnorrSignatureScheme;
import ch.bfh.unicrypt.helper.Alphabet;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.StringMonoid;
import ch.bfh.unicrypt.math.algebra.dualistic.classes.Z;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phil
 */
public class BoardSignatureHelper {

    protected String pStr
	    = "178011905478542266528237562450159990145232156369120674273274450314442865788737020770612695252123463079567156784778466449970650770920727857050009668388144034129745221171818506047231150039301079959358067395348717066319802262019714966524135060945913707594956514672855690606794135837542707371727429551343320695239";
    protected String qStr = "864205495604807476120572616017955259175325408501";
    protected String gStr
	    = "174068207532402095185811980123523436538604490794561350978495831040599953488455823147851597408940950725307797094915759492368300574252438761037084473467180148876118103083043754985190983472601550494691329488083395492313850000361646482644608492304078721818959999056496097769368017749273708962006689187956744210730";

    protected String xStr
	    = "785073364343293933733805077957886131995723437137";
    protected String yStr
	    = "66958355597810698489471425362307177643942027542459889847139582549668126282135390777783858460746726000662328230820010568386790445146483265529575179386210607141523872577665302631137940868775056108711129683088056561604904097738690582247866539343707184146771244022706478825219472668923353222121740341182394461614";
    protected static final Logger logger = Logger.getLogger(BoardSignatureHelper.class.getName());

    public BoardSignatureHelper() {
    }

    public BigInteger sign(byte[] message, Attributes alpha, Attributes beta) {
	return this.sign(this.createMessageElement(message, alpha, beta)).getBigInteger();
    }

    private Element sign(Element message) {

	GStarModPrime g_q = GStarModPrime.getInstance(new BigInteger(pStr), new BigInteger(qStr));
	GStarModElement g = g_q.getElement(new BigInteger(gStr));

	SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(message.getSet(), g, HASH_METHOD);
	Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(new BigInteger(xStr));
	return schnorr.sign(privateKeyElement, message);
    }

    protected Element createMessageElement(byte[] message, Attributes alpha) {
	StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
	Z z = Z.getInstance();
	ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
	Element messageElement = byteSpace.getElement(message);
	List<Element> alphaElements = new ArrayList<>();
	//iterate over alpha until one reaches the property = signature
	for (Map.Entry<String, Value> e : alpha.getEntries()) {
	    if (e.getKey().equals("signature")) {
		break;
	    }
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
	    } else {
		logger.log(Level.SEVERE, "Unsupported Value type.");
	    }
	}
	DenseArray immuElements = DenseArray.getInstance(alphaElements);
	Element alphaElement = Tuple.getInstance(immuElements);
	System.out.println("message hash: " + messageElement.getHashValue(HASH_METHOD));
	System.out.println("alpha hash: " + alphaElement.getHashValue(HASH_METHOD));
	System.out.println("post element: " + Pair.getInstance(messageElement, alphaElement));
	System.out.println("post hash: " + Pair.getInstance(messageElement, alphaElement).getHashValue(HASH_METHOD));
	return Pair.getInstance(messageElement, alphaElement);
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
	    if (e.getKey().equals("boardSignature")) {
		continue;
	    }

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
	StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
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
}
