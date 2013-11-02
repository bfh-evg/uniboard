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
package lu.uni.uniboard.bft.proxy.test;



import javax.ejb.EJB;
import lu.uni.uniboard.bft.proxy.BFTUniboardApplicationProxyBean;
import lu.uni.uniboard.bft.service.proxy.BFTReplicatedService;
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
public class BFTUniboardApplicationProxyBeanTest {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
            .addClass(BFTUniboardApplicationProxyBean.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return ja;
    }

    @EJB(beanName = "BFTUniboardApplicationProxyBean")
    private BFTReplicatedService bean;

    @Test
    public void testProcessRequest() throws Exception {
        bean.processRequest(null);
    }
}
