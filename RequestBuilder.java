package de.matga.ufoapi.rest;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ReqBuilder {

    private String jsonString;
    private String goalDataCollection;
    private String reqMethode;
    private String parameter;
    private boolean async;
    private final HttpClient client;

    public ReqBuilder() {
        this.async = true;
        this.client = HttpClient.newHttpClient();
    }

    public ReqBuilder async(boolean async) {
        this.async = async;
        return this;
    }

    public ReqBuilder addParameter(Map<String, String> parameters) {
        parameters.forEach((key, value) -> parameter = parameter + key + "=" + value + "&");
        this.parameter = parameter.substring(0, parameter.length() - 1);
        return this;
    }

    public ReqBuilder addObject(Object object) {
        this.jsonString = new Gson().toJson(object);
        return this;
    }

    public ReqBuilder setGoalDataCollection(String goalDataCollection) {
        this.goalDataCollection = goalDataCollection;
        return this;
    }

    public ReqBuilder setMethod(String method) {
        this.reqMethode = method;
        return this;
    }

    public String build() {

        final HttpRequest request;
        final HttpResponse<String> response;

        try {

            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(new URI("http://127.0.0.1/" + goalDataCollection.toLowerCase().replace("_", "") + "/" + parameter))
                    .timeout(Duration.ofMillis(5000));

            switch (reqMethode) {
                case "GET" -> builder.GET();
                case "POST" -> builder.POST(HttpRequest.BodyPublishers.ofString(jsonString));
                case "DELETE" -> builder.DELETE();
                case "PUT" -> builder.PUT(HttpRequest.BodyPublishers.ofString(jsonString));
            }

            if (async) {
                CompletableFuture.supplyAsync(()->{

                    try {

                        HttpRequest.Builder asyncBuilder = HttpRequest.newBuilder()
                                .uri(new URI("http://127.0.0.1/" + goalDataCollection.toLowerCase().replace("_", "") + "/" + parameter))
                                .timeout(Duration.ofMillis(5000));

                        switch (reqMethode) {
                            case "GET" -> asyncBuilder.GET();
                            case "POST" -> asyncBuilder.POST(HttpRequest.BodyPublishers.ofString(jsonString));
                            case "DELETE" -> asyncBuilder.DELETE();
                            case "PUT" -> asyncBuilder.PUT(HttpRequest.BodyPublishers.ofString(jsonString));
                        }

                        final HttpRequest asyncRequest;
                        final HttpResponse<String> asyncResponse;

                        asyncRequest = asyncBuilder.build();
                        asyncResponse = client.send(asyncRequest, HttpResponse.BodyHandlers.ofString());

                        if (!(asyncResponse.statusCode() < 299 && asyncResponse.statusCode() > 200)) {
                            System.out.println("Response exit with code " + asyncResponse.statusCode());
                            return null;
                        }

                        if (reqMethode.equals("GET")) {
                            return new Gson().toJson(asyncResponse.body());
                        } else {
                            return "success";
                        }
                    } catch (IOException | InterruptedException | URISyntaxException e) {
                        throw new RuntimeException(e);
                    }

                });


            } else {
                request = builder.build();
                response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (!(response.statusCode() < 299 && response.statusCode() > 200)) {
                    System.out.println("Response exit with code " + response.statusCode());
                    return null;
                }
                if (reqMethode.equals("GET")) {
                    return new Gson().toJson(response.body());
                } else {
                    return "success";
                }
            }


        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

}
