package dataAccess;

import model.UserData;

import java.util.HashMap;

public class UserDAO {

    HashMap<String, UserData> userTable = new HashMap<String, UserData>();

    public void createUser(UserData u) {
        String userName = u.username();
        this.userTable.put(u.username(), u);
    }

    public UserData readUser(String username) {
        return userTable.get(username);
    }

    public void clearUsers() {
        userTable.clear();
    }


}
