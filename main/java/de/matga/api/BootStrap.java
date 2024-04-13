package de.matga.api;

import de.matga.config.Config;
import de.matga.multidb.*;
import de.matga.multidb.cache.DataAPI;
import de.matga.multidb.cache.GlobalCache;
import lombok.Getter;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Getter
public class BootStrap {


    public final Config config;
    public static BootStrap instance;
    private final WebServer server;
    private final GlobalCache cache;
    private final DataAPI api;

    public final DataBaseSwitch dataBaseSwitch;

    public BootStrap() {
        String absolutePath = FileSystems.getDefault().getPath("").toAbsolutePath() + "/config/";

        System.out.println(absolutePath);

        //define needed services for running rest API
        instance = this;
        this.api = new DataAPI();
        this.config = new Config("config", absolutePath);
        int i = 0;


        //go through files and check if config is existing
        for (File file : Objects.requireNonNull(Path.of(absolutePath).toFile().listFiles())){
            if (file.getName().equals("config.json")){
                i = 1;
                break;
            }
        }

        if (i == 0){
            createConfig("config file was created! Configure and then start the rest api again.");
        }

        //check validation of config
        if (!new Checker().isValid()){
            createConfig("Config was recreated from image.",
                    "It got bricked so please pay attention to your configuration!",
                    "You now have to configure it new.");
        }

        this.server = new WebServer();
        this.server.start();

        switch (config.getString("dataBaseType").toLowerCase()){
            case "mongodb" -> this.dataBaseSwitch = new MongoDB();
            case "mariadb" -> this.dataBaseSwitch = new MariaDB();
            case "mysql" -> this.dataBaseSwitch = new MySQL();
            default -> {
                System.out.println("No valid database was selected so automatically the json mode was enabled.");
                this.dataBaseSwitch = new ConfigDB();
            }
        }

        dataBaseSwitch.connect(config.getInt("port"), config.getString("dataBaseAddress"));
        this.cache = new GlobalCache();
    }

    public void shutdown() {
        server.stop();
        dataBaseSwitch.disconnect();
    }

    public void createConfig(String... message) {
        String absolutePath = FileSystems.getDefault().getPath("").toAbsolutePath() + "/config/";

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        //get config from resources path
        try (InputStream inputStream = classloader.getResourceAsStream("config.json")) {

            if (inputStream == null){
                throw new Exception("file was not found");
            }

            //copy config.json to the absolute path
            File file = new File(absolutePath);
            int readBytes;
            byte[] buffer = new byte[4096];

            try (OutputStream outputStream = new FileOutputStream(file + "config.json")) {
                while ((readBytes = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, readBytes);
                }
            }

        } catch (Exception e){
            throw new RuntimeException(e);
        }

        for (String s : message) {
            System.out.println(s);
        }

        System.exit(0);
    }

}
