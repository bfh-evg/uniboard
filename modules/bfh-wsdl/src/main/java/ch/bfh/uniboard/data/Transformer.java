/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project Univote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.data;

import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.data.LessEqual;
import ch.bfh.uniboard.service.data.Post;
import ch.bfh.uniboard.service.data.Less;
import ch.bfh.uniboard.service.data.PropertyIdentifier;
import ch.bfh.uniboard.service.data.NotEqual;
import ch.bfh.uniboard.service.data.ResultContainer;
import ch.bfh.uniboard.service.data.MessageIdentifier;
import ch.bfh.uniboard.service.data.GreaterEqual;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.PropertyIdentifierType;
import ch.bfh.uniboard.service.data.In;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Greater;
import ch.bfh.uniboard.service.data.Identifier;
import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.data.Order;
import ch.bfh.uniboard.service.data.Between;
import ch.bfh.uniboard.service.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Transformer {

	protected static final Logger logger = Logger.getLogger(Transformer.class.getName());

	static public AttributesDTO convertAttributesToDTO(Attributes attributes) throws TransformException {

		AttributesDTO aDTO = new AttributesDTO();
		for (Map.Entry<String, String> e : attributes.getEntries()) {
			AttributesDTO.AttributeDTO ent = new AttributesDTO.AttributeDTO();
			ent.setKey(e.getKey());
			ent.setValue(e.getValue());
			aDTO.getAttribute().add(ent);
		}
		return aDTO;
	}

	static public Attributes convertAttributesDTOtoAttributes(AttributesDTO attributesDTO) throws TransformException {

		Attributes attributes = new Attributes();
		for (AttributesDTO.AttributeDTO attr : attributesDTO.getAttribute()) {
			attributes.add(attr.getKey(), attr.getValue());
		}
		return attributes;
	}

	static public Identifier convertIdentifierDTOtoIdentifier(IdentifierDTO identifierDTO) throws TransformException {

		if (identifierDTO instanceof PropertyIdentifierDTO) {
			PropertyIdentifierDTO tmp = (PropertyIdentifierDTO) identifierDTO;
			return new PropertyIdentifier(PropertyIdentifierType.fromValue(tmp.type.value()), tmp.key);
		} else if (identifierDTO instanceof MessageIdentifierDTO) {
			MessageIdentifierDTO tmp = (MessageIdentifierDTO) identifierDTO;
			return new MessageIdentifier(tmp.keyPath, DataType.fromValue(tmp.dataType.value()));
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier: {0}", identifierDTO.getClass().getCanonicalName());
			throw new TransformException("Unsupported Identitifer");
		}
	}

	static public Query convertQueryDTOtoQuery(QueryDTO queryDTO) throws TransformException {

		List<Constraint> constraints = new ArrayList<>();

		for (ConstraintDTO cDTO : queryDTO.getConstraint()) {
			Identifier identifier = Transformer.convertIdentifierDTOtoIdentifier(cDTO.getIdentifier());
			if (cDTO instanceof BetweenDTO) {
				BetweenDTO cTmp = (BetweenDTO) cDTO;
				Between cNew = new Between(identifier, cTmp.getLowerBound(), cTmp.getUpperBound());
				constraints.add(cNew);
			} else if (cDTO instanceof EqualDTO) {
				EqualDTO cTmp = (EqualDTO) cDTO;
				Equal cNew = new Equal(identifier, cTmp.getValue());
				constraints.add(cNew);
			} else if (cDTO instanceof GreaterDTO) {
				GreaterDTO cTmp = (GreaterDTO) cDTO;
				Greater cNew = new Greater(identifier, cTmp.getValue());
				constraints.add(cNew);
			} else if (cDTO instanceof GreaterEqualDTO) {
				GreaterEqualDTO cTmp = (GreaterEqualDTO) cDTO;
				GreaterEqual cNew = new GreaterEqual(identifier, cTmp.getValue());
				constraints.add(cNew);
			} else if (cDTO instanceof InDTO) {
				InDTO cTmp = (InDTO) cDTO;
				In cNew = new In(identifier, cTmp.getElement());
				constraints.add(cNew);
			} else if (cDTO instanceof LessDTO) {
				LessDTO cTmp = (LessDTO) cDTO;
				Less cNew = new Less(identifier, cTmp.getValue());
				constraints.add(cNew);
			} else if (cDTO instanceof LessEqualDTO) {
				LessEqualDTO cTmp = (LessEqualDTO) cDTO;
				LessEqual cNew = new LessEqual(identifier, cTmp.getValue());
				constraints.add(cNew);
			} else if (cDTO instanceof NotEqualDTO) {
				NotEqualDTO cTmp = (NotEqualDTO) cDTO;
				NotEqual cNew = new NotEqual(identifier, cTmp.getValue());
				constraints.add(cNew);
			}
		}
		List<Order> orders = new ArrayList<>();

		for (OrderDTO order : queryDTO.getOrder()) {

			Order tmp = new Order(Transformer.convertIdentifierDTOtoIdentifier(order.getIdentifier()), order.ascDesc);
			orders.add(tmp);
		}

		return new Query(constraints, orders, queryDTO.getLimit());
	}

	static public ResultDTO convertResulttoResultDTO(List<Post> result) throws TransformException {
		ResultDTO result2 = new ResultDTO();
		// create empty list of posts
		List<PostDTO> posts = result2.getPost();
		for (ch.bfh.uniboard.service.data.Post p : result) {
			PostDTO pNew = new PostDTO();
			pNew.setAlpha(Transformer.convertAttributesToDTO(p.getAlpha()));
			pNew.setBeta(Transformer.convertAttributesToDTO(p.getBeta()));
			pNew.setMessage(p.getMessage());
			posts.add(pNew);
		}
		return result2;
	}

	public static ResultContainerDTO convertResultContainertoResultContainerDTO(ResultContainer resultContainer)
			throws TransformException {

		ResultContainerDTO result = new ResultContainerDTO(
				Transformer.convertResulttoResultDTO(resultContainer.getResult()),
				Transformer.convertAttributesToDTO(resultContainer.getGamma()));
		return result;
	}
}
