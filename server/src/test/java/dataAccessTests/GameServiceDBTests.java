package dataAccessTests;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.MemoryAuthDAO;
import model.AuthData;
import model.GameName;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import service.GameService;
import service.UserService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class GameServiceDBTests {
    GameService gameService = new GameService(new MemoryAuthDAO(), null);
    UserService userService = new UserService(new MemoryAuthDAO(), null);
    UserData user;
    AuthData auth;

    @BeforeEach
    public void setup() throws DataAccessException {
        userService.clearUsers();
        gameService.clearGames();
        user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        auth = userService.register(user);
    }

    @Test
    @Order(1)
    @DisplayName("Positive List Games")
    public void successList() throws DataAccessException {
        assertEquals(gameService.listGames(auth.authToken()), new HashSet<>());
    }

    @Test
    @Order(2)
    @DisplayName("Negative List Games")
    public void failList() {
        assertThrows(DataAccessException.class, () -> gameService.listGames("bad token"));
    }

    @Test
    @Order(3)
    @DisplayName("Positive Create Game")
    public void successCreate() throws DataAccessException {
        int ID = gameService.createGame(new GameName("newGame"), auth.authToken());
        assertEquals(1, ID);
        assertEquals(gameService.listGames(auth.authToken()).size(), 1);
    }

    @Test
    @Order(4)
    @DisplayName("Negative Create Game")
    public void failCreate() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameService.createGame(new GameName("name"), "badToken"));
        assertEquals(gameService.listGames(auth.authToken()).size(), 0);
    }

    @Test
    @Order(5)
    @DisplayName("Positive Join Game")
    public void successJoin() throws DataAccessException {
        gameService.createGame(new GameName("newGame"), auth.authToken());
        assertDoesNotThrow(() -> gameService.joinGame("Seth", ChessGame.TeamColor.WHITE, 1));
    }

    @Test
    @Order(6)
    @DisplayName("Negative Join Game")
    public void failJoin() {
        assertThrows(DataAccessException.class, () -> gameService.joinGame("Seth", ChessGame.TeamColor.WHITE, 1));
    }
}
