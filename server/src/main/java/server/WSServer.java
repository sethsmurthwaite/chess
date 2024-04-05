//package server;
//
//import org.eclipse.jetty.websocket.api.Session;
//import org.eclipse.jetty.websocket.api.annotations.*;
//import spark.Spark;
//import server.ServerMessage.*;
//import server.UserGameCommand.*;
//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//
//import static org.glassfish.grizzly.http.util.Header.Connection;
//
//@WebSocket
//public class WSServer {
//
//    Gson gson = new Gson();
//
//    public static void main(String[] args) {
//        Spark.port(8080);
//        Spark.webSocket("/connect", WSServer.class);
//        Spark.get("/echo/:msg", (req, res) -> "HTTP response: " + req.params(":msg"));
//    }
//
//    @OnWebSocketMessage
//    public void onMessage(Session session, String msg) throws Exception {
//        UserGameCommand command = gson.fromJson(msg, UserGameCommand.class);
//
//        var conn = getConnection(command.authToken, session);
//        if (conn != null) {
//            switch (command.commandType) {
//                case JOIN_PLAYER -> join(conn, msg);
//                case JOIN_OBSERVER -> observe(conn, msg);
//                case MAKE_MOVE -> move(conn, msg));
//                case LEAVE -> leave(conn, msg);
//                case RESIGN -> resign(conn, msg);
//            }
//        } else {
//            Connection.sendError(session.getRemote(), "unknown user");
//        }
//    }
//
//}
