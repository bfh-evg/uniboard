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

import ch.bfh.uniboard.service.data.*;
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

	static public List<AttributeDTO> convertAttributesToDTOList(Attributes attributes) throws TransformException {

		List<AttributeDTO> aDTO = new ArrayList<>();
		for (Map.Entry<String, Attribute> e : attributes.getEntries()) {
			Attribute a = e.getValue();
			AttributeDTO attributeDTO = new AttributeDTO();
			attributeDTO.setKey(a.getKey());
			attributeDTO.setValue(a.getValue());
			if (a.getDataType() != null) {
				attributeDTO.setDataType(DataTypeDTO.fromValue(a.getDataType().value()));
			}
			aDTO.add(attributeDTO);
		}
		return aDTO;
	}

	static public Attributes convertDTOListtoAttributes(List<AttributeDTO> attributesDTO) throws TransformException {

		Attributes attributes = new Attributes();
		for (AttributeDTO attr : attributesDTO) {
			Attribute attribute;
			if (attr.getDataType() != null) {
				attribute = new Attribute(attr.getKey(), attr.getValue(),
						DataType.fromValue(attr.getDataType().value()));
			} else {
				attribute = new Attribute(attr.getKey(), attr.getValue());
			}
			attributes.add(attribute);
		}
		return attributes;
	}

	static public Identifier convertIdentifierDTOtoIdentifier(IdentifierDTO identifierDTO) throws TransformException {

		if (identifierDTO instanceof PropertyIdentifierDTO) {
			PropertyIdentifierDTO tmp = (PropertyIdentifierDTO) identifierDTO;
			return new PropertyIdentifier(PropertyIdentifierType.fromValue(tmp.propertyType.value()), tmp.key);
		} else if (identifierDTO instanceof MessageIdentifierDTO) {
			MessageIdentifierDTO tmp = (MessageIdentifierDTO) identifierDTO;
			return new MessageIdentifier(tmp.keyPath);
		} else {
			logger.log(Level.SEVERE, "Unsupported Identifier: {0}", identifierDTO.getClass().getCanonicalName());
			throw new TransformException("Unsupported Identitifer");
		}
	}

	static public Query convertQueryDTOtoQuery(QueryDTO queryDTO) throws TransformException {

		List<Constraint> constraints = new ArrayList<>();

		for (ConstraintDTO cDTO : queryDTO.getConstraint()) {
			Identifier identifier = Transformer.convertIdentifierDTOtoIdentifier(cDTO.getIdentifier());
			Constraint cNew;
			if (cDTO instanceof BetweenDTO) {
				BetweenDTO cTmp = (BetweenDTO) cDTO;
				cNew = new Between(identifier, cTmp.getLowerBound(), cTmp.getUpperBound());

			} else if (cDTO instanceof EqualDTO) {
				EqualDTO cTmp = (EqualDTO) cDTO;
				cNew = new Equal(identifier, cTmp.getValue());
			} else if (cDTO instanceof GreaterDTO) {
				GreaterDTO cTmp = (GreaterDTO) cDTO;
				cNew = new Greater(identifier, cTmp.getValue());
			} else if (cDTO instanceof GreaterEqualDTO) {
				GreaterEqualDTO cTmp = (GreaterEqualDTO) cDTO;
				cNew = new GreaterEqual(identifier, cTmp.getValue());
			} else if (cDTO instanceof InDTO) {
				InDTO cTmp = (InDTO) cDTO;
				cNew = new In(identifier, cTmp.getElement());
			} else if (cDTO instanceof LessDTO) {
				LessDTO cTmp = (LessDTO) cDTO;
				cNew = new Less(identifier, cTmp.getValue());
			} else if (cDTO instanceof LessEqualDTO) {
				LessEqualDTO cTmp = (LessEqualDTO) cDTO;
				cNew = new LessEqual(identifier, cTmp.getValue());
			} else if (cDTO instanceof NotEqualDTO) {
				NotEqualDTO cTmp = (NotEqualDTO) cDTO;
				cNew = new NotEqual(identifier, cTmp.getValue());
			} else {
				throw new TransformException("Unsupported constraint type");
			}
			if (cDTO.getDataType() != null) {
				cNew.setDataType(DataType.fromValue(cDTO.getDataType().value()));
			}
			constraints.add(cNew);
		}
		List<Order> orders = new ArrayList<>();

		for (OrderDTO order : queryDTO.getOrder()) {

			Order tmp = new Order(Transformer.convertIdentifierDTOtoIdentifier(order.getIdentifier()), order.ascDesc);
			orders.add(tmp);
		}

		return new Query(constraints, orders, queryDTO.getLimit());
	}

	static public List<PostDTO> convertResulttoResultDTO(List<Post> result) throws TransformException {
		List<PostDTO> result2 = new ArrayList();
		// create empty list of posts
		for (ch.bfh.uniboard.service.data.Post p : result) {
			PostDTO pNew = new PostDTO();
			pNew.setAlpha(Transformer.convertAttributesToDTOList(p.getAlpha()));
			pNew.setBeta(Transformer.convertAttributesToDTOList(p.getBeta()));
			pNew.setMessage(p.getMessage());
			result2.add(pNew);
		}
		return result2;
	}

	public static ResultContainerDTO convertResultContainertoResultContainerDTO(ResultContainer resultContainer)
			throws TransformException {

		ResultContainerDTO result = new ResultContainerDTO(
				Transformer.convertResulttoResultDTO(resultContainer.getResult()),
				Transformer.convertAttributesToDTOList(resultContainer.getGamma()));
		return result;
	}
}
