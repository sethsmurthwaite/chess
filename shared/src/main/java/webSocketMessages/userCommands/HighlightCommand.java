package webSocketMessages.userCommands;

public class HighlightCommand extends UserGameCommand {
    int gameID;
    public HighlightCommand(String authToken, CommandType type, int GameID) {
        super(authToken, type);
        gameID = GameID;
    }
}
