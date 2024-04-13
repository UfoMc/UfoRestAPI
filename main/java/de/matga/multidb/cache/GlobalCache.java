package de.matga.multidb.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.matga.api.BootStrap;
import de.matga.config.Config;
import de.matga.multidb.DataBaseSwitch;
import lombok.Getter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Getter
public class GlobalCache {

    private final LoadingCache<String, Object> cache;
    private final Config config;
    private final DataBaseSwitch dataBaseSwitch;
    private final Gson gson;

    public GlobalCache() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.dataBaseSwitch = BootStrap.instance.getDataBaseSwitch();
        this.config = BootStrap.instance.getConfig();
        this.cache = CacheBuilder
                .newBuilder()
                .maximumSize(config.getInt("maxCacheEntrys"))
                .maximumWeight(config.getInt("maxWeight"))
                .expireAfterAccess(config.getInt("cacheExpiration"), TimeUnit.MINUTES)
                .build(new CacheLoader<>() {
                        @Override
                        public Object load(String key) {
                            String[] array = key.split("-");
                            return fetchSaves(array[2], array[0], array[1]);
                        }
                });
    }

    public Object getObject(String key, String collection, String field){
        try {
            return cache.get(key + "-" + collection + "-" + field);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public void addObject(String collection, String key, String field){
        cache.put(key + "-" + collection + "-" + field, dataBaseSwitch.find(collection, field, key));
    }

    public void addObject(String collection, String key, String field, Object o){
        cache.put(key + "-" + collection + "-" + field, o);
    }

    public void removeObject(String collection, String key, String field){
        cache.invalidate(key + "-" + collection + "-" + field);
    }

    public void updateObject(String collection, String key, String field){
        removeObject(collection, key, field);
        addObject(collection, key, field);
    }

    private Object fetchSaves(String collection, String key, String field) {

        Object o = dataBaseSwitch.find(collection, field, key);

        if(o == null) {
            return null;
        }

        return gson.fromJson(gson.toJson(o), Object.class);

    }

}
