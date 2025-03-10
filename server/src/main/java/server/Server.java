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

        MySQLAuthTokenDAO sqlAuthDao;
        MySQLUserDAO sqlUserDao;
        MySQLGameDAO sqlGameDao;

        try {
            sqlAuthDao = new MySQLAuthTokenDAO();
            sqlUserDao = new MySQLUserDAO();
            sqlGameDao = new MySQLGameDAO();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }

        MemoryAuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();
        UserDAO userDAO = new MemoryUserDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        // Register your endpoints and handle exceptions here.
        try {
            Spark.delete("/db", (request, response) -> {
                sqlUserDao.clear();
                sqlAuthDao.clear();
                sqlGameDao.clear();
                response.status(200);
                return "";
            });
            Spark.delete("/db", (request, response) -> (new ClearHandler(sqlUserDao, sqlAuthDao, sqlGameDao).clear(request, response)));
            Spark.post("/user", (request, response) -> (new RegisterHandler(sqlUserDao, sqlAuthDao).register(request, response)));
            Spark.post("/session", (request, response) -> (new LoginHandler(sqlUserDao, sqlAuthDao).login(request, response)));
            Spark.delete("/session", (request, response) -> (new LogoutHandler(sqlUserDao, sqlAuthDao).logout(request, response)));
            Spark.get("/game", (request, response) -> (new ListGamesHandler(sqlGameDao, sqlAuthDao).listGames(request, response)));
            Spark.post("/game", (request, response) -> (new CreateGameHandler(sqlGameDao, sqlAuthDao).createGame(request, response)));
            Spark.put("/game", (request, response) -> (new JoinGameHandler(sqlGameDao, sqlAuthDao).joinGame(request, response)));

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