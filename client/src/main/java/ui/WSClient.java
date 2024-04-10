package ui;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;
import webSocketMessages.serverMessages.ErrorNotification;
import webSocketMessages.serverMessages.LoadGame;
import webSocketMessages.serverMessages.Notification;
import webSocketMessages.serverMessages.ServerMessage;
import webSocketMessages.userCommands.*;

import javax.websocket.*;
import java.io.PrintStream;
import java.net.URI;
import java.util.ArrayList;

public class WSClient extends Endpoint {
    ChessClient chessClient;
    GameData currentGame;
    String username;
    ChessGame.TeamColor playerColor;
    String opponentUsername;
    String clientID;
    ArrayList<String> messagesArray = new ArrayList<>();
    public Session session;
    Gson gson = new Gson();

    public WSClient(ChessClient chessClient, String clientID) throws Exception {
        URI uri = new URI("ws://localhost:8080/connect");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);
        this.chessClient = chessClient;
        this.clientID = clientID;
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
//                chessClient.sendMessage(message);
                int i = 0;
                while(chessClient.isPrinting) {
                    i++;
                }
                typeAdapter(message);
            }
        });
    }

    public void typeAdapter(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        String type = serverMessage.getServerMessageType().toString();
        switch (type) {
            case "NOTIFICATION" -> {
                Notification notice = gson.fromJson(message, Notification.class);
                chessClient.printNotification(notice);
            }
            case "LOAD_GAME" -> {
                LoadGame load = gson.fromJson(message, LoadGame.class);
                chessClient.printLoadGame(load);
            }
            case "ERROR" -> {
                ErrorNotification error = gson.fromJson(message, ErrorNotification.class);
                chessClient.printError(error);
            }
        }
    }
    public String joinNotification(JoinPlayer joinPlayer) {
        return gson.toJson(joinPlayer);
    }
    public String leaveNotification(Leave leave) {
        return gson.toJson(leave);
    }
    public String resignNotification(Resign resign) { return gson.toJson(resign); }
    public String observeNotification(Observe observe) { return gson.toJson(observe); }
    public String moveNotification(MakeMove move) { return gson.toJson(move); }
    public Notification notification(String json) { return gson.fromJson(json, Notification.class); }
    public LoadGame loadGameNotification(String json) { return gson.fromJson(json, LoadGame.class); }
}
