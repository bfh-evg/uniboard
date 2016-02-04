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
package ch.bfh.uniboard.data;

import ch.bfh.uniboard.service.data.Between;
import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.data.DataType;
import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.data.Greater;
import ch.bfh.uniboard.service.data.GreaterEqual;
import ch.bfh.uniboard.service.data.In;
import ch.bfh.uniboard.service.data.Less;
import ch.bfh.uniboard.service.data.LessEqual;
import ch.bfh.uniboard.service.data.MessageIdentifier;
import ch.bfh.uniboard.service.data.NotEqual;
import ch.bfh.uniboard.service.data.Order;
import ch.bfh.uniboard.service.data.Query;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class TransformerTest {

	public TransformerTest() {
	}

	/**
	 * Test if a between constraint works
	 */
	@Test
	public void testQuery1() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		BetweenDTO constraint = new BetweenDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setLowerBound(string);
		constraint.setUpperBound(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Between)) {
			Assert.fail();
		}
		Between bconstraint = (Between) resultingConstraint;
		assertEquals(bconstraint.getLowerBound(), "test2");
		assertEquals(bconstraint.getUpperBound(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a equals constraint works
	 */
	@Test
	public void testQuery2() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		EqualDTO constraint = new EqualDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Equal)) {
			Assert.fail();
		}
		Equal bconstraint = (Equal) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");;
	}

	/**
	 * Test if a greater constraint works
	 */
	@Test
	public void testQuery3() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterDTO constraint = new GreaterDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Greater)) {
			Assert.fail();
		}
		Greater bconstraint = (Greater) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a greaterequals constraint works
	 */
	@Test
	public void testQuery4() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterEqualDTO constraint = new GreaterEqualDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";;
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof GreaterEqual)) {
			Assert.fail();
		}
		GreaterEqual bconstraint = (GreaterEqual) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a in constraint works
	 */
	@Test
	public void testQuery5() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		InDTO constraint = new InDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.getElement().add(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof In)) {
			Assert.fail();
		}
		In bconstraint = (In) resultingConstraint;
		assertEquals(bconstraint.getSet().get(0), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a less constraint works
	 */
	@Test
	public void testQuery6() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessDTO constraint = new LessDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Less)) {
			Assert.fail();
		}
		Less bconstraint = (Less) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a lessequals constraint works
	 */
	@Test
	public void testQuery7() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessEqualDTO constraint = new LessEqualDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof LessEqual)) {
			Assert.fail();
		}
		LessEqual bconstraint = (LessEqual) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a notequal constraint works
	 */
	@Test
	public void testQuery8() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		NotEqualDTO constraint = new NotEqualDTO();
		constraint.setDataType(DataTypeDTO.DATE);
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		String string = "test2";
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof NotEqual)) {
			Assert.fail();
		}
		NotEqual bconstraint = (NotEqual) resultingConstraint;
		assertEquals(bconstraint.getValue(), "test2");
		assertEquals(bconstraint.getDataType(), DataType.DATE);
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) bconstraint.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if a Orders works
	 */
	@Test
	public void testQuery9() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		OrderDTO order = new OrderDTO(identifier, true);

		query.getOrder().add(order);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getOrder().size(), 1);
		Order resultingOrder = resultingQuery.getOrder().get(0);
		assertTrue(resultingOrder.isAscDesc());
		assertEquals(resultingOrder.getIdentifier().getClass(), MessageIdentifier.class);
		assertEquals(((MessageIdentifier) resultingOrder.getIdentifier()).getKeyPath(), "test");
	}

	/**
	 * Test if limits works
	 */
	@Test
	public void testQuery10() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		int limit = 12;
		query.setLimit(limit);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(limit, resultingQuery.getLimit());
	}
}
