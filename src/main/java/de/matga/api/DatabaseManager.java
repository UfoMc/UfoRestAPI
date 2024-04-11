package de.matga.api;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

@Getter
public class DatabaseManager {

    private final MongoClient client;
    private final Map<String, MongoCollection<Document>> dataCollections;
    private final MongoDatabase database;

    public DatabaseManager() {
        /*define mongoDB clinet
        (change the connection string as you like but keep it localhost if not multi root to ensure secure usage)*/
        this.client = MongoClients.create("mongodb://127.0.0.1:27017/admin");
        this.database = client.getDatabase("network");
        dataCollections = new HashMap<>();

    }

    public Map<String, MongoCollection<Document>> addDataCollection(String s){
        //get collection from database
        final MongoCollection<Document> tempCollection = database.getCollection(s);
        //put collection into map
        dataCollections.put(s, tempCollection);
        return dataCollections;
    }

    public void stop() {
        //stop database connection

        if (client == null) {
            throw new NullPointerException("Client cannot be null");
        }

        client.close();
    }

}
