package dataAccess;

import model.UserData;
import java.util.List;
import java.util.Map;


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
            dbman.executeUpdate(statement, username, password, email);
        } catch (DataAccessException e) {
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
        }
        assert list != null;
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
        }
    }

}
