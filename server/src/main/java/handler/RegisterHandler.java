package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.UserDAO;
import endpoint.RegisterResult;
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
            RegisterResult registerResult = userService.register(user);
            response.status(200);
            return new Gson().toJson(registerResult);

        } catch (Exception e) {
                if (e instanceof UserService.DuplicateUserException) {
                    response.status(403); // HTTP 403 Forbidden for duplicate users
                } else {
                    response.status(400); // HTTP 400 for other errors
                }
                return new Gson().toJson(Map.of(
                        "message", "Error: " + e.getMessage(),
                        "success", false
                ));
        }
    }
}
