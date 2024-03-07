package dataAccessTests;

import chess.ChessGame;
import dataAccess.DBGameDAO;
import dataAccess.DataAccessException;
import dataAccess.DatabaseManager;
import dataAccess.MemoryGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class DBGameDAOTests {

    DatabaseManager dbman = new DatabaseManager();
    DBGameDAO gameDAO = new DBGameDAO(dbman);

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO.clearGames();
    }

    @Test
    @Order(1)
    @DisplayName("Positive Create Game")
    public void successCreate() throws DataAccessException {
        int id = gameDAO.createGame("testgame");
        GameData gameData = gameDAO.getGame(id);
        assertEquals(id, gameData.gameID());
    }

    @Test
    @Order(2)
    @DisplayName("Negative Create Game")
    public void failCreate() throws DataAccessException {
        int id = gameDAO.createGame("");
        GameData gameData = gameDAO.getGame(id);
        assertFalse(id != gameData.gameID());
    }

    @Test
    @Order(3)
    @DisplayName("Positive List Games")
    public void successList() throws DataAccessException {
        gameDAO.createGame("testgame");
        gameDAO.createGame("testgame");
        gameDAO.createGame("testgame");
        gameDAO.createGame("testgame");
        Set<GameData> gameList = gameDAO.listGames();
        assertFalse(gameList.isEmpty());
    }

    @Test
    @Order(4)
    @DisplayName("Negative List Games")
    public void failList() throws DataAccessException {
        HashSet<GameData> gameList = gameDAO.listGames();
        assertEquals(gameList.size(), 0);
    }

    @Test
    @Order(5)
    @DisplayName("Positive Update Game")
    public void successUpdate() throws DataAccessException {
        gameDAO.createGame("testgame");
        gameDAO.updateGame(new GameData(1, "name", "name", "testgame", new ChessGame()));
        GameData gameData = gameDAO.getGame(1);
        assertEquals("name", gameData.whiteUsername());
    }

    @Test
    @Order(6)
    @DisplayName("Negative Update Game")
    public void failUpdate() throws DataAccessException {
        gameDAO.updateGame(new GameData(1, null, null, "testgame", new ChessGame()));
        GameData gameData = gameDAO.getGame(1);
        assertFalse("name" == null);
    }

    @Test
    @Order(7)
    @DisplayName("Positive Clear Games")
    public void successClear() throws DataAccessException {
        HashSet<GameData> gameList = gameDAO.listGames();
        assertEquals(gameList.size(), 0);
    }
}