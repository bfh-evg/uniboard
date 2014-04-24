/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 * A data container for the result of a query and some other attributes. Values should be immutable.
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
class ResultContainer {
    
    private Result result;
    private Attributes gamma;

    /**
     * Initialize the data container
     * @param result the Result object containing the result of the query
     * @param gamma additional attributes
     */
    public ResultContainer(Result result, Attributes gamma) {
        this.result = result;
        this.gamma = gamma;
    }

    /**
     * Returns the Result object of the query
     * @return a Result object
     */
    public Result getResult() {
        return result;
    }

    /**
     * Sets the Result object of the query
     * @param result a Result object
     */
    public void setResult(Result result) {
        this.result = result;
    }

    /**
     * Get the additional attributes
     * @return the object contaning additional attributes
     */
    public Attributes getGamma() {
        return gamma;
    }

    /**
     * Set the additional attributes
     * @param gamma the object contaning additional attributes
     */
    public void setGamma(Attributes gamma) {
        this.gamma = gamma;
    }
    
    
}
