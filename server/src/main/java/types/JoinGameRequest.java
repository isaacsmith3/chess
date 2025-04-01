package types;

import chess.ChessGame;

public record JoinGameRequest (int gameID, String playerColor) {}
