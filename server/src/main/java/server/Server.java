package server;

import dataaccess.MemoryAuthTokenDAO;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDAO;
import handler.LoginHandler;
import handler.RegisterHandler;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        MemoryAuthTokenDAO authTokenDAO = new MemoryAuthTokenDAO();
        UserDAO userDAO = new MemoryUserDAO();

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