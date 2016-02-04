/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project UniBoard.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */

package ch.bfh.uniboard.webservice;

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.PostService;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Singleton
@LocalBean
public class PostServiceTestBean implements PostService{
    
    private ch.bfh.uniboard.service.data.Post lastPost;

    @Override
    public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
        this.lastPost = new ch.bfh.uniboard.service.data.Post();
        this.lastPost.setAlpha(alpha);
        this.lastPost.setMessage(message);
        return beta;
    }
    
    public ch.bfh.uniboard.service.data.Post getLastPost() {
        return lastPost;
    }
}
