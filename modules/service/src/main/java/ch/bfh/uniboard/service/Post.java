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
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * A post represents a posted message and all belonging attributes.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "post", propOrder = {
	"message",
	"alpha",
	"beta"
})
public class Post implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElement(required = true)
	protected byte[] message;
	@XmlElement(required = true)
	protected Attributes alpha;
	@XmlElement(required = true)
	protected Attributes beta;

	public Post() {
	}

	public Post(byte[] message, Attributes alpha, Attributes beta) {
		this.message = message;
		this.alpha = alpha;
		this.beta = beta;
	}

	public byte[] getMessage() {
		return message;
	}

	public Attributes getAlpha() {
		return alpha;
	}

	public Attributes getBeta() {
		return beta;
	}

	public void setMessage(byte[] message) {
		this.message = message;
	}

	public void setAlpha(Attributes alpha) {
		this.alpha = alpha;
	}

	public void setBeta(Attributes beta) {
		this.beta = beta;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + Arrays.hashCode(this.message);
		hash = 11 * hash + Objects.hashCode(this.alpha);
		hash = 11 * hash + Objects.hashCode(this.beta);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Post other = (Post) obj;
		if (!Arrays.equals(this.message, other.message)) {
			return false;
		}
		if (!Objects.equals(this.alpha, other.alpha)) {
			return false;
		}
		if (!Objects.equals(this.beta, other.beta)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Post{" + "message=" + Base64.getEncoder().encodeToString(message)
				+ ", alpha=" + alpha + ", beta=" + beta + '}';
	}

}
