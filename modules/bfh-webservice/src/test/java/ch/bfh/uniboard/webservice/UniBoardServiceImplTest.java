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

import ch.bfh.uniboard.UniBoardService;
import ch.bfh.uniboard.data.AttributeDTO;
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.LessEqualDTO;
import ch.bfh.uniboard.data.MessageIdentifierDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
//TODO Seperate Transformer Tests from UniBoardServiceTests
@RunWith(Arquillian.class)
public class UniBoardServiceImplTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addPackage(UniBoardServiceImpl.class.getPackage())
				.addClass(PostServiceTestBean.class)
				.addClass(GetServiceTestBean.class)
				.addAsManifestResource("ejb-jar.xml", "ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(name = "UniBoardServiceImpl")
	UniBoardService service;

	@EJB(name = "PostServiceTestBean")
	PostServiceTestBean postService;

	@EJB(name = "GetServiceTestBean")
	GetServiceTestBean getService;

	public UniBoardServiceImplTest() {
	}

	@Test
	public void testPost() {
		byte[] message = new byte[1];
		message[0] = 0x16;

		AttributesDTO adto = new AttributesDTO();
		AttributeDTO attribute1 = new AttributeDTO();
		attribute1.setKey("test");
		attribute1.setValue("test");
		adto.getAttribute().add(attribute1);
		AttributeDTO attribute2 = new AttributeDTO();
		attribute2.setKey("test2");
		attribute2.setValue("test2");
		adto.getAttribute().add(attribute2);

		service.post(message, adto);

		ch.bfh.uniboard.service.data.Post p = postService.getLastPost();

		assertEquals(2, p.getAlpha().getEntries().size());
		assertEquals("test", p.getAlpha().getAttribute("test").getKey());
		assertEquals("test2", p.getAlpha().getAttribute("test2").getKey());
		Assert.assertArrayEquals(message, p.getMessage());

	}

	/**
	 * Test if the right resultcontainer is transfered
	 */
	@Test
	public void testGet() {
		//Set the input
		QueryDTO query = new QueryDTO();
		LessEqualDTO constraint = new LessEqualDTO();
		MessageIdentifierDTO identifier = new MessageIdentifierDTO();
		identifier.setKeyPath("test");
		constraint.setIdentifier(identifier);
		constraint.setValue("test2");
		query.getConstraint().add(constraint);

		//Setup the expected result
		List<ch.bfh.uniboard.service.data.Post> posts = new ArrayList<>();
		ch.bfh.uniboard.service.data.Post post = new ch.bfh.uniboard.service.data.Post();
		post.setAlpha(new Attributes());
		post.getAlpha().add(new Attribute("alpha", "alpha"));
		post.setBeta(new Attributes());
		post.getBeta().add(new Attribute("beta", "beta"));
		byte[] message = new byte[1];
		message[0] = 0x16;
		post.setMessage(message);
		posts.add(post);

		Attributes attributes = new Attributes();
		attributes.add(new Attribute("gamma", "gamma"));

		ResultContainer expectedResult = new ResultContainer(posts, attributes);
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().size(), 1);
		assertEquals(result.getGamma().get(0).getKey(), "gamma");
		assertEquals((result.getGamma().get(0).getValue()), "gamma");
		assertEquals(result.getResult().size(), 1);
		Assert.assertArrayEquals(result.getResult().get(0).getMessage(), message);
		assertEquals(result.getResult().get(0).getAlpha().get(0).getKey(), "alpha");
		assertEquals((result.getResult().get(0).getAlpha().get(0).getValue()), "alpha");
		assertEquals(result.getResult().get(0).getBeta().get(0).getKey(), "beta");
		assertEquals((result.getResult().get(0).getBeta().get(0).getValue()), "beta");
	}
}
