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
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A post represents a posted message and all belonging attributes.
 *
 * @author Severin Hauser &lt;severin.hauser@bfh.ch&gt;
 */
public class Post implements Serializable, Comparable {

    private static final long serialVersionUID = 1L;

    protected byte[] message;
    protected Attributes alpha;
    protected Attributes beta;

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
            Logger.getLogger(Post.class.getName()).log(Level.INFO, "messages are not equal: "+ Arrays.toString(this.message)+ " " + Arrays.toString(other.message));
            return false;
        }
        if (!Objects.equals(this.alpha, other.alpha)) {
            Logger.getLogger(Post.class.getName()).log(Level.INFO, "alphas are not equal");
            return false;
        }
        if (!Objects.equals(this.beta, other.beta)) {
            Logger.getLogger(Post.class.getName()).log(Level.INFO, "betas are not equal");
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Post{" + "message=" + message + ", alpha=" + alpha + ", beta=" + beta + '}';
    }

    @Override
    public int compareTo(Object aThat) {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;
        
        final String key = "n";

        if (this == aThat) {
            return EQUAL;
        }
        
        final Post that = (Post) aThat;
        
//        if(this.beta.getAllAttributes().containsKey(key)){
//            if(that.beta.getAllAttributes().containsKey(key)){
//                if(Integer.parseInt(this.beta.getAllAttributes().get(key)) < Integer.parseInt(that.beta.getAllAttributes().get(key))){
//                    return BEFORE;
//                } else if (Integer.parseInt(this.beta.getAllAttributes().get(key)) > Integer.parseInt(that.beta.getAllAttributes().get(key))){
//                    return AFTER;
//                } else {
//                    return EQUAL;
//                }
//            } else {
//                return BEFORE;
//            }
//        } else {
//            if(that.beta.getAllAttributes().containsKey(key)){
//                return AFTER;
//            } else {
//                return EQUAL;
//            }
//        }
        try {
            String myS = new String(message, "UTF-8");
            String yourS = new String(that.getMessage(), "UTF-8");
            if(myS.compareTo(yourS)<0) return BEFORE;
            else if( myS.compareTo(yourS)>0) return AFTER;
            else return EQUAL;
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Post.class.getName()).log(Level.SEVERE, null, ex);
            return EQUAL;
        }
        
    }

}
