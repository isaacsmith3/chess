package handler;

import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import service.ClearService;
import spark.Request;
import spark.Response;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO, GameDAO gameDAO) {
        this.clearService = new ClearService(userDAO, authTokenDAO, gameDAO);
    }

    public Object clear(Request request, Response response) {
        clearService.clear();
        response.status(200);
        return "";
    }
}
