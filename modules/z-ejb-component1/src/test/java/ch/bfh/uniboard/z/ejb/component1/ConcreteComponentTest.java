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
package ch.bfh.uniboard.z.ejb.component1;

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
public class ConcreteComponentTest {

	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addClass(WorkerBean1.class)
				.addClass(WorkerBean2.class)
				.addClass(WorkerBean3.class)
				.addClass(WorkerBean4.class)
				.addClass(Component1Bean.class)
				.addClass(TerminatingComponentBean.class)
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB(beanName = "Component1Bean")
	private Service bean;

	@Test
	public void testPost() throws Exception {
		byte[] m = new byte[1];
		Attributes beta = bean.post(m, null, null);
		Assert.assertNull(beta);
	}
}
