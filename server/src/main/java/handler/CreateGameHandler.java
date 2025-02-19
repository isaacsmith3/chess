package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import endpoint.GameResult;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class CreateGameHandler {
    private final GameService gameService;

    public CreateGameHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    public Object createGame(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            String gameName = new Gson().fromJson(request.body(), GameData.class).gameName();
            GameResult gameResult = gameService.createGame(authToken, gameName);
            response.status(200);
            return new Gson().toJson(gameResult);
        } catch (Exception e) {
            response.status(500);
            return new Gson().toJson(Map.of(
                    "message", "Error: " + e.getMessage(),
                    "success", false
            ));
        }
    }


}
