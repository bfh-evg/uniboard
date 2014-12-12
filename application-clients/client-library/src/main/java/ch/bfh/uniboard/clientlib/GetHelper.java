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
import ch.bfh.uniboard.data.AttributesDTO.AttributeDTO;
import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.StringValueDTO;
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

    private String posterPublicKey;

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
     * @throws PostException exception thrown in case of keys error or connection error to UniBoard
     */
    public GetHelper(PublicKey boardPublicKey, String boardWSDLLocation, String boardEndpointURL) throws PostException {

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
	    UniBoardService_Service mixingService = new UniBoardService_Service(wsdlLocation, qname);
	    board = mixingService.getUniBoardServicePort();
	    BindingProvider bp = (BindingProvider) board;
	    bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, boardEndpointURL);
	} catch (Exception ex) {
	    throw new PostException("Unable to connect to UniBoard service: " + boardEndpointURL + ", exception: " + ex);
	}
    }

    public ResultContainerDTO get(QueryDTO query) throws GetException, SignatureException {
	ResultContainerDTO rc = board.get(query);

	AttributeDTO attr = AttributeHelper.searchAttribute(rc.getGamma(), UniBoardAttributesName.BOARD_SIGNATURE.
		getName());

	if (attr == null) {
	    logger.log(Level.SEVERE, "Error on getting: no board signature found");
	    throw new GetException("Error on getting: no board signature found");
	}

	String boardSig = ((StringValueDTO) attr.getValue()).getValue();
	if (!this.signatureVerificatorHelper.verify(query, rc, new BigInteger(boardSig, 10))) {
	    logger.log(Level.SEVERE, "Error on getting: UniBoard signature is wrong");
	    throw new GetException("Error on getting: UniBoard signature is wrong");
	} else {
	    for (PostDTO p : rc.getResult().getPost()) {
		attr = AttributeHelper.searchAttribute(p.getBeta(), UniBoardAttributesName.BOARD_SIGNATURE.getName());

		if (attr == null) {
		    logger.log(Level.SEVERE, "Error on getting: no board signature found");
		    throw new GetException("Error on getting: no board signature found");
		}
		
		boardSig = ((StringValueDTO) attr.getValue()).getValue();
		if (!this.signatureVerificatorHelper.verify(p.getMessage(), p.getAlpha(), p.getBeta(), new BigInteger(boardSig,10))) {
		    logger.log(Level.SEVERE, "Error on getting: UniBoard signature is wrong");
		    throw new GetException("Error on getting: UniBoard signature is wrong");
		}
	    }
	    return rc;
	}

    }

}