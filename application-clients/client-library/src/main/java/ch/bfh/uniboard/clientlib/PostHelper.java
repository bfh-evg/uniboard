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
package ch.bfh.uniboard.clientlib;

import ch.bfh.uniboard.UniBoardService;
import ch.bfh.uniboard.UniBoardService_Service;
import ch.bfh.uniboard.clientlib.signaturehelper.RSASignatureHelper;
import ch.bfh.uniboard.clientlib.signaturehelper.SchnorrSignatureHelper;
import ch.bfh.uniboard.clientlib.signaturehelper.SignatureException;
import ch.bfh.uniboard.clientlib.signaturehelper.SignatureHelper;
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.AttributeDTO;
import ch.bfh.uniboard.data.DataTypeDTO;
import ch.bfh.unicrypt.helper.math.MathUtil;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URL;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.DSAPrivateKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * Class encapsulating code for posting on UniBoard This class is designed for the single instance of UniBoard
 *
 * @author Philémon von Bergen
 */
public class PostHelper {

	private String posterPublicKey;

	private UniBoardService board;
	private SignatureHelper signatureCreatorHelper;
	private SignatureHelper signatureVerificatorHelper;

	private static final Logger logger = Logger.getLogger(PostHelper.class.getName());

	/**
	 * Create a helper object allowing to post on UniBoard
	 *
	 * @param posterPublicKey public key of poster. This key is added in the user attributes when posting
	 * @param posterPrivateKey private key of poster. This key is needed to generate the signature required for posting
	 * on the board
	 * @param boardPublicKey public key of UniBoard. This key is needed for verifying the signature generated by the
	 * board after successful posting
	 * @param boardWSDLLocation location of WSDL file of UniBoard webservice
	 * @param boardEndpointURL location of UniBoard webservice endpoint
	 * @throws PostException exception thrown in case of keys error or connection error to UniBoard
	 */
	public PostHelper(PublicKey posterPublicKey, PrivateKey posterPrivateKey,
			PublicKey boardPublicKey, String boardWSDLLocation, String boardEndpointURL) throws PostException {

		//Poster signature helper
		if (posterPrivateKey instanceof RSAPrivateCrtKey) {
			if (!(posterPublicKey instanceof RSAPublicKey)) {
				throw new PostException("Incompatible private and public key");
			}

			RSAPublicKey publicKey = (RSAPublicKey) posterPublicKey;
			this.posterPublicKey = MathUtil.pair(publicKey.getPublicExponent(), publicKey.getModulus()).toString(10);
			this.signatureCreatorHelper = new RSASignatureHelper((RSAPrivateCrtKey) posterPrivateKey);
		} else if (posterPrivateKey instanceof DSAPrivateKey) {
			if (!(posterPublicKey instanceof DSAPublicKey)) {
				throw new PostException("Incompatible private and public key");
			}
			this.posterPublicKey = ((DSAPublicKey) posterPublicKey).getY().toString(10);
			this.signatureCreatorHelper = new SchnorrSignatureHelper((DSAPrivateKey) posterPrivateKey);
		} else {
			throw new PostException("Unsupported key type");
		}

		//Board signature helper
		if (boardPublicKey instanceof RSAPublicKey) {
			this.signatureVerificatorHelper = new RSASignatureHelper((RSAPublicKey) boardPublicKey);
		} else if (boardPublicKey instanceof DSAPublicKey) {
			this.signatureVerificatorHelper = new SchnorrSignatureHelper((DSAPublicKey) boardPublicKey);
		} else {
			throw new PostException("Unsupported key type");
		}

		try {
			URL wsdlLocation = new URL(boardWSDLLocation);
			QName qname = new QName("http://uniboard.bfh.ch/", "UniBoardService");
			UniBoardService_Service ubService = new UniBoardService_Service(wsdlLocation, qname);
			board = ubService.getUniBoardServicePort();
			BindingProvider bp = (BindingProvider) board;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, boardEndpointURL);
		} catch (Exception ex) {
			throw new PostException("Unable to connect to UniBoard service: " + boardEndpointURL + ", exception: "
					+ ex);
		}
	}

	/**
	 * Method allowing to post a message on UniBoard
	 *
	 * @param message the message to post as string
	 * @param section the section where to post the message
	 * @param group the group where to post the message
	 * @return true if post was successful, false if signature of board was wrong
	 * @throws PostException exception throw when error occured during posting
	 * @throws SignatureException exception throw when error occured during generating of poster signature or verifying
	 * board signature
	 */
	public List<AttributeDTO> post(String message, String section, String group) throws PostException,
			SignatureException, UnsupportedEncodingException, BoardErrorException {
		return this.post(message.getBytes("UTF-8"), section, group);
	}

	/**
	 * Method allowing to post a message on UniBoard
	 *
	 * @param message the message to post as byte array
	 * @param section the section where to post the message
	 * @param group the group where to post the message
	 * @return true if post was successful, false if signature of board was wrong
	 * @throws PostException exception throw when error occured during posting
	 * @throws SignatureException exception throw when error occured during generating of poster signature or verifying
	 * board signature
	 * @throws ch.bfh.uniboard.clientlib.BoardErrorException
	 */
	public List<AttributeDTO> post(byte[] message, String section, String group) throws PostException,
			SignatureException, BoardErrorException {
		List<AttributeDTO> alpha = new ArrayList();
		alpha.add(
				new AttributeDTO(UniBoardAttributesName.SECTION.getName(), section, DataTypeDTO.STRING));
		alpha.add(new AttributeDTO(UniBoardAttributesName.GROUP.getName(), group, DataTypeDTO.STRING));

		BigInteger signature = this.signatureCreatorHelper.sign(message, alpha);

		alpha.add(new AttributeDTO(UniBoardAttributesName.SIGNATURE.getName(),
				signature.toString(10), DataTypeDTO.STRING));
		alpha.add(new AttributeDTO(UniBoardAttributesName.PUBLIC_KEY.getName(),
				posterPublicKey, DataTypeDTO.STRING));

		AttributesDTO alphaTmp = new AttributesDTO(alpha);
		AttributesDTO betaTmp = board.post(message, alphaTmp);
		List<AttributeDTO> beta = betaTmp.getAttribute();
		if (beta.get(0).getKey().contains("rejected") || beta.get(0).getKey().contains(
				"error")) {
			String errorKey = beta.get(0).getKey();
			String error = beta.get(0).getValue();
			logger.log(Level.SEVERE, "UniBoard response was {0}, description: {1}", new Object[]{
				errorKey, error});
			throw new BoardErrorException(error);
		} else {
			AttributeDTO attr = AttributeHelper.searchAttribute(beta, UniBoardAttributesName.BOARD_SIGNATURE.getName());
			if (attr == null) {
				logger.log(Level.SEVERE, "No board signature found");
				throw new PostException("No board signature found");
			} else {
				String boardSig = attr.getValue();
				if (!this.signatureVerificatorHelper.verify(message, alpha, beta, new BigInteger(boardSig, 10))) {
					logger.log(Level.SEVERE, "UniBoard signature is invalid");
					throw new PostException("UniBoard signature is invalid");
				} else {
					return beta;
				}
			}

		}
	}

}
