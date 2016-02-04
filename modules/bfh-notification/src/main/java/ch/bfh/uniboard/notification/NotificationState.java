/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.bfh.uniboard.notification;

import ch.bfh.uniboard.service.configuration.State;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class NotificationState extends State {

	private Map<String, Observer> observers;

	public NotificationState() {
		this.observers = new HashMap<>();
	}

	public Map<String, Observer> getObservers() {
		return observers;
	}

	public void setObservers(Map<String, Observer> observers) {
		this.observers = observers;
	}

}
