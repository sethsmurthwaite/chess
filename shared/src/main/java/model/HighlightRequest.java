package model;

import chess.ChessPosition;

public record HighlightRequest(int gameID, ChessPosition position) {}
