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
package ch.bfh.uniboard.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A data container for a list of constraints.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Eric Dubuis &lt;eric.dubuis@bfh.ch&gt;
 */
public class Query implements Serializable {

	private static final long serialVersionUID = 1L;

	private final List<Constraint> constraints;
	private final List<Order> order;
	private final int limit;

	public Query(List<Constraint> constraints, List<Order> order, int limit) {
		this.constraints = constraints;
		this.order = order;
		//Limit has to be >=0, if limit=0 then there is no limit applied
		if (limit >= 0) {
			this.limit = limit;
		} else {
			this.limit = 0;
		}
	}

	public Query(List<Constraint> constraints) {
		this.constraints = constraints;
		this.order = new ArrayList<>();
		this.limit = 0;
	}

	public Query(List<Constraint> constraints, List<Order> order) {
		this.constraints = constraints;
		this.order = order;
		this.limit = 0;
	}

	public Query(List<Constraint> constraints, int limit) {
		this.constraints = constraints;
		this.order = new ArrayList<>();
		if (limit >= 0) {
			this.limit = limit;
		} else {
			this.limit = 0;
		}
	}

	public List<Constraint> getConstraints() {
		return constraints;
	}

	public List<Order> getOrder() {
		return order;
	}

	public int getLimit() {
		return limit;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Constraint constraint : constraints) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(constraint.toString());
		}
		return "Query{" + "constraints=[" + builder + "], limit=" + limit + "}";
	}

}
