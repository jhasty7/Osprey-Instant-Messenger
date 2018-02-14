
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ServerInstructions {

    // most likely the database will remain on the same machine as the server
    // but, if it doesn't the url is changed here,
    // and you can not use 'root' for the mySql login. 'root' is reserved for local machine only.
    private static final String DB_NAME = "jdbc:mysql://127.0.0.1:3306/unf_im_database";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "l83r6q4E$8io";

    private Connection myConnection = null;
    private Server myServer;

    public ServerInstructions(Server myServer) {
        this.myServer = myServer;
    }

    public boolean login(String username, String password) {
        boolean isSuccessful = false;
        // perform login with database (check username/password)
        String sqlString = processSQLString(SQL_CALLS.Login, username, password);

        try {
            ResultSet rs = ExecuteQueryDatabase(sqlString);
            rs.next();
            if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                isSuccessful = true;
            }
        }
        catch (SQLException ex) {
            System.err.println("Error: in ServerInstruction at login method");
            System.out.println(ex);
        }

        return isSuccessful;
    }

    /**
     * This will call the register_user stored procedure in the database (will
     * register the user and create the user specific table)
     *
     * @param username
     * @param password
     * @return
     */
    public boolean register(String username, String password) {
        String sqlString = processSQLString(SQL_CALLS.Register, null);
        return ExecuteDatabaseRegisterStoredProcedure(sqlString, username, password);
    }

    /**
     * This will add friend to sql table
     *
     * @param username
     * @param friendName
     * @return
     */
    public boolean addFriend(String username, String friendName) {
        return ExecuteUpdateDatabase(processSQLString(SQL_CALLS.AddFriend, username, friendName));
    }

    /**
     * This is remove friend from sql table
     *
     * @param username
     * @param friendName
     * @return
     */
    public boolean removeFriend(String username, String friendName) {
        return ExecuteUpdateDatabase(processSQLString(SQL_CALLS.RemoveFriend, username, friendName));
    }

    /**
     * this will update user status in user_credentials table
     *
     * @param username
     * @param textStatus
     * @return
     */
    public boolean setTextStatus(String username, String textStatus) {
        return ExecuteUpdateDatabase(processSQLString(SQL_CALLS.UpdateTextStatus, username, textStatus));
    }

    /**
     * this will update online status in user_credentials table
     *
     * @param username
     * @param onlineStatus
     * @return
     */
    public boolean setOnlineStatus(String username, eONLINE_STATUS onlineStatus) {
        int parser;
        if (onlineStatus == eONLINE_STATUS.online) {
            parser = 1;
        }
        else {
            parser = 0;
        }
        return ExecuteUpdateDatabase(processSQLString(SQL_CALLS.UpdateOnlineStatus, username, String.valueOf(parser)));
    }

    /**
     * this will update current status in user_credentials table
     *
     * @param username
     * @param currentStatus
     * @return
     */
    public boolean setCurrentStatus(String username, eCURRENT_STATUS currentStatus) {
        return ExecuteUpdateDatabase(processSQLString(SQL_CALLS.UpdateCurrentStatus, username, currentStatus.toString()));
    }

    /**
     * this will get get friend info from database, could be client requesting,
     * itself or requesting one of its friends
     *
     * @param username
     * @return
     */
    public Friend getFriendInfoFromDatabase(String name) {
        Friend tempFriend = null;

        String sqlString = processSQLString(SQL_CALLS.GetFriendInfo, name);
        ResultSet rs = ExecuteQueryDatabase(sqlString);
        try {
            while (rs.next()) {
                boolean temp;
                if (rs.getString(2).equals("1")) {
                    temp = true;
                }
                else {
                    temp = false;
                }
                tempFriend = new Friend(
                        rs.getString(1),
                        temp,
                        eCURRENT_STATUS.valueOf(rs.getString(3)),
                        rs.getString(4));
            }
        }
        catch (SQLException e) {
            myServer.writeToConsole("Error getting friend info for " + name + "\n");
        }
        return tempFriend;
    }

    /**
     * Retrieve clients friends list from the database
     *
     * @param username
     * @return
     */
    public FriendsList retrieveFriendsList(String username) {

        ArrayList<Friend> tempFriendList = new ArrayList<>();

        String sqlString = processSQLString(SQL_CALLS.GetFriendsList, username);
        ResultSet rs = ExecuteQueryDatabase(sqlString);

        try {

            while (rs.next()) {
                tempFriendList.add(new Friend(
                        rs.getString(1),
                        Boolean.parseBoolean(rs.getString(2)),
                        eCURRENT_STATUS.valueOf(rs.getString(3)),
                        rs.getString(4)));
            }

            return new FriendsList(tempFriendList);
        }
        catch (SQLException e) {
            myServer.writeToConsole("Error generating friends list for " + username + "\n");
        }
        return new FriendsList();
    }

    /**
     * retrieve only the only the users online friends from the database
     *
     * @param username
     * @return
     */
    public ArrayList<Friend> retrieveOnlyOnlineFriends(String username) {
        ArrayList<Friend> tempFriendList = new ArrayList<>();

        String sqlString = processSQLString(SQL_CALLS.GetOnlyOnlineFriends, username);
        ResultSet rs = ExecuteQueryDatabase(sqlString);

        try {
            while (rs.next()) {
                tempFriendList.add(new Friend(
                        rs.getString(1),
                        Boolean.parseBoolean(rs.getString(2)),
                        eCURRENT_STATUS.valueOf(rs.getString(3)),
                        rs.getString(4)));
            }
        }
        catch (SQLException e) {
            myServer.writeToConsole("Error generating friends list for " + username + "\n");
        }
        return tempFriendList;
    }

    /**
     * updating data from the database
     *
     * @return
     */
    private boolean ExecuteUpdateDatabase(String sqlString) {
        boolean isSuccessful = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection(DB_NAME, DB_USERNAME, DB_PASSWORD);
            PreparedStatement statement = myConnection.prepareStatement(sqlString);
            statement.executeUpdate();
            isSuccessful = true;
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }
        return isSuccessful;
    }

    /**
     * retrieving data from the database
     *
     * @return
     */
    private ResultSet ExecuteQueryDatabase(String sqlString) {

        ResultSet rs = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection(DB_NAME, DB_USERNAME, DB_PASSWORD);
            PreparedStatement statement = myConnection.prepareStatement(sqlString);
            rs = statement.executeQuery();
        }
        catch (SQLException | ClassNotFoundException e) {
            System.err.println(e);
        }
        return rs;
    }

    private boolean ExecuteDatabaseRegisterStoredProcedure(String sqlString, String username, String password) {
        boolean isSuccessful = false;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            myConnection = DriverManager.getConnection(DB_NAME, DB_USERNAME, DB_PASSWORD);
            CallableStatement statement = myConnection.prepareCall(sqlString);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
            isSuccessful = true;
        }
        catch (ClassNotFoundException | SQLException e) {
            System.out.println(e);
        }

        return isSuccessful;
    }

    /*
     * Overloading methods for processSQLString
     */
    private String processSQLString(SQL_CALLS calls, String var1) {

        return processSQLString(calls, var1, null, null);
    }

    private String processSQLString(SQL_CALLS calls, String var1, String var2) {

        return processSQLString(calls, var1, var2, null);
    }

    /**
     * Gets info and returns the string for SQL command
     *
     * @param calls
     * @param var1
     * @param var2
     * @param var3
     * @return
     */
    private String processSQLString(SQL_CALLS calls, String var1, String var2, String var3) {
        String sqlString;

        switch (calls) {
            case Login:
                sqlString = "SELECT username, password FROM user_credentials"
                        + " WHERE username = '" + var1
                        + "' AND password = '" + var2 + "'";
                break;
            case Register:
                sqlString = "{ call register_user(?,?)}";
                break;
            case GetFriendsList:
                return "SELECT username, online_status, current_status, text_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE EXISTS ( SELECT username\n"
                        + "FROM " + var1 + "FL\n"
                        + "WHERE username = friend AND blocked = 0)";
            case GetOnlyOnlineFriends:
                return "SELECT username, online_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE EXISTS ( SELECT username\n"
                        + "FROM " + var1 + "FL\n"
                        + "WHERE username = friend AND blocked = 0) AND online_status = 1";
            case GetFriendInfo:
                return "SELECT username, online_status, current_status, text_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE username = '" + var1 + "'";
            case AddFriend:
                return "INSERT INTO " + var1 + "FL (friend,blocked) VALUES ('"
                        + var2 + "', 0)";
            case RemoveFriend:
                return "DELETE FROM " + var1 + "FL WHERE friend = '" + var2 + "'";
            case UpdateTextStatus:
                return "UPDATE user_credentials SET text_status = '" + var2
                        + "' WHERE username = '" + var1 + "'";
            case UpdateOnlineStatus:
                return "UPDATE user_credentials SET online_status = " + var2
                        + " WHERE Username = '" + var1 + "'";
            case UpdateCurrentStatus:
                return "UPDATE user_credentials SET current_status = '" + var2 + "'"
                        + " WHERE Username = '" + var1 + "'";
            default:
                sqlString = null;
        }

        if (sqlString == null) {
            System.err.println("Error: at ServerInstructions processSQLString; string is null");
        }
        return sqlString;
    }

    private enum SQL_CALLS {
        Login,
        Register,
        GetFriendsList,
        GetOnlyOnlineFriends,
        GetFriendInfo,
        AddFriend,
        RemoveFriend,
        UpdateTextStatus,
        UpdateOnlineStatus,
        UpdateCurrentStatus
    }

}
