package de.matga.multidb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import de.matga.api.BootStrap;
import de.matga.config.Config;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;

public class MongoDB implements DataBaseSwitch{

    private MongoClient client;
    private Map<String, MongoCollection<Document>> dataCollections;
    private MongoDatabase database;
    private Config config = BootStrap.instance.getConfig();

    @Override
    public void connect(int port, String address) {
        /*define mongoDB clinet
        (change the connection string as you like but keep it localhost if not multi root to ensure secure usage)*/
        this.client = MongoClients.create("mongodb://"
                + address + ":" + port + "/"
                + config.getString("user"));

        this.database = client.getDatabase(config.getString("dataBaseName"));
        dataCollections = new HashMap<>();
    }

    @Override
    public Object find(String dataCollection, String goalField, String key) {
        return dataCollections.get(dataCollection)
                .find(Filters.eq(goalField, key)).first();
    }

    @Override
    public void delete(String dataCollection, String goalField, String key) {
        dataCollections.get(dataCollection)
                .deleteOne(Filters.eq(goalField, key));
    }

    @Override
    public void post(String json, String dataCollection) {
        dataCollections.get(dataCollection)
                .insertOne(Document.parse(json));
    }

    @Override
    public void update(String json, String dataCollection, String goalField, String key) {
        dataCollections.get(dataCollection).findOneAndReplace(Filters.eq(goalField, key),
                Document.parse(json));
    }


    @Override
    public Map<String, MongoCollection<Document>> addCollection(String s){

        //get collection from database
        final MongoCollection<Document> tempCollection = database.getCollection(s);
        //put collection into map
        dataCollections.put(s, tempCollection);
        return dataCollections;

    }

    @Override
    public Map<String, ?> getCollections() {
        return dataCollections;
    }

    @Override
    public void disconnect() {

        if (client == null) {
            throw new NullPointerException("Client cannot be null");
        }
        client.close();

    }

}
