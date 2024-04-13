package de.matga.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.matga.api.BootStrap;
import de.matga.multidb.DataBaseSwitch;
import de.matga.multidb.cache.DataAPI;

public class WebHandler extends HandlerMethodes implements HttpHandler {

    private final BootStrap bootStrap = BootStrap.instance;

    @Override
    public void handle(HttpExchange exchange) {

        if (!exchange.getRemoteAddress().getAddress().equals("127.0.0.1")){
            response(403, "access forbidden", exchange);
            return;
        }

        boolean hasCache = bootStrap.getConfig().getBoolean("enableCache");
        DataAPI api = bootStrap.getApi();

        //search for parameter "database" in request string and return if connection is not valid
        if (!exists("database", exchange)) {
            response(404, "There was no given goal database. Please insert parameter 'database=<database name>'", exchange);
            return;
        }

        if (!(exists("searchField", exchange) && exchange.getRequestMethod().equals("POST"))) {
            response(404, "There was no given field to search for", exchange);
            return;
        }

        final String goalDatabase = getParameter("database", exchange);
        final String goalField;
        final DataBaseSwitch databaseManager = bootStrap.getDataBaseSwitch();
        final Gson gson = new Gson();

        if (!databaseManager.getCollections().containsKey(goalDatabase)) {
            databaseManager.addCollection(goalDatabase);
        }

        switch (exchange.getRequestMethod()) {

            case "GET" -> {
                goalField = getParameter("searchField", exchange);
                String key = getParameter(goalField, exchange);
                if (exists(goalField, exchange)) {
                    Object o;

                    if (hasCache) {

                        o = api.get(goalDatabase, goalField, key);

                        if (o == null) {
                            response(404, "not found", exchange);
                            return;
                        }

                    } else {

                        //initialize document
                        o = databaseManager.find(goalDatabase, goalField, key);
                        if (o == null) {
                            //return not found
                            response(404,
                                    "document not found (" + key + ")",
                                    exchange);
                            return;
                        }
                        //return successful
                    }
                    response(200, gson.toJson(o), exchange);
                }
            }

            case "PUT" -> {
                goalField = getParameter("searchField", exchange);
                String key = getParameter(goalField, exchange);

                if (exists(goalField, exchange)) {

                    if (hasCache) {
                        api.set(goalField, gson.toJson(getBody(exchange)), goalDatabase);
                        response(200, "successfull update", exchange);
                    } else {
                        //put
                        databaseManager.update(gson.toJson(getBody(exchange)), goalDatabase, goalField, key);
                        response(200, "successfull update", exchange);
                    }
                    return;
                }


                response(404, "parameter was not found for document: " + getParameter(goalField, exchange), exchange);
            }

            case "POST" -> {

                if (hasCache) {
                    goalField = getParameter("searchField", exchange);
                    String key = getParameter(goalField, exchange);

                    api.post(goalDatabase, goalField, key, gson.toJson(getBody(exchange)));
                }

                databaseManager.post(gson.toJson(getBody(exchange)), goalDatabase);

            }

            case "DELETE" -> {

                goalField = getParameter("searchField", exchange);
                String key = getParameter(goalField, exchange);

                if ((!exists(goalField, exchange))) {
                    response(404, "field not found", exchange);
                    return;
                }

                if (hasCache) {
                    api.delete(goalDatabase, goalField, key);

                    response(200, "success", exchange);
                } else {

                    if (databaseManager.find(goalDatabase, goalField, key) == null) {
                        response(404, "not found eighter the search field was not given or the object is null", exchange);
                        return;
                    }

                    databaseManager.delete(goalDatabase, goalField, key);
                    response(200, "success", exchange);

                }
            }
        }
    }
}
