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
package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.DoubleValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import ch.bfh.uniboard.webservice.data.ByteArrayValueDTO;
import ch.bfh.uniboard.webservice.data.DateValueDTO;
import ch.bfh.uniboard.webservice.data.DoubleValueDTO;
import ch.bfh.uniboard.webservice.data.IntegerValueDTO;
import ch.bfh.uniboard.webservice.data.StringValueDTO;
import ch.bfh.uniboard.webservice.data.ValueDTO;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class UniBoardServiceImplConvertTest {

	public UniBoardServiceImplConvertTest() {
	}

	/**
	 * Test for a byte array value
	 */
	@Test
	public void testConvertValueToDTO1() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			byte[] value = new byte[2];
			value[0] = 0x15;
			value[1] = 0x16;
			ByteArrayValue bvalue = new ByteArrayValue(value);
			ValueDTO result = service.convertValueToDTO(bvalue);
			if (!(result instanceof ByteArrayValueDTO)) {
				Assert.fail();
			}
			ByteArrayValueDTO bresult = (ByteArrayValueDTO) result;
			Assert.assertArrayEquals(value, bresult.getValue());
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a date value
	 */
	@Test
	public void testConvertValueToDTO2() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			Date date = new Date();
			DateValue dvalue = new DateValue(date);
			ValueDTO result = service.convertValueToDTO(dvalue);
			if (!(result instanceof DateValueDTO)) {
				Assert.fail();
			}
			DateValueDTO dresult = (DateValueDTO) result;
			assertEquals(dresult.getValue().toGregorianCalendar().getTime(), date);
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a double value
	 */
	@Test
	public void testConvertValueToDTO3() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			Double doubl = 1.0;
			DoubleValue dvalue = new DoubleValue(doubl);
			ValueDTO result = service.convertValueToDTO(dvalue);
			if (!(result instanceof DoubleValueDTO)) {
				Assert.fail();
			}
			DoubleValueDTO dresult = (DoubleValueDTO) result;
			assertEquals(dresult.getValue(), doubl, 0.0);
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a integer value
	 */
	@Test
	public void testConvertValueToDTO4() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			Integer intege = 1;
			IntegerValue ivalue = new IntegerValue(intege);
			ValueDTO result = service.convertValueToDTO(ivalue);
			if (!(result instanceof IntegerValueDTO)) {
				Assert.fail();
			}
			IntegerValueDTO iresult = (IntegerValueDTO) result;
			assertEquals(intege, iresult.getValue(), 0);
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a string value
	 */
	@Test
	public void testConvertValueToDTO5() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			String s = "test";
			StringValue svalue = new StringValue(s);
			ValueDTO result = service.convertValueToDTO(svalue);
			if (!(result instanceof StringValueDTO)) {
				Assert.fail();
			}
			StringValueDTO sresult = (StringValueDTO) result;
			assertEquals(s, sresult.getValue());
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Test for a unknown value
	 */
	@Test
	public void testConvertValueToDTO6() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			UnknownValue uValue = new UnknownValue();
			service.convertValueToDTO(uValue);
			Assert.fail();
		} catch (UniBoardServiceException ex) {
		}
	}

	/**
	 * Transform a ByteArrayValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue1() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			byte[] value = new byte[2];
			value[0] = 0x15;
			value[1] = 0x16;
			ByteArrayValueDTO bvalue = new ByteArrayValueDTO(value);
			Value result = service.convertValueDTOToValue(bvalue);
			if (!(result instanceof ByteArrayValue)) {
				Assert.fail();
			}
			ByteArrayValue bresult = (ByteArrayValue) result;
			Assert.assertArrayEquals(value, bresult.getValue());
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a DateValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue2() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			GregorianCalendar c = new GregorianCalendar();
			XMLGregorianCalendar value = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			DateValueDTO bvalue = new DateValueDTO(value);
			Value result = service.convertValueDTOToValue(bvalue);
			if (!(result instanceof DateValue)) {
				Assert.fail();
			}
			DateValue dresult = (DateValue) result;
			assertEquals(value.toGregorianCalendar().getTime(), dresult.getValue());
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		} catch (DatatypeConfigurationException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a DoubleValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue3() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			Double value = 1.9;
			DoubleValueDTO bvalue = new DoubleValueDTO(value);
			Value result = service.convertValueDTOToValue(bvalue);
			if (!(result instanceof DoubleValue)) {
				Assert.fail();
			}
			DoubleValue dresult = (DoubleValue) result;
			assertEquals(value, dresult.getValue(), 0.0);
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a IntegerValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue4() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			Integer value = 1;
			IntegerValueDTO bvalue = new IntegerValueDTO(value);
			Value result = service.convertValueDTOToValue(bvalue);
			if (!(result instanceof IntegerValue)) {
				Assert.fail();
			}
			IntegerValue dresult = (IntegerValue) result;
			assertEquals(value, dresult.getValue(), 0.0);
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a StringValueDTO
	 */
	@Test
	public void testConvertValueDTOToValue5() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			String value = "1";
			StringValueDTO bvalue = new StringValueDTO(value);
			Value result = service.convertValueDTOToValue(bvalue);
			if (!(result instanceof StringValue)) {
				Assert.fail();
			}
			StringValue dresult = (StringValue) result;
			assertEquals(value, dresult.getValue());
		} catch (UniBoardServiceException ex) {
			Assert.fail();
		}
	}

	/**
	 * Transform a unknown value dto
	 */
	@Test
	public void testConvertValueDTOToValue6() {
		UniBoardServiceImpl service = new UniBoardServiceImpl();
		try {
			UnknownValueDTO uValue = new UnknownValueDTO();
			service.convertValueDTOToValue(uValue);
			Assert.fail();
		} catch (UniBoardServiceException ex) {
		}
	}
}
