package dataAccess;

import model.UserData;
import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class DBUserDAO implements UserDAO {
    DatabaseManager dbman;

    public DBUserDAO(DatabaseManager dbman) throws DataAccessException {
        this.dbman = dbman;
        dbman.configureDatabase();
    }
    @Override
    public void createUser(UserData u) {

    }

    @Override
    public UserData readUser(String username) {
        return null;
    }

    @Override
    public void clearUsers() {

    }

}
