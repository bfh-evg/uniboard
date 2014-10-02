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

import ch.bfh.uniboard.data.QueryDTO;
import ch.bfh.uniboard.service.Query;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class NotificationServiceImplTest {

	public NotificationServiceImplTest() {
	}

	@Deployment
	public static WebArchive createDeployment() {
		WebArchive ja = ShrinkWrap.create(WebArchive.class)
				.addClass(NotificationServiceImpl.class)
				.addClass(ObserverManagerMock.class)
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
		System.out.println(ja.toString(true));
		return ja;
	}

	@EJB
	ObserverManagerMock observerManager;

	@EJB
	NotificationService notificationService;

	@Test
	public void testRegister() {
		this.observerManager.getObservers().clear();
		QueryDTO query = new QueryDTO();
		query.setLimit(23);
		String nc = this.notificationService.register("http://test", query);

		assertEquals(1, this.observerManager.getObservers().size());
		assertNotNull(this.observerManager.getObservers().get(nc));
	}

	@Test
	public void testUnregister() {
		this.observerManager.getObservers().clear();
		Query query = new Query(null, 12);
		Observer obs = new Observer("test", query);
		String nc = "test";
		this.observerManager.addObserver(nc, obs);
		this.notificationService.unregister(nc);

		assertEquals(0, this.observerManager.getObservers().size());
		assertNull(this.observerManager.getObservers().get(nc));
	}

}
