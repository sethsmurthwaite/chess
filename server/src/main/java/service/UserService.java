package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.*;

import java.util.Objects;

public class UserService {

    AuthDAO authDAO;
    UserDAO userDAO = new UserDAO();

    public UserService(AuthDAO authDAO) {
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) throws DataAccessException {
        if (this.userDAO.readUser(user.username()) != null) {
            throw new DataAccessException("User with username '" + user.username() + "' already exists.", 403);
        }
        if (user.password() == null || user.username() == null || user.email() == null) {
            throw new DataAccessException("Bad Request", 400);
        }
        this.userDAO.createUser(user);
        return this.authDAO.createAuth(user.username());
    }
    public AuthData login(UserData user) throws DataAccessException {
        UserData storedUserData = userDAO.readUser(user.username());
        if (storedUserData == null) throw new DataAccessException("Error: No user with username '" + user.username() + "'", 401);
        if (!Objects.equals(user.password(), storedUserData.password())) {
            throw new DataAccessException("Error: Incorrect Password", 401);
        }
        return authDAO.createAuth(user.username());
    }
    public void logout(String authToken) throws DataAccessException {
        AuthData authData = authDAO.readAuth(authToken);
        authDAO.deleteAuth(authToken);
    }

    public void clearUsers() {
        userDAO.clearUsers();
    }
}
