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
package ch.bfh.uniboard.ordered;

import ch.bfh.uniboard.service.PostService;
import java.util.Properties;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@LocalBean
@Singleton
public class OrderedServiceTestBean extends OrderedService implements PostService {

    public Properties getHeads() {
        return super.sectionHeads;
    }

    @Override
    public void save() {
        super.save();
    }

    @Override
    public void init() {
        super.init();
    }

}
