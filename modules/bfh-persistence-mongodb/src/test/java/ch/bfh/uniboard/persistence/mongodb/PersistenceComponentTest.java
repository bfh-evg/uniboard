package ch.bfh.uniboard.persistence.mongodb;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equals;
import ch.bfh.uniboard.service.NotEquals;
import ch.bfh.uniboard.service.PostElement;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author phil
 */
public class PersistenceComponentTest {
    
    private static PersistenceComponent pc;
    private static byte[] message;
    private static Attributes alpha;
    private static Attributes beta;
    private static PersistedPost pp;
    
    private static byte[] message2;
    private static Attributes alpha2;
    private static Attributes beta2;
    private static PersistedPost pp2;
    
    public PersistenceComponentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        pc = new PersistenceComponent();
        
        message = new byte[]{1,2,3,4};
        
        alpha = new Attributes();
        alpha.add("first", "value1");
        alpha.add("second", "value2");
        alpha.add("third", "value3");
        alpha.add("fourth", "value4");
        
        beta = new Attributes();
        beta.add("fifth", "value5");
        beta.add("sixth", "value6");
        beta.add("seventh", "value7");
        beta.add("eighth", "value8");
        
        pp = new PersistedPost(message, alpha, beta);
        
        try {
            message2 = "{ \"sub1\" : { \"subsub1\" : \"subsubvalue1\"} , \"sub2\" : \"subvalue2\"}".getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PersistenceComponentTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        alpha2 = new Attributes();
        alpha2.add("first", "value12");
        alpha2.add("second", "value22");
        alpha2.add("third", "value32");
        alpha2.add("fourth", "value42");
        
        beta2 = new Attributes();
        beta2.add("fifth", "value52");
        beta2.add("sixth", "value62");
        beta2.add("seventh", "value72");
        beta2.add("eighth", "value82");
        
        pp2 = new PersistedPost(message2, alpha2, beta2);
    }
    
    @AfterClass
    public static void tearDownClass() {
        
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
        pc.getCollection().remove(pp.toDBObject());
        pc.getCollection().remove(pp2.toDBObject());
    }

    @Test
    public void connectionTest() {
        assertNotNull(pc.getCollection());
    }
    
    @Test
    public void postTest() {
        pc.post(message, alpha, beta);
        
        DBCursor cursor = pc.getCollection().find();
        
        assertEquals(1, cursor.size());
        
        cursor = pc.getCollection().find(pp.toDBObject());
        
        assertEquals(1, cursor.size());
        
        DBObject query = new BasicDBObject();
        query.put("alpha.first", "value1");
        
        cursor = pc.getCollection().find(query);
        
        assertEquals(1, cursor.size());
        
        assertEquals(pp, PersistedPost.fromDBObject(cursor.next()));
    }
    
    @Test
    public void getInMessageTest() {
        pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());
        
        List<Constraint> constraints = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        keys.add("sub1");
        keys.add("subsub1");
        constraints.add(new Equals("subsubvalue1", keys, PostElement.MESSAGE));
        Query q = new Query(constraints);
        ResultContainer rc = pc.get(q);
        
        assertEquals(1, rc.getResult().size());
    }
    
    @Test
    public void getSimpleEqualsTest() {
        pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
        pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());
        
        List<Constraint> constraints = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        keys.add("first");
        constraints.add(new Equals("value1", keys, PostElement.ALPHA));
        Query q = new Query(constraints);
        ResultContainer rc = pc.get(q);
        
        assertEquals(1, rc.getResult().size());
        assertEquals(pp, rc.getResult().first());
    }
    
    @Test
    public void getMultipleEqualsTest() {
        pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
        pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());
        
        List<Constraint> constraints = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        keys.add("first");
        constraints.add(new Equals("value1", keys, PostElement.ALPHA));
        List<String> keys2 = new ArrayList<>();
        keys2.add("fifth");
        constraints.add(new Equals("value5", keys2, PostElement.BETA));
        Query q = new Query(constraints);
        ResultContainer rc = pc.get(q);
        
        assertEquals(1, rc.getResult().size());
        assertEquals(pp, rc.getResult().first());
    }
    
    @Test
    public void getNotEqualsTest() {
        pc.post(pp.getMessage(), pp.getAlpha(), pp.getBeta());
        pc.post(pp2.getMessage(), pp2.getAlpha(), pp2.getBeta());
        
        List<Constraint> constraints = new ArrayList<>();
        List<String> keys = new ArrayList<>();
        keys.add("first");
        constraints.add(new NotEquals("value4", keys, PostElement.ALPHA));
        Query q = new Query(constraints);
        ResultContainer rc = pc.get(q);
                
        assertEquals(2, rc.getResult().size());
        
        assertTrue(rc.getResult().contains(pp));
        assertTrue(rc.getResult().contains(pp2));
    }
    
    
}
