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
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.service.Value;
import java.io.File;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class OrderedServiceTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(OrderedServiceTestBean.class)
				.addClass(PostServiceTestBean.class)
				.addClass(ConfigurationManagerTestBean.class)
				.addAsWebInfResource(new File("src/test/resources/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(beanName = "OrderedServiceTestBean")
	OrderedServiceTestBean postService;

	@EJB
	PostServiceTestBean postServiceMock;

	@EJB
	ConfigurationManagerTestBean configurationManager;

	public OrderedServiceTest() {
	}

	/**
	 * Test of post method, of class OrderedService.
	 */
	@Test
	public void testPostExistingSectionValid() {

		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("section1"));
		Attributes beta = new Attributes();
		this.postServiceMock.setError(false);
		this.postService.init();
		Attributes resultingBeta = this.postService.post(message, alpha, beta);
		Value tmp = resultingBeta.getValue("rank");
		assertTrue(tmp instanceof IntegerValue);
		IntegerValue order = (IntegerValue) tmp;
		assertEquals(new Integer(2), order.getValue());
		assertEquals(new Integer("2"), this.postService.getHeads().getSections().get("section1"));

	}

	/**
	 * Test of post method, of class OrderedService.
	 */
	@Test
	public void testPostExistingSectionInvalid() {

		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("section2"));
		Attributes beta = new Attributes();
		this.postServiceMock.setError(true);
		this.postService.init();
		this.postService.post(message, alpha, beta);
		assertEquals(new Integer("26"), this.postService.getHeads().getSections().get("section2"));

	}

	/**
	 * Test of post method, of class OrderedService.
	 */
	@Test
	public void testPostNonExistingSectionValid() {

		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("section3"));
		Attributes beta = new Attributes();
		this.postServiceMock.setError(false);
		this.postService.init();
		assertNull(this.postService.getHeads().getSections().get("section3"));
		Attributes resultingBeta = this.postService.post(message, alpha, beta);
		Value tmp = resultingBeta.getValue("rank");
		assertTrue(tmp instanceof IntegerValue);
		IntegerValue order = (IntegerValue) tmp;
		assertEquals(new Integer(1), order.getValue());
		assertEquals(new Integer("1"), this.postService.getHeads().getSections().get("section1"));

	}

	/**
	 * Test of post method, of class OrderedService.
	 */
	@Test
	public void testPostNonExistingSectionInvalid() {

		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("section", new StringValue("section4"));
		Attributes beta = new Attributes();
		this.postServiceMock.setError(true);
		this.postService.init();
		assertNull(this.postService.getHeads().getSections().get("section4"));
		Attributes resultingBeta = this.postService.post(message, alpha, beta);
		Value tmp = resultingBeta.getValue("rank");
		assertTrue(tmp instanceof IntegerValue);
		IntegerValue order = (IntegerValue) tmp;
		assertEquals(new Integer(1), order.getValue());
		assertNull(this.postService.getHeads().getSections().get("section4"));

	}

	@Test
	public void testInitSave() {
		this.postService.init();
		assertNotNull(this.postService.getHeads().getSections().get("section1"));
		this.postService.getHeads().getSections().put("section5", 3);
		this.postService.save();
		OrderedState p = (OrderedState) this.configurationManager.getSaved();
		assertNotNull(p);
		assertEquals(new Integer("3"), p.getSections().get("section5"));
	}

}
