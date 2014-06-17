/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package ch.bfh.uniboard.persistence.mongodb;

import ch.bfh.uniboard.service.Attributes;
import ch.bfh.uniboard.service.Between;
import ch.bfh.uniboard.service.Constraint;
import ch.bfh.uniboard.service.Equals;
import ch.bfh.uniboard.service.GetService;
import ch.bfh.uniboard.service.Greater;
import ch.bfh.uniboard.service.GreaterEquals;
import ch.bfh.uniboard.service.In;
import ch.bfh.uniboard.service.InternalGet;
import ch.bfh.uniboard.service.Less;
import ch.bfh.uniboard.service.LessEquals;
import ch.bfh.uniboard.service.NotEquals;
import ch.bfh.uniboard.service.Post;
import ch.bfh.uniboard.service.PostService;
import ch.bfh.uniboard.service.Query;
import ch.bfh.uniboard.service.ResultContainer;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author phil
 */
public class PersistenceComponent implements PostService, GetService, InternalGet {
    private DBCollection collection;
    
    public PersistenceComponent(){
        MongoClient mongoClient = null;
        try {
            //MongoClient already works as a pool if only one instance is used
            //http://docs.mongodb.org/ecosystem/tutorial/getting-started-with-java-driver/
            mongoClient = new MongoClient();
        } catch (UnknownHostException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
        }
        DB db = mongoClient.getDB("testDB");
        
        //authentication not needed here
        //boolean auth = db.authenticate(myUserName, myPassword);
        
        collection = db.getCollection("test");
        
        if (collection == null) {
            collection = db.createCollection("test", null);
        } 
    }

    @Override
    public Attributes post(byte[] message, Attributes alpha, Attributes beta) {
        PersistedPost pp = new PersistedPost();
        pp.setMessage(message);
        pp.setAlpha(alpha);
        pp.setBeta(beta);
        
        this.collection.insert(pp.toDBObject());
        
        return beta;
    }

    @Override
    public ResultContainer get(Query query) {
        
        DBObject completeQuery = new BasicDBObject();
        
        List<DBObject> constraintsList = new ArrayList<DBObject>();
        
        for(Constraint c : query.getConstraints()){
            
            String keyString = "";           
            //Construct the key
            switch(c.getPostElement()){
                case MESSAGE:
                    keyString += "message";
                    break;
                case ALPHA:
                    keyString += "alpha";
                    break;
                case BETA:
                    keyString += "beta";
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown post element "+ c.getPostElement());
            }
            
            for(String key : c.getKeys()){
                keyString += "."+key;
            }
            
            DBObject actualConstraint = new BasicDBObject();
            
            //construct the value
            if(c instanceof Equals){
                Equals op = (Equals)c;
                actualConstraint.put(keyString, op.getValue());
            } else if ( c instanceof NotEquals ){
                NotEquals op = (NotEquals)c;
                actualConstraint.put(keyString, new BasicDBObject("$ne",op.getValue()));
            } else if ( c instanceof In ){
                In op = (In) c;
                actualConstraint.put(keyString, new BasicDBObject("$in", op.getSet()));
            } else if ( c instanceof Between ){
                Between op = (Between)c;
                actualConstraint.put(keyString, new BasicDBObject("$gt",op.getStart()).append("$lt", op.getEnd()));
            } else if ( c instanceof Greater ){
                Greater op = (Greater)c;
                actualConstraint.put(keyString, new BasicDBObject("$gt",op.getValue()));
            } else if ( c instanceof GreaterEquals ){
                GreaterEquals op = (GreaterEquals)c;
                actualConstraint.put(keyString, new BasicDBObject("$gte",op.getValue()));
            } else if ( c instanceof Less ){
                Less op = (Less)c;
                actualConstraint.put(keyString, new BasicDBObject("$lt",op.getValue()));
            } else if ( c instanceof LessEquals ){
                LessEquals op = (LessEquals)c;
                actualConstraint.put(keyString, new BasicDBObject("$lte",op.getValue()));
            } else {
                throw new UnsupportedOperationException("Unknown constraint "+ c);
            }
            constraintsList.add(actualConstraint);
            
        }
        completeQuery.put("$and", constraintsList);
        
        DBCursor cursor = this.collection.find(completeQuery);
        SortedSet<Post> ss = new TreeSet<>();
        while(cursor.hasNext()){
            DBObject object = cursor.next();
            ss.add(PersistedPost.fromDBObject(object));
        }
                
        return new ResultContainer(ss,new Attributes());
    }

    @Override
    public SortedSet<Post> internalGet(Query q) {
        return this.get(q).getResult();
    }
    
    public DBCollection getCollection(){
        return this.collection;
    }
}
