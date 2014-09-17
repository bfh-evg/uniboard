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
package ch.bfh.uniboard.configuration;

import ch.bfh.uniboard.service.ConfigurationManager;
import java.util.Properties;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class ConfigurationManagerImplTest {

	/**
	 * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
	 *
	 * @return a Java archive
	 */
	@Deployment
	public static JavaArchive createDeployment() {
		JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
				.addPackage(ConfigurationManagerImpl.class.getPackage())
				.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
		return ja;
	}

	@EJB
	ConfigurationManager cm;

	public ConfigurationManagerImplTest() {
	}

	@Test
	public void testJNDIProperty() {
		Properties p = cm.getConfiguration("jndiTest");
		String result = p.getProperty("testvalue");
		assertEquals(result, "test");
	}

	@Test
	public void testReplaceJNDIProperty() {
		Properties p = cm.getConfiguration("jndiReplace");
		String result = p.getProperty("replacevalue");
		assertEquals(result, "test");
		p.put("replacevalue", "replaced");
		cm.saveConfiguration("replaceTest", p);
		Properties p2 = cm.getConfiguration("jndiReplace");
		String result2 = p2.getProperty("replacevalue");
		assertEquals(result2, "replaced");
	}

	@Test
	public void testNewJNDIProperty() {
		Properties p = new Properties();
		p.put("newvalue", "newvalue");
		cm.saveConfiguration("newConfig", p);
		Properties p2 = cm.getConfiguration("newConfig");
		String result2 = p2.getProperty("newvalue");
		assertEquals(result2, "newvalue");
	}

}
