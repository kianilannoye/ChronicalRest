package be.ugent;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;
 
public class MongoDBSingleton {
 
 private static MongoDBSingleton mDbSingleton;
  
 private static MongoClient mongoClient;
     
 private static DB db ;
  
  
 private static final String dbHost = "localhost";
 private static final int dbPort = 9000;
 private static final String dbName = "CHRONIC";
 
 private MongoDBSingleton(){};
  
 public static MongoDBSingleton getInstance(){
  if(mDbSingleton == null){
   mDbSingleton = new MongoDBSingleton();
  }
  return mDbSingleton;
 } 
  
 public DB getTestdb(){
  if(mongoClient == null){
   try {
    mongoClient = new MongoClient(dbHost , dbPort);
   } catch (UnknownHostException e) {
    return null;
   }
  }
  if(db == null)
   db = mongoClient.getDB(dbName);
//  if(!db.isAuthenticated()){
//   boolean auth = db.authenticate(dbUser, dbPassword.toCharArray());
//  }
  return db;
 }
}