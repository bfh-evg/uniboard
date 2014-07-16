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
package ch.bfh.uniboard.sectioned;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class SectionedComponentTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addPackage(SectionedComponent.class.getPackage())
				.addClass(PostServiceTestBean.class)
				.addClass(GetServiceTestBean.class)
				.addClass(ConfigurationManagerTestBean.class)
				.addAsManifestResource("ejb-jar.xml", "ejb-jar.xml")
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		//System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(name = "SectionedComponent")
	PostService postService;

//	@EJB(name = "SectionedComponent")
//	GetService getService;
	@EJB(name = "PostServiceTestBean")
	PostServiceTestBean postServiceMock;

	@EJB(name = "GeterviceTestBean")
	GetServiceTestBean getServiceMock;

	public SectionedComponentTest() {
	}

	@Test
	public void testPostAlphaAttributeMissing() {
		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add("test", new StringValue("test"));
		Attributes beta = new Attributes();
		beta = this.postService.post(message, alpha, beta);
		if (!beta.containsKey(Attributes.REJECTED)) {
			fail();
		}
	}

}
