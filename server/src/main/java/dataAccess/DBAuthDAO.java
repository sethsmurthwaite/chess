package dataAccess;

import model.AuthData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DBAuthDAO implements AuthDAO {

    DatabaseManager dbman;
    public DBAuthDAO(DatabaseManager dbman) {
        this.dbman = dbman;
    }
    @Override
    public AuthData createAuth(String username) throws DataAccessException {
        var statement = "INSERT INTO auth (authToken, name) VALUES (?, ?)";
        String authToken = UUID.randomUUID().toString();
        String dbString = dbman.toString();
        var id = dbman.executeUpdate(statement, authToken, username);
        return new AuthData(authToken, username);
    }

    @Override
    public void clearAuth() {
        var statement = "TRUNCATE auth";
        try {
            dbman.executeUpdate(statement);
        } catch (DataAccessException e) {
        }
    }

    @Override
    public void deleteAuth(String authToken) {
        var statement = "DELETE FROM auth WHERE authToken = (?);";
        try {
            dbman.executeUpdate(statement, authToken);
        } catch (DataAccessException e) {
        }
    }

    @Override
    public AuthData readAuth(String authToken) throws DataAccessException {
        var statement = "SELECT name FROM auth WHERE authToken = (?)";
        AuthData authData = null;
        List<Map<String, Object>> list = dbman.executeQuery(statement, authToken);
        if (list.isEmpty()) throw new DataAccessException("Bad Auth Token", 401);
        for (Map<String, Object> row : list) {
            String username = (String) row.get("name");
            if (username == null) throw new DataAccessException("Bad Auth Token", 401);
            authData = new AuthData(authToken, (String) row.get("name"));
        }
        return authData;
    }



}

