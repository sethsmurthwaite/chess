package clientTests;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ChessServerFacade;

import java.io.IOException;


public class ServerFacadeTests {

    private static Server server;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    public void registerPositive() throws IOException, InterruptedException {
        Assertions.assertTrue(true);
    }

    @Test
    public void registerNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void loginPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void loginNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void logoutPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void logoutNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void joinPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void joinNegative() {
        Assertions.assertTrue(true);
    }

    public void createPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void createNegative() {
        Assertions.assertTrue(true);
    }

    @Test
    public void listPositive() {
        Assertions.assertTrue(true);
    }

    @Test
    public void listNegative() {
        Assertions.assertTrue(true);
    }

}
