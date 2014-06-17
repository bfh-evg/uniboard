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

import ch.bfh.uniboard.webservice.data.AttributesDTO;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class UniBoardServiceImplIT {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addClass(UniBoardServiceImpl.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB
	UniBoardService service;

	public UniBoardServiceImplIT() {
	}

	@Test
	public void testPost1() {
		byte[] message = new byte[1];
		message[0] = 0x16;

		AttributesDTO adto = new AttributesDTO();

		service.post(message, null);

	}

}
