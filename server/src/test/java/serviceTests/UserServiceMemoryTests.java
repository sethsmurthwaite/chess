package serviceTests;
import dataAccess.DatabaseManager;
import dataAccess.MemoryAuthDAO;
import dataAccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.UserService;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserServiceMemoryTests {
    UserService userService;

    @BeforeEach
    public void setup() {
        userService = new UserService(new MemoryAuthDAO(), null);
    }

    @Test
    @Order(1)
    @DisplayName("Positive User Register")
    public void successRegister() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        AuthData authData = userService.register(user);
        assertEquals(authData.username(), user.username());
    }

    @Test
    @Order(2)
    @DisplayName("Negative User Register")
    public void failRegister() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        userService.register(user);
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(user));
    }

    @Test
    @Order(3)
    @DisplayName("Positive User Login")
    public void successLogin() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        userService.register(user);
        AuthData authData = userService.login(user);
        assertEquals(authData.username(), user.username());
    }

    @Test
    @Order(4)
    @DisplayName("Negative User Login")
    public void failLogin() {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(user));
    }

    @Test
    @Order(5)
    @DisplayName("Positive User Logout")
    public void successLogout() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        AuthData a = userService.register(user);
        Assertions.assertDoesNotThrow(() -> userService.logout(a.authToken()));
    }

    @Test
    @Order(6)
    @DisplayName("Negative User Logout")
    public void failLogout() {
        new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        Assertions.assertThrows(DataAccessException.class, () -> userService.logout("badauthToken"));
    }

    @Test
    @Order(7)
    @DisplayName("Positive Clear Users")
    public void successClear() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        userService.register(user);
        userService.clearUsers();
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(user));
    }

    @Test
    @Order(7)
    @DisplayName("Negative Clear Users")
    public void failClear() throws DataAccessException {
        UserData user = new UserData("Seth Smurthwaite", "reallygoodpassword", "no@gmail.com");
        userService.register(user);
        userService.clearUsers();
        Assertions.assertDoesNotThrow(() -> userService.register(user));
    }
}
