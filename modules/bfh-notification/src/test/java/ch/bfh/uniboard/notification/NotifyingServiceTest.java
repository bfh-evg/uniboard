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
package ch.bfh.uniboard.notification;

import ch.bfh.uniboard.data.PostDTO;
import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.configuration.Configuration;
import ch.bfh.uniboard.service.data.Constraint;
import ch.bfh.uniboard.service.data.Equal;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.data.Attribute;
import ch.bfh.uniboard.service.data.PropertyIdentifier;
import ch.bfh.uniboard.service.data.PropertyIdentifierType;
import ch.bfh.uniboard.service.data.Query;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.ejb.EJB;
import static junit.framework.Assert.assertEquals;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class NotifyingServiceTest {

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(NotifyingService.class)
				.addClass(PostGetServiceTestBean.class)
				.addClass(ObserverManagerMock.class)
				.addClass(ObserverClientMock.class)
				.addClass(ConfigurationManagerTestBean.class)
				.addAsWebInfResource(new File("src/test/resources/ejb-jar.xml"), "ejb-jar.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB(beanName = "NotifyingService")
	PostService notificationService;

	@EJB
	ConfigurationManagerTestBean configurationManager;

	@EJB
	PostGetServiceTestBean postGetService;

	@EJB
	ObserverManagerMock observerManager;

	@EJB
	ObserverClientMock observerClient;

	public NotifyingServiceTest() {
	}

	public void setContraint() {
		List<Constraint> constraints = new ArrayList<>();
		Constraint c1 = new Equal(new PropertyIdentifier(PropertyIdentifierType.ALPHA, "alpha1"), "test");
		constraints.add(c1);
		Query q = new Query(constraints);
		Observer obs = new Observer("http://test1", q);
		this.observerManager.addObserver("a", obs);
	}

	@Test
	public void testNotFittingQuery() {
		Properties config = new Properties();
		config.put("unique", "section");
		configurationManager.saveState(null);
		this.setContraint();
		this.observerClient.reset();
		this.postGetService.correctRunning(false);
		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		Attributes beta = new Attributes();
		beta.add(new Attribute("section", "test"));
		notificationService.post(message, alpha, beta);
		assertNull(this.observerClient.getPost());
	}

	@Test
	public void testFittingQuery() {
		Configuration config = new Configuration();
		config.getEntries().put("unique", "section");
		configurationManager.setConfiguration(config);
		this.setContraint();
		this.observerClient.reset();
		this.postGetService.correctRunning(true);
		byte[] message = new byte[1];
		Attributes alpha = new Attributes();
		alpha.add(new Attribute("alpha1", "test"));
		Attributes beta = new Attributes();
		beta.add(new Attribute("section", "test"));
		notificationService.post(message, alpha, beta);
		assertNotNull(this.observerClient.getPost());
		PostDTO result = this.observerClient.getPost();
		assertEquals(1, result.getAlpha().size());
	}

}
