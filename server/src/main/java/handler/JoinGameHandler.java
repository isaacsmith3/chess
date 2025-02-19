package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import endpoint.JoinGameRequest;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Map;

public class JoinGameHandler {
    private final GameService gameService;

    public JoinGameHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    public Object joinGame(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            JoinGameRequest gameRequest = new Gson().fromJson(request.body(), JoinGameRequest.class);
            gameService.joinGame(authToken, gameRequest);
            return "";
        } catch (Exception e) {
            if (e instanceof GameService.InvalidAuthTokenException) {
                response.status(401);
            } else if (e instanceof GameService.InvalidGameException) {
                response.status(403);
            }
            else if (e instanceof GameService.InvalidGameRequestException) {
                response.status(400);
            }
            else {
                response.status(400);
            }
            return new Gson().toJson(Map.of(
                    "message", "Error: " + e.getMessage(),
                    "success", false
            ));
        }
    }
}
