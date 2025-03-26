package server;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import exception.ResponseException;
import types.ListGamesResult;
import ui.EscapeSequences;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.Integer.parseInt;

public class PostLoginClient {
    private final ServerFacade serverFacade;
    private String authToken;
    private List<ListGamesResult> cachedGames;

    public PostLoginClient(String serverUrl) {
        this.serverFacade = new ServerFacade(serverUrl);
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String eval(String input) {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "logout":
                return logout(authToken);
            case "list":
                return list();
            case "create":
                if (tokens.length != 2) {
                    return "Usage: create <NAME>";
                }
                return createGame(tokens[1], authToken);
            case "join":
                if (tokens.length != 3) {
                    return "Usage: join <ID> [WHITE|BLACK]";
                }

                try {
                    int gameId = Integer.parseInt(tokens[1]);
                } catch (NumberFormatException e) {
                    return "Game ID must be a valid integer.";
                }

                String color = tokens[2].toUpperCase();
                if (!color.equals("WHITE") && !color.equals("BLACK")) {
                    return "Invalid color. Please use WHITE or BLACK.";
                }

                return joinGame(tokens[1], tokens[2]);
            case "observe":
                if (tokens.length != 2) {
                    return "Usage: observe <ID>";
                }
                ChessGame game = new ChessGame();
                return drawBoard("WHITE", game.getBoard());

        }
        return "Invalid command";
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("logout - logout of your account\n");
        output.append("list - list all games\n");
        output.append("create <NAME> - create a new game\n");
        output.append("join <ID> [WHITE|BLACK] - join an existing game \n");
        output.append("observe <ID> - observe a game\n");
        output.append("quit - quit the program\n");

        return output.toString();
    }

    public String logout(String authToken) {
        try {
            serverFacade.logout(authToken);
            return "Logged Out Successfully";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String list() {
        try {
            Collection<ListGamesResult> games = serverFacade.listGames(authToken);
            this.cachedGames = new ArrayList<>(games);

            StringBuilder output = new StringBuilder();
            output.append("                           GAME LIST                          \n");
            output.append("--------------------------------------------------------------\n");
            output.append("| NUM  | GAME NAME             | WHITE PLAYER | BLACK PLAYER |\n");
            output.append("--------------------------------------------------------------\n");

            int number = 0;
            for (ListGamesResult g : games) {
                String whitePlayer = (g.whiteUsername() != null) ? g.whiteUsername() : "-";
                String blackPlayer = (g.blackUsername() != null) ? g.blackUsername() : "-";

                output.append(String.format("| %-4d | %-21s | %-12s | %-11s  |\n",
                        number,
                        g.gameName(),
                        whitePlayer,
                        blackPlayer));
                number++;
            }
            output.append("--------------------------------------------------------------\n");

            return output.toString();
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String createGame(String gameName, String authToken) {
        try {
            serverFacade.createGame(gameName, authToken);
            return gameName + " Game Created Successfully";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String joinGame(String gameId, String playerColor) {
        try {
            int displayedGameId = parseInt(gameId);

            if (cachedGames == null || cachedGames.isEmpty()) {
                Collection<ListGamesResult> gamesCollection = serverFacade.listGames(authToken);
                this.cachedGames = new ArrayList<>(gamesCollection);
            }

            if (displayedGameId < 0 || displayedGameId >= cachedGames.size()) {
                return "Invalid game ID. Use the 'list' command to see valid IDs";
            }

            ListGamesResult selectedGame = cachedGames.get(displayedGameId);

            playerColor = playerColor.toUpperCase();
            if (!playerColor.equals("WHITE") && !playerColor.equals("BLACK")) {
                return "Invalid color. Please use WHITE or BLACK";
            }

            types.JoinGameRequest request = new types.JoinGameRequest(selectedGame.gameID(), playerColor);

            serverFacade.joinGame(request, authToken);

            return "Successfully joined game: " + selectedGame.gameName() + " as " + playerColor;

        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    };

    public String drawBoard(String playerColor, ChessBoard board) {
        boolean isBlack = playerColor.equalsIgnoreCase("BLACK");
        StringBuilder boardOutput = new StringBuilder();

        boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);
        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);

        boardOutput.append("\n");
        printLetters(isBlack, boardOutput);

        boardOutput.append("\n");

        for (int row = 0; row < 8; row++) {
            int displayRow = isBlack ? row + 1 : 8 - row;

            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(displayRow).append(" ");

            for (int col = 0; col < 8; col++) {
                int displayCol = isBlack ? 8 - col : col + 1;

                boolean isLightSquare = (row + col) % 2 == 0;
                String bgColor = isLightSquare ? EscapeSequences.SET_BG_COLOR_WHITE : EscapeSequences.SET_BG_COLOR_BLACK;
                boardOutput.append(bgColor);

                ChessPosition position = new ChessPosition(displayRow, displayCol);
                ChessPiece piece = board.getPiece(position);

                if (piece != null) {
                    String textColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE
                            ? EscapeSequences.SET_TEXT_COLOR_BLUE
                            : EscapeSequences.SET_TEXT_COLOR_RED;

                    String pieceChar = getPieceChar(piece.getPieceType());
                    boardOutput.append(" ").append(textColor).append(pieceChar).append(" ");
                } else {
                    boardOutput.append("   ");
                }
            }

            boardOutput.append(EscapeSequences.SET_BG_COLOR_LIGHT_GREY);

            boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);
            boardOutput.append(" ").append(displayRow).append("\n");
        }

        boardOutput.append(EscapeSequences.SET_TEXT_COLOR_BLACK);

        printLetters(isBlack, boardOutput);

        boardOutput.append(EscapeSequences.RESET_BG_COLOR);
        boardOutput.append(EscapeSequences.RESET_TEXT_COLOR);
        boardOutput.append("\n");

        return boardOutput.toString();
    }

    private void printLetters(boolean isBlack, StringBuilder boardOutput) {
        boardOutput.append("  ");
        for (int col = 0; col < 8; col++) {
            char file;
            if (isBlack) {
                file = (char) ('a' + (7 - col));
            } else {
                file = (char) ('a' + col);
            }
            boardOutput.append(" ").append(file).append(" ");
        }
    }

    private String getPieceChar(ChessPiece.PieceType type) {
        switch (type) {
            case KING: return "K";
            case QUEEN: return "Q";
            case BISHOP: return "B";
            case KNIGHT: return "N";
            case ROOK: return "R";
            case PAWN: return "P";
            default: return " ";
        }
    }



}
