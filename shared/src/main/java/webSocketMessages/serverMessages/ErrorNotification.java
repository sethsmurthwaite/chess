package webSocketMessages.serverMessages;

public class ErrorNotification extends ServerMessage {
    String errorMessage;
    public ErrorNotification(String m) {
        super(ServerMessageType.ERROR);
        errorMessage = m;
    }
    public String getMessage() {return errorMessage;}
}
