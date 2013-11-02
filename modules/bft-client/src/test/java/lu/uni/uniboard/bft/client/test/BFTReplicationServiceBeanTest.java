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
package lu.uni.uniboard.bft.client.test;

import java.io.Serializable;
import javax.ejb.EJB;
import lu.uni.uniboard.bft.client.BFTReplicationServiceBean;
import lu.uni.uniboard.bft.service.proxy.BFTReplicationService;
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
public class BFTReplicationServiceBeanTest {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
            .addClass(BFTReplicationServiceBean.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return ja;
    }

    @EJB(beanName = "BFTReplicationServiceBean")
    private BFTReplicationService bean;

    @Test
    public void testProcessRequest() throws Exception {
        Serializable r = bean.processRequest(null);
        Assert.assertNull(r);
    }
}
