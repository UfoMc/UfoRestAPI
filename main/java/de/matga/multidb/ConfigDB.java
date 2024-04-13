package de.matga.multidb;

import java.util.Map;

public class ConfigDB implements DataBaseSwitch{

    @Override
    public void connect(int port, String address) {

    }

    @Override
    public Object find(String dataCollection, String goalField, String key) {
        return null;
    }

    @Override
    public void delete(String dataCollection, String goalField, String key) {

    }

    @Override
    public void post(String json, String dataCollection) {

    }

    @Override
    public void update(String json, String dataCollection, String goalField, String key) {

    }

    @Override
    public Map<String, ?> addCollection(String collection) {
        return null;
    }

    @Override
    public Map<String, ?> getCollections() {
        return null;
    }

    @Override
    public void disconnect() {

    }
}
