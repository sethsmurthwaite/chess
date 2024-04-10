package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import model.AuthData;
import model.GameData;
import model.GameList;
import model.UserData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;
import service.GameService;
import service.UserService;
import webSocketMessages.serverMessages.*;
import webSocketMessages.userCommands.*;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLOutput;
import java.util.*;

@WebSocket
public class WSServer {
    Gson gson = new Gson();
    private ArrayList<Session> sessions = new ArrayList<>();
    private HashMap<Integer, GameSession> activeGames = new HashMap<>();
    DatabaseManager dbman;
    AuthDAO authDAO;
    int port;
    UserService userService;
    GameService gameService;
    public WSServer(DatabaseManager dbman, AuthDAO authDAO, int port, UserService userService, GameService gameService) {
        this.dbman = dbman;
        this.authDAO = authDAO;
        this.port = port;
        this.userService = userService;
        this.gameService = gameService;
    }
    @OnWebSocketConnect
    public void onConnect(Session session) {
        sessions.add(session);
    }
    @OnWebSocketError
    public void onError(Session session, Throwable throwable) {
        System.out.println("Error occurred: " + throwable.getMessage());
        try {
            session.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        sessions.remove(session);

        for (Integer gameID : activeGames.keySet()) {
            GameSession gameSession = activeGames.get(gameID);

            if (gameSession.getBlackSession() != null) {
                if (gameSession.getBlackSession().equals(session)) {
                    String blackUsername = gameSession.getBlackUsername();
                    gameSession.setBlackPlayer(blackUsername, null);
                }
            }

            if (gameSession.getWhiteSession() != null) {
                if (gameSession.getWhiteSession().equals(session)) {
                    String whiteUsername = gameSession.getWhiteUsername();
                    gameSession.setWhitePlayer(whiteUsername, null);
                }
            }

            HashMap<String, Session> observerMap = gameSession.getObservers();

            if (observerMap.isEmpty()) {
                activeGames.put(gameID, gameSession);
                return;
            }

            String[] observerUsernames = gameSession.getObservers().keySet().toArray(new String[0]);

            for (String observerUsername : observerUsernames) {
                if (observerMap.get(observerUsername).equals(session)) {
                    gameSession.removeObserver(observerUsername);
                }
            }

            activeGames.put(gameID, gameSession);
        }
    }
    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        String type = command.getCommandType().toString();
        switch (type) {
            case "JOIN_PLAYER" -> {
                JoinPlayer joinPlayer = gson.fromJson(message, JoinPlayer.class);
                String username = joinLogic(session, joinPlayer);
                try {
                    sendJoinNotification(joinPlayer, session, username);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            case "JOIN_OBSERVER" -> {
                Observe observer = gson.fromJson(message, Observe.class);
                try {
                    sendObserveNotification(observer, session);
                } catch (Exception e) {
                    session.getRemote().sendString(gson.toJson(new ErrorNotification("Error while joining as observer: " + e.getMessage())));
                }
            }
            case "MAKE_MOVE" -> {
                MakeMove makeMove = gson.fromJson(message, MakeMove.class);
                try {
                    sendMakeMoveNotification(makeMove, session);
                } catch (IOException | InvalidMoveException e) {
                    session.getRemote().sendString(gson.toJson(new ErrorNotification("Error while sending make move notificaiton: " + e.getMessage())));
                }
            }
            case "LEAVE" -> {
                Leave leave = gson.fromJson(message, Leave.class);
                try {
                    sendLeaveNotification(leave, session);
                } catch (IOException e) {
                    session.getRemote().sendString(gson.toJson(new ErrorNotification("Error during leave: " + e.getMessage())));
                }
            }
            case "RESIGN" -> {
                Resign resign = gson.fromJson(message, Resign.class);
                try {
                    sendResignNotification(resign, session);
                } catch (Exception e) {
                    session.getRemote().sendString(gson.toJson(new ErrorNotification("Error while resigning: " + e.getMessage())));
                }
            }
            default -> {
                ErrorNotification errorNotification = new ErrorNotification("Unexpected Value " + message.toLowerCase());
            }
        }
    }
    public GameData getGameFromDB(String authToken, int gameID, Session session) {
        GameData game = null;
        HashSet<GameData> gameSet;
        try {
            gameSet = gameService.listGames(authToken);
            for (GameData maybeGame : gameSet ) {
                if (maybeGame.gameID() == gameID) {
                    game = maybeGame;
                }
            }
            if (game == null) session.getRemote().sendString(gson.toJson(new ErrorNotification("Invalid game id")));
        } catch (DataAccessException | IOException e) {
            ErrorNotification error = new ErrorNotification("Error: " + e.getMessage());
        }
        return game;
    }
    public String getUsernameFromDB(String authToken) throws DataAccessException {
        AuthData userData = authDAO.readAuth(authToken);
        return userData.username();
    }
    private void addObserverToGame(GameSession gameSession, String observerUsername, Session observerSession) {
        gameSession.addObservers(observerUsername, observerSession);
    }
    private String joinLogic(Session session, JoinPlayer joinPlayer) throws DataAccessException, IOException {

        String username = null;
        try {
            username = getUsernameFromDB(joinPlayer.getAuthString());
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorNotification("Bad Auth Token")));
        }

        GameData game = getGameFromDB(joinPlayer.getAuthString(), joinPlayer.getGameID(), session);
        if (game.gameName().equals("testGameEmpty")) session.getRemote().sendString(gson.toJson(new ErrorNotification("Game is Empty")));
        GameSession gameSession = null;
        if (activeGames.containsKey(game.gameID())) gameSession = activeGames.get(joinPlayer.getGameID());

        else gameSession = new GameSession(game);

        String whiteUsername = game.whiteUsername();
        String blackUsername = game.blackUsername();
        ChessGame.TeamColor playerColor = joinPlayer.getColor();
        boolean isError = false;

        if (joinPlayer.getColor() == ChessGame.TeamColor.WHITE) {
            if (!game.whiteUsername().equals(username)) isError = true;
            else gameSession.setWhitePlayer(username, session);
        }
        if (joinPlayer.getColor() == ChessGame.TeamColor.BLACK) {
            if (game.blackUsername() != null) {
                if (!game.blackUsername().equals(username)) isError = true;
                else gameSession.setBlackPlayer(username, session);
            }
        }

        if (isError) {
            ErrorNotification error = new ErrorNotification("Username already taken");
            session.getRemote().sendString(gson.toJson(error));
        }

        activeGames.put(game.gameID(), gameSession);
        return username;
    }
    public void sendJoinNotification(JoinPlayer joinPlayer, Session session, String username) throws IOException {
        int gameID = joinPlayer.getGameID();
        GameSession gameSession = activeGames.get(gameID);
        String opponentUsername;
        Session opponentSession;
        Session playerSession;

        if (joinPlayer.getColor() == ChessGame.TeamColor.WHITE) {
            opponentUsername = gameSession.getBlackUsername();
            opponentSession = gameSession.getBlackSession();
            playerSession = gameSession.getWhiteSession();
        } else {
            opponentUsername = gameSession.getWhiteUsername();
            opponentSession = gameSession.getWhiteSession();
            playerSession = gameSession.getBlackSession();
        }

        if (joinPlayer.getAuthString() == null) {
            ErrorNotification error = new ErrorNotification("Missing Auth Token");
            session.getRemote().sendString(gson.toJson(error));
        }

        Session[] observerSessions = gameSession.getObserverSessions();

        Notification oppJoinNotice = new Notification("\tPlayer \'" + username + "\' has joined the game as the " +
                joinPlayer.getColor().toString().toLowerCase() + " player.");

        if (opponentSession != null) {
            opponentSession.getRemote().sendString(gson.toJson(oppJoinNotice));
        }

        if (observerSessions != null) {
            for (Session observerSession : observerSessions) {
                if (observerSession != null) observerSession.getRemote().sendString(gson.toJson(oppJoinNotice));
            }
        }

        try {
            LoadGame load = new LoadGame(gameSession.getGameData(), gameSession.gameOver);
            playerSession.getRemote().sendString(gson.toJson(load));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendLeaveNotification(Leave leave, Session session) throws IOException {

        String username = null;
        try {
            username = getUsernameFromDB(leave.getAuthString());
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorNotification("Bad Auth Token")));
        }


        GameSession game = activeGames.get(leave.getGameID());
        if (game == null) {
            try {
                GameData gameData = getGameFromDB(leave.getAuthString(), leave.getGameID(), session);
                game = new GameSession(gameData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        String whiteUsername = game.getWhiteUsername();
        String blackUsername = game.getBlackUsername();

        String notice = gson.toJson(new Notification("'" + username + "' has left the game."));

        Session whiteSession = game.getWhiteSession();
        Session blackSession = game.getBlackSession();
        Session[] observerSessions = game.getObserverSessions();

        if (whiteSession != null) { if (!whiteUsername.equals(username)) whiteSession.getRemote().sendString(notice); }
        if (blackSession != null) { if (!blackUsername.equals(username)) blackSession.getRemote().sendString(notice); }

        if (session != null) session.getRemote().sendString(notice);
        for (Session sess : observerSessions) {
            if (sess == session) continue;
            if (sess != null) sess.getRemote().sendString(notice);
        }

        if (Objects.equals(username, whiteUsername)) {
            game.setWhitePlayer(whiteUsername, null);
        }
        else if (Objects.equals(username, blackUsername)) {
            game.setBlackPlayer(blackUsername, null);
        }

        game.removeObserver(username);
    }
    public void sendResignNotification(Resign resign, Session session) throws Exception {
        GameSession game = activeGames.get(resign.getGameID());

        if (game.gameOver) throw new Exception("Game already over");

        game.setGameOver(true);

        String username = null;
        try {
            username = getUsernameFromDB(resign.getAuthString());
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorNotification("Bad Auth Token")));
        }

        String[] observerUsernames = game.getObservers().keySet().toArray(new String[0]);
        for (String observerUsername : observerUsernames) if (observerUsername.equals(username)) throw new Exception("Observer attempted to resign");

        activeGames.put(resign.getGameID(), game);
        String whiteUsername = game.getWhiteUsername();
        String blackUsername = game.getBlackUsername();

        String notice = gson.toJson(new Notification("\tPlayer '" + username + "' has resigned."));


        Session oppSession = null;
        if (username.equals(whiteUsername)) {
            oppSession = game.getBlackSession();
        }
        if (username.equals(blackUsername)) {
            oppSession = game.getWhiteSession();
        }
        Session[] observerSessions = game.getObserverSessions();


        try {
            if (session != null) session.getRemote().sendString(notice);
            if (oppSession != null) oppSession.getRemote().sendString(notice);
            for (Session observerSession : observerSessions) {
                if (observerSession != null) observerSession.getRemote().sendString(notice);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendObserveNotification(Observe observer, Session session) throws Exception {

        String username = null;
        try {
            username = getUsernameFromDB(observer.getAuthString());
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorNotification("Bad Auth Token")));
            return;
        }

        GameData game;
        GameSession gameSession;
        try {
            gameSession = activeGames.get(observer.getGameID());
            game = gameSession.getGameData();
        } catch (Exception e) {
            game = getGameFromDB(observer.getAuthString(), observer.getGameID(), session);
            if (game == null) return;
            gameSession = new GameSession(game);
        }

        String[] players = new String[2];
        boolean whitePlayer = false;
        boolean blackPlayer = false;
        String whiteUsername = gameSession.getWhiteUsername();
        String blackUsername = gameSession.getBlackUsername();


        Notification playerNotification = new Notification("\t'" + username + "' is observing the game.");

        if (whiteUsername != null) {
            players[0] = whiteUsername;
            whitePlayer = true;
            Session whiteSession = gameSession.getWhiteSession();
            if (whiteSession != null) whiteSession.getRemote().sendString(gson.toJson(playerNotification));
        }
        if (blackUsername != null) {
            players[0] = blackUsername;
            blackPlayer = true;
            Session blackSession = gameSession.getBlackSession();
            if (blackSession != null) blackSession.getRemote().sendString(gson.toJson(playerNotification));
        }


        StringBuilder sb =  new StringBuilder();
        sb.append("\tYou are observing the game ");
        if (blackPlayer && whitePlayer) {
            sb.append("between white player'" + whiteUsername + "' and black player '" + blackUsername + "'.");
        }
        else if (blackPlayer) sb.append("between a future opponent and black player '" + blackUsername + "'.");
        else if (whitePlayer) sb.append("between white player'\" + whiteUsername + \"' and a future opponent.");
        else sb.append("with no players yet.");

        addObserverToGame(gameSession, username, session);
        activeGames.put(observer.getGameID(), gameSession);
        Notification observerNotification = new Notification(sb.toString());

        Session[] observerSessions = gameSession.getObserverSessions();

        if (observerSessions != null) {
            for (Session observerSession : observerSessions) {
                if (observerSession == session) continue;
                observerSession.getRemote().sendString(gson.toJson(playerNotification));
            }
        }

        try {
            session.getRemote().sendString(gson.toJson(new LoadGame(gameSession.getGameData(), gameSession.gameOver)));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void sendMakeMoveNotification(MakeMove move, Session session) throws IOException, InvalidMoveException {
        GameSession gameSession = activeGames.get(move.getGameID());
        if (gameSession.gameOver) throw new InvalidMoveException("Game is over");
        String whiteUsername = gameSession.getWhiteUsername();
        String blackUsername = gameSession.getBlackUsername();

        String username = null;
        try {
            username = getUsernameFromDB(move.getAuthString());
        } catch (DataAccessException e) {
            session.getRemote().sendString(gson.toJson(new ErrorNotification("Bad Auth Token")));
        }

        ChessGame.TeamColor playerColor = null;
        if (whiteUsername != null) if (whiteUsername.equals(username)) playerColor = ChessGame.TeamColor.WHITE;
        if (blackUsername != null) if (blackUsername.equals(username)) playerColor = ChessGame.TeamColor.BLACK;
        ChessGame.TeamColor oppColor = playerColor == ChessGame.TeamColor.WHITE ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;

        ChessMove chessMove = move.getMove();
        ChessGame game = gameSession.getGameData().game();
        if (game.getBoard().getPiece(move.getMove().getStartPosition()) == null) throw new InvalidMoveException("Selected piece is null");

        if (playerColor != game.getTeamTurn()) throw new InvalidMoveException("Out of turn");

        Collection<ChessMove> validMoves = game.validMoves(chessMove.getStartPosition());
        if (!validMoves.contains(move.getMove())) throw new InvalidMoveException("Invalid Move");

        game.makeMove(chessMove);
        GameData gameData = gameSession.getGameData().setGame(game);
        gameService.makeMove(gameData);

        String notice = gson.toJson(new Notification("Player '" + username + "' has moved a piece."));
        String loadGameNotice = gson.toJson(new LoadGame(gameData, false));

        session.getRemote().sendString(loadGameNotice);

        Session oppSession = oppColor == ChessGame.TeamColor.WHITE ? gameSession.getWhiteSession() : gameSession.getBlackSession();
        if (oppSession != null) {
            oppSession.getRemote().sendString(notice);
            oppSession.getRemote().sendString(loadGameNotice);
        }

        Session[] observerSessions = gameSession.getObserverSessions();
        for (Session observerSession : observerSessions) {
            if (observerSession == session) continue;
            observerSession.getRemote().sendString(notice);
            observerSession.getRemote().sendString(loadGameNotice);
        }
    }
    public void clearActiveGames() {
        activeGames.clear();
    }
}
