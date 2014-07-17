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
package ch.bfh.uniboard.sectioned;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.StringValue;
import java.io.File;
import javax.ejb.EJB;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import static org.junit.Assert.fail;
import static org.junit.Assert.fail;
import static org.junit.Assert.fail;
import static org.junit.Assert.fail;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@RunWith(Arquillian.class)
public class SectionedComponentTest {

    /**
     * Helper method for building the in-memory variant of a deployable unit. See Arquillian for more information.
     *
     * @return a Java archive
     */
    @Deployment
    public static WebArchive createDeployment() {
        WebArchive ja = ShrinkWrap.create(WebArchive.class)
                .addPackage(SectionedComponent.class.getPackage())
                .addClass(PostServiceTestBean.class)
                .addClass(GetServiceTestBean.class)
                .addClass(ConfigurationManagerTestBean.class)
                .addAsWebInfResource(new File("src/test/resources/ejb-jar.xml"), "ejb-jar.xml")
                .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        System.out.println(ja.toString(true));
        return ja;
    }

    @EJB(name = "SectionedComponent")
    PostService postService;

    @EJB(name = "SectionedComponent")
    GetService getService;

    @EJB(name = "PostServiceTestBean")
    PostServiceTestBean postServiceMock;

    @EJB(name = "GeterviceTestBean")
    GetServiceTestBean getServiceMock;

    @EJB
    ConfigurationManagerTestBean configurationManager;

    public SectionedComponentTest() {
    }

    @Test
    public void testCorrectRequest() {
        byte[] message = new byte[1];
        Attributes alpha = new Attributes();
        alpha.add("section", new StringValue("test"));
        Attributes beta = new Attributes();
        this.configurationManager.setCorrect(true);
        beta = this.postService.post(message, alpha, beta);
        if (beta.containsKey(Attributes.REJECTED) || beta.containsKey(Attributes.ERROR)) {
            fail();
        }
    }

    @Test
    public void testPostAlphaAttributeMissing() {
        byte[] message = new byte[1];
        Attributes alpha = new Attributes();
        alpha.add("test", new StringValue("test"));
        Attributes beta = new Attributes();
        this.configurationManager.setCorrect(true);
        beta = this.postService.post(message, alpha, beta);
        if (!beta.containsKey(Attributes.REJECTED)) {
            fail();
        }
    }

    @Test
    public void testPostAlphaAttributeValueInvalid() {
        byte[] message = new byte[1];
        Attributes alpha = new Attributes();
        alpha.add("test", new IntegerValue(1));
        Attributes beta = new Attributes();
        this.configurationManager.setCorrect(true);
        beta = this.postService.post(message, alpha, beta);
        if (!beta.containsKey(Attributes.REJECTED)) {
            fail();
        }
    }

    @Test
    public void testPostConfigurationMissing() {
        byte[] message = new byte[1];
        Attributes alpha = new Attributes();
        alpha.add("section", new StringValue("test"));
        Attributes beta = new Attributes();
        this.configurationManager.setCorrect(false);
        beta = this.postService.post(message, alpha, beta);
        if (!beta.containsKey(Attributes.ERROR)) {
            fail();
        }
    }

    @Test
    public void testPostUnkownSection() {
        byte[] message = new byte[1];
        Attributes alpha = new Attributes();
        alpha.add("section", new StringValue("test3"));
        Attributes beta = new Attributes();
        this.configurationManager.setCorrect(true);
        beta = this.postService.post(message, alpha, beta);
        if (!beta.containsKey(Attributes.REJECTED)) {
            fail();
        }
    }

}
