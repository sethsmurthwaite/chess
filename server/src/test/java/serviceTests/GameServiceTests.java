package serviceTests;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.*;

import chess.ChessGame;
import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.GameData;
import model.GameName;
import service.GameService;

import java.util.HashSet;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameServiceTests {

    private GameService gameService;

    @BeforeEach
    void setUp() {
        gameService = new GameService(new AuthDAO());
    }

    @Test
    @Order(1)
    void testListGames_Positive() throws DataAccessException {
        HashSet<GameData> games = gameService.listGames("validToken");
        assertNotNull(games);
        assertEquals(0, games.size());
    }

    @Test
    @Order(2)
    void testListGames_Negative_InvalidToken() {
        assertThrows(DataAccessException.class, () -> {
            gameService.listGames("invalidToken");
        });
    }

    // Similarly, write test cases for clearGames(), createGame(), and joinGame() methods.
}
