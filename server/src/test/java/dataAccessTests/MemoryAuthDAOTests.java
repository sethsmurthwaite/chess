package dataAccessTests;

import dataAccess.*;
import model.AuthData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class MemoryAuthDAOTests {


    MemoryAuthDAO authDAO = new MemoryAuthDAO();

    @BeforeEach
    public void setup() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
    }

    @AfterEach
    public void cleanup() throws DataAccessException {
        authDAO.clearAuth();
    }

    @Test
    @Order(1)
    @DisplayName("Positive Create Auth")
    public void successCreate() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> authDAO.createAuth("tests"));
    }

    @Test
    @Order(2)
    @DisplayName("Negative Create Auth")
    public void failCreate() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
        assertNotEquals(authData.authToken(), authData.username());
    }

    @Test
    @Order(3)
    @DisplayName("Positive Delete Auth")
    public void successDelete() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
        authDAO.deleteAuth(authData.authToken());
        Assertions.assertThrows(DataAccessException.class, () -> authDAO.readAuth(authData.authToken()));
    }

    @Test
    @Order(4)
    @DisplayName("Negative Delete Auth")
    public void failDelete() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
        authDAO.deleteAuth("authToken");
        assertEquals(authData.authToken(), authDAO.readAuth(authData.authToken()).authToken());
    }

    @Test
    @Order(5)
    @DisplayName("Positive Read Auth")
    public void successRead() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
        assertEquals(authData, authDAO.readAuth(authData.authToken()));
    }

    @Test
    @Order(6)
    @DisplayName("Negative Read Auth")
    public void failRead() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authDAO.readAuth("authtoken"));
    }

    @Test
    @Order(7)
    @DisplayName("Positive Clear Auth")
    public void successClear() throws DataAccessException {
        AuthData authData = authDAO.createAuth("USERNAME");
        authDAO.clearAuth();
        assertThrows(DataAccessException.class, () -> authDAO.readAuth(authData.authToken()));
    }
}