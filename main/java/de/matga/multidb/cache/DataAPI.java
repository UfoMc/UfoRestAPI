package de.matga.multidb.cache;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.matga.api.BootStrap;
import de.matga.multidb.DataBaseSwitch;

import java.io.FileReader;

public class DataAPI {

    private final DataBaseSwitch data;
    private final BootStrap bootStrap;
    private final Gson gson;
    private final GlobalCache cache;

    public DataAPI() {
        this.gson = new Gson();
        this.data = BootStrap.instance.dataBaseSwitch;
        this.bootStrap = BootStrap.instance;
        this.cache = bootStrap.getCache();
    }

    public void delete(String goalDataCollection, String goalField, String key) {
         data.delete(goalDataCollection, goalField, key);
         cache.removeObject(goalDataCollection, key, goalField);
    }

    public void post(String goalDataCollection, String goalField, String key, Object o) {
        data.post(gson.toJson(o), goalDataCollection);
        cache.addObject(goalDataCollection, key, goalField, o);
    }

    public Object get(String goalDataCollection, String goalField, String key){
        return cache.getObject(key, goalDataCollection, goalField);
    }

    public void set(String searchField, Object o, String collection) {

        String key = new JsonParser().parse(gson.toJson(o)).getAsJsonObject().get(searchField).getAsString();

        data.update(gson.toJson(o), collection, searchField, key);
        cache.updateObject(collection, key, searchField);

    }

}
