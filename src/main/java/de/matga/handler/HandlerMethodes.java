package de.matga.handler;

import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HandlerMethodes {

    private Map<String, String> getParameters(HttpExchange exchange){

        String query = exchange.getRequestURI().getQuery();

        if (query == null){
            return null;
        }

        Map<String, String> parameters = new HashMap<>();

        for (String s : query.split("&")){
            String[] splitParameter = s.split("=");
            parameters.put(splitParameter[0], splitParameter[1]);
        }

        return parameters;

    }

    public boolean exists(String key, HttpExchange exchange) {
        return getParameters(exchange) != null && getParameters(exchange).containsKey(key);
    }

    public String getParameter(String key, HttpExchange exchange){
        return getParameters(exchange).get(key);
    }

    protected String getBody(HttpExchange exchange) {
        final StringBuilder builder = new StringBuilder();
        final InputStream stream = exchange.getRequestBody();

        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            reader.lines().forEach(builder::append);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return builder.toString();
    }

    protected void response(int code, String response, HttpExchange exchange) {
        try (final OutputStream stream = exchange.getResponseBody()) {
            final byte[] output = response.getBytes(StandardCharsets.UTF_8);

            exchange.sendResponseHeaders(code, output.length);
            stream.write(output);
            stream.flush();
            stream.close();
            exchange.close();

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void updateValue() {

    }

}
