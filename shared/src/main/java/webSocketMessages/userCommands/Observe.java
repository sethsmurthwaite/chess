package webSocketMessages.userCommands;

public class Observe extends UserGameCommand {
    int gameID;

    public Observe(String authToken, int gameID) {
        super(authToken, CommandType.JOIN_OBSERVER);
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}