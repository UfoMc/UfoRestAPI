package de.matga.multidb;

import java.util.Map;

public interface DataBaseSwitch {

    void connect(int port, String address);

    Object find(String dataCollection, String goalField, String key);

    void delete(String dataCollection, String goalField, String key);

    void post(String json, String dataCollection);

    void update(String json, String dataCollection, String goalField, String key);

    Map<String, ?> addCollection(String collection);

    Map<String, ?> getCollections();

    void disconnect();

}
