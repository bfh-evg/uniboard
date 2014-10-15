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
package ch.bfh.uniboard.accesscontrolled;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.PostService;
import com.fasterxml.jackson.databind.JsonNode;
import javax.ejb.Stateless;

/**
 * Skips the signature checks
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
@Stateless
public class AccessControlledServiceSimply extends AccessControlledService implements PostService {

	@Override
	protected boolean checkECDLSignature(JsonNode key, byte[] message, Attributes alpha) {
		return true;
	}

	@Override
	protected boolean checkDLSignature(JsonNode key, byte[] message, Attributes alpha) {
		return true;
	}

	@Override
	protected boolean checkRSASignature(JsonNode key, byte[] message, Attributes alpha) {
		return true;
	}

}
