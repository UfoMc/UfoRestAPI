package de.matga.api;

import lombok.Getter;

public class BootStrap {

    @Getter
    private static BootStrap instance;
    private final WebServer server;
    @Getter
    private final DatabaseManager databaseManager;

    public BootStrap() {
        //define needed services for running rest API
        instance = this;
        this.server = new WebServer();
        this.server.start();
        this.databaseManager = new DatabaseManager();
    }

    public void shutdown() {
        server.stop();
        databaseManager.stop();
    }

}
