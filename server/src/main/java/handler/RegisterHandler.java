package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import endpoint.RegisterRequest;
import endpoint.RegisterResult;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegisterHandler {

    private final UserService userService;

    public RegisterHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userService = new UserService(userDAO, authTokenDAO);
    }

    public Object register(Request request, Response response) {
        try {
            var user = new Gson().fromJson(request.body(), UserData.class);
            AuthData authData = userService.register(user);
            response.status(200);
            return new Gson().toJson(authData);
        } catch (Exception e) {
            response.status(400);
            return new Gson().toJson(Map.of("error", e.getMessage()));
        }

    }
}
