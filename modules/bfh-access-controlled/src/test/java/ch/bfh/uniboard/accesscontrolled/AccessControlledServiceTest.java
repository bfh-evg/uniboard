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
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.io.File;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test the flow on the Service
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class AccessControlledServiceTest {

	public AccessControlledServiceTest() {
	}

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(AccessControlledServiceSimply.class)
				.addClass(PostServiceTestBean.class)
				.addClass(GetServiceTestBean.class)
				.addAsWebInfResource(new File("src/test/resources/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(beanName = "AccessControlledServiceSimply")
	PostService postService;

	@EJB
	PostServiceTestBean postServiceTestBean;

	@EJB
	GetServiceTestBean getServiceTestBean;

	@Test
	public void testWithValidAuthNoTimeNoAmount() {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"}}").getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", group));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		Attributes beta = new Attributes();
		Attributes result = postService.post(message, alpha, beta);
		assertEquals(0, result.getEntries().size());
		assertArrayEquals(this.postServiceTestBean.getLastPost().getMessage(), message);
	}

	@Test
	public void testWithValidAuthNoTimeWithAmount() {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"amount\":2}").getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", "accessRight"));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		//Set resultContainer for amount query
		List<Post> posts2 = new ArrayList<>();
		Post p2 = new Post();
		byte[] blub = ("{}").getBytes(Charset.forName("UTF-8"));
		p2.setMessage(blub);
		Attributes alpha2 = new Attributes();
		alpha2.add(new Attribute("section", section));
		alpha2.add(new Attribute("group", group));
		p2.setAlpha(alpha2);
		Attributes beta2 = new Attributes();
		p2.setBeta(beta2);
		posts2.add(p2);
		ResultContainer rc2 = new ResultContainer(posts2, alpha2);
		getServiceTestBean.addFeedback(rc2);

		Attributes beta = new Attributes();
		Attributes result = postService.post(message, alpha, beta);
		assertEquals(0, result.getEntries().size());
		assertArrayEquals(this.postServiceTestBean.getLastPost().getMessage(), message);
	}

	@Test
	public void testWithValidAuthNoTimeWithAmountExceeded() {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"amount\":1}").getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", "accessRight"));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		//Set resultContainer for amount query
		List<Post> posts2 = new ArrayList<>();
		Post p2 = new Post();
		byte[] blub = ("{}").getBytes(Charset.forName("UTF-8"));
		p2.setMessage(blub);
		Attributes alpha2 = new Attributes();
		alpha2.add(new Attribute("section", section));
		alpha2.add(new Attribute("group", group));
		p2.setAlpha(alpha2);
		Attributes beta2 = new Attributes();
		p2.setBeta(beta2);
		posts2.add(p2);
		ResultContainer rc2 = new ResultContainer(posts2, alpha2);
		getServiceTestBean.addFeedback(rc2);

		Attributes beta = new Attributes();
		Attributes result = postService.post(message, alpha, beta);
		assertEquals(1, result.getEntries().size());
		assertEquals("BAC-007", result.getAttribute(Attributes.REJECTED).getValue().subSequence(0, 7));
		assertNull(this.postServiceTestBean.getLastPost());
	}

	@Test
	public void testWithValidAuthWithStartTimeValid() throws ParseException {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));
		Attributes beta = new Attributes();
		beta.add(new Attribute("timestamp", "2014-10-15T12:00:00Z"));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"startTime\":\"2014-10-15T11:00:00Z\"}")
				.getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", group));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		Attributes result = postService.post(message, alpha, beta);
		assertTrue(result.containsKey("timestamp"));
		assertArrayEquals(this.postServiceTestBean.getLastPost().getMessage(), message);
	}

	@Test
	public void testWithValidAuthWithStartTimeNotValid() throws ParseException {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));
		Attributes beta = new Attributes();
		beta.add(new Attribute("timestamp", "2014-10-15T12:00:00Z"));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"startTime\":\"2014-10-15T13:00:00Z\"}")
				.getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", group));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		Attributes result = postService.post(message, alpha, beta);
		assertTrue(result.containsKey(Attributes.REJECTED));
		assertEquals("BAC-004", result.getAttribute(Attributes.REJECTED).getValue().subSequence(0, 7));
		assertNull(this.postServiceTestBean.getLastPost());
	}

	@Test
	public void testWithValidAuthWithEndTimeValid() throws ParseException {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));
		Attributes beta = new Attributes();
		beta.add(new Attribute("timestamp", "2014-10-15T12:00:00Z"));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"endTime\":\"2014-10-15T13:00:00Z\"}")
				.getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", group));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		Attributes result = postService.post(message, alpha, beta);
		assertTrue(result.containsKey("timestamp"));
		assertArrayEquals(this.postServiceTestBean.getLastPost().getMessage(), message);
	}

	@Test
	public void testWithValidAuthWithEndTimeNotValid() throws ParseException {

		String key = "aBBe";
		String section = "sec1";
		String group = "group1";
		byte[] message = new byte[1];
		message[0] = 0x1;
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("section", section));
		alpha.add(new Attribute("group", group));
		alpha.add(new Attribute("signature", "xxx"));
		alpha.add(new Attribute("publickey", key));
		Attributes beta = new Attributes();
		beta.add(new Attribute("timestamp", "2014-10-15T12:00:00Z"));

		//Set resultContainer for auth query
		List<Post> posts = new ArrayList<>();
		Post p = new Post();
		byte[] authorization = ("{\"group\":\"group1\",\"crypto\":{\"type\":\"RSA\",\"publickey\":\""
				+ key + "\"},\"endTime\":\"2014-10-15T11:00:00Z\"}")
				.getBytes(Charset.forName("UTF-8"));
		p.setMessage(authorization);
		Attributes authAlpha = new Attributes();
		authAlpha.add(new Attribute("section", section));
		authAlpha.add(new Attribute("group", group));
		p.setAlpha(authAlpha);
		Attributes authBeta = new Attributes();
		p.setBeta(authBeta);
		posts.add(p);
		ResultContainer rc = new ResultContainer(posts, alpha);
		getServiceTestBean.addFeedback(rc);
		Attributes result = postService.post(message, alpha, beta);
		assertTrue(result.containsKey(Attributes.REJECTED));
		assertEquals("BAC-005", result.getAttribute(Attributes.REJECTED).getValue().subSequence(0, 7));
		assertNull(this.postServiceTestBean.getLastPost());
	}
}
