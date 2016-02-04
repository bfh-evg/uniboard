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
package ch.bfh.uniboard.typed;

import ch.bfh.uniboard.PostServiceTestBean;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import java.io.File;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class TypedServiceTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(TypedService.class)
				.addClass(PostServiceTestBean.class)
				.addClass(ConfigurationManagerTestBean.class)
				.addAsWebInfResource(new File("src/test/resources/typed-ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		//System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(beanName = "TypedService")
	PostService postService;

	@EJB
	PostServiceTestBean postServiceMock;

	@EJB
	ConfigurationManagerTestBean configurationManager;

	public TypedServiceTest() {
	}

	@Test
	public void testCorrectRequestGrouped() {
		byte[] message = "1".getBytes();
		Attributes alpha = new Attributes();
		alpha.add("group", new StringValue("number"));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (beta.containsKey(Attributes.REJECTED) || beta.containsKey(Attributes.ERROR)) {
			fail();
		}
	}

	@Test
	public void testAttributeMissingGrouped() {
		byte[] message = "1".getBytes();
		Attributes alpha = new Attributes();
		alpha.add("group2", new StringValue("number"));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.REJECTED);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-001", tmp2);
	}

	@Test
	public void testPostAlphaAttributeValueInvalidGrouped() {
		byte[] message = "1".getBytes();
		Attributes alpha = new Attributes();
		alpha.add("group", new IntegerValue(1));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.REJECTED);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-002", tmp2);
	}

	@Test
	public void testPostConfigurationMissingGrouped() {
		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("group", new StringValue("number"));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(false);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.ERROR)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.ERROR);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-003", tmp2);
	}

	@Test
	public void testPostUnkownGroupGrouped() {
		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("group", new StringValue("invalid"));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.REJECTED);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-004", tmp2);
	}

	@Test
	public void testInvalidMessageGrouped() {
		byte[] message = "\"1.1.1.1\"".getBytes();
		Attributes alpha = new Attributes();
		alpha.add("group", new StringValue("number"));
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(true);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.REJECTED);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-006", tmp2);
	}

	@Test
	public void testCorrectRequestNoNGrouped() {
		byte[] message = "1".getBytes();
		Attributes alpha = new Attributes();
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(false);
		beta = this.postService.post(message, alpha, beta);
		if (beta.containsKey(Attributes.REJECTED) || beta.containsKey(Attributes.ERROR)) {
			fail();
		}
	}

	@Test
	public void testInvalidMessageNoNGrouped() {
		byte[] message = "\"1.1.1.1\"".getBytes();
		Attributes alpha = new Attributes();
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(true);
		this.configurationManager.setGroupded(false);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.REJECTED);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-005", tmp2);
	}

	@Test
	public void testPostConfigurationMissingNoNGrouped() {
		byte[] message = "1".getBytes();
		Attributes alpha = new Attributes();
		Attributes beta = new Attributes();
		this.configurationManager.setCorrect(false);
		this.configurationManager.setGroupded(false);
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.ERROR)) {
			fail();
		}
		StringValue tmp = (StringValue) beta.getValue(Attributes.ERROR);
		String tmp2 = tmp.getValue().substring(0, 7);
		assertEquals("BGT-003", tmp2);
	}
}
