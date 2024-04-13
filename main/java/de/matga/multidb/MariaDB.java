package de.matga.multidb;

import org.mariadb.jdbc.Connection;

import java.sql.DriverManager;
import java.util.Map;

public class MariaDB implements DataBaseSwitch{


    @Override
    public void connect(int port, String address) {
        try {
            Connection connection = (Connection) DriverManager
                    .getConnection("jdbc:mariadb://localhost:3306/DB?user=root&password=myPassword");
        } catch (Exception e){
            throw new RuntimeException(e);
        }

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
