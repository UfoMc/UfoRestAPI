package de.matga.api;

import com.sun.net.httpserver.HttpServer;
import de.matga.config.Config;
import de.matga.handler.WebHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {

    private final HttpServer server;
    private final Config config;

    public WebServer() {
        config = BootStrap.instance.getConfig();
        //add new http listener
        try {
            this.server = HttpServer.create(new InetSocketAddress(config.getString("httpAddress"), config.getInt("httpPort")), 0);
            //bind a new WebHandler to the http path http://127.0.0.1/<your path>/ wich has HttpHandler implemented
            server.createContext("/" + config.getString("httpPath") + "/", new WebHandler());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void start() {
        //start http listener
        if(server == null) {
            throw new NullPointerException("Server cannot be null");
        }

        server.start();
    }

    public void stop() {
        //stop http listener
        if(server == null) {
            throw new NullPointerException("Server cannot be null");
        }

        server.stop(0);
    }

}
