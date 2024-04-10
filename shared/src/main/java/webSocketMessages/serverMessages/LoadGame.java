package webSocketMessages.serverMessages;

import model.GameData;

public class LoadGame extends ServerMessage {
    public GameData game;
    Boolean gameOver;
    public LoadGame(GameData game, Boolean gameOver) {
        super(ServerMessageType.LOAD_GAME);
        this.game = game;
        this.gameOver = gameOver;
    }

    public GameData getGame() { return this.game; }
    public Boolean getGameOver() { return this.gameOver; }
}
