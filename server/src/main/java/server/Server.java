package server;

import dataaccess.*;
import handler.CreateGameHandler;
import handler.LoginHandler;
import handler.LogoutHandler;
import handler.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        MemoryAuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Register your endpoints and handle exceptions here.
        try {
            Spark.delete("/db", (request, response) -> {
                userDAO.clear();
                authTokenDAO.clearAuth();
                response.status(200);
                return "";
            });
            Spark.post("/user", (request, response) -> (new RegisterHandler(userDAO, authTokenDAO).register(request, response)));
            Spark.post("/session", (request, response) -> (new LoginHandler(userDAO, authTokenDAO).login(request, response)));
            Spark.delete("/session", (request, response) -> (new LogoutHandler(userDAO, authTokenDAO).logout(request, response)));

            Spark.post("/game", (request, response) -> (new CreateGameHandler(gameDAO, authTokenDAO).createGame(request, response)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();
        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}