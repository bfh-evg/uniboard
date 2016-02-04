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

import ch.bfh.uniboard.service.data.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.data.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.data.Query;
import ch.bfh.uniboard.service.data.ResultContainer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
@LocalBean
public class PostGetServiceTestBean implements PostService, GetService {

	private ResultContainer result;
	private boolean correct = false;

	public PostGetServiceTestBean() {
		result = new ResultContainer(new ArrayList<Post>(), new Attributes());
	}

	@Override
	public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
		if (correct) {
			Post p = new Post();
			p.setAlpha(alpha);
			p.setBeta(beta);
			p.setMessage(message);
			this.result.getResult().add(p);
		}
		return beta;
	}

	@Override
	public ResultContainer get(Query query) {
		return result;
	}

	public void setResult(List<Post> posts) {
		result = new ResultContainer(posts, new Attributes());
	}

	public void correctRunning(boolean correct) {
		this.correct = correct;
		result.getResult().clear();
	}
}
