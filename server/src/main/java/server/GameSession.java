package server;

import model.GameData;
import org.eclipse.jetty.websocket.api.Session;

import java.util.Collection;
import java.util.HashMap;

public class GameSession {

    private Session whiteSession = null;
    private Session blackSession = null;
    private String whiteUsername = null;
    private String blackUsername = null;
    private HashMap<String, Session> observers = new HashMap<>();
    private GameData gameData = null;
    boolean gameOver = false;

    public GameSession(GameData gameData) {
        this.gameData = gameData;
        this.whiteUsername = gameData.whiteUsername();
        this.blackUsername = gameData.blackUsername();
    }

    public void setWhitePlayer(String username, Session session) {
        whiteUsername = username;
        whiteSession = session;
    }
    public void setBlackPlayer(String username, Session session) {
        blackUsername = username;
        blackSession = session;
    }
    public void setGameOver(boolean b) {
        gameOver = b;
    }
    public void setGameData(GameData game) {
        this.gameData = game;
    }
    public void addObservers(String username, Session session) {
        observers.put(username, session);
    }
    public void removeObserver(String username) {
        observers.remove(username);
    }
    public Session getObserverSession(String username) {
        if (observers.isEmpty()) return null;
        else return observers.get(username);
    }
    public Session[] getObserverSessions() {
        Collection<Session> sessions = observers.values();
        return sessions.toArray(new Session[0]);
    }
    public HashMap<String, Session> getObservers() {
        return this.observers;
    }
    public Session getWhiteSession() {
        return whiteSession;
    }
    public Session getBlackSession() {
        return blackSession;
    }
    public String getWhiteUsername() {
        return whiteUsername;
    }
    public String getBlackUsername() {
        return blackUsername;
    }
    public GameData getGameData() {
        return gameData;
    }
    public boolean isGameOver() {
        return gameOver;
    }
}
