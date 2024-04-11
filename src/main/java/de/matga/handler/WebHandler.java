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
            response(404, "There was no givin goal database. Please insert parameter 'database=<database name>'", exchange);
            return;
        }

        final String goalDatabase = getParameter("database", exchange);
        final DatabaseManager databaseManager = bootStrap.getDatabaseManager();

        if (!databaseManager.getDataCollections().containsKey(goalDatabase)){
            databaseManager.addDataCollection(goalDatabase);
        }

        switch (exchange.getRequestMethod()) {

            case "GET" -> {

                if (exists("uuid", exchange)) {

                    Document document = databaseManager.getDataCollections().get(goalDatabase)
                            .find(Filters.eq("uuid", getParameter("uuid", exchange))).first();

                    if (document == null) {
                        response(404,
                                "dungeonplayer not found (" + getParameter("uuid", exchange) + ")",
                                exchange);
                        return;
                    }

                    response(200, document.toJson(), exchange);

                }

            }

            case "PUT" -> {

                if (exists("uuid", exchange)) {

                    String jsonPlayer = getBody(exchange);

                    databaseManager.getDataCollections().get(goalDatabase)
                            .findOneAndReplace(Filters.eq("uuid", getParameter("uuid", exchange)),
                                    Document.parse(jsonPlayer));

                    return;

                }

                response(404, "parameter was not found for dungeon player: " + getParameter("uuid", exchange), exchange);

            }

            case "POST" -> {

                String jsonPlayer = getBody(exchange);

                databaseManager.getDataCollections().get(goalDatabase)
                        .insertOne(Document.parse(jsonPlayer));

            }

            case "DELETE" -> {


                if (exists("uuid", exchange)
                        || databaseManager.getDataCollections().get(goalDatabase)
                        .find(Filters.eq("uuid", getParameter("uuid", exchange))) == null) {
                    response(404, "not found eighter the uuid was not given or the object is null", exchange);
                    return;
                }


                databaseManager.getDataCollections().get(goalDatabase)
                        .deleteOne(Filters.eq("uuid", getParameter("uuid", exchange)));

            }

        }

    }

}
