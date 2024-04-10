package server;

import server.MessageObj;

import java.util.ArrayList;

public interface ServerMessageObserver {
    ArrayList<MessageObj> messages = new ArrayList<>();

    public void sendMessages();

}
