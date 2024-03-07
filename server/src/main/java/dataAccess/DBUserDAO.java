package dataAccess;

import model.AuthData;
import model.UserData;
import java.sql.*;
import java.util.List;
import java.util.Map;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBUserDAO implements UserDAO {
    public final DatabaseManager dbman;

    public DBUserDAO(DatabaseManager dbmanager) {
        this.dbman = dbmanager;
    }

    @Override
    public void createUser(UserData u) {
        String username = u.username();
        String password = u.password();
        String email = u.email();
        String statement = "INSERT INTO user (`name`, `password`, `email`) VALUES (?, ?, ?)";
        try {
            var id = dbman.executeUpdate(statement, username, password, email);
        } catch (DataAccessException e) {
            System.out.println("Error in createUser in DBUserDAO: " + e.getMessage());
        }
    }

    @Override
    public UserData readUser(String username) {
        var statement = "SELECT name, password, email FROM user WHERE name = (?)";
        UserData userData = null;
        List<Map<String, Object>> list = null;
        try {
            list = dbman.executeQuery(statement, username);
        } catch (DataAccessException e) {
            System.out.println("Error in readUser in DBUserDAO: " + e.getMessage());
        }
        if (list.isEmpty()) System.out.println("Error in readUser in DBUserDAO: list is empty.");
        for (Map<String, Object> row : list) {
            userData = new UserData(username, (String) row.get("password"), (String) row.get("email"));
        }
        return userData;
    }

    @Override
    public void clearUsers() {
        var statement = "TRUNCATE user";
        try {
            dbman.executeUpdate(statement);
        } catch (DataAccessException e) {
            System.out.println("ERROR IN clearAuth IN DBUserDAO " + e);
        }
    }

}
