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

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class GetServiceTestBean implements GetService {

	private ResultContainer result;

	public GetServiceTestBean() {
		result = new ResultContainer(new ArrayList<Post>(), new Attributes());
	}

	@Override
	public ResultContainer get(Query query) {
		return result;
	}

	public void setResult(List<Post> posts) {
		result = new ResultContainer(posts, new Attributes());
	}
}
