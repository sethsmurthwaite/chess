package dataAccess;

import model.AuthData;

import java.util.HashMap;
import java.util.UUID;

public class MemoryAuthDAO implements AuthDAO {

    static HashMap<String, String> authTable = new HashMap<String, String>();

    public AuthData createAuth(String username) throws DataAccessException {
        String authToken = UUID.randomUUID().toString();
        this.authTable.put(authToken, username);
        return new AuthData(authToken, username);
    }

    public void clearAuth() {
        authTable.clear();
    }

    public void deleteAuth(String authToken) {
        authTable.remove(authToken);
    }

    public AuthData readAuth(String authToken) throws DataAccessException {
        String username = authTable.get(authToken);
        if (username == null) throw new DataAccessException("Bad Auth Token", 401);
        return new AuthData(authToken, username);
    }
}
