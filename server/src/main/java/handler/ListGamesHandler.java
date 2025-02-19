package handler;

import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import types.ListGamesResult;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Collection;
import java.util.Map;

public class ListGamesHandler {
    private final GameService gameService;

    public ListGamesHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
    }

    public Object listGames(Request request, Response response) {
        try {
            String authToken = request.headers("authorization");
            Collection<ListGamesResult> games = gameService.listGames(authToken);
            return new Gson().toJson(Map.of("games", games));
        } catch (Exception e) {
            if (e instanceof GameService.InvalidAuthTokenException) {
                response.status(401);
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
