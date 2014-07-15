/*
 * Copyright (c) 2013 Berner Fachhochschule, Switzerland.
 * Bern University of Applied Sciences, Engineering and Information Technology,
 * Research Institute for Security in the Information Society, E-Voting Group,
 * Biel, Switzerland.
 *
 * Project Univote.
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 */
package ch.bfh.uniboard.singleinstance.client;

import ch.bfh.uniboard.*;
import ch.bfh.uniboard.data.*;
import ch.bfh.uniboard.data.Attributes.Attribute;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class SimpleClient {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		String endpointUrl = "http://ziu:8080/UniBoardService/UniBoardService?wsdl";
		UniBoardService port;
		try {
			URL wsdlLocation = new URL(endpointUrl); // Decision: expect endpoint URL having "?wsdl" appended
			QName qname = new QName("http://uniboard.bfh.ch/", "UniBoardService");
			UniBoardService_Service mixingService = new UniBoardService_Service(wsdlLocation, qname);
			port = mixingService.getUniBoardServicePort();
			BindingProvider bp = (BindingProvider) port;
			bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointUrl);
		} catch (MalformedURLException ex) {
			System.out.println("Malformed URL for mixing  service: " + endpointUrl + ", exception: " + ex);
			throw new RuntimeException(ex);
		}
		//Set msg
		byte[] message = "{ \"sub1\" : { \"subsub1\" : \"subsubvalue1\"} , \"sub2\" : 2}".getBytes("UTF-8");

		Attributes alpha = new Attributes();

		Attribute a1 = new Attribute();
		a1.setKey("key1");
		StringValue value1 = new StringValue();
		value1.setValue("value1");
		a1.setValue(value1);
		alpha.getAttribute().add(a1);
		Attributes beta = port.post(message, alpha);
	}

}
