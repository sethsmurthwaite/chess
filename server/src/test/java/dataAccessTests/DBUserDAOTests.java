package dataAccessTests;

import dataAccess.*;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class DBUserDAOTests {

    DatabaseManager dbman = new DatabaseManager();
    DBUserDAO userDAO = new DBUserDAO(dbman);
    UserData userData = new UserData("username", "password", "email");
    @BeforeEach
    public void setup() throws DataAccessException {
        userDAO.createUser(userData);
    }
    @AfterEach
    public void cleanup() throws DataAccessException {
        userDAO.clearUsers();
    }
    @Test
    @Order(1)
    @DisplayName("Positive Create User")
    public void successCreate() throws DataAccessException {
        Assertions.assertDoesNotThrow(() -> userDAO.createUser(userData));
    }

    @Test
    @Order(2)
    @DisplayName("Negative Create User")
    public void failCreate() throws DataAccessException {
        userDAO.createUser(userData);
        userDAO.createUser(userData);
        assertEquals(userDAO.readUser(userData.username()), userData);
    }

    @Test
    @Order(3)
    @DisplayName("Positive Read User")
    public void successDelete() throws DataAccessException {
        userDAO.createUser(userData);
        assertEquals(userDAO.readUser(userData.username()), userData);
    }

    @Test
    @Order(4)
    @DisplayName("Negative Read User")
    public void failDelete() throws DataAccessException {
        assertNotEquals(userDAO.readUser(userData.username()), "userData");
    }

    @Test
    @Order(5)
    @DisplayName("Positive Clear Users")
    public void successClear() throws DataAccessException {
        userDAO.clearUsers();
        assertEquals(null, userDAO.readUser(userData.username()));
    }
}
