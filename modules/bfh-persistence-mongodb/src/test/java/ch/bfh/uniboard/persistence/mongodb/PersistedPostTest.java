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
package ch.bfh.uniboard.persistence.mongodb;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.ByteArrayValue;
import ch.bfh.uniboard.service.DateValue;
import ch.bfh.uniboard.service.DoubleValue;
import ch.bfh.uniboard.service.IntegerValue;
import ch.bfh.uniboard.service.StringValue;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Philémon von Bergen &lt;philemon.vonbergen@bfh.ch&gt;
 */
public class PersistedPostTest {
    
    private static byte[] message;
    private static Attributes alpha;
    private static Attributes beta;
    private static PersistedPost pp;
            
    public PersistedPostTest() {
    }
    
    /**
     * Prepare a post that will be used in the tests
     */
    @BeforeClass
    public static void setUpClass() {
        message = new byte[]{1,2,3,4};
        
        alpha = new Attributes();
        alpha.add("first", new StringValue("value1"));
        alpha.add("second", new IntegerValue(2));
        alpha.add("third", new ByteArrayValue(new byte[]{3,3}));
        alpha.add("fourth", new DateValue(new Date(System.currentTimeMillis())));
        
        beta = new Attributes();
        beta.add("fifth", new StringValue("value5"));
        beta.add("sixth", new DoubleValue(0.5));
        beta.add("seventh", new IntegerValue(7));
        beta.add("eighth", new ByteArrayValue(new byte[]{8,8}));
        
        pp = new PersistedPost(message, alpha, beta);
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test if the PersistedPost was created correctly
     */
    @Test
    public void createPersistedPostTest() {
        
        
        assertEquals(message[0], pp.getMessage()[0]);
        assertEquals(message[1], pp.getMessage()[1]);
        assertEquals(message[2], pp.getMessage()[2]);
        assertEquals(message[3], pp.getMessage()[3]);
        
        assertEquals(alpha.getValue("first"), pp.getAlpha().getValue("first"));
        assertEquals(alpha.getValue("second"), pp.getAlpha().getValue("second"));
        assertEquals(alpha.getValue("third"), pp.getAlpha().getValue("third"));
        assertEquals(alpha.getValue("fourth"), pp.getAlpha().getValue("fourth"));
        assertEquals(null, pp.getAlpha().getValue("fifth"));
        
        assertEquals(beta.getValue("fifth"), pp.getBeta().getValue("fifth"));
        assertEquals(beta.getValue("sixth"), pp.getBeta().getValue("sixth"));
        assertEquals(beta.getValue("seventh"), pp.getBeta().getValue("seventh"));
        assertEquals(beta.getValue("eighth"), pp.getBeta().getValue("eighth"));
        assertEquals(null, pp.getBeta().getValue("first"));
        
    }
    
    /**
     * Test the conversion from a PersistedPost to a DBObject
     */
    @Test
    public void toDbObjectTest() {
        DBObject dbObj = pp.toDBObject();
        
        assertTrue(dbObj.containsField("message"));
        assertTrue(dbObj.containsField("alpha"));
        assertTrue(dbObj.containsField("beta"));
        
        assertEquals(message[0], pp.getMessage()[0]);
        assertEquals(message[1], pp.getMessage()[1]);
        assertEquals(message[2], pp.getMessage()[2]);
        assertEquals(message[3], pp.getMessage()[3]);
        
        assertTrue(((ArrayList<DBObject>)dbObj.get("alpha")).get(0).containsField("first"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("alpha")).get(1).containsField("second"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("alpha")).get(2).containsField("third"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("alpha")).get(3).containsField("fourth"));
        
        assertEquals(alpha.getValue("first").getValue(),((ArrayList<DBObject>)dbObj.get("alpha")).get(0).get("first"));
        assertEquals(alpha.getValue("second").getValue(),((ArrayList<DBObject>)dbObj.get("alpha")).get(1).get("second"));
        assertEquals(alpha.getValue("third").getValue(),((ArrayList<DBObject>)dbObj.get("alpha")).get(2).get("third"));
        assertEquals(alpha.getValue("fourth").getValue(),((ArrayList<DBObject>)dbObj.get("alpha")).get(3).get("fourth"));

        assertTrue(((ArrayList<DBObject>)dbObj.get("beta")).get(0).containsField("fifth"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("beta")).get(1).containsField("sixth"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("beta")).get(2).containsField("seventh"));
        assertTrue(((ArrayList<DBObject>)dbObj.get("beta")).get(3).containsField("eighth"));
        
        assertEquals(beta.getValue("fifth").getValue(),((ArrayList<DBObject>)dbObj.get("beta")).get(0).get("fifth"));
        assertEquals(beta.getValue("sixth").getValue(),((ArrayList<DBObject>)dbObj.get("beta")).get(1).get("sixth"));
        assertEquals(beta.getValue("seventh").getValue(),((ArrayList<DBObject>)dbObj.get("beta")).get(2).get("seventh"));
        assertEquals(beta.getValue("eighth").getValue(),((ArrayList<DBObject>)dbObj.get("beta")).get(3).get("eighth"));

        
    }
    
    /**
     * Test the conversion from DBObject to PersistedPost
     */
    @Test
    public void fromDbObjectTest() {
        DBObject dbObj = pp.toDBObject();
        
        PersistedPost newPP = PersistedPost.fromDBObject(dbObj);
        
        assertEquals(this.message[0], newPP.getMessage()[0]);
        assertEquals(this.message[1], newPP.getMessage()[1]);
        assertEquals(this.message[2], newPP.getMessage()[2]);
        assertEquals(this.message[3], newPP.getMessage()[3]);
        
        assertEquals(this.alpha.getValue("first"), newPP.getAlpha().getValue("first"));
        assertEquals(this.alpha.getValue("second"), newPP.getAlpha().getValue("second"));
        assertEquals(this.alpha.getValue("third"), newPP.getAlpha().getValue("third"));
        assertEquals(this.alpha.getValue("fourth"), newPP.getAlpha().getValue("fourth"));
        
        assertEquals(this.beta.getValue("fifth"), newPP.getBeta().getValue("fifth"));
        assertEquals(this.beta.getValue("sixth"), newPP.getBeta().getValue("sixth"));
        assertEquals(this.beta.getValue("seventh"), newPP.getBeta().getValue("seventh"));
        assertEquals(this.beta.getValue("eighth"), newPP.getBeta().getValue("eighth"));
    }
    
    /**
     * Test the use of a JSON string as Message
     * @throws UnsupportedEncodingException 
     */
    @Test
    public void jsonMessageTest() throws UnsupportedEncodingException {
        String jsonMessage = "{ \"alpha\" : [ { \"first\" : \"value1\"} , { \"second\" : \"value2\"}] , \"beta\" : [ { \"fifth\" : \"value5\"} , { \"sixth\" : \"value6\"}]}";
           
        PersistedPost pp2 = new PersistedPost(jsonMessage.getBytes("UTF-8"), alpha, beta);
        
        DBObject dbObj = pp2.toDBObject();
        
        assertEquals(jsonMessage, JSON.serialize(dbObj.get("message")));
        
        PersistedPost pp3 = PersistedPost.fromDBObject(dbObj);
        
        assertEquals(jsonMessage, new String(pp3.getMessage(), "UTF-8"));
    
    }
    
}
