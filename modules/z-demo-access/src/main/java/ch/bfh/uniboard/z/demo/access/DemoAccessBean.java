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
package ch.bfh.uniboard.z.demo.access;

import ch.bfh.uniboard.service.Component;
import ch.bfh.uniboard.service.Service;
import javax.ejb.EJB;
import javax.ejb.Stateless;

/**
 *
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
@Stateless()
public class DemoAccessBean extends Component implements Service, DemoAccessRemote {

    @EJB//(beanName = "TerminatingComponentBean")
    private Service successor;

    @Override
    protected Service getSuccessor() {
        return this.successor;
    }
}
