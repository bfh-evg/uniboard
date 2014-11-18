/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.restservice;

import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.TransformException;
import ch.bfh.uniboard.data.Transformer;
import ch.bfh.uniboard.restservice.mock.LanguageCode;
import ch.bfh.uniboard.restservice.mock.LocalizedText;
import ch.bfh.uniboard.restservice.mock.election.Candidate;
import ch.bfh.uniboard.restservice.mock.election.CandidateElection;
import ch.bfh.uniboard.restservice.mock.election.CandidateList;
import ch.bfh.uniboard.restservice.mock.election.Choice;
import ch.bfh.uniboard.restservice.mock.election.Election;
import ch.bfh.uniboard.restservice.mock.election.ElectionDefinition;
import ch.bfh.uniboard.restservice.mock.election.ForAllRule;
import ch.bfh.uniboard.restservice.mock.election.MultiElection;
import ch.bfh.uniboard.restservice.mock.election.Option;
import ch.bfh.uniboard.restservice.mock.election.Party;
import ch.bfh.uniboard.restservice.mock.election.PartyCandidate;
import ch.bfh.uniboard.restservice.mock.election.PartyElection;
import ch.bfh.uniboard.restservice.mock.election.PartyList;
import ch.bfh.uniboard.restservice.mock.election.Rule;
import ch.bfh.uniboard.restservice.mock.election.SummationRule;
import ch.bfh.uniboard.restservice.mock.election.Vote;
import ch.bfh.uniboard.restservice.mock.election.crypto.DLEncryptionSetting;
import ch.bfh.uniboard.restservice.mock.election.crypto.DLSignatureSetting;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
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
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModElement;
import ch.bfh.unicrypt.math.algebra.multiplicative.classes.GStarModPrime;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response.Status;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.eclipse.persistence.jaxb.JAXBContextProperties;

/**
 * The class UniBoardRestServiceImpl implements a RESTful interface of the UniBoard by delegating the get and post
 * requests to the successor services.
 *
 * @author Stephan Fischli &lt;stephan.fischli@bfh.ch&gt;
 */
@Stateless(name = "UniBoardRestService")
public class UniBoardRestServiceImpl implements UniBoardRestService {

	protected static final Logger logger = Logger.getLogger(UniBoardRestServiceImpl.class.getName());

	@Override
	public ResultContainerDTO query(QueryDTO queryDTO) {
		Query query = null;
		try {
			query = Transformer.convertQueryDTOtoQuery(queryDTO);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}

		logger.info("Retrieve posts using " + query);
		logger.info(query.getConstraints().get(1).getIdentifier().getParts().get(0));

		if (((Equal) query.getConstraints().get(1)).getValue().getValue().equals("electionData")) {
			try {
				Attributes gamma = new Attributes();
				long time = new Date().getTime();
				time = 1000 * (time / 1000);
				gamma.add("timestamp", new DateValue(new Date(time)));
				Element rcElement = createMessageElement(electionDataPost.getMessage(), electionDataPost.
						getAlpha(), electionDataPost.getBeta());
				logger.log(Level.SEVERE, "RC element: " + rcElement);
				logger.log(Level.SEVERE, "RC hash: " + rcElement.getHashValue(HASH_METHOD));
				Pair signature = (Pair) this.sign(rcElement);
				String signatureString = signature.getBigInteger().toString(10);
				gamma.add("boardSignature", new StringValue(signatureString));

				//TODO gamma
				return Transformer.convertResultContainertoResultContainerDTO(new ResultContainer(Collections.
						singletonList(electionDataPost), gamma));
			} catch (TransformException ex) {
				Logger.getLogger(UniBoardRestServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			}

		} else if (((Equal) query.getConstraints().get(1)).getValue().getValue().equals("electionDefinition")) {
			try {
				//TODO gamma
				Attributes gamma = new Attributes();
				long time = new Date().getTime();
				time = 1000 * (time / 1000);
				gamma.add("timestamp", new DateValue(new Date(time)));

				String signatureString = "signature";
				gamma.add("boardSignature", new StringValue(signatureString));
				return Transformer.convertResultContainertoResultContainerDTO(new ResultContainer(
						electionDefinitionPosts, gamma));
			} catch (TransformException ex) {
				Logger.getLogger(UniBoardRestServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
			}
		}

		return null;
	}

	@Override
	public AttributesDTO post(PostContainerDTO postContainer) {
		try {
			byte[] message = DatatypeConverter.parseBase64Binary(postContainer.getMessage());
			Attributes alpha = Transformer.convertAttributesDTOtoAttributes(postContainer.getAlpha());
			Attributes beta = new Attributes();
			logger.info("Post message=" + DatatypeConverter.printBase64Binary(message) + ", alpha=" + alpha + ", beta="
					+ beta);
			logger.info("Poster signature=" + checkDLSignature(message, alpha));
			return Transformer.convertAttributesToDTO(beta);
		} catch (TransformException ex) {
			logger.log(Level.SEVERE, null, ex);
			throw new WebApplicationException(Status.INTERNAL_SERVER_ERROR);
		}
	}

	protected static final HashMethod HASH_METHOD = HashMethod.getInstance(
			HashAlgorithm.SHA256,
			ConvertMethod.getInstance(
					BigIntegerToByteArray.getInstance(ByteOrder.BIG_ENDIAN),
					ByteArrayToByteArray.getInstance(false),
					StringToByteArray.getInstance(Charset.forName("UTF-8"))),
			HashMethod.Mode.RECURSIVE);

	List<LocalizedText> texts1;
	List<LocalizedText> texts2;
	List<LocalizedText> texts3;

	DLSignatureSetting sigSetup;
	DLEncryptionSetting encSetup;

	private Post electionDataPost;
	private List<Post> electionDefinitionPosts;

	@PostConstruct
	public void init() {
		try {
			Map<String, Object> properties = new HashMap<String, Object>(2);
			properties.put(JAXBContextProperties.MEDIA_TYPE, "application/json");
			properties.put(JAXBContextProperties.JSON_INCLUDE_ROOT, true);

			JAXBContext jc = JAXBContext.newInstance(new Class[]{MultiElection.class, ElectionDefinition.class},
					properties);
			Marshaller marshallerElectionData = jc.createMarshaller();
			marshallerElectionData.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

//	    GsonBuilder builder = new GsonBuilder();
//	    gson = builder.create();
			//standard g is used as ghat for this exampel => alpha = 1 => g^alpha=g
			sigSetup = new DLSignatureSetting(
					"109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709",
					"161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297",
					"65133683824381501983523684796057614145070427752690897588060462960319251776021");
			//10 is taken as private key, so g^10 = 1048576
			encSetup = new DLEncryptionSetting(
					"127557310857026250526155290716175721659501699151591799276600227376716505297573619294610035498965642711634086243287869889860211239877645998908773071410481719856828493012051757158513651215977686324747806475706581177754781891491034188437985448668758765692160128854525678725065063346126289455727622203325341952627",
					"63778655428513125263077645358087860829750849575795899638300113688358252648786809647305017749482821355817043121643934944930105619938822999454386535705240859928414246506025878579256825607988843162373903237853290588877390945745517094218992724334379382846080064427262839362532531673063144727863811101662670976313",
					"4", "1048576");

			CandidateElection e = createCandidateElection();
			Vote v = createVote();
			PartyElection pe = createPartyElection1();

			List<Election> electionList = new ArrayList<>();
//	    electionList.add(e);
//	    electionList.add(v);
			electionList.add(pe);
			MultiElection me = new MultiElection(electionList);

			StringWriter sw = new StringWriter();
			marshallerElectionData.marshal(me, sw);
			byte[] messageEDa = null;
			try {
				messageEDa = sw.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, null, ex);
			}

			Attributes alphaEDa = new Attributes();
			Attributes betaEDa = new Attributes();
			alphaEDa.add("section", new StringValue("electionid"));
			alphaEDa.add("group", new StringValue("electionData"));
			Element messageElement = this.createMessageElement(messageEDa, alphaEDa, betaEDa);
//	    logger.log(Level.SEVERE, "direct hash: " + messageElement.getHashValue(HASH_METHOD));
			Pair signature = (Pair) this.sign(messageElement);
			String signatureString = signature.getBigInteger().toString(10);//signature.getAt(0).getBigInteger().toString(10) + "," + signature.getAt(1).getBigInteger().toString(10);
			alphaEDa.add("signature", new StringValue(signatureString));
			alphaEDa.add("publickey", new StringValue("ElectionCoordinatorKey"));

			long time = new Date().getTime();
			time = 1000 * (time / 1000);
			betaEDa.add("timestamp", new DateValue(new Date(time)));
			betaEDa.add("rank", new IntegerValue(1));

			messageElement = this.createMessageElement(messageEDa, alphaEDa, betaEDa);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);//signature.getAt(0).getBigInteger().toString(10) + "," + signature.getAt(1).getBigInteger().toString(10);
//	    logger.log(Level.SEVERE, "sig string: "+signatureString);
//	    logger.log(Level.SEVERE, "sig a: "+signature.getAt(0).getBigInteger().toString(10));
//	    logger.log(Level.SEVERE, "sig b: "+signature.getAt(1).getBigInteger().toString(10));
//	    logger.log(Level.SEVERE, "sig string: "+Arrays.deepToString(MathUtil.unpair(new BigInteger(signatureString))));
			betaEDa.add("boardSignature", new StringValue(signatureString));

			electionDataPost = new Post(messageEDa, alphaEDa, betaEDa);

			/**
			 * ***********************************************************************************
			 */
			// Election Definition
			/**
			 * ***********************************************************************************
			 */
			ElectionDefinition ed = new ElectionDefinition(createLocalizedText("Election name 1", "Wahlname 1",
					"Nom de l'élection 1"),
					createLocalizedText("This is an election 1", "Das ist Wahl 1", "Ceci est l'élection 1"), new Date(
							114, 5, 24), new Date(114, 5, 25));
			ElectionDefinition ed2 = new ElectionDefinition(createLocalizedText("Election name 2", "Wahlname 2",
					"Nom de l'élection 2"),
					createLocalizedText("This is an election 2", "Das ist Wahl 2", "Ceci est l'élection 2"), new Date(
							114, 9, 23), new Date(114, 10, 30, 23, 59, 00));
			ElectionDefinition ed3 = new ElectionDefinition(createLocalizedText("Election name 3", "Wahlname 3",
					"Nom de l'élection 3"),
					createLocalizedText("This is an election 3", "Das ist Wahl 3", "Ceci est l'élection 3"), new Date(
							114, 11, 24), new Date(114, 11, 25, 23, 59, 00));

			StringWriter sw1 = new StringWriter();
			StringWriter sw2 = new StringWriter();
			StringWriter sw3 = new StringWriter();
			marshallerElectionData.marshal(ed, sw1);
			marshallerElectionData.marshal(ed2, sw2);
			marshallerElectionData.marshal(ed3, sw3);

			byte[] messageEDe1 = null;
			byte[] messageEDe2 = null;
			byte[] messageEDe3 = null;
			try {
				messageEDe1 = sw1.toString().getBytes("UTF-8");
				messageEDe2 = sw2.toString().getBytes("UTF-8");
				messageEDe3 = sw3.toString().getBytes("UTF-8");
			} catch (UnsupportedEncodingException ex) {
				logger.log(Level.SEVERE, null, ex);
			}

			Attributes alphaEDe1 = new Attributes();
			Attributes betaEDe1 = new Attributes();
			alphaEDe1.add("section", new StringValue("electionid"));
			alphaEDe1.add("group", new StringValue("electionDefinition"));
			messageElement = this.createMessageElement(messageEDe1, alphaEDe1, betaEDe1);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			alphaEDe1.add("signature", new StringValue(signatureString));
			alphaEDe1.add("publickey", new StringValue("ElectionAdministratorKey"));

			time = new Date().getTime();
			time = 1000 * (time / 1000);
			betaEDe1.add("timestamp", new DateValue(new Date(time)));
			betaEDe1.add("rank", new IntegerValue(1));

			messageElement = this.createMessageElement(messageEDe1, alphaEDe1, betaEDe1);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			betaEDe1.add("boardSignature", new StringValue(signatureString));

			Attributes alphaEDe2 = new Attributes();
			Attributes betaEDe2 = new Attributes();
			alphaEDe2.add("section", new StringValue("electionid2"));
			alphaEDe2.add("group", new StringValue("electionDefinition"));
			messageElement = this.createMessageElement(messageEDe2, alphaEDe2, betaEDe2);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			alphaEDe2.add("signature", new StringValue(signatureString));
			alphaEDe2.add("publickey", new StringValue("ElectionAdministratorKey"));

			time = new Date().getTime();
			time = 1000 * (time / 1000);
			betaEDe2.add("timestamp", new DateValue(new Date(time)));
			betaEDe2.add("rank", new IntegerValue(1));

			messageElement = this.createMessageElement(messageEDe2, alphaEDe2, betaEDe2);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			betaEDe2.add("boardSignature", new StringValue(signatureString));

			Attributes alphaEDe3 = new Attributes();
			Attributes betaEDe3 = new Attributes();
			alphaEDe3.add("section", new StringValue("electionid3"));
			alphaEDe3.add("group", new StringValue("electionDefinition"));
			messageElement = this.createMessageElement(messageEDe3, alphaEDe3, betaEDe3);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			alphaEDe3.add("signature", new StringValue(signatureString));
			alphaEDe3.add("publickey", new StringValue("ElectionAdministratorKey"));

			time = new Date().getTime();
			time = 1000 * (time / 1000);
			betaEDe3.add("timestamp", new DateValue(new Date(time)));
			betaEDe3.add("rank", new IntegerValue(1));

			messageElement = this.createMessageElement(messageEDe3, alphaEDe3, betaEDe3);
			signature = (Pair) this.sign(messageElement);
			signatureString = signature.getBigInteger().toString(10);
			betaEDe3.add("boardSignature", new StringValue(signatureString));

			electionDefinitionPosts = new ArrayList<>();
			electionDefinitionPosts.add(new Post(messageEDe1, alphaEDe1, betaEDe1));
			electionDefinitionPosts.add(new Post(messageEDe2, alphaEDe2, betaEDe2));
			electionDefinitionPosts.add(new Post(messageEDe3, alphaEDe3, betaEDe3));

			logger.info("init finished");
		} catch (JAXBException ex) {
			logger.log(Level.SEVERE, null, ex);
		}
	}

	public Element sign(Element message) {
		String pStr
				= "161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297";
		String qStr = "65133683824381501983523684796057614145070427752690897588060462960319251776021";
		String gStr
				= "109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709";

		GStarModPrime g_q = GStarModPrime.getInstance(new BigInteger(pStr), new BigInteger(qStr));
		GStarModElement g = g_q.getElement(new BigInteger(gStr));

		String xStr
				= "51516542789660752564970758874585026766797080570786905454441989426850471029242";
		String yStr
				= "155751513570869228260439384867241770655307495308374243153609793138899116622831741452119851346987003110901710955537759710017986742227890863909207286976163651279893171147044975311392911280219209156950267453786814037865924781368598641596911942039733700968941983269218092942368253177801756957171582556425236939612";
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(message.getSet(), g, HASH_METHOD);
		Element privateKeyElement = schnorr.getSignatureKeySpace().getElement(new BigInteger(xStr));
//	new BigInteger(yStr)), message, schnorr.sign(privateKeyElement, message)));
		return schnorr.sign(privateKeyElement, message);
	}

	protected boolean checkDLSignature(byte[] message, Attributes alpha) {
		String pStr
				= "161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297";
		String qStr = "65133683824381501983523684796057614145070427752690897588060462960319251776021";
		String gStr
				= "109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709";

		GStarModPrime g_q = GStarModPrime.getInstance(new BigInteger(pStr), new BigInteger(qStr));
		GStarModElement g = g_q.getElement(new BigInteger(gStr));

		String yStr = ((StringValue) alpha.getValue("publickey")).getValue();
		//= "155751513570869228260439384867241770655307495308374243153609793138899116622831741452119851346987003110901710955537759710017986742227890863909207286976163651279893171147044975311392911280219209156950267453786814037865924781368598641596911942039733700968941983269218092942368253177801756957171582556425236939612";

		Element messageElement = this.createMessageElement(message, alpha);
		SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(
				messageElement.getSet(), g, HASH_METHOD);
		Element publicKey = schnorr.getVerificationKeySpace()
				.getElement(new BigInteger(yStr));
		System.out.println("alpha " + alpha);
		String signature = ((StringValue) alpha.getValue("signature")).getValue();
		System.out.println("signature: " + signature);
		Element signatureElement = schnorr.getSignatureSpace().getElementFrom(signature);
		return schnorr.verify(publicKey, messageElement, signatureElement).getValue();
	}

//    public boolean verify(Element message, String signature) {
//	String[] sigValues = signature.split(",");
//
//	String pStr
//		= "161931481198080639220214033595931441094586304918402813506510547237223787775475425991443924977419330663170224569788019900180050114468430413908687329871251101280878786588515668012772798298511621634145464600626619548823238185390034868354933050128115662663653841842699535282987363300852550784188180264807606304297";
//	String qStr = "65133683824381501983523684796057614145070427752690897588060462960319251776021";
//	String gStr
//		= "109291242937709414881219423205417309207119127359359243049468707782004862682441897432780127734395596275377218236442035534825283725782836026439537687695084410797228793004739671835061419040912157583607422965551428749149162882960112513332411954585778903685207256083057895070357159920203407651236651002676481874709";
//
//	GStarModPrime g_q = GStarModPrime.getInstance(new BigInteger(pStr), new BigInteger(qStr));
//	GStarModElement g = g_q.getElement(new BigInteger(gStr));
//
//	SchnorrSignatureScheme schnorr = SchnorrSignatureScheme.getInstance(message.getSet(), g, HASH_METHOD);
//
//	String xStr
//		= "51516542789660752564970758874585026766797080570786905454441989426850471029242";
//	String yStr
//		= "155751513570869228260439384867241770655307495308374243153609793138899116622831741452119851346987003110901710955537759710017986742227890863909207286976163651279893171147044975311392911280219209156950267453786814037865924781368598641596911942039733700968941983269218092942368253177801756957171582556425236939612";
//	Element publicKeyElement = schnorr.getVerificationKeySpace().getElement(new BigInteger(yStr));
//
//	Element[] sigValue = new Element[2];
//	sigValue[0] = ZMod.getInstance(new BigInteger(qStr)).getElement(new BigInteger(sigValues[0]));
//	sigValue[1] = ZMod.getInstance(new BigInteger(qStr)).getElement(new BigInteger(sigValues[1]));
//
//	DenseArray<Element> da = DenseArray.getInstance(sigValue[0], sigValue[1]);
//	Element sig = schnorr.getSignatureSpace().getElement(da);
//	logger.log(Level.SEVERE, "UniCrypt verification: "+schnorr.verify(publicKeyElement, message, sig).getValue());
//
//
//	logger.log(Level.SEVERE, "a: "+sigValue[0]);
//	logger.log(Level.SEVERE, "b: "+sigValue[1]);
//	Element c = g.selfApply(sigValue[1]); //Mod q
//	Element d = publicKeyElement.selfApply(sigValue[0]); //mod p
//	logger.log(Level.SEVERE, "c: "+c);
//	logger.log(Level.SEVERE, "d: "+d);
//	Element a2Verif = c.apply(d.invert()); //mod q //TODO invert !!!
//	logger.log(Level.SEVERE, "a2Verif: "+a2Verif);
//	Pair p = Pair.getInstance(message, a2Verif);
//	logger.log(Level.SEVERE, "b verif hash: "+this.bytesToHex(p.getHashValue(HASH_METHOD).getBytes()));
//	BigInteger bVerif = new BigInteger(p.getHashValue(HASH_METHOD).getBytes()).mod(new BigInteger(qStr));//ZMod.getInstance(new BigInteger(qStr)).getElement(new BigInteger(p.getHashValue(HASH_METHOD).getBytes()).mod(new BigInteger(qStr)));
//	Element bVerif2 = ZMod.getInstance(new BigInteger(qStr)).getElement(bVerif);
//	logger.log(Level.SEVERE, "b: "+sigValue[1]);
//	logger.log(Level.SEVERE, "sig: "+sig);
//	logger.log(Level.SEVERE, "bVerif: "+bVerif);
//	logger.log(Level.SEVERE, "bVerif2: "+bVerif2);
//	logger.log(Level.SEVERE, "Is equivalent: "+sigValue[0].isEquivalent(bVerif2));
//
//	return schnorr.verify(publicKeyElement, message, sig).getValue();
//    }
	public String bytesToHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	protected Element createMessageElement(byte[] message, Attributes alpha) {
		StringMonoid stringSpace = StringMonoid.getInstance(Alphabet.PRINTABLE_ASCII);
		Z z = Z.getInstance();
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();
		Element messageElement = byteSpace.getElement(message);
		List<Element> alphaElements = new ArrayList<>();
		//itterate over alpha until one reaches the property = signature
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

	private List<LocalizedText> createLocalizedText(String english, String german, String french) {
		List<LocalizedText> texts = new ArrayList<>();

		if (english != null) {
			texts.add(new LocalizedText(LanguageCode.EN, english));
		}
		if (german != null) {
			texts.add(new LocalizedText(LanguageCode.DE, german));
		}
		if (french != null) {
			texts.add(new LocalizedText(LanguageCode.FR, french));
		}
		return texts;
	}

	private CandidateElection createCandidateElection() {
		//Candidate election
		List<Candidate> candidates = new ArrayList<>();
		candidates.add(new Candidate(1, "Peter", "Farmer", "1", createLocalizedText("Math,Master", "Math,Master",
				"Math,Master")));
		candidates.add(new Candidate(2, "Mike", "Example", "2", createLocalizedText("CS,Master", "CS,Master",
				"CS,Master")));
		candidates.add(new Candidate(3, "John", "Doe", "3", createLocalizedText("Biology,Bachelor",
				"Biologie,Bachelor", "Biologie,Bachelor")));

		List<Rule> rules = new ArrayList<>();
		List<Integer> ruleChoices1 = new ArrayList<>();
		ruleChoices1.add(1);
		ruleChoices1.add(2);
		ruleChoices1.add(3);
		rules.add(new ForAllRule(0, 1, ruleChoices1));

		List<CandidateList> candList = new ArrayList<>();
		List<Integer> choicesIdList = new ArrayList<>();
		choicesIdList.add(1);
		choicesIdList.add(2);
		choicesIdList.add(3);

		texts1 = createLocalizedText("Unique List", "Einzige Liste", "Liste Unique");
		texts2 = createLocalizedText("UL", "EL", "LU");
		candList.add(new CandidateList(texts1, texts2, choicesIdList));

		texts1 = createLocalizedText("Candidate Election", "Kandidatwahl", "Election de candidates");
		texts2 = createLocalizedText("Please elect some candidates.", "Bitte wählen Sie einige Kandidate",
				"Veuillez choisir quelques candidats");
		CandidateElection e = new CandidateElection(texts1, texts2, candidates, new ArrayList<CandidateList>(),
				rules, sigSetup,
				encSetup);
		return e;
	}

	private Vote createVote() {
		//Vote
		List<Option> options = new ArrayList<>();
		texts1 = createLocalizedText("Yes", "Ja", "Oui");
		texts2 = createLocalizedText("No", "Nein", "Non");
		texts3 = createLocalizedText("Blank", "Leer", "Blanc");
		options.add(new Option(4, texts1));
		options.add(new Option(5, texts2));
		options.add(new Option(6, texts3));
		List<Rule> rules2 = new ArrayList<>();
		List<Integer> ruleChoices2 = new ArrayList<>();
		ruleChoices2.add(4);
		ruleChoices2.add(5);
		ruleChoices2.add(6);
		rules2.add(new ForAllRule(0, 1, ruleChoices2));
		texts1 = createLocalizedText("Vote", "Abstimmung", "Vote");
		texts2 = createLocalizedText("Please choose one of the proposed options.",
				"Bitte wählen Sie eine der Optionen.", "Veuillez choisir une des options.");
		texts3 = createLocalizedText("Do you agree with the decision?", "Nehmen Sie die Entscheindung an?",
				"Acceptez-vous la décision?");
		Vote v = new Vote(texts1, texts2, texts3, options, rules2, sigSetup, encSetup);

		return v;
	}

	private PartyElection createPartyElection1() {
		//PartyElection
		List<Choice> choices = new ArrayList<>();
		choices.add(new PartyCandidate(10, "Barack", "Obama", "1", 13, createLocalizedText("Math,Master",
				"Math,Master", "Math,Master")));
		choices.add(new PartyCandidate(11, "Georges W.", "Bush", "2", 13, createLocalizedText("Math,Master",
				"Math,Master", "Math,Master")));
		choices.add(new PartyCandidate(12, "Georges", "Washington", "3", 14, createLocalizedText("Math,Master",
				"Math,Master", "Math,Master")));
		texts1 = createLocalizedText("Presidents of the 20th century", "Presidenten vom 20. Jahrhundert",
				"Président du 20ème siècle");
		choices.add(new Party(13, texts1));
		texts1 = createLocalizedText("Older presidents", "Älteren Presidenten", "Présidents plus anciens");
		choices.add(new Party(14, texts1));

		List<Integer> choicesIdListParty1 = new ArrayList<>();
		choicesIdListParty1.add(10);
		choicesIdListParty1.add(10);
		choicesIdListParty1.add(11);

		List<Integer> choicesIdListParty2 = new ArrayList<>();
		choicesIdListParty2.add(12);

		// !!!! Redundant information: partyId in PartyCandidate and choicesId in PartyList must be the same!!
		List<PartyList> partyLists = new ArrayList<>();
		texts1 = createLocalizedText("List of Presidents of the 20th century",
				"Liste von Presidenten vom 20. Jahrhundert", "Liste de Président du 20ème siècle");
		texts2 = createLocalizedText("pln1en", "pln1de", "pln1fr");
		partyLists.add(new PartyList(texts1, texts2, 13, choicesIdListParty1));
		texts1 = createLocalizedText("List of Older presidents", "Liste von Älteren Presidenten",
				"Liste de Présidents plus anciens");
		texts2 = createLocalizedText("pln2en", "pln2de", "pln2fr");
		partyLists.add(new PartyList(texts1, texts2, 14, choicesIdListParty2));

		List<Rule> rules3 = new ArrayList<>();
		List<Integer> ruleChoices3 = new ArrayList<>();
		ruleChoices3.add(10);
		ruleChoices3.add(11);
		ruleChoices3.add(12);
		rules3.add(new ForAllRule(0, 1, ruleChoices3));

		texts1 = createLocalizedText("Party Election", "Parteiwahl", "Election de partis");
		texts2 = createLocalizedText("Please elect a party and some candidates.",
				"Bitte wählen Sie eine Partei und einige Kandidate.",
				"Veuillez choisir un parti et quelques candidats.");
		PartyElection pe = new PartyElection(texts1, texts2, choices, partyLists, rules3, sigSetup, encSetup);

		return pe;
	}

	private PartyElection createPartyElection2() {
		LocalizedText lt = new LocalizedText();
		lt.setLanguageCode(LanguageCode.FR);
		lt.setText("Master");

		LocalizedText lt_1 = new LocalizedText();
		lt_1.setLanguageCode(LanguageCode.FR);
		lt_1.setText("Bachelor");

		PartyCandidate c1 = new PartyCandidate();
		c1.setCandidateNumber("1.1.1");
		c1.setFirstname("Adrian");
		c1.setLastname("Amstutz");
		c1.setPartyId(4);
		c1.setChoiceId(1);
		LocalizedText lt0 = new LocalizedText();
		lt0.setLanguageCode(LanguageCode.FR);
		lt0.setText("Informatique");
		LocalizedText lt01 = new LocalizedText();
		lt01.setLanguageCode(LanguageCode.EN);
		lt01.setText("Computer Science");
		LocalizedText lt02 = new LocalizedText();
		lt02.setLanguageCode(LanguageCode.DE);
		lt02.setText("Informatik");

		PartyCandidate c2 = new PartyCandidate();
		c2.setCandidateNumber("1.2.1");
		c2.setFirstname("Andreas");
		c2.setLastname("Aebi");
		c2.setPartyId(4);
		c2.setChoiceId(2);
		LocalizedText lt20 = new LocalizedText();
		lt20.setLanguageCode(LanguageCode.FR);
		lt20.setText("Electricité");
		LocalizedText lt21 = new LocalizedText();
		lt21.setLanguageCode(LanguageCode.EN);
		lt21.setText("Electricity");
		LocalizedText lt22 = new LocalizedText();
		lt22.setLanguageCode(LanguageCode.DE);
		lt22.setText("Elecrizität");

		PartyCandidate c3 = new PartyCandidate();
		c3.setCandidateNumber("1.3.1");
		c3.setFirstname("Erich");
		c3.setLastname("von Siebenthal");
		c3.setPartyId(4);
		c3.setChoiceId(3);

		PartyCandidate c4 = new PartyCandidate();
		c4.setCandidateNumber("2.1.1");
		c4.setFirstname("Ursula");
		c4.setLastname("Wyss");
		c4.setPartyId(5);
		c4.setChoiceId(7);

		PartyCandidate c5 = new PartyCandidate();
		c5.setCandidateNumber("2.2.1");
		c5.setFirstname("Evi");
		c5.setLastname("Allemann");
		c5.setPartyId(5);
		c5.setChoiceId(8);

		PartyCandidate c6 = new PartyCandidate();
		c6.setCandidateNumber("2.3.1");
		c6.setFirstname("Margret");
		c6.setLastname("Kiener Nellen");
		c6.setPartyId(5);
		c6.setChoiceId(9);

		/////////
		PartyCandidate c10 = new PartyCandidate();
		c10.setCandidateNumber("1.1.10");
		c10.setFirstname("Adrian10");
		c10.setLastname("Amstutz10");
		c10.setPartyId(4);
		c10.setChoiceId(20);
		PartyCandidate c11 = new PartyCandidate();
		c11.setCandidateNumber("1.1.11");
		c11.setFirstname("Adrian11");
		c11.setLastname("Amstutz11");
		c11.setPartyId(4);
		c11.setChoiceId(21);
		PartyCandidate c12 = new PartyCandidate();
		c12.setCandidateNumber("1.1.12");
		c12.setFirstname("Adrian12");
		c12.setLastname("Amstutz12");
		c12.setPartyId(4);
		c12.setChoiceId(22);
		PartyCandidate c13 = new PartyCandidate();
		c13.setCandidateNumber("1.1.13");
		c13.setFirstname("Adrian13");
		c13.setLastname("Amstutz13");
		c13.setPartyId(4);
		c13.setChoiceId(23);
		PartyCandidate c14 = new PartyCandidate();
		c14.setCandidateNumber("1.1.14");
		c14.setFirstname("Adrian14");
		c14.setLastname("Amstutz14");
		c14.setPartyId(4);
		c14.setChoiceId(24);
		PartyCandidate c15 = new PartyCandidate();
		c15.setCandidateNumber("1.1.15");
		c15.setFirstname("Adrian15");
		c15.setLastname("Amstutz15");
		c15.setPartyId(4);
		c15.setChoiceId(25);
		PartyCandidate c16 = new PartyCandidate();
		c16.setCandidateNumber("1.1.16");
		c16.setFirstname("Adrian16");
		c16.setLastname("Amstutz16");
		c16.setPartyId(4);
		c16.setChoiceId(26);
		PartyCandidate c17 = new PartyCandidate();
		c17.setCandidateNumber("1.1.17");
		c17.setFirstname("Adrian17");
		c17.setLastname("Amstutz17");
		c17.setPartyId(4);
		c17.setChoiceId(27);
		PartyCandidate c18 = new PartyCandidate();
		c18.setCandidateNumber("1.1.18");
		c18.setFirstname("Adrian18");
		c18.setLastname("Amstutz18");
		c18.setPartyId(4);
		c18.setChoiceId(28);
		PartyCandidate c19 = new PartyCandidate();
		c19.setCandidateNumber("1.1.19");
		c19.setFirstname("Adrian19");
		c19.setLastname("Amstutz19");
		c19.setPartyId(4);
		c19.setChoiceId(29);
		PartyCandidate c20 = new PartyCandidate();
		c20.setCandidateNumber("1.1.20");
		c20.setFirstname("Adrian20");
		c20.setLastname("Amstutz20");
		c20.setPartyId(4);
		c20.setChoiceId(30);
		PartyCandidate c21 = new PartyCandidate();
		c21.setCandidateNumber("1.1.21");
		c21.setFirstname("Adrian21");
		c21.setLastname("Amstutz21");
		c21.setPartyId(4);
		c21.setChoiceId(31);
		PartyCandidate c22 = new PartyCandidate();
		c22.setCandidateNumber("1.1.22");
		c22.setFirstname("Adrian22");
		c22.setLastname("Amstutz22");
		c22.setPartyId(4);
		c22.setChoiceId(32);
		PartyCandidate c23 = new PartyCandidate();
		c23.setCandidateNumber("1.1.23");
		c23.setFirstname("Adrian23");
		c23.setLastname("Amstutz23");
		c23.setPartyId(4);
		c23.setChoiceId(33);
		PartyCandidate c24 = new PartyCandidate();
		c24.setCandidateNumber("1.1.24");
		c24.setFirstname("Adrian24");
		c24.setLastname("Amstutz24");
		c24.setPartyId(4);
		c24.setChoiceId(34);
		PartyCandidate c25 = new PartyCandidate();
		c25.setCandidateNumber("1.1.25");
		c25.setFirstname("Adrian25");
		c25.setLastname("Amstutz25");
		c25.setPartyId(4);
		c25.setChoiceId(35);
		PartyCandidate c26 = new PartyCandidate();
		c26.setCandidateNumber("1.1.26");
		c26.setFirstname("Adrian26");
		c26.setLastname("Amstutz26");
		c26.setPartyId(4);
		c26.setChoiceId(36);
		PartyCandidate c27 = new PartyCandidate();
		c27.setCandidateNumber("1.1.27");
		c27.setFirstname("Adrian27");
		c27.setLastname("Amstutz27");
		c27.setPartyId(4);
		c27.setChoiceId(37);
		PartyCandidate c28 = new PartyCandidate();
		c28.setCandidateNumber("1.1.28");
		c28.setFirstname("Adrian28");
		c28.setLastname("Amstutz28");
		c28.setPartyId(4);
		c28.setChoiceId(38);
		PartyCandidate c29 = new PartyCandidate();
		c29.setCandidateNumber("1.1.29");
		c29.setFirstname("Adrian29");
		c29.setLastname("Amstutz29");
		c29.setPartyId(4);
		c29.setChoiceId(39);
		PartyCandidate c30 = new PartyCandidate();
		c30.setCandidateNumber("1.1.30");
		c30.setFirstname("Adrian30");
		c30.setLastname("Amstutz30");
		c30.setPartyId(4);
		c30.setChoiceId(40);
		PartyCandidate c31 = new PartyCandidate();
		c31.setCandidateNumber("1.1.31");
		c31.setFirstname("Adrian31");
		c31.setLastname("Amstutz31");
		c31.setPartyId(4);
		c31.setChoiceId(41);
		PartyCandidate c32 = new PartyCandidate();
		c32.setCandidateNumber("1.1.32");
		c32.setFirstname("Adrian32");
		c32.setLastname("Amstutz32");
		c32.setPartyId(4);
		c32.setChoiceId(42);
		PartyCandidate c33 = new PartyCandidate();
		c33.setCandidateNumber("1.1.33");
		c33.setFirstname("Adrian33");
		c33.setLastname("Amstutz33");
		c33.setPartyId(4);
		c33.setChoiceId(43);
		PartyCandidate c34 = new PartyCandidate();
		c34.setCandidateNumber("1.1.34");
		c34.setFirstname("Adrian34");
		c34.setLastname("Amstutz34");
		c34.setPartyId(4);
		c34.setChoiceId(44);
		PartyCandidate c35 = new PartyCandidate();
		c35.setCandidateNumber("1.1.35");
		c35.setFirstname("Adrian35");
		c35.setLastname("Amstutz35");
		c35.setPartyId(4);
		c35.setChoiceId(45);
		PartyCandidate c36 = new PartyCandidate();
		c36.setCandidateNumber("1.1.36");
		c36.setFirstname("Adrian36");
		c36.setLastname("Amstutz36");
		c36.setPartyId(4);
		c36.setChoiceId(46);
		PartyCandidate c37 = new PartyCandidate();
		c37.setCandidateNumber("1.1.37");
		c37.setFirstname("Adrian37");
		c37.setLastname("Amstutz37");
		c37.setPartyId(4);
		c37.setChoiceId(47);
		PartyCandidate c38 = new PartyCandidate();
		c38.setCandidateNumber("1.1.38");
		c38.setFirstname("Adrian38");
		c38.setLastname("Amstutz38");
		c38.setPartyId(4);
		c38.setChoiceId(48);
		PartyCandidate c39 = new PartyCandidate();
		c39.setCandidateNumber("1.1.39");
		c39.setFirstname("Adrian39");
		c39.setLastname("Amstutz39");
		c39.setPartyId(4);
		c39.setChoiceId(49);
		PartyCandidate c40 = new PartyCandidate();
		c40.setCandidateNumber("1.1.40");
		c40.setFirstname("Adrian40");
		c40.setLastname("Amstutz40");
		c40.setPartyId(4);
		c40.setChoiceId(50);
		PartyCandidate c41 = new PartyCandidate();
		c41.setCandidateNumber("1.1.41");
		c41.setFirstname("Adrian41");
		c41.setLastname("Amstutz41");
		c41.setPartyId(4);
		c41.setChoiceId(51);
		PartyCandidate c42 = new PartyCandidate();
		c42.setCandidateNumber("1.1.42");
		c42.setFirstname("Adrian42");
		c42.setLastname("Amstutz42");
		c42.setPartyId(4);
		c42.setChoiceId(52);
		PartyCandidate c43 = new PartyCandidate();
		c43.setCandidateNumber("1.1.43");
		c43.setFirstname("Adrian43");
		c43.setLastname("Amstutz43");
		c43.setPartyId(4);
		c43.setChoiceId(53);
		PartyCandidate c44 = new PartyCandidate();
		c44.setCandidateNumber("1.1.44");
		c44.setFirstname("Adrian44");
		c44.setLastname("Amstutz44");
		c44.setPartyId(4);
		c44.setChoiceId(54);
		PartyCandidate c45 = new PartyCandidate();
		c45.setCandidateNumber("1.1.45");
		c45.setFirstname("Adrian45");
		c45.setLastname("Amstutz45");
		c45.setPartyId(4);
		c45.setChoiceId(55);
		PartyCandidate c46 = new PartyCandidate();
		c46.setCandidateNumber("1.1.46");
		c46.setFirstname("Adrian46");
		c46.setLastname("Amstutz46");
		c46.setPartyId(4);
		c46.setChoiceId(56);
		PartyCandidate c47 = new PartyCandidate();
		c47.setCandidateNumber("1.1.47");
		c47.setFirstname("Adrian47");
		c47.setLastname("Amstutz47");
		c47.setPartyId(4);
		c47.setChoiceId(57);
		PartyCandidate c48 = new PartyCandidate();
		c48.setCandidateNumber("1.1.48");
		c48.setFirstname("Adrian48");
		c48.setLastname("Amstutz48");
		c48.setPartyId(4);
		c48.setChoiceId(58);
		PartyCandidate c49 = new PartyCandidate();
		c49.setCandidateNumber("1.1.49");
		c49.setFirstname("Adrian49");
		c49.setLastname("Amstutz49");
		c49.setPartyId(4);
		c49.setChoiceId(59);
		PartyCandidate c50 = new PartyCandidate();
		c50.setCandidateNumber("1.1.50");
		c50.setFirstname("Adrian50");
		c50.setLastname("Amstutz50");
		c50.setPartyId(4);
		c50.setChoiceId(60);
		PartyCandidate c51 = new PartyCandidate();
		c51.setCandidateNumber("1.1.51");
		c51.setFirstname("Adrian51");
		c51.setLastname("Amstutz51");
		c51.setPartyId(4);
		c51.setChoiceId(61);

		PartyCandidate c52 = new PartyCandidate();
		c52.setCandidateNumber("1.1.52");
		c52.setFirstname("Adrian52");
		c52.setLastname("Amstutz52");
		c52.setPartyId(4);
		c52.setChoiceId(62);
		PartyCandidate c53 = new PartyCandidate();
		c53.setCandidateNumber("1.1.53");
		c53.setFirstname("Adrian53");
		c53.setLastname("Amstutz53");
		c53.setPartyId(4);
		c53.setChoiceId(63);
		PartyCandidate c54 = new PartyCandidate();
		c54.setCandidateNumber("1.1.54");
		c54.setFirstname("Adrian54");
		c54.setLastname("Amstutz54");
		c54.setPartyId(4);
		c54.setChoiceId(64);
		PartyCandidate c55 = new PartyCandidate();
		c55.setCandidateNumber("1.1.55");
		c55.setFirstname("Adrian55");
		c55.setLastname("Amstutz55");
		c55.setPartyId(4);
		c55.setChoiceId(65);
		PartyCandidate c56 = new PartyCandidate();
		c56.setCandidateNumber("1.1.56");
		c56.setFirstname("Adrian56");
		c56.setLastname("Amstutz56");
		c56.setPartyId(4);
		c56.setChoiceId(66);
		PartyCandidate c57 = new PartyCandidate();
		c57.setCandidateNumber("1.1.57");
		c57.setFirstname("Adrian57");
		c57.setLastname("Amstutz57");
		c57.setPartyId(4);
		c57.setChoiceId(67);
		PartyCandidate c58 = new PartyCandidate();
		c58.setCandidateNumber("1.1.58");
		c58.setFirstname("Adrian58");
		c58.setLastname("Amstutz58");
		c58.setPartyId(4);
		c58.setChoiceId(68);
		PartyCandidate c59 = new PartyCandidate();
		c59.setCandidateNumber("1.1.59");
		c59.setFirstname("Adrian59");
		c59.setLastname("Amstutz59");
		c59.setPartyId(4);
		c59.setChoiceId(69);
		PartyCandidate c60 = new PartyCandidate();
		c60.setCandidateNumber("1.1.60");
		c60.setFirstname("Adrian60");
		c60.setLastname("Amstutz60");
		c60.setPartyId(4);
		c60.setChoiceId(70);
		PartyCandidate c61 = new PartyCandidate();
		c61.setCandidateNumber("1.1.61");
		c61.setFirstname("Adrian61");
		c61.setLastname("Amstutz61");
		c61.setPartyId(4);
		c61.setChoiceId(71);
		PartyCandidate c62 = new PartyCandidate();
		c62.setCandidateNumber("1.1.62");
		c62.setFirstname("Adrian62");
		c62.setLastname("Amstutz62");
		c62.setPartyId(4);
		c62.setChoiceId(72);
		PartyCandidate c63 = new PartyCandidate();
		c63.setCandidateNumber("1.1.63");
		c63.setFirstname("Adrian63");
		c63.setLastname("Amstutz63");
		c63.setPartyId(4);
		c63.setChoiceId(73);
		PartyCandidate c64 = new PartyCandidate();
		c64.setCandidateNumber("1.1.64");
		c64.setFirstname("Adrian64");
		c64.setLastname("Amstutz64");
		c64.setPartyId(4);
		c64.setChoiceId(74);
		PartyCandidate c65 = new PartyCandidate();
		c65.setCandidateNumber("1.1.65");
		c65.setFirstname("Adrian65");
		c65.setLastname("Amstutz65");
		c65.setPartyId(4);
		c65.setChoiceId(75);
		PartyCandidate c66 = new PartyCandidate();
		c66.setCandidateNumber("1.1.66");
		c66.setFirstname("Adrian66");
		c66.setLastname("Amstutz66");
		c66.setPartyId(4);
		c66.setChoiceId(76);
		PartyCandidate c67 = new PartyCandidate();
		c67.setCandidateNumber("1.1.67");
		c67.setFirstname("Adrian67");
		c67.setLastname("Amstutz67");
		c67.setPartyId(4);
		c67.setChoiceId(77);
		PartyCandidate c68 = new PartyCandidate();
		c68.setCandidateNumber("1.1.68");
		c68.setFirstname("Adrian68");
		c68.setLastname("Amstutz68");
		c68.setPartyId(4);
		c68.setChoiceId(78);
		PartyCandidate c69 = new PartyCandidate();
		c69.setCandidateNumber("1.1.69");
		c69.setFirstname("Adrian69");
		c69.setLastname("Amstutz69");
		c69.setPartyId(4);
		c69.setChoiceId(79);
		PartyCandidate c70 = new PartyCandidate();
		c70.setCandidateNumber("1.1.70");
		c70.setFirstname("Adrian70");
		c70.setLastname("Amstutz70");
		c70.setPartyId(4);
		c70.setChoiceId(80);
		PartyCandidate c71 = new PartyCandidate();
		c71.setCandidateNumber("1.1.71");
		c71.setFirstname("Adrian71");
		c71.setLastname("Amstutz71");
		c71.setPartyId(4);
		c71.setChoiceId(81);
		PartyCandidate c72 = new PartyCandidate();
		c72.setCandidateNumber("1.1.72");
		c72.setFirstname("Adrian72");
		c72.setLastname("Amstutz72");
		c72.setPartyId(4);
		c72.setChoiceId(82);
		PartyCandidate c73 = new PartyCandidate();
		c73.setCandidateNumber("1.1.73");
		c73.setFirstname("Adrian73");
		c73.setLastname("Amstutz73");
		c73.setPartyId(4);
		c73.setChoiceId(83);
		PartyCandidate c74 = new PartyCandidate();
		c74.setCandidateNumber("1.1.74");
		c74.setFirstname("Adrian74");
		c74.setLastname("Amstutz74");
		c74.setPartyId(4);
		c74.setChoiceId(84);
		PartyCandidate c75 = new PartyCandidate();
		c75.setCandidateNumber("1.1.75");
		c75.setFirstname("Adrian75");
		c75.setLastname("Amstutz75");
		c75.setPartyId(4);
		c75.setChoiceId(85);
		PartyCandidate c76 = new PartyCandidate();
		c76.setCandidateNumber("1.1.76");
		c76.setFirstname("Adrian76");
		c76.setLastname("Amstutz76");
		c76.setPartyId(4);
		c76.setChoiceId(86);
		PartyCandidate c77 = new PartyCandidate();
		c77.setCandidateNumber("1.1.77");
		c77.setFirstname("Adrian77");
		c77.setLastname("Amstutz77");
		c77.setPartyId(4);
		c77.setChoiceId(87);
		PartyCandidate c78 = new PartyCandidate();
		c78.setCandidateNumber("1.1.78");
		c78.setFirstname("Adrian78");
		c78.setLastname("Amstutz78");
		c78.setPartyId(4);
		c78.setChoiceId(88);
		PartyCandidate c79 = new PartyCandidate();
		c79.setCandidateNumber("1.1.79");
		c79.setFirstname("Adrian79");
		c79.setLastname("Amstutz79");
		c79.setPartyId(4);
		c79.setChoiceId(89);
		PartyCandidate c80 = new PartyCandidate();
		c80.setCandidateNumber("1.1.80");
		c80.setFirstname("Adrian80");
		c80.setLastname("Amstutz80");
		c80.setPartyId(4);
		c80.setChoiceId(90);
		PartyCandidate c81 = new PartyCandidate();
		c81.setCandidateNumber("1.1.81");
		c81.setFirstname("Adrian81");
		c81.setLastname("Amstutz81");
		c81.setPartyId(4);
		c81.setChoiceId(91);
		PartyCandidate c82 = new PartyCandidate();
		c82.setCandidateNumber("1.1.82");
		c82.setFirstname("Adrian82");
		c82.setLastname("Amstutz82");
		c82.setPartyId(4);
		c82.setChoiceId(92);
		PartyCandidate c83 = new PartyCandidate();
		c83.setCandidateNumber("1.1.83");
		c83.setFirstname("Adrian83");
		c83.setLastname("Amstutz83");
		c83.setPartyId(4);
		c83.setChoiceId(93);
		PartyCandidate c84 = new PartyCandidate();
		c84.setCandidateNumber("1.1.84");
		c84.setFirstname("Adrian84");
		c84.setLastname("Amstutz84");
		c84.setPartyId(4);
		c84.setChoiceId(94);
		PartyCandidate c85 = new PartyCandidate();
		c85.setCandidateNumber("1.1.85");
		c85.setFirstname("Adrian85");
		c85.setLastname("Amstutz85");
		c85.setPartyId(4);
		c85.setChoiceId(95);
		PartyCandidate c86 = new PartyCandidate();
		c86.setCandidateNumber("1.1.86");
		c86.setFirstname("Adrian86");
		c86.setLastname("Amstutz86");
		c86.setPartyId(4);
		c86.setChoiceId(96);
		PartyCandidate c87 = new PartyCandidate();
		c87.setCandidateNumber("1.1.87");
		c87.setFirstname("Adrian87");
		c87.setLastname("Amstutz87");
		c87.setPartyId(4);
		c87.setChoiceId(97);
		PartyCandidate c88 = new PartyCandidate();
		c88.setCandidateNumber("1.1.88");
		c88.setFirstname("Adrian88");
		c88.setLastname("Amstutz88");
		c88.setPartyId(4);
		c88.setChoiceId(98);
		PartyCandidate c89 = new PartyCandidate();
		c89.setCandidateNumber("1.1.89");
		c89.setFirstname("Adrian89");
		c89.setLastname("Amstutz89");
		c89.setPartyId(4);
		c89.setChoiceId(99);
		PartyCandidate c90 = new PartyCandidate();
		c90.setCandidateNumber("1.1.90");
		c90.setFirstname("Adrian90");
		c90.setLastname("Amstutz90");
		c90.setPartyId(4);
		c90.setChoiceId(100);
		PartyCandidate c91 = new PartyCandidate();
		c91.setCandidateNumber("1.1.91");
		c91.setFirstname("Adrian91");
		c91.setLastname("Amstutz91");
		c91.setPartyId(4);
		c91.setChoiceId(101);
		PartyCandidate c92 = new PartyCandidate();
		c92.setCandidateNumber("1.1.92");
		c92.setFirstname("Adrian92");
		c92.setLastname("Amstutz92");
		c92.setPartyId(4);
		c92.setChoiceId(102);
		PartyCandidate c93 = new PartyCandidate();
		c93.setCandidateNumber("1.1.93");
		c93.setFirstname("Adrian93");
		c93.setLastname("Amstutz93");
		c93.setPartyId(4);
		c93.setChoiceId(103);

		PartyCandidate c94 = new PartyCandidate();
		c94.setCandidateNumber("1.1.94");
		c94.setFirstname("Adrian94");
		c94.setLastname("Amstutz94");
		c94.setPartyId(4);
		c94.setChoiceId(104);
		PartyCandidate c95 = new PartyCandidate();
		c95.setCandidateNumber("1.1.95");
		c95.setFirstname("Adrian95");
		c95.setLastname("Amstutz95");
		c95.setPartyId(4);
		c95.setChoiceId(105);
		PartyCandidate c96 = new PartyCandidate();
		c96.setCandidateNumber("1.1.96");
		c96.setFirstname("Adrian96");
		c96.setLastname("Amstutz96");
		c96.setPartyId(4);
		c96.setChoiceId(106);
		PartyCandidate c97 = new PartyCandidate();
		c97.setCandidateNumber("1.1.97");
		c97.setFirstname("Adrian97");
		c97.setLastname("Amstutz97");
		c97.setPartyId(4);
		c97.setChoiceId(107);
		PartyCandidate c98 = new PartyCandidate();
		c98.setCandidateNumber("1.1.98");
		c98.setFirstname("Adrian98");
		c98.setLastname("Amstutz98");
		c98.setPartyId(4);
		c98.setChoiceId(108);
		PartyCandidate c99 = new PartyCandidate();
		c99.setCandidateNumber("1.1.99");
		c99.setFirstname("Adrian99");
		c99.setLastname("Amstutz99");
		c99.setPartyId(4);
		c99.setChoiceId(109);
		PartyCandidate c100 = new PartyCandidate();
		c100.setCandidateNumber("1.1.100");
		c100.setFirstname("Adrian100");
		c100.setLastname("Amstutz100");
		c100.setPartyId(4);
		c100.setChoiceId(110);
		PartyCandidate c101 = new PartyCandidate();
		c101.setCandidateNumber("1.1.101");
		c101.setFirstname("Adrian101");
		c101.setLastname("Amstutz101");
		c101.setPartyId(4);
		c101.setChoiceId(111);
		PartyCandidate c102 = new PartyCandidate();
		c102.setCandidateNumber("1.1.102");
		c102.setFirstname("Adrian102");
		c102.setLastname("Amstutz102");
		c102.setPartyId(4);
		c102.setChoiceId(112);
		PartyCandidate c103 = new PartyCandidate();
		c103.setCandidateNumber("1.1.103");
		c103.setFirstname("Adrian103");
		c103.setLastname("Amstutz103");
		c103.setPartyId(4);
		c103.setChoiceId(113);
		PartyCandidate c104 = new PartyCandidate();
		c104.setCandidateNumber("1.1.104");
		c104.setFirstname("Adrian104");
		c104.setLastname("Amstutz104");
		c104.setPartyId(4);
		c104.setChoiceId(114);
		PartyCandidate c105 = new PartyCandidate();
		c105.setCandidateNumber("1.1.105");
		c105.setFirstname("Adrian105");
		c105.setLastname("Amstutz105");
		c105.setPartyId(4);
		c105.setChoiceId(115);
		PartyCandidate c106 = new PartyCandidate();
		c106.setCandidateNumber("1.1.106");
		c106.setFirstname("Adrian106");
		c106.setLastname("Amstutz106");
		c106.setPartyId(4);
		c106.setChoiceId(116);
		PartyCandidate c107 = new PartyCandidate();
		c107.setCandidateNumber("1.1.107");
		c107.setFirstname("Adrian107");
		c107.setLastname("Amstutz107");
		c107.setPartyId(4);
		c107.setChoiceId(117);
		PartyCandidate c108 = new PartyCandidate();
		c108.setCandidateNumber("1.1.108");
		c108.setFirstname("Adrian108");
		c108.setLastname("Amstutz108");
		c108.setPartyId(4);
		c108.setChoiceId(118);
		PartyCandidate c109 = new PartyCandidate();
		c109.setCandidateNumber("1.1.109");
		c109.setFirstname("Adrian109");
		c109.setLastname("Amstutz109");
		c109.setPartyId(4);
		c109.setChoiceId(119);
		PartyCandidate c110 = new PartyCandidate();
		c110.setCandidateNumber("1.1.110");
		c110.setFirstname("Adrian110");
		c110.setLastname("Amstutz110");
		c110.setPartyId(4);
		c110.setChoiceId(120);
		PartyCandidate c111 = new PartyCandidate();
		c111.setCandidateNumber("1.1.111");
		c111.setFirstname("Adrian111");
		c111.setLastname("Amstutz111");
		c111.setPartyId(4);
		c111.setChoiceId(121);
		PartyCandidate c112 = new PartyCandidate();
		c112.setCandidateNumber("1.1.112");
		c112.setFirstname("Adrian112");
		c112.setLastname("Amstutz112");
		c112.setPartyId(4);
		c112.setChoiceId(122);
		PartyCandidate c113 = new PartyCandidate();
		c113.setCandidateNumber("1.1.113");
		c113.setFirstname("Adrian113");
		c113.setLastname("Amstutz113");
		c113.setPartyId(4);
		c113.setChoiceId(123);
		PartyCandidate c114 = new PartyCandidate();
		c114.setCandidateNumber("1.1.114");
		c114.setFirstname("Adrian114");
		c114.setLastname("Amstutz114");
		c114.setPartyId(4);
		c114.setChoiceId(124);
		PartyCandidate c115 = new PartyCandidate();
		c115.setCandidateNumber("1.1.115");
		c115.setFirstname("Adrian115");
		c115.setLastname("Amstutz115");
		c115.setPartyId(4);
		c115.setChoiceId(125);
		PartyCandidate c116 = new PartyCandidate();
		c116.setCandidateNumber("1.1.116");
		c116.setFirstname("Adrian116");
		c116.setLastname("Amstutz116");
		c116.setPartyId(4);
		c116.setChoiceId(126);
		PartyCandidate c117 = new PartyCandidate();
		c117.setCandidateNumber("1.1.117");
		c117.setFirstname("Adrian117");
		c117.setLastname("Amstutz117");
		c117.setPartyId(4);
		c117.setChoiceId(127);
		PartyCandidate c118 = new PartyCandidate();
		c118.setCandidateNumber("1.1.118");
		c118.setFirstname("Adrian118");
		c118.setLastname("Amstutz118");
		c118.setPartyId(4);
		c118.setChoiceId(128);
		PartyCandidate c119 = new PartyCandidate();
		c119.setCandidateNumber("1.1.119");
		c119.setFirstname("Adrian119");
		c119.setLastname("Amstutz119");
		c119.setPartyId(4);
		c119.setChoiceId(129);

		LocalizedText lt1 = new LocalizedText();
		lt1.setLanguageCode(LanguageCode.FR);
		lt1.setText("UDC");

		LocalizedText lt2 = new LocalizedText();
		lt2.setLanguageCode(LanguageCode.FR);
		lt2.setText("Union démocratique du centre");

		LocalizedText lt3 = new LocalizedText();
		lt3.setLanguageCode(LanguageCode.FR);
		lt3.setText("PS");

		LocalizedText lt4 = new LocalizedText();
		lt4.setLanguageCode(LanguageCode.FR);
		lt4.setText("Parti socialiste");

		LocalizedText lt5 = new LocalizedText();
		lt5.setLanguageCode(LanguageCode.FR);
		lt5.setText("PDC");

		LocalizedText lt6 = new LocalizedText();
		lt6.setLanguageCode(LanguageCode.FR);
		lt6.setText("Parti démocrate chrétien");

		Party p1 = new Party();
		p1.setName(Collections.singletonList(lt2));
		p1.setChoiceId(4);

		Party p2 = new Party();
		p2.setName(Collections.singletonList(lt4));
		p2.setChoiceId(5);

		Party p3 = new Party();
		p3.setName(Collections.singletonList(lt6));
		p3.setChoiceId(6);

		lt = new LocalizedText();
		lt.setLanguageCode(LanguageCode.FR);
		lt.setText("Liste UDC");

		PartyList pl1 = new PartyList();
		pl1.setListNumber(Collections.singletonList(new LocalizedText(LanguageCode.FR, "1")));
		pl1.setPartyId(4);
		pl1.setName(Collections.singletonList(lt));

		lt = new LocalizedText();
		lt.setLanguageCode(LanguageCode.FR);
		lt.setText("Liste PS");

		PartyList pl2 = new PartyList();
		pl2.setListNumber(Collections.singletonList(lt3));
		pl2.setPartyId(5);
		pl2.setName(Collections.singletonList(lt));

		lt = new LocalizedText();
		lt.setLanguageCode(LanguageCode.FR);
		lt.setText("Liste PDC");

		PartyList pl3 = new PartyList();
		pl3.setListNumber(Collections.singletonList(lt5));
		pl3.setPartyId(6);
		pl3.setName(Collections.singletonList(lt));

		PartyElection eo = new PartyElection();
		eo.setChoices(new ArrayList<Choice>());

		eo.getChoices().add(c1);
		eo.getChoices().add(c2);
		eo.getChoices().add(c3);
		eo.getChoices().add(p1);
		eo.getChoices().add(p2);
		eo.getChoices().add(p3);
		eo.getChoices().add(c4);
		eo.getChoices().add(c5);
		eo.getChoices().add(c6);

		eo.getChoices().add(c10);
		eo.getChoices().add(c11);
		eo.getChoices().add(c12);
		eo.getChoices().add(c13);
		eo.getChoices().add(c14);
		eo.getChoices().add(c15);
		eo.getChoices().add(c16);
		eo.getChoices().add(c17);
		eo.getChoices().add(c18);
		eo.getChoices().add(c19);
		eo.getChoices().add(c20);
		eo.getChoices().add(c21);
		eo.getChoices().add(c22);
		eo.getChoices().add(c23);
		eo.getChoices().add(c24);
		eo.getChoices().add(c25);
		eo.getChoices().add(c26);
		eo.getChoices().add(c27);
		eo.getChoices().add(c28);
		eo.getChoices().add(c29);
		eo.getChoices().add(c30);
		eo.getChoices().add(c31);
		eo.getChoices().add(c32);
		eo.getChoices().add(c33);
		eo.getChoices().add(c34);
		eo.getChoices().add(c35);
		eo.getChoices().add(c36);
		eo.getChoices().add(c37);
		eo.getChoices().add(c38);
		eo.getChoices().add(c39);
		eo.getChoices().add(c40);
		eo.getChoices().add(c41);
		eo.getChoices().add(c42);
		eo.getChoices().add(c43);
		eo.getChoices().add(c44);
		eo.getChoices().add(c45);
		eo.getChoices().add(c46);
		eo.getChoices().add(c47);
		eo.getChoices().add(c48);
		eo.getChoices().add(c49);
		eo.getChoices().add(c50);
		eo.getChoices().add(c51);
		eo.getChoices().add(c52);
		eo.getChoices().add(c53);
		eo.getChoices().add(c54);
		eo.getChoices().add(c55);
		eo.getChoices().add(c56);
		eo.getChoices().add(c57);
		eo.getChoices().add(c58);
		eo.getChoices().add(c59);
		eo.getChoices().add(c60);
		eo.getChoices().add(c61);
		eo.getChoices().add(c62);
		eo.getChoices().add(c63);
		eo.getChoices().add(c64);
		eo.getChoices().add(c65);
		eo.getChoices().add(c66);
		eo.getChoices().add(c67);
		eo.getChoices().add(c68);
		eo.getChoices().add(c69);
		eo.getChoices().add(c70);
		eo.getChoices().add(c71);
		eo.getChoices().add(c72);
		eo.getChoices().add(c73);
		eo.getChoices().add(c74);
		eo.getChoices().add(c75);
		eo.getChoices().add(c76);
		eo.getChoices().add(c77);
		eo.getChoices().add(c78);
		eo.getChoices().add(c79);
		eo.getChoices().add(c80);
		eo.getChoices().add(c81);
		eo.getChoices().add(c82);
		eo.getChoices().add(c83);
		eo.getChoices().add(c84);
		eo.getChoices().add(c85);
		eo.getChoices().add(c86);
		eo.getChoices().add(c87);
		eo.getChoices().add(c88);
		eo.getChoices().add(c89);
		eo.getChoices().add(c90);
		eo.getChoices().add(c91);
		eo.getChoices().add(c92);
		eo.getChoices().add(c93);
		eo.getChoices().add(c94);
		eo.getChoices().add(c95);
		eo.getChoices().add(c96);
		eo.getChoices().add(c97);
		eo.getChoices().add(c98);
		eo.getChoices().add(c99);
		eo.getChoices().add(c100);
		eo.getChoices().add(c101);
		eo.getChoices().add(c102);
		eo.getChoices().add(c103);
		eo.getChoices().add(c104);
		eo.getChoices().add(c105);
		eo.getChoices().add(c106);
		eo.getChoices().add(c107);
		eo.getChoices().add(c108);
		eo.getChoices().add(c109);
		eo.getChoices().add(c110);
		eo.getChoices().add(c111);
		eo.getChoices().add(c112);
		eo.getChoices().add(c113);
		eo.getChoices().add(c114);
		eo.getChoices().add(c115);
		eo.getChoices().add(c116);
		eo.getChoices().add(c117);
		eo.getChoices().add(c118);
		eo.getChoices().add(c119);

		//list rules
		ForAllRule farule1 = new ForAllRule();
		farule1.setChoiceIds(new ArrayList<Integer>());
		farule1.getChoiceIds().add(4);
		farule1.getChoiceIds().add(5);
		farule1.getChoiceIds().add(6);
		farule1.setLowerBound(0);
		farule1.setUpperBound(1);

		SummationRule srule1 = new SummationRule();
		srule1.setChoiceIds(new ArrayList<Integer>());
		srule1.getChoiceIds().add(4);
		srule1.getChoiceIds().add(5);
		srule1.getChoiceIds().add(6);
		srule1.setLowerBound(0);
		srule1.setUpperBound(1);

		//PartyCandidate rules
		ForAllRule farule2 = new ForAllRule();
		farule2.setChoiceIds(new ArrayList<Integer>());
		farule2.getChoiceIds().add(1);
		farule2.getChoiceIds().add(2);
		farule2.getChoiceIds().add(3);
		farule2.getChoiceIds().add(7);
		farule2.getChoiceIds().add(8);
		farule2.getChoiceIds().add(9);
		farule2.setLowerBound(0);
		farule2.setUpperBound(2);

		/////
		farule2.getChoiceIds().add(10);
		farule2.getChoiceIds().add(11);
		farule2.getChoiceIds().add(12);
		farule2.getChoiceIds().add(13);
		farule2.getChoiceIds().add(14);
		farule2.getChoiceIds().add(15);
		farule2.getChoiceIds().add(16);
		farule2.getChoiceIds().add(17);
		farule2.getChoiceIds().add(18);
		farule2.getChoiceIds().add(19);
		farule2.getChoiceIds().add(20);
		farule2.getChoiceIds().add(21);
		farule2.getChoiceIds().add(22);
		farule2.getChoiceIds().add(23);
		farule2.getChoiceIds().add(24);
		farule2.getChoiceIds().add(25);
		farule2.getChoiceIds().add(26);
		farule2.getChoiceIds().add(27);
		farule2.getChoiceIds().add(28);
		farule2.getChoiceIds().add(29);
		farule2.getChoiceIds().add(30);
		farule2.getChoiceIds().add(31);
		farule2.getChoiceIds().add(32);
		farule2.getChoiceIds().add(33);
		farule2.getChoiceIds().add(34);
		farule2.getChoiceIds().add(35);
		farule2.getChoiceIds().add(36);
		farule2.getChoiceIds().add(37);
		farule2.getChoiceIds().add(38);
		farule2.getChoiceIds().add(39);
		farule2.getChoiceIds().add(40);
		farule2.getChoiceIds().add(41);
		farule2.getChoiceIds().add(42);
		farule2.getChoiceIds().add(43);
		farule2.getChoiceIds().add(44);
		farule2.getChoiceIds().add(45);
		farule2.getChoiceIds().add(46);
		farule2.getChoiceIds().add(47);
		farule2.getChoiceIds().add(48);
		farule2.getChoiceIds().add(49);
		farule2.getChoiceIds().add(50);
		farule2.getChoiceIds().add(51);
		farule2.getChoiceIds().add(52);
		farule2.getChoiceIds().add(53);
		farule2.getChoiceIds().add(54);
		farule2.getChoiceIds().add(55);
		farule2.getChoiceIds().add(56);
		farule2.getChoiceIds().add(57);
		farule2.getChoiceIds().add(58);
		farule2.getChoiceIds().add(59);
		farule2.getChoiceIds().add(60);
		farule2.getChoiceIds().add(61);
		farule2.getChoiceIds().add(62);
		farule2.getChoiceIds().add(63);
		farule2.getChoiceIds().add(64);
		farule2.getChoiceIds().add(65);
		farule2.getChoiceIds().add(66);
		farule2.getChoiceIds().add(67);
		farule2.getChoiceIds().add(68);
		farule2.getChoiceIds().add(69);
		farule2.getChoiceIds().add(70);
		farule2.getChoiceIds().add(71);
		farule2.getChoiceIds().add(72);
		farule2.getChoiceIds().add(73);
		farule2.getChoiceIds().add(74);
		farule2.getChoiceIds().add(75);
		farule2.getChoiceIds().add(76);
		farule2.getChoiceIds().add(77);
		farule2.getChoiceIds().add(78);
		farule2.getChoiceIds().add(79);
		farule2.getChoiceIds().add(80);
		farule2.getChoiceIds().add(81);
		farule2.getChoiceIds().add(82);
		farule2.getChoiceIds().add(83);
		farule2.getChoiceIds().add(84);
		farule2.getChoiceIds().add(85);
		farule2.getChoiceIds().add(86);
		farule2.getChoiceIds().add(87);
		farule2.getChoiceIds().add(88);
		farule2.getChoiceIds().add(89);
		farule2.getChoiceIds().add(90);
		farule2.getChoiceIds().add(91);
		farule2.getChoiceIds().add(92);
		farule2.getChoiceIds().add(93);
		farule2.getChoiceIds().add(94);
		farule2.getChoiceIds().add(95);
		farule2.getChoiceIds().add(96);
		farule2.getChoiceIds().add(97);
		farule2.getChoiceIds().add(98);
		farule2.getChoiceIds().add(99);
		farule2.getChoiceIds().add(100);
		farule2.getChoiceIds().add(101);
		farule2.getChoiceIds().add(102);
		farule2.getChoiceIds().add(103);
		farule2.getChoiceIds().add(104);
		farule2.getChoiceIds().add(105);
		farule2.getChoiceIds().add(106);
		farule2.getChoiceIds().add(107);
		farule2.getChoiceIds().add(108);
		farule2.getChoiceIds().add(109);
		farule2.getChoiceIds().add(110);
		farule2.getChoiceIds().add(111);
		farule2.getChoiceIds().add(112);
		farule2.getChoiceIds().add(113);
		farule2.getChoiceIds().add(114);
		farule2.getChoiceIds().add(115);
		farule2.getChoiceIds().add(116);
		farule2.getChoiceIds().add(117);
		farule2.getChoiceIds().add(118);
		farule2.getChoiceIds().add(119);
		farule2.getChoiceIds().add(120);
		farule2.getChoiceIds().add(121);
		farule2.getChoiceIds().add(122);
		farule2.getChoiceIds().add(123);
		farule2.getChoiceIds().add(124);
		farule2.getChoiceIds().add(125);
		farule2.getChoiceIds().add(126);
		farule2.getChoiceIds().add(127);
		farule2.getChoiceIds().add(128);
		farule2.getChoiceIds().add(129);

		SummationRule srule2 = new SummationRule();
		srule2.setChoiceIds(new ArrayList<Integer>());
		srule2.getChoiceIds().add(1);
		srule2.getChoiceIds().add(2);
		srule2.getChoiceIds().add(3);
		srule2.getChoiceIds().add(7);
		srule2.getChoiceIds().add(8);
		srule2.getChoiceIds().add(9);
		srule2.setLowerBound(0);
		srule2.setUpperBound(100);

		////
		srule2.getChoiceIds().add(10);
		srule2.getChoiceIds().add(11);
		srule2.getChoiceIds().add(12);
		srule2.getChoiceIds().add(13);
		srule2.getChoiceIds().add(14);
		srule2.getChoiceIds().add(15);
		srule2.getChoiceIds().add(16);
		srule2.getChoiceIds().add(17);
		srule2.getChoiceIds().add(18);
		srule2.getChoiceIds().add(19);
		srule2.getChoiceIds().add(20);
		srule2.getChoiceIds().add(21);
		srule2.getChoiceIds().add(22);
		srule2.getChoiceIds().add(23);
		srule2.getChoiceIds().add(24);
		srule2.getChoiceIds().add(25);
		srule2.getChoiceIds().add(26);
		srule2.getChoiceIds().add(27);
		srule2.getChoiceIds().add(28);
		srule2.getChoiceIds().add(29);
		srule2.getChoiceIds().add(30);
		srule2.getChoiceIds().add(31);
		srule2.getChoiceIds().add(32);
		srule2.getChoiceIds().add(33);
		srule2.getChoiceIds().add(34);
		srule2.getChoiceIds().add(35);
		srule2.getChoiceIds().add(36);
		srule2.getChoiceIds().add(37);
		srule2.getChoiceIds().add(38);
		srule2.getChoiceIds().add(39);
		srule2.getChoiceIds().add(40);
		srule2.getChoiceIds().add(41);
		srule2.getChoiceIds().add(42);
		srule2.getChoiceIds().add(43);
		srule2.getChoiceIds().add(44);
		srule2.getChoiceIds().add(45);
		srule2.getChoiceIds().add(46);
		srule2.getChoiceIds().add(47);
		srule2.getChoiceIds().add(48);
		srule2.getChoiceIds().add(49);
		srule2.getChoiceIds().add(50);
		srule2.getChoiceIds().add(51);
		srule2.getChoiceIds().add(52);
		srule2.getChoiceIds().add(53);
		srule2.getChoiceIds().add(54);
		srule2.getChoiceIds().add(55);
		srule2.getChoiceIds().add(56);
		srule2.getChoiceIds().add(57);
		srule2.getChoiceIds().add(58);
		srule2.getChoiceIds().add(59);
		srule2.getChoiceIds().add(60);
		srule2.getChoiceIds().add(61);
		srule2.getChoiceIds().add(62);
		srule2.getChoiceIds().add(63);
		srule2.getChoiceIds().add(64);
		srule2.getChoiceIds().add(65);
		srule2.getChoiceIds().add(66);
		srule2.getChoiceIds().add(67);
		srule2.getChoiceIds().add(68);
		srule2.getChoiceIds().add(69);
		srule2.getChoiceIds().add(70);
		srule2.getChoiceIds().add(71);
		srule2.getChoiceIds().add(72);
		srule2.getChoiceIds().add(73);
		srule2.getChoiceIds().add(74);
		srule2.getChoiceIds().add(75);
		srule2.getChoiceIds().add(76);
		srule2.getChoiceIds().add(77);
		srule2.getChoiceIds().add(78);
		srule2.getChoiceIds().add(79);
		srule2.getChoiceIds().add(80);
		srule2.getChoiceIds().add(81);
		srule2.getChoiceIds().add(82);
		srule2.getChoiceIds().add(83);
		srule2.getChoiceIds().add(84);
		srule2.getChoiceIds().add(85);
		srule2.getChoiceIds().add(86);
		srule2.getChoiceIds().add(87);
		srule2.getChoiceIds().add(88);
		srule2.getChoiceIds().add(89);
		srule2.getChoiceIds().add(90);
		srule2.getChoiceIds().add(91);
		srule2.getChoiceIds().add(92);
		srule2.getChoiceIds().add(93);
		srule2.getChoiceIds().add(94);
		srule2.getChoiceIds().add(95);
		srule2.getChoiceIds().add(96);
		srule2.getChoiceIds().add(97);
		srule2.getChoiceIds().add(98);
		srule2.getChoiceIds().add(99);
		srule2.getChoiceIds().add(100);
		srule2.getChoiceIds().add(101);
		srule2.getChoiceIds().add(102);
		srule2.getChoiceIds().add(103);
		srule2.getChoiceIds().add(104);
		srule2.getChoiceIds().add(105);
		srule2.getChoiceIds().add(106);
		srule2.getChoiceIds().add(107);
		srule2.getChoiceIds().add(108);
		srule2.getChoiceIds().add(109);
		srule2.getChoiceIds().add(110);
		srule2.getChoiceIds().add(111);
		srule2.getChoiceIds().add(112);
		srule2.getChoiceIds().add(113);
		srule2.getChoiceIds().add(114);
		srule2.getChoiceIds().add(115);
		srule2.getChoiceIds().add(116);
		srule2.getChoiceIds().add(117);
		srule2.getChoiceIds().add(118);
		srule2.getChoiceIds().add(119);
		srule2.getChoiceIds().add(120);
		srule2.getChoiceIds().add(121);
		srule2.getChoiceIds().add(122);
		srule2.getChoiceIds().add(123);
		srule2.getChoiceIds().add(124);
		srule2.getChoiceIds().add(125);
		srule2.getChoiceIds().add(126);
		srule2.getChoiceIds().add(127);
		srule2.getChoiceIds().add(128);
		srule2.getChoiceIds().add(129);

		eo.setRules(new ArrayList<Rule>());
		eo.getRules().add(farule1);
		eo.getRules().add(srule1);
		eo.getRules().add(farule2);
		eo.getRules().add(srule2);

		List<PartyList> lists = new ArrayList<>();
		lists.add(pl1);
		lists.add(pl2);
		lists.add(pl3);
		eo.setPartyLists(lists);
		eo.setEncryptionSetting(encSetup);
		eo.setSignatureSetting(sigSetup);
		attributeCandidateToLists(eo.getChoices(), eo.getPartyLists());

		texts1 = createLocalizedText("Party Election", "Parteiwahl", "Election de partis");
		texts2 = createLocalizedText("Please elect a party and some candidates.",
				"Bitte wählen Sie eine Partei und einige Kandidate.",
				"Veuillez choisir un parti et quelques candidats.");

		eo.setTitle(texts1);
		eo.setDescription(texts2);
		return eo;
	}

	private void attributeCandidateToLists(List<Choice> choices, List<PartyList> partyLists) {
		for (PartyList pl : partyLists) {
			pl.setChoicesIds(new ArrayList<Integer>());
		}
		for (Choice c : choices) {
			if (c instanceof PartyCandidate) {
				PartyCandidate pc = (PartyCandidate) c;
				if (pc.getPartyId() == 4) {
					partyLists.get(0).getChoicesIds().add(pc.getChoiceId());
				} else if (pc.getPartyId() == 5) {
					partyLists.get(1).getChoicesIds().add(pc.getChoiceId());
				} else if (pc.getPartyId() == 6) {
					partyLists.get(2).getChoicesIds().add(pc.getChoiceId());
				}
			}
		}

	}
}
