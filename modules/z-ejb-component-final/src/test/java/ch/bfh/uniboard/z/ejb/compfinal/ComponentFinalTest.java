/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniVote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.z.ejb.compfinal;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Service;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class ComponentFinalTest {

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addClass(ComponentFinalBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB(beanName = "ComponentFinalBean")
	private Service bean;

	@Test
	public void testPost() throws Exception {
		byte[] m = new byte[1];
		Attributes r = bean.post(m, null, null);
		Assert.assertNull(r);
	}
}