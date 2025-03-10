package server;

import dataaccess.*;
import dataaccess.memory.MemoryAuthTokenDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.mysql.MySQLAuthTokenDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import handler.*;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        MySQLAuthTokenDAO SQLauthTokenDAO;
        MySQLUserDAO SQLuserDAO;
        MySQLGameDAO SQLgameDAO;

        try {
            SQLauthTokenDAO = new MySQLAuthTokenDAO();
            SQLuserDAO = new MySQLUserDAO();
            SQLgameDAO = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        MemoryAuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Register your endpoints and handle exceptions here.
        try {
            Spark.delete("/db", (request, response) -> {
                SQLuserDAO.clear();
                SQLauthTokenDAO.clear();
                SQLgameDAO.clear();
                response.status(200);
                return "";
            });
            Spark.delete("/db", (request, response) -> (new ClearHandler(SQLuserDAO, SQLauthTokenDAO, SQLgameDAO).clear(request, response)));
            Spark.post("/user", (request, response) -> (new RegisterHandler(SQLuserDAO, SQLauthTokenDAO).register(request, response)));
            Spark.post("/session", (request, response) -> (new LoginHandler(SQLuserDAO, SQLauthTokenDAO).login(request, response)));
            Spark.delete("/session", (request, response) -> (new LogoutHandler(SQLuserDAO, SQLauthTokenDAO).logout(request, response)));
            Spark.get("/game", (request, response) -> (new ListGamesHandler(SQLgameDAO, SQLauthTokenDAO).listGames(request, response)));
            Spark.post("/game", (request, response) -> (new CreateGameHandler(SQLgameDAO, SQLauthTokenDAO).createGame(request, response)));
            Spark.put("/game", (request, response) -> (new JoinGameHandler(SQLgameDAO, SQLauthTokenDAO).joinGame(request, response)));

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