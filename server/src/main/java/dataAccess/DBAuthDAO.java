package dataAccess;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;

import java.sql.SQLException;
import java.util.UUID;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBAuthDAO implements AuthDAO {

    DatabaseManager dbman;
    public DBAuthDAO(DatabaseManager dbman) {
        this.dbman = dbman;
    }
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (name, type, json) VALUES (?, ?, ?)";
        String authToken = UUID.randomUUID().toString();
        var id = dbman.executeUpdate(statement, username, authToken);
        return new AuthData(username, authToken);
    }

    @Override
    public void clearAuth() {
        var statement = "TRUNCATE pet";
        try {
            dbman.executeUpdate(statement);
        } catch (DataAccessException e) {
            System.out.println("ERROR IN CLEAR AUTH IN DBAUTHDAO " + e);
        }
    }

    @Override
    public void deleteAuth(String authToken) {

    }

    @Override
    public AuthData readAuth(String authToken) throws DataAccessException {
        return null;
    }



}
