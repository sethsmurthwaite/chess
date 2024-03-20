package clientTests;

import model.AuthData;
import model.GameData;
import model.GameList;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ChessServerFacade;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws IOException, InterruptedException {
        UserData user = new UserData("test_user1", "password123", "");
        AuthData auth = ChessServerFacade.register(user);
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        ChessServerFacade.logout(auth);
    }

    @Test
    public void registerNegative() {
        UserData existingUser = new UserData("test_user", "password123", "");
        assertThrows(Error.class, () -> {
            ChessServerFacade.register(existingUser);
        });
    }

    @Test
    public void loginPositive() throws IOException, InterruptedException {
        AuthData auth1 = ChessServerFacade.register(new UserData("test_user", "password123", ""));
        ChessServerFacade.logout(auth1);
        UserData user = new UserData("test_user", "password123", "");
        AuthData auth = ChessServerFacade.login(user);
        assertNotNull(auth);
    }

    @Test
    public void loginNegative() {
        UserData invalidUser = new UserData("invalid_user", "invalid_password", "");
        assertThrows(Error.class, () -> {
            ChessServerFacade.login(invalidUser);
        });
    }

    @Test
    public void logoutPositive() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person", "pass", ""));
        assertDoesNotThrow(() -> {
            ChessServerFacade.logout(auth);
        });
    }

    @Test
    public void logoutNegative() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person1", "pass1", ""));
        ChessServerFacade.logout(auth);
        assertThrows(IOException.class, () -> {
            ChessServerFacade.logout(auth);
        });
    }

    @Test
    public void joinPositive() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person2", "pass2", ""));
        ChessServerFacade.create("game", auth);
        GameData game = new GameData(1, null,null,"game", null);
        assertDoesNotThrow(() -> {
            ChessServerFacade.join(auth, "WHITE", game);
        });
    }

    @Test
    public void joinNegative() {
        AuthData invalidAuth = new AuthData("invalid_auth_token", "fake_name");
        GameData game = new GameData(1, null,null,"game1", null);
        assertThrows(IOException.class, () -> {
            ChessServerFacade.join(invalidAuth, "white", game);
        });
    }

    @Test
    public void createPositive() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person3", "pass3", ""));
        ChessServerFacade.create("game1", auth);
        GameData game = new GameData(2, null,null,"game1", null);
        assertDoesNotThrow(() -> {
            ChessServerFacade.join(auth, "WHITE", game);
        });
    }

    @Test
    public void createNegative() {
        AuthData invalidAuth = new AuthData("invalid_auth_token", "");
        assertThrows(Error.class, () -> {
            ChessServerFacade.create("game1", invalidAuth);
        });
    }

    @Test
    public void listPositive() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person4", "pass4", ""));
        ChessServerFacade.create("game1", auth);
        GameData game = new GameData(3, null,null,"game1", null);
        GameList gameList = ChessServerFacade.list(auth);
        Assertions.assertNotEquals(0, gameList.games().size());
    }

    @Test
    public void listNegative() throws IOException, InterruptedException {
        AuthData auth = ChessServerFacade.register(new UserData("person5", "pass6", ""));
        ChessServerFacade.create("game1", auth);
        GameData game = new GameData(4, null,null,"game1", null);
        GameList gameList = ChessServerFacade.list(auth);
        Assertions.assertNotEquals(0, gameList.games().size());
    }
}
