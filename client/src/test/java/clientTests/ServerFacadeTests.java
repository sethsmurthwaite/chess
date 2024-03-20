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
    private static ChessServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        facade = new ChessServerFacade(port);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @Test
    public void registerPositive() throws IOException, InterruptedException {
        UserData user = new UserData("test_user1", "password123", "");
        AuthData auth = facade.register(user);
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        facade.logout(auth);
    }

    @Test
    public void registerNegative() {
        UserData existingUser = new UserData("test_user", "password123", "");
        assertThrows(Error.class, () -> {
            facade.register(existingUser);
        });
    }

    @Test
    public void loginPositive() throws IOException, InterruptedException {
        AuthData auth1 = facade.register(new UserData("test_user", "password123", ""));
        facade.logout(auth1);
        UserData user = new UserData("test_user", "password123", "");
        AuthData auth = facade.login(user);
        assertNotNull(auth);
    }

    @Test
    public void loginNegative() {
        UserData invalidUser = new UserData("invalid_user", "invalid_password", "");
        assertThrows(Error.class, () -> {
            facade.login(invalidUser);
        });
    }

    @Test
    public void logoutPositive() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person", "pass", ""));
        assertDoesNotThrow(() -> {
            facade.logout(auth);
        });
    }

    @Test
    public void logoutNegative() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person1", "pass1", ""));
        facade.logout(auth);
        assertThrows(IOException.class, () -> {
            facade.logout(auth);
        });
    }

    @Test
    public void joinPositive() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person2", "pass2", ""));
        facade.create("game", auth);
        GameData game = new GameData(1, null,null,"game", null);
        assertDoesNotThrow(() -> {
            facade.join(auth, "WHITE", game);
        });
    }

    @Test
    public void joinNegative() {
        AuthData invalidAuth = new AuthData("invalid_auth_token", "fake_name");
        GameData game = new GameData(1, null,null,"game1", null);
        assertThrows(IOException.class, () -> {
            facade.join(invalidAuth, "white", game);
        });
    }

    @Test
    public void createPositive() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person3", "pass3", ""));
        facade.create("game1", auth);
        GameData game = new GameData(2, null,null,"game1", null);
        assertDoesNotThrow(() -> {
            facade.join(auth, "WHITE", game);
        });
    }

    @Test
    public void createNegative() {
        AuthData invalidAuth = new AuthData("invalid_auth_token", "");
        assertThrows(Error.class, () -> {
            facade.create("game1", invalidAuth);
        });
    }

    @Test
    public void listPositive() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person4", "pass4", ""));
        facade.create("game1", auth);
        GameData game = new GameData(3, null,null,"game1", null);
        GameList gameList = facade.list(auth);
        Assertions.assertNotEquals(0, gameList.games().size());
    }

    @Test
    public void listNegative() throws IOException, InterruptedException {
        AuthData auth = facade.register(new UserData("person5", "pass6", ""));
        facade.create("game1", auth);
        GameData game = new GameData(4, null,null,"game1", null);
        GameList gameList = facade.list(auth);
        Assertions.assertNotEquals(0, gameList.games().size());
    }
}
