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
import ch.bfh.uniboard.data.AttributesDTO;
import ch.bfh.uniboard.data.LessEqualDTO;
import ch.bfh.uniboard.data.MessageIdentifierDTO;
import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.data.ResultContainerDTO;
import ch.bfh.uniboard.data.StringValueDTO;
import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ResultContainer;
import ch.bfh.uniboard.service.StringValue;
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
		AttributesDTO.AttributeDTO attribute1 = new AttributesDTO.AttributeDTO();
		attribute1.setKey("test");
		StringValueDTO string1 = new StringValueDTO();
		string1.setValue("test");
		attribute1.setValue(string1);
		adto.getAttribute().add(attribute1);
		AttributesDTO.AttributeDTO attribute2 = new AttributesDTO.AttributeDTO();
		attribute2.setKey("test2");
		StringValueDTO string2 = new StringValueDTO();
		string2.setValue("test2");
		attribute2.setValue(string2);
		adto.getAttribute().add(attribute2);

		service.post(message, adto);

		ch.bfh.uniboard.service.Post p = postService.getLastPost();

		assertEquals(2, p.getAlpha().getEntries().size());
		assertEquals(new StringValue("test"), p.getAlpha().getValue("test"));
		assertEquals(new StringValue("test2"), p.getAlpha().getValue("test2"));
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
		identifier.getPart().add("test");
		constraint.setIdentifier(identifier);
		StringValueDTO string = new StringValueDTO("test2");
		constraint.setValue(string);
		query.getConstraint().add(constraint);

		//Setup the expected result
		List<ch.bfh.uniboard.service.Post> posts = new ArrayList<>();
		ch.bfh.uniboard.service.Post post = new ch.bfh.uniboard.service.Post();
		post.setAlpha(new Attributes());
		post.getAlpha().add("alpha", new StringValue("alpha"));
		post.setBeta(new Attributes());
		post.getBeta().add("beta", new StringValue("beta"));
		byte[] message = new byte[1];
		message[0] = 0x16;
		post.setMessage(message);
		posts.add(post);

		Attributes attributes = new Attributes();
		attributes.add("gamma", new StringValue("gamma"));

		ResultContainer expectedResult = new ResultContainer(posts, attributes);
		getService.setFeedback(expectedResult);

		ResultContainerDTO result = service.get(query);

		assertEquals(result.getGamma().getAttribute().size(), 1);
		assertEquals(result.getGamma().getAttribute().get(0).getKey(), "gamma");
		assertEquals(((StringValueDTO) result.getGamma().getAttribute().get(0).getValue()).getValue(), "gamma");
		assertEquals(result.getResult().getPost().size(), 1);
		Assert.assertArrayEquals(result.getResult().getPost().get(0).getMessage(), message);
		assertEquals(result.getResult().getPost().get(0).getAlpha().getAttribute().get(0).getKey(), "alpha");
		assertEquals(((StringValueDTO) result.getResult().getPost().get(0).getAlpha().getAttribute().get(0).getValue()).getValue(), "alpha");
		assertEquals(result.getResult().getPost().get(0).getBeta().getAttribute().get(0).getKey(), "beta");
		assertEquals(((StringValueDTO) result.getResult().getPost().get(0).getBeta().getAttribute().get(0).getValue()).getValue(), "beta");
	}
}
