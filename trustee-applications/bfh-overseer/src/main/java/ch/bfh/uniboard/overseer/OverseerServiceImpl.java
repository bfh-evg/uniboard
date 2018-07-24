/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.overseer;

import ch.bfh.uniboard.data.AttributeDTO;
import ch.bfh.uniboard.data.PostDTO;
import static ch.bfh.uniboard.overseer.SigningHelper.stringSpace;
import ch.bfh.uniboard.service.configuration.ConfigurationManager;
import ch.bfh.unicrypt.helper.array.classes.DenseArray;
import ch.bfh.unicrypt.math.algebra.concatenative.classes.ByteArrayMonoid;
import ch.bfh.unicrypt.math.algebra.general.classes.Pair;
import ch.bfh.unicrypt.math.algebra.general.classes.Tuple;
import ch.bfh.unicrypt.math.algebra.general.interfaces.Element;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.ejb.EJB;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class OverseerServiceImpl implements OverseerService {

	private static final Logger logger = Logger.getLogger(OverseerServiceImpl.class.getName());

	private String currentHash;

	@EJB
	ConfigurationManager configurationManager;

	@EJB
	SignatureService signatureService;

	@Override
	public Signature confirmPost(PostDTO post, String previousHash, List<Signature> previousSignatures) {
		if (!previousHash.equals(currentHash)) {
			//TODO check signatures

			this.currentHash = previousHash;
		}
		Element postElement = this.createMessageElement(post.getMessage(), post.getAlpha(), post.getBeta());
		Element hashElement = stringSpace.getElement(previousHash);
		Element toSign = Pair.getInstance(postElement, hashElement);
		String signatureString = this.signatureService.sign(toSign).toString(10);
		Signature signature = new Signature(signatureString, this.signatureService.getPublicKey());
		return signature;
	}

	@Override
	public Signature confirmCompleteness(String hash, String section, String timestamp) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	protected Element createMessageElement(byte[] message, List<AttributeDTO> alpha, List<AttributeDTO> beta) {
		ByteArrayMonoid byteSpace = ByteArrayMonoid.getInstance();

		Element messageElement = byteSpace.getElement(message);

		List<Element> alphaElements = new ArrayList<>();
		for (AttributeDTO e : alpha) {
			Element element = this.createAttributeElement(e);
			if (element != null) {
				alphaElements.add(element);

			}
		}
		DenseArray alphaDenseElements = DenseArray.getInstance(alphaElements);
		Element alphaElement = Tuple.getInstance(alphaDenseElements);

		List<Element> betaElements = new ArrayList<>();
		for (AttributeDTO e : beta) {
			Element element = this.createAttributeElement(e);
			if (element != null) {
				betaElements.add(element);
			}
		}
		DenseArray beteDenseElements = DenseArray.getInstance(betaElements);
		Element betaElement = Tuple.getInstance(beteDenseElements);

		return Tuple.getInstance(messageElement, alphaElement, betaElement);
	}

	protected Element createAttributeElement(AttributeDTO attribute) {
		List<Element> attributeElements = new ArrayList<>();
		attributeElements.add(stringSpace.getElement(attribute.getKey()));
		attributeElements.add(stringSpace.getElement(attribute.getValue()));
		if (attribute.getDataType() != null) {
			attributeElements.add(stringSpace.getElement(attribute.getDataType().value()));
		}

		DenseArray attributeDenseElements = DenseArray.getInstance(attributeElements);
		return Tuple.getInstance(attributeDenseElements);
	}

}
