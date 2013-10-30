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
package lu.uni.uniboard.bft.client;

import lu.uni.uniboard.bft.proxy.BFTClientProxyBean;
import ch.bfh.uniboard.service.Message;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.Response;
import ch.bfh.uniboard.service.Result;
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
 * @author Rui Joaquim
 */
@RunWith(Arquillian.class)
public class BFTClientBeanTest {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
            .addClass(BFTClientProxyBean.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return ja;
    }

    @EJB(beanName = "BFTClientProxyBean")
    private Service bean;

    @Test
    public void testPost() throws Exception {
        Message m = new Message(null);
        Response r = bean.post(m);
        Assert.assertNotNull(r);
    }
    
    @Test
    public void testGet() throws Exception {
        Query q = new Query(null);
        Result r = bean.get(q);
        Assert.assertNotNull(r);
    }
}
