package dataAccess;

import model.AuthData;

public interface AuthDAO {
    public AuthData createAuth(String username) throws DataAccessException;
    public void clearAuth();
    public void deleteAuth(String authToken);
    public AuthData readAuth(String authToken) throws DataAccessException;

}
