package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import types.AuthResult;
import model.UserData;
import spark.Request;
import spark.Response;
import service.UserService;

import java.util.Map;

public class LoginHandler {

    private final UserService userService;

    public LoginHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userService = new UserService(userDAO, authTokenDAO);
    }

    public Object login(Request request, Response response) {
        try {
            var user = new Gson().fromJson(request.body(), UserData.class);
            AuthResult loginResult = userService.login(user);
            response.status(200);
            return new Gson().toJson(loginResult);
        } catch (Exception e) {
            if (e instanceof UserService.InvalidCredentialsException) {
                response.status(401); // Unauthorized
            } else {
                response.status(400);
            }
            return new Gson().toJson(Map.of(
                    "message", "Error: " + e.getMessage(),
                    "success", false
            ));
        }

    }

}
