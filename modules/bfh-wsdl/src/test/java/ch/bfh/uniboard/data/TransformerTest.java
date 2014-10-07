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

import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.Equal;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEqual;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEqual;
import ch.bfh.uniboard.service.MessageIdentifier;
import ch.bfh.uniboard.service.NotEqual;
import ch.bfh.uniboard.service.Order;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class TransformerTest {

	public TransformerTest() {
	}

	/**
	 * Test for a byte array value
	 */
	@Test
	public void testConvertValueToDTO1() {
		try {
			byte[] value = new byte[2];
			value[0] = 0x15;
			value[1] = 0x16;
			ByteArrayValue bvalue = new ByteArrayValue(value);
			ValueDTO result = Transformer.convertValueToDTO(bvalue);
			if (!(result instanceof ByteArrayValueDTO)) {
				Assert.fail();
			}
			ByteArrayValueDTO bresult = (ByteArrayValueDTO) result;
			Assert.assertArrayEquals(value, bresult.getValue());
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a date value
	 */
	@Test
	public void testConvertValueToDTO2() {
		try {
			Date date = new Date();
			DateValue dvalue = new DateValue(date);
			ValueDTO result = Transformer.convertValueToDTO(dvalue);
			if (!(result instanceof DateValueDTO)) {
				Assert.fail();
			}
			DateValueDTO dresult = (DateValueDTO) result;
			assertEquals(dresult.getValue().toGregorianCalendar().getTime(), date);
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a integer value
	 */
	@Test
	public void testConvertValueToDTO4() {
		try {
			Integer intege = 1;
			IntegerValue ivalue = new IntegerValue(intege);
			ValueDTO result = Transformer.convertValueToDTO(ivalue);
			if (!(result instanceof IntegerValueDTO)) {
				Assert.fail();
			}
			IntegerValueDTO iresult = (IntegerValueDTO) result;
			assertEquals(intege, iresult.getValue(), 0);
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a string value
	 */
	@Test
	public void testConvertValueToDTO5() {
		try {
			String s = "test";
			StringValue svalue = new StringValue(s);
			ValueDTO result = Transformer.convertValueToDTO(svalue);
			if (!(result instanceof StringValueDTO)) {
				Assert.fail();
			}
			StringValueDTO sresult = (StringValueDTO) result;
			assertEquals(s, sresult.getValue());
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a unknown value
	 */
	@Test
	public void testConvertValueToDTO6() {
		try {
			UnknownValue uValue = new UnknownValue();
			Transformer.convertValueToDTO(uValue);
			Assert.fail();
		} catch (TransformException ex) {
		}
	}

	/**
	 * Transform a ByteArrayValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue1() {
		try {
			byte[] value = new byte[2];
			value[0] = 0x15;
			value[1] = 0x16;
			ByteArrayValueDTO bvalue = new ByteArrayValueDTO(value);
			Value result = Transformer.convertValueDTOToValue(bvalue);
			if (!(result instanceof ByteArrayValue)) {
				Assert.fail();
			}
			ByteArrayValue bresult = (ByteArrayValue) result;
			Assert.assertArrayEquals(value, bresult.getValue());
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a DateValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue2() {
		try {
			GregorianCalendar c = new GregorianCalendar();
			XMLGregorianCalendar value = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			DateValueDTO bvalue = new DateValueDTO(value);
			Value result = Transformer.convertValueDTOToValue(bvalue);
			if (!(result instanceof DateValue)) {
				Assert.fail();
			}
			DateValue dresult = (DateValue) result;
			assertEquals(value.toGregorianCalendar().getTime(), dresult.getValue());
		} catch (TransformException | DatatypeConfigurationException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a IntegerValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue4() {
		try {
			Integer value = 1;
			IntegerValueDTO bvalue = new IntegerValueDTO(value);
			Value result = Transformer.convertValueDTOToValue(bvalue);
			if (!(result instanceof IntegerValue)) {
				Assert.fail();
			}
			IntegerValue dresult = (IntegerValue) result;
			assertEquals(value, dresult.getValue(), 0.0);
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a StringValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue5() {
		try {
			String value = "1";
			StringValueDTO bvalue = new StringValueDTO(value);
			Value result = Transformer.convertValueDTOToValue(bvalue);
			if (!(result instanceof StringValue)) {
				Assert.fail();
			}
			StringValue dresult = (StringValue) result;
			assertEquals(value, dresult.getValue());
		} catch (TransformException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a unknown value dto
	 */
	@Test
	public void testConvertValueDTOToValue6() {
		try {
			UnknownValueDTO uValue = new UnknownValueDTO();
			Transformer.convertValueDTOToValue(uValue);
			Assert.fail();
		} catch (TransformException ex) {
		}
	}

	/**
	 * Test if a between constraint works
	 */
	@Test
	public void testQuery1() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		BetweenDTO constraint = new BetweenDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
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
		assertEquals(((StringValue) bconstraint.getStart()).getValue(), "test2");
		assertEquals(((StringValue) bconstraint.getEnd()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a equals constraint works
	 */
	@Test
	public void testQuery2() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		EqualDTO constraint = new EqualDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Equal)) {
			Assert.fail();
		}
		Equal bconstraint = (Equal) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a greater constraint works
	 */
	@Test
	public void testQuery3() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterDTO constraint = new GreaterDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Greater)) {
			Assert.fail();
		}
		Greater bconstraint = (Greater) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a greaterequals constraint works
	 */
	@Test
	public void testQuery4() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		GreaterEqualDTO constraint = new GreaterEqualDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof GreaterEqual)) {
			Assert.fail();
		}
		GreaterEqual bconstraint = (GreaterEqual) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a in constraint works
	 */
	@Test
	public void testQuery5() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		InDTO constraint = new InDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.getElement().add(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof In)) {
			Assert.fail();
		}
		In bconstraint = (In) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getSet().get(0)).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a less constraint works
	 */
	@Test
	public void testQuery6() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessDTO constraint = new LessDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof Less)) {
			Assert.fail();
		}
		Less bconstraint = (Less) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a lessequals constraint works
	 */
	@Test
	public void testQuery7() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessEqualDTO constraint = new LessEqualDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof LessEqual)) {
			Assert.fail();
		}
		LessEqual bconstraint = (LessEqual) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a notequal constraint works
	 */
	@Test
	public void testQuery8() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		NotEqualDTO constraint = new NotEqualDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getConstraints().size(), 1);
		Constraint resultingConstraint = resultingQuery.getConstraints().get(0);
		if (!(resultingConstraint instanceof NotEqual)) {
			Assert.fail();
		}
		NotEqual bconstraint = (NotEqual) resultingConstraint;
		assertEquals(((StringValue) bconstraint.getValue()).getValue(), "test2");
		assertEquals(bconstraint.getIdentifier().getParts().get(0), "test");
		assertEquals(bconstraint.getIdentifier().getClass(), MessageIdentifier.class);
	}

	/**
	 * Test if a Orders works
	 */
	@Test
	public void testQuery9() throws TransformException {
		//Set the input
		QueryDTO query = new QueryDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.getPart().add("test");
		OrderDTO order = new OrderDTO(identifier, true);

		query.getOrder().add(order);

		Query resultingQuery = Transformer.convertQueryDTOtoQuery(query);
		assertEquals(resultingQuery.getOrder().size(), 1);
		Order resultingOrder = resultingQuery.getOrder().get(0);
		assertTrue(resultingOrder.isAscDesc());
		if (!(resultingOrder.getIdentifier() instanceof MessageIdentifier)) {
			fail();
		}
		assertEquals("test", resultingOrder.getIdentifier().getParts().get(0));
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
