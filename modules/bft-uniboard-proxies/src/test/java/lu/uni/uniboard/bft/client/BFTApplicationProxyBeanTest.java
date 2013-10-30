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



import javax.ejb.EJB;
import lu.uni.uniboard.bft.proxy.BFTApplicationProxyBean;
import lu.uni.uniboard.bft.service.BFTReplicaService;
import lu.uni.uniboard.bft.service.BFTReplicatedService;
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
public class BFTApplicationProxyBeanTest {

    @Deployment
    public static JavaArchive createDeployment() {
        JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
            .addClass(BFTApplicationProxyBean.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        return ja;
    }

    @EJB(beanName = "BFTReplicatedApplicationProxyBean")
    private BFTReplicatedService bean;

    @Test
    public void testProcessRequest() throws Exception {
        try{
            //BFTServiceMessage m = new BFTServiceMessage();
            bean.processRequest(null);
        } catch (UnsupportedOperationException e){
            return;
        }
        Assert.fail();
    }
}
