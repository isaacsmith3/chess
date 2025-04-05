package server.websocket;

import chess.ChessGame;
import chess.ChessPosition;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.AuthTokenDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import types.JoinGameRequest;
import types.UpdateGameRequest;
import websocket.commands.*;
import websocket.messages.LoadGame;
import websocket.messages.Notification;
import websocket.messages.ServerMessage;
import websocket.messages.Error;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    // Session
    public boolean gameOver;
    private final GameService gameService;
    private final ConnectionManager connectionManager = new ConnectionManager();

    public WebSocketHandler(GameDAO gameDAO, AuthTokenDAO authTokenDAO) {
        this.gameService = new GameService(gameDAO, authTokenDAO);
//        this.gameOver = false;
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("WebSocket connection closed: " + reason);
        connectionManager.removeSession(session);
    }

    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("WebSocket connection established from: " + session.getRemoteAddress());
        System.out.println("Connection parameters: " + session.getUpgradeRequest().getParameterMap());
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
        error.printStackTrace();
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws GameService.InvalidAuthTokenException, IOException {
        UserGameCommand cmd = new Gson().fromJson(message, UserGameCommand.class);
        System.out.println(cmd);

        switch (cmd.getCommandType()) {
            case CONNECT -> connect(new Gson().fromJson(message, ConnectCommand.class), session);
            case MAKE_MOVE -> makeMove(new Gson().fromJson(message, MakeMoveCommand.class), session);
            case RESIGN -> resign(new Gson().fromJson(message, ResignCommand.class), session);
            case LEAVE -> leave(new Gson().fromJson(message, LeaveCommand.class), session);
        }
    }

    private void leave(LeaveCommand cmd, Session session) throws GameService.InvalidAuthTokenException, IOException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        try {
            GameData gameData = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (gameData == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
            } else {
                AuthData authData = gameService.getUserNameByAuthToken(cmd.getAuthToken());
                String username = authData.userName();

                boolean isWhitePlayer = username.equals(gameData.whiteUsername());
                boolean isBlackPlayer = username.equals(gameData.blackUsername());
                boolean isObserver = !isWhitePlayer && !isBlackPlayer;

                if (isWhitePlayer || isBlackPlayer) {
                    String playerColor = isWhitePlayer ? "WHITE" : "BLACK";
                    gameService.leaveGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), playerColor, gameData.game()));
                    String message = username + " stopped playing the game";
                    Notification notification = new Notification(
                            ServerMessage.ServerMessageType.NOTIFICATION,
                            message
                    );
                    connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());
                } else if (isObserver) {
                    String message = username + " stopped observing the game";
                    Notification notification = new Notification(
                            ServerMessage.ServerMessageType.NOTIFICATION,
                            message
                    );
                    connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());
                }

                connectionManager.connections.remove(cmd.getAuthToken());

                Notification confirmation = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        "You have left the game"
                );
                connectionManager.sendMessage(cmd.getAuthToken(), confirmation);

            }
        } catch (Exception e) {
            System.err.println("Error in connect handler: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private void resign(ResignCommand cmd, Session session) throws GameService.InvalidAuthTokenException, IOException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        try {
            Result result = getResult(cmd);
            if (result == null) {
                return;
            }

            ChessGame chessGame = result.gameData().game();

            if (chessGame.isGameOver()) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            boolean isWhitePlayer = result.username().equals(result.gameData().whiteUsername());
            boolean isBlackPlayer = result.username().equals(result.gameData().blackUsername());

            if (!isWhitePlayer && !isBlackPlayer) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: only players can resign");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            // check if game is already over
            boolean isGameOver = chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.BLACK);

            if (isGameOver) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            chessGame.setGameOver(true);

            gameService.updateGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), null, chessGame));

            String message = result.username() + " resigned the game";
            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    message
            );

            for (Connection connection : connectionManager.connections.values()) {
                if (connection.gameId == cmd.getGameID()) {
                    connectionManager.sendMessage(connection.authToken, notification);
                }
            }

        } catch (GameService.InvalidGameException e) {
            throw new RuntimeException(e);
        } catch (GameService.InvalidCredentialsException e) {
            throw new RuntimeException(e);
        } catch (GameService.InvalidGameRequestException e) {
            throw new RuntimeException(e);
        }

    }

    private Result getResult(ResignCommand cmd) throws GameService.InvalidAuthTokenException, IOException {
        GameData gameData = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

        if (gameData == null) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
            connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
            return null;
        }

        AuthData authData = gameService.getUserNameByAuthToken(cmd.getAuthToken());
        String username = authData.userName();
        Result result = new Result(gameData, username);
        return result;
    }

    private record Result(GameData gameData, String username) {
    }

    private void makeMove(MakeMoveCommand cmd, Session session) throws GameService.InvalidAuthTokenException, IOException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        try {
            GameData gameData = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (gameData == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            AuthData authData = gameService.getUserNameByAuthToken(cmd.getAuthToken());
            String username = authData.userName();

            ChessGame chessGame = gameData.game();
            ChessGame.TeamColor currentTurn = chessGame.getTeamTurn();

            if (chessGame.isGameOver()) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is already over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            boolean isWhitePlayer = username.equals(gameData.whiteUsername());
            boolean isBlackPlayer = username.equals(gameData.blackUsername());

            boolean isGameOver = chessGame.isInCheckmate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInCheckmate(ChessGame.TeamColor.BLACK) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.WHITE) ||
                    chessGame.isInStalemate(ChessGame.TeamColor.BLACK);

            if (isGameOver) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game is over");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            if ((isWhitePlayer && currentTurn != ChessGame.TeamColor.WHITE) ||
                    (isBlackPlayer && currentTurn != ChessGame.TeamColor.BLACK) ||
                    (!isWhitePlayer && !isBlackPlayer)) {

                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: not your turn");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
                return;
            }

            chessGame.makeMove(cmd.getMove());
            gameService.updateGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), null, chessGame));

            GameData updatedGame = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));
            ChessGame updatedChessGame = updatedGame.game();

            LoadGame loadGameMessage = new LoadGame(updatedGame);
            for (Connection connection : connectionManager.connections.values()) {
                if (connection.gameId == cmd.getGameID()) {
                    connectionManager.sendMessage(connection.authToken, loadGameMessage);
                }
            }

            String startSquare = formatPositionToAlgebraic(cmd.move.getStartPosition());
            String endSquare = formatPositionToAlgebraic(cmd.move.getEndPosition());

            Notification notification = new Notification(
                    ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " moved from " + startSquare + " to " + endSquare
            );
            connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());

            ChessGame.TeamColor opposingTeam = updatedChessGame.getTeamTurn();

            if (updatedChessGame.isInCheckmate(opposingTeam)) {
                String checkmatedPlayer = opposingTeam == ChessGame.TeamColor.WHITE ?
                        updatedGame.whiteUsername() : updatedGame.blackUsername();
                Notification checkmateNotification = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        checkmatedPlayer + " is in checkmate"
                );
                for (Connection connection : connectionManager.connections.values()) {
                    if (connection.gameId == cmd.getGameID()) {
                        connectionManager.sendMessage(connection.authToken, checkmateNotification);
                    }
                }

                updatedChessGame.setGameOver(true);
                gameService.updateGame(cmd.getAuthToken(), new UpdateGameRequest(cmd.getGameID(), null, updatedChessGame));
            }
            else if (updatedChessGame.isInCheck(opposingTeam)) {
                String checkedPlayer = opposingTeam == ChessGame.TeamColor.WHITE ?
                        updatedGame.whiteUsername() : updatedGame.blackUsername();
                Notification checkNotification = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        checkedPlayer + " is in check"
                );
                for (Connection connection : connectionManager.connections.values()) {
                    if (connection.gameId == cmd.getGameID()) {
                        connectionManager.sendMessage(connection.authToken, checkNotification);
                    }
                }
            }

        } catch (GameService.InvalidAuthTokenException e){
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
        } catch (InvalidMoveException e) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid move");
            connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
        } catch (GameService.InvalidGameException e) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid game");
            connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
        } catch (GameService.InvalidCredentialsException | GameService.InvalidGameRequestException e) {
            throw new RuntimeException(e);
        }
    }


    private void connect(ConnectCommand cmd, Session session) throws IOException, GameService.InvalidAuthTokenException {
        if (!gameService.verifyAuthToken(cmd.getAuthToken())) {
            Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: invalid auth token");
            connectionManager.sendMessage(session, errorMessage);
            return;
        }

        connectionManager.add(cmd.getAuthToken(), session, cmd.getGameID());

        try {
            GameData game = gameService.getGame(new JoinGameRequest(cmd.getGameID(), null));

            if (game != null && game.game().isGameOver()) {
                Notification notification = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        "This game has already ended."
                );
                connectionManager.sendMessage(cmd.getAuthToken(), notification);
            }

            if (game == null) {
                Error errorMessage = new Error(ServerMessage.ServerMessageType.ERROR, "Error: game not found");
                connectionManager.sendMessage(cmd.getAuthToken(), errorMessage);
            } else {
                LoadGame loadGameMessage = new LoadGame(game);
                connectionManager.sendMessage(cmd.getAuthToken(), loadGameMessage);

                if (cmd.playerColor == null) {
                    Notification notification = new Notification(
                            ServerMessage.ServerMessageType.NOTIFICATION,
                            cmd.username + " connected to the game as observer"
                    );
                    connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());
                    return;
                }

                Notification notification = new Notification(
                        ServerMessage.ServerMessageType.NOTIFICATION,
                        cmd.username + " connected to the game as " + cmd.playerColor
                );
                connectionManager.broadcast(cmd.getAuthToken(), notification, cmd.getGameID());
            }
        } catch (Exception e) {
            System.err.println("Error in connect handler: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String formatPositionToAlgebraic(ChessPosition position) {
        char file = (char)('a' + position.getColumn() - 1);
        int rank = position.getRow();
        return file + "" + rank;
    }



}





