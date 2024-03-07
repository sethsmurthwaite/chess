package serviceTests;
import chess.ChessGame;
import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.GameName;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceMemoryTests {
    GameService gameService;
    UserService userService;
    UserData user;
    AuthData auth;

    @BeforeEach
    public void setup() throws DataAccessException {
        gameService = new GameService(new MemoryAuthDAO(), null);
        userService = new UserService(new MemoryAuthDAO(), null);
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
