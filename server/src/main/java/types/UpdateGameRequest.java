package types;

import chess.ChessGame;

public record UpdateGameRequest(int gameID, String playerColor, ChessGame game) {
}
