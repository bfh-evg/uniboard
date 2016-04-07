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
import ch.bfh.uniboard.data.AttributeDTO;
import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import java.math.BigInteger;
import java.net.URL;
import java.security.PublicKey;
import java.security.interfaces.DSAPublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 * Class encapsulating code for get request on UniBoard This class is designed for the single instance of UniBoard
 *
 * @author Philémon von Bergen
 */
public class GetHelper {

	private UniBoardService board;
	private SignatureHelper signatureVerificatorHelper;

	private static final Logger logger = Logger.getLogger(GetHelper.class.getName());

	/**
	 * Create a helper object allowing to send get request on UniBoard
	 *
	 * @param boardPublicKey public key of UniBoard. This key is needed for verifying the signature generated by the
	 * board after successful get request
	 * @param boardWSDLLocation location of WSDL file of UniBoard webservice
	 * @param boardEndpointURL location of UniBoard webservice endpoint
	 * @throws GetException exception thrown in case of keys error or connection error to UniBoard
	 */
	public GetHelper(PublicKey boardPublicKey, String boardWSDLLocation, String boardEndpointURL) throws GetException {

		//Board signature helper
		if (boardPublicKey instanceof RSAPublicKey) {
			this.signatureVerificatorHelper = new RSASignatureHelper((RSAPublicKey) boardPublicKey);
		} else if (boardPublicKey instanceof DSAPublicKey) {
			this.signatureVerificatorHelper = new SchnorrSignatureHelper((DSAPublicKey) boardPublicKey);
		} else {
			throw new GetException("Unsupported key type");
		}

		try {
			URL wsdlLocation = new URL(boardWSDLLocation);
			QName qname = new QName("http://uniboard.bfh.ch/", "UniBoardService");
			UniBoardService_Service ubService = new UniBoardService_Service(wsdlLocation, qname);
			board = ubService.getUniBoardServicePort();
			BindingProvider bp = (BindingProvider) board;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, boardEndpointURL);
		} catch (Exception ex) {
			throw new GetException("Unable to connect to UniBoard service: " + boardEndpointURL + ", exception: " + ex);
		}
	}

	/**
	 * Execute a query on UniBoard
	 *
	 * @param query the query to execute
	 * @return the result container containing the result and the gamma attributes
	 * @throws SignatureException thrown when an error occurred during signature validation
	 */
	public ResultContainerDTO get(QueryDTO query) throws SignatureException {
		ResultContainerDTO rc = board.get(query);

		AttributeDTO attr = AttributeHelper.searchAttribute(rc.getGamma(), UniBoardAttributesName.BOARD_SIGNATURE.
				getName());

		if (attr == null) {
			logger.log(Level.SEVERE, "No board signature found.");
			throw new SignatureException("No board signature found.");
		}

		String boardSig = attr.getValue();
		if (!this.signatureVerificatorHelper.verify(query, rc, new BigInteger(boardSig, 10))) {
			throw new SignatureException("UniBoard signature is invalid.");
		} else {
			for (PostDTO p : rc.getResult()) {
				attr = AttributeHelper.searchAttribute(p.getBeta(), UniBoardAttributesName.BOARD_SIGNATURE.getName());

				if (attr == null) {
					throw new SignatureException("No board signature found.");
				}

				boardSig = attr.getValue();
				if (!this.signatureVerificatorHelper.verify(p.getMessage(), p.getAlpha(), p.getBeta(), new BigInteger(
						boardSig, 10))) {
					throw new SignatureException("UniBoard signature is invalid for post.");
				}
			}
			return rc;
		}

	}

	/**
	 * Helper method to verify poster's signature of a post
	 *
	 * @param post the post to verify
	 * @param posterPublicKey the public key of the poster
	 * @return true if signature is valid, false otherwise
	 * @throws SignatureException if an error occurred during signature validation
	 */
	public boolean verifyPosterSignature(PostDTO post, PublicKey posterPublicKey) throws SignatureException {

		SignatureHelper sigHelper;
		if (posterPublicKey instanceof RSAPublicKey) {
			sigHelper = new RSASignatureHelper((RSAPublicKey) posterPublicKey);
		} else if (posterPublicKey instanceof DSAPublicKey) {
			sigHelper = new SchnorrSignatureHelper((DSAPublicKey) posterPublicKey);
		} else {
			throw new SignatureException("Unsupported key type.");
		}

		AttributeDTO attr
				= AttributeHelper.searchAttribute(post.getAlpha(), UniBoardAttributesName.SIGNATURE.getName());
		if (attr == null) {
			throw new SignatureException("No poster signature found.");
		}

		String posterSig = attr.getValue();
		return sigHelper.verify(post.getMessage(), post.getAlpha(), new BigInteger(posterSig, 10));
	}

}
