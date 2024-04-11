package de.matga.handler;

import com.mongodb.client.model.Filters;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import de.matga.api.BootStrap;
import de.matga.api.DatabaseManager;
import org.bson.Document;

public class WebHandler extends HandlerMethodes implements HttpHandler {

    private final BootStrap bootStrap = BootStrap.getInstance();

    @Override
    public void handle(HttpExchange exchange) {

        //search for parameter "database" in request string and return if connection is not valid
        if (!exists("database", exchange)){
            response(404, "There was no given goal database. Please insert parameter 'database=<database name>'", exchange);
            return;
        }

        if (!(exists("searchField", exchange) && exchange.getRequestMethod().equals("POST"))){
            response(404, "There was no given field to search for", exchange);
        }

        final String goalDatabase = getParameter("database", exchange);
        final String goalField;
        final DatabaseManager databaseManager = bootStrap.getDatabaseManager();

        if (!databaseManager.getDataCollections().containsKey(goalDatabase)){
            databaseManager.addDataCollection(goalDatabase);
        }

        switch (exchange.getRequestMethod()) {

            case "GET" -> {
                
                goalField = getParameter("searchField", exchange);

                if (exists(goalField, exchange)) {

                    //initialize document
                    Document document = databaseManager.getDataCollections().get(goalDatabase)
                            .find(Filters.eq(goalField, getParameter(goalField, exchange))).first();

                    if (document == null) {
                        //return not found
                        response(404,
                                "dungeonplayer not found (" + getParameter(goalField, exchange) + ")",
                                exchange);
                        return;
                    }

                    //return successful
                    response(200, document.toJson(), exchange);

                }

            }

            case "PUT" -> {

                goalField = getParameter("searchField", exchange);

                if (exists(goalField, exchange)) {

                    //put
                    String jsonPlayer = getBody(exchange);

                    databaseManager.getDataCollections().get(goalDatabase)
                            .findOneAndReplace(Filters.eq(goalField, getParameter(goalField, exchange)),
                                    Document.parse(jsonPlayer));

                    return;

                }

                response(404, "parameter was not found for dungeon player: " + getParameter(goalField, exchange), exchange);

            }

            case "POST" -> {

                String jsonPlayer = getBody(exchange);

                databaseManager.getDataCollections().get(goalDatabase)
                        .insertOne(Document.parse(jsonPlayer));

            }

            case "DELETE" -> {

                goalField = getParameter("searchField", exchange);

                if (exists(goalField, exchange)
                        || databaseManager.getDataCollections().get(goalDatabase)
                        .find(Filters.eq(goalField, getParameter(goalField, exchange))) == null) {
                    response(404, "not found eighter the uuid was not given or the object is null", exchange);
                    return;
                }


                databaseManager.getDataCollections().get(goalDatabase)
                        .deleteOne(Filters.eq(goalField, getParameter(goalField, exchange)));

            }

        }

    }

}
