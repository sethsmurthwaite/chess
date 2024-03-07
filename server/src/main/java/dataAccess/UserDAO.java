package dataAccess;

import model.UserData;

public interface UserDAO {
    public void createUser(UserData u);
    public UserData readUser(String username);
    public void clearUsers();
}
