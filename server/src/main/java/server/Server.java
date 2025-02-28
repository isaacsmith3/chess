package server;

import dataaccess.*;
import dataaccess.memory.MemoryAuthTokenDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import handler.*;
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
                gameDAO.clear();
                response.status(200);
                return "";
            });
            Spark.delete("/db", (request, response) -> (new ClearHandler(userDAO, authTokenDAO, gameDAO).clear(request, response)));
            Spark.post("/user", (request, response) -> (new RegisterHandler(userDAO, authTokenDAO).register(request, response)));
            Spark.post("/session", (request, response) -> (new LoginHandler(userDAO, authTokenDAO).login(request, response)));
            Spark.delete("/session", (request, response) -> (new LogoutHandler(userDAO, authTokenDAO).logout(request, response)));
            Spark.get("/game", (request, response) -> (new ListGamesHandler(gameDAO, authTokenDAO).listGames(request, response)));
            Spark.post("/game", (request, response) -> (new CreateGameHandler(gameDAO, authTokenDAO).createGame(request, response)));
            Spark.put("/game", (request, response) -> (new JoinGameHandler(gameDAO, authTokenDAO).joinGame(request, response)));

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