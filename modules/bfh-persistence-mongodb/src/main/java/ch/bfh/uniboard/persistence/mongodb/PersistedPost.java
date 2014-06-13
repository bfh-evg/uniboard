/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.persistence.mongodb;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Post;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import com.mongodb.util.JSONParseException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phil
 */
public class PersistedPost extends Post {
    
    public PersistedPost(byte[] message, Attributes alpha, Attributes beta){
        this.message = message;
        this.alpha = alpha;
        this.beta = beta;
    };
    
    public PersistedPost(){};
    
    public BasicDBObject toDBObject() {
        BasicDBObject doc = new BasicDBObject();
        
        //Check if message is a JSON message
        DBObject jsonMessageContent = null;
        try{
            jsonMessageContent = (DBObject)JSON.parse(new String(message,"UTF-8"));
        } catch (JSONParseException | UnsupportedEncodingException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Message is not a JSON string "+ex.getMessage());
            System.out.println("Message is not a JSON string "+ex.getMessage());
        }
        
        if(jsonMessageContent!=null) {
            //save message as JSON content
            DBObject jsonMessage = new BasicDBObject("message", jsonMessageContent);
            doc.putAll(jsonMessage);
        } else {
            //save message as byte[]
            doc.put("message", message);
        }
        
        //TODO take into account the type of the attribute value => see this.prepareForJson()
        List<BasicDBObject> alphaList = new ArrayList<BasicDBObject>();
        //BasicDBObject alphaDB = new BasicDBObject();
        //alphaList.putAll(alpha.getAllAttributes()); Not possible since keys are not integers
        for(Entry<String,String> entry: alpha.getEntries()){
            alphaList.add(new BasicDBObject(entry.getKey(), entry.getValue()));
            //alphaDB.put(entry.getKey(), this.prepareForJson(entry.getValue()));
        }
        doc.put("alpha", alphaList);
        
        List<BasicDBObject> betaList = new ArrayList<BasicDBObject>();
        for(Entry<String,String> entry: beta.getEntries()){
            betaList.add(new BasicDBObject(entry.getKey(), entry.getValue()));
        }
        doc.put("beta", betaList);

        return doc;
    }

    public static PersistedPost fromDBObject(DBObject doc) {
        PersistedPost pp = new PersistedPost();
        
        //Check if message is a JSON message
        if(doc.get("message") instanceof BasicDBObject){
            try {
                //is a JSON string
                pp.setMessage(JSON.serialize(doc.get("message")).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(PersistedPost.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            //otherwise is a byte[]
            pp.message = (byte[]) doc.get("message");
        }
        
        
//        List<BasicDBObject> alphaList = new ArrayList<BasicDBObject>();
//        alphaList = (ArrayList<BasicDBObject>)doc.get("alpha");
//        for(BasicDBObject o : alphaList){
//            o.g
//        }
        Attributes alpha = new Attributes();
        ArrayList<DBObject> alphaList = (ArrayList<DBObject>)doc.get("alpha");
        for(DBObject dbObj : alphaList){
            String key = dbObj.keySet().iterator().next();
            alpha.add(key, (String)dbObj.get(key));
        }
        pp.setAlpha(alpha);
        
        Attributes beta = new Attributes();
        ArrayList<DBObject> betaList = (ArrayList<DBObject>)doc.get("beta");
        for(DBObject dbObj : betaList){
            String key = dbObj.keySet().iterator().next();
            beta.add(key, (String)dbObj.get(key));
        }
        pp.setBeta(beta);
//        BasicDBObject betaList = (BasicDBObject)doc.get("beta");
//        for(Entry<String, Object> entry : betaList.entrySet()){
//            beta.add(entry.getKey(), convertFromJson(entry.getValue()));
//        }

        return pp;
    }
    
    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
}
