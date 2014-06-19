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

import ch.bfh.uniboard.service.StringValue;
import ch.bfh.uniboard.webservice.data.AttributesDTO;
import ch.bfh.uniboard.webservice.data.StringValueDTO;
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
		return ja;
	}

	@EJB(name = "UniBoardServiceImpl")
	UniBoardService service;

	@EJB(name = "PostServiceTestBean")
	PostServiceTestBean postService;

	public UniBoardServiceImplTest() {
	}

	@Test
	public void testPost1() {
		byte[] message = new byte[1];
		message[0] = 0x16;

		AttributesDTO adto = new AttributesDTO();
		AttributesDTO.EntryDTO entry1 = new AttributesDTO.EntryDTO();
		entry1.setKey("test");
		StringValueDTO string1 = new StringValueDTO();
		string1.setValue("test");
		entry1.setValue(string1);
		adto.getEntry().add(entry1);
		AttributesDTO.EntryDTO entry2 = new AttributesDTO.EntryDTO();
		entry2.setKey("test2");
		StringValueDTO string2 = new StringValueDTO();
		string2.setValue("test2");
		entry2.setValue(string2);
		adto.getEntry().add(entry2);

		service.post(message, adto);

		ch.bfh.uniboard.service.Post p = postService.getLastPost();

		assertEquals(2, p.getAlpha().getEntries().size());
		assertEquals(new StringValue("test"), p.getAlpha().getValue("test"));
		assertEquals(new StringValue("test2"), p.getAlpha().getValue("test2"));
		Assert.assertArrayEquals(message, p.getMessage());

	}

}
