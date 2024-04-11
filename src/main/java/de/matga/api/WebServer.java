package de.matga.api;

import com.sun.net.httpserver.HttpServer;
import de.matga.handler.WebHandler;

import java.io.IOException;
import java.net.InetSocketAddress;

public class WebServer {

    private final HttpServer server;

    public WebServer() {
        //add new http listener
        try {
            this.server = HttpServer.create(new InetSocketAddress("127.0.0.1", 4281), 0);
            server.createContext("/webInteraction/", new WebHandler());
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public void start() {
        if(server == null) {
            throw new NullPointerException("Server cannot be null");
        }

        server.start();
    }

    public void stop() {
        if(server == null) {
            throw new NullPointerException("Server cannot be null");
        }

        server.stop(0);
    }

}
