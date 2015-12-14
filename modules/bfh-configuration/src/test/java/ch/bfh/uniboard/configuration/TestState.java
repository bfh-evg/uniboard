/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.configuration;

import ch.bfh.uniboard.service.State;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class TestState extends State {

	private String test1;
	private int test2;

	public String getTest1() {
		return test1;
	}

	public void setTest1(String test1) {
		this.test1 = test1;
	}

	public int getTest2() {
		return test2;
	}

	public void setTest2(int test2) {
		this.test2 = test2;
	}

}
