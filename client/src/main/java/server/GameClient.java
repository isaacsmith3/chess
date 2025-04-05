package server;

import chess.*;
import model.AuthData;
import websocket.GameHandler;
import websocket.WebSocketFacade;

import java.io.IOException;
import java.rmi.ServerException;
import java.util.Collection;
import java.util.Scanner;

public class GameClient {
    private String playerColor = null;
    private final int gameId;
    private GameHandler gameHandler;
    private WebSocketFacade webSocketFacade;
    private AuthData authData;

    public GameClient(String serverUrl, String playerColor, int gameId, AuthData authData) throws ServerException {
        this.gameId = gameId;
        this.gameHandler = new GameHandler();
        this.gameHandler.setPlayerColor(playerColor);
        this.webSocketFacade = new WebSocketFacade(serverUrl, gameHandler);
        this.authData = authData;
        this.playerColor = playerColor;
        this.webSocketFacade.connect(this.authData.authToken(), this.gameId, playerColor, this.authData.userName());
    }

    public String eval(String input) throws IOException {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return help();
            case "draw":
                return gameHandler.drawBoard(this.playerColor, this.gameHandler.chessGame.getBoard());
            case "leave":
                webSocketFacade.leave(authData.authToken(), gameId);
                return "Left game successfully";
            case "highlight":
                return highlight();
            case "move":
                return makeMove();
            case "resign":
                return resign();

        }
        return "Invalid command";
    }

    public String evalObserver(String input) throws IOException {
        String[] tokens = input.split(" ");
        String command = tokens[0].toLowerCase();

        switch (command) {
            case "help":
                return helpObserver();
            case "draw":
                return gameHandler.drawBoard(this.playerColor, this.gameHandler.chessGame.getBoard());
            case "leave":
                webSocketFacade.leave(authData.authToken(), gameId);
                return "Left game successfully";
        }
        return "Invalid command";
    }

    public String help() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("draw - redraw the board\n");
        output.append("highlight - highlight the legal moves\n");
        output.append("move - make a move\n");
        output.append("resign - resign and leave the game\n");
        output.append("leave - leave the game\n");
        output.append("quit - quit the program\n");
        return output.toString();
    }

    public String helpObserver() {
        var output = new StringBuilder();
        output.append("Help Menu:\n");
        output.append("help - print this message again :)\n");
        output.append("draw - redraw the board\n");
        output.append("leave - leave the game\n");
        output.append("quit - quit the program\n");
        return output.toString();
    }

    private String makeMove() {
        if (gameHandler.chessGame.isGameOver() || gameHandler.gameOver) {
            return "Game not running";
        }

        String gameTurn = gameHandler.chessGame.getTeamTurn().toString().toLowerCase();
        if(!gameTurn.equals(playerColor)){
            return "Please wait your turn";
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter piece position (like 'e2'):");

        String startInput = scanner.nextLine().toLowerCase().trim();

        if (startInput.length() != 2 ||
                !Character.isLetter(startInput.charAt(0)) ||
                !Character.isDigit(startInput.charAt(1))) {
            return "Invalid position format. Use format like 'e2'";
        }

        char startColumn = startInput.charAt(0);
        int startRank = Character.getNumericValue(startInput.charAt(1));

        int startCol = startColumn - 'a' + 1;
        int startRow = startRank;

        ChessPosition startPosition = new ChessPosition(startRow, startCol);
        ChessPiece piece = gameHandler.chessGame.getBoard().getPiece(startPosition);

        if (piece == null) {
            return "No piece at position " + startInput;
        }

        if (piece.getTeamColor() != gameHandler.chessGame.getTeamTurn()) {
            return "That's not your piece to move";
        }

        Collection<ChessMove> validMoves = gameHandler.chessGame.validMoves(startPosition);
        if (validMoves.isEmpty()) {
            return "No valid moves for this piece";
        }

        System.out.println("Valid moves for " + startInput + ":");
        int i = 1;
        for (ChessMove move : validMoves) {
            ChessPosition endPos = move.getEndPosition();
            char endFile = (char)('a' + endPos.getColumn() - 1);
            int endRank = endPos.getRow();
            System.out.print(i + ". " + endFile + endRank);

            if (move.getPromotionPiece() != null) {
                System.out.println(" (promotes to " + move.getPromotionPiece() + ")");
            } else {
                System.out.println();
            }
            i++;
        }

        System.out.println("Enter destination position (like 'e4'):");
        String endInput = scanner.nextLine().toLowerCase().trim();

        if (endInput.length() != 2 ||
                !Character.isLetter(endInput.charAt(0)) ||
                !Character.isDigit(endInput.charAt(1))) {
            return "Invalid position format. Use format like 'e4'";
        }

        char endFile = endInput.charAt(0);
        int endRank = Character.getNumericValue(endInput.charAt(1));

        int endCol = endFile - 'a' + 1;
        int endRow = endRank;

        ChessPosition endPosition = new ChessPosition(endRow, endCol);

        ChessMove selectedMove = null;
        for (ChessMove move : validMoves) {
            if (move.getEndPosition().getRow() == endPosition.getRow() &&
                    move.getEndPosition().getColumn() == endPosition.getColumn()) {
                selectedMove = move;
                break;
            }
        }

        if (selectedMove == null) {
            return "Invalid move. That is not a legal destination for this piece.";
        }

        // Deal with promotions
        ChessPiece.PieceType promotionPiece = null;
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN &&
                (endRow == 8 || endRow == 1)) {
            System.out.println("Choose promotion piece (q=Queen, r=Rook, b=Bishop, n=Knight):");
            String promotion = scanner.nextLine().toLowerCase().trim();

            switch (promotion) {
                case "q": promotionPiece = ChessPiece.PieceType.QUEEN; break;
                case "r": promotionPiece = ChessPiece.PieceType.ROOK; break;
                case "b": promotionPiece = ChessPiece.PieceType.BISHOP; break;
                case "n": promotionPiece = ChessPiece.PieceType.KNIGHT; break;
                default: return "Invalid promotion piece. Move canceled.";
            }
        }

        ChessMove moveToMake = new ChessMove(startPosition, endPosition, promotionPiece);

        try {
            webSocketFacade.makeMove(authData.authToken(), gameId, moveToMake);
            return "Move sent: " + startInput + " to " + endInput;
        } catch (IOException e) {
            return "Error sending move: " + e.getMessage();
        }
    }

    private String highlight() {
        if (gameHandler.chessGame.isGameOver() || gameHandler.gameOver) {
            return "Game not running";
        }

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter piece position (e.g., e2):");
        String input = scanner.nextLine().toLowerCase().trim();

        if (input.length() != 2 ||
                !Character.isLetter(input.charAt(0)) ||
                !Character.isDigit(input.charAt(1))) {
            System.out.println("Invalid position format. Use format like 'e2'.");
            return highlight();
        }

        char fileLetter = input.charAt(0);
        int rank = Character.getNumericValue(input.charAt(1));

        int col = fileLetter - 'a' + 1;
        int row = rank;

        ChessPosition position = new ChessPosition(row, col);
        ChessPiece piece = gameHandler.chessGame.getBoard().getPiece(position);

        if (piece == null) {
            return "No piece at position " + input;
        }

        boolean isBlack = playerColor != null && playerColor.equalsIgnoreCase("BLACK");
        gameHandler.highlight(piece, position, isBlack);

        return "Highlighted valid moves for piece at " + input;


    }

    private String resign() throws IOException {
        if (gameHandler.chessGame.isGameOver() || gameHandler.gameOver) {
            return "Game not running";
        }
        gameHandler.gameOver = true;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Are you sure you want to resign? (y/n):");
        String input = scanner.nextLine().toLowerCase().trim();

        if (input.equals("y")) {
            webSocketFacade.resign(authData.authToken(), gameId);
//            webSocketFacade.leave(authData.authToken(), gameId);
            return "Resigned.";
        } else if (input.equals("n")) {
            return "Good choice. Never give up.";
        } else {
            System.out.println("Invalid resign. Try again.");
            return resign();
        }
    }

}
