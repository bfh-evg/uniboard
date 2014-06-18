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

import java.util.List;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Between extends Constraint {

    private Value start;
    private Value end;

    public Between(Value start, Value end, List<String> keys, PostElement postElement) {
        super(keys, postElement);
        this.start = start;
        this.end = end;
    }

    public Value getStart() {
        return start;
    }

    public void setStart(Value start) {
        this.start = start;
    }

    public Value getEnd() {
        return end;
    }

    public void setEnd(Value end) {
        this.end = end;
    }

}
