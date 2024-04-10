package server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class MessageObj {
    String message;
    Session session;

    public MessageObj (String m, Session s) {
        message = m;
        session = s;
    }

    public void sendMessage() {
        try {
            session.getRemote().sendString(message);
        } catch (IOException e) { System.out.println(e.getMessage()); throw new RuntimeException(e);}
    }

}
