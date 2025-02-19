package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class LogoutHandler {
    private final UserService userService;

    public LogoutHandler(UserDAO userDAO, AuthTokenDAO authTokenDAO) {
        this.userService = new UserService(userDAO, authTokenDAO);
    }

    public Object logout(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            userService.logout(authToken);
            response.status(200);
            return "";
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
