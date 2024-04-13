package de.matga.config;

import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Config {

    private final File file;
    private final Gson gson;
    private final ExecutorService pool;
    private JsonObject json;

    public Config(String name, String folder) {
        this.file = new File(folder, name.toLowerCase() + ".json");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.pool = Executors.newFixedThreadPool(2);
        this.initFile();
    }

    private void initFile() {
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        if (!file.exists()) {
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print(gson.toJson(json = new JsonObject()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                json = new JsonParser().parse(new FileReader(file)).getAsJsonObject();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public <V> Config add(String key, V value) {
        if (json.get(key) == null || json.get(key) instanceof JsonNull) {
            return set(key, value);
        }

        return this;
    }

    public <V> Config set(String key, V value) {
        if (value instanceof String) {
            json.addProperty(key, (String) value);
            save();
            return this;
        }

        if (value instanceof Character) {
            json.addProperty(key, (Character) value);
            save();
            return this;
        }

        if (value instanceof Number) {
            json.addProperty(key, (Number) value);
            save();
            return this;
        }

        if (value instanceof Boolean) {
            json.addProperty(key, (Boolean) value);
            save();
            return this;
        }

        save();
        json.add(key, (JsonElement) value);
        return this;
    }

    public void save() {
        pool.execute(() -> {
            try (final PrintWriter writer = new PrintWriter(file)) {
                writer.print(gson.toJson(json));
                writer.flush();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public String getString(String key) {
        return json.get(key).getAsString();
    }

    public double getDouble(String key) {
        return json.get(key).getAsDouble();
    }

    public float getFloat(String key) {
        return json.get(key).getAsFloat();
    }

    public long getLong(String key) {
        return json.get(key).getAsLong();
    }

    public int getInt(String key) {
        return json.get(key).getAsInt();
    }

    public byte getByte(String key) {
        return json.get(key).getAsByte();
    }

    public char getChar(String key) {
        return json.get(key).getAsCharacter();
    }

    public boolean getBoolean(String key) {
        return json.get(key).getAsBoolean();
    }

    public JsonArray getArray(String key) {
        return json.get(key).getAsJsonArray();
    }

}
