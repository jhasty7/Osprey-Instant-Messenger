
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
    
    public ServerInstructions(Server myServer){
        this.myServer = myServer;
    }
    
    public boolean login(String username, String password) {
        boolean isSuccessful = false;
        // perform login with database (check username/password)
        String sqlString = processSQLString(SQL_CALLS.Login, username, password, null);

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
     * This will call the register_user stored procedure in the database
     * (will register the user and create the user specific table)
     *
     * @param username
     * @param password
     * @return
     */
    public boolean register(String username, String password) {
        String sqlString = processSQLString(SQL_CALLS.Register, null , null, null);
        return ExecuteDatabaseRegisterStoredProcedure(sqlString, username, password);
    }
    
    /**
     * This will add friend to sql table
     * @param username
     * @param friendName
     * @return 
     */
    public boolean addFriend(String username, String friendName){
        boolean isSuccessful = false;
        
        
        return isSuccessful;
    }
    
    /**
     * This is remove friend from sql table
     * @param username
     * @param friendName
     * @return 
     */
    public boolean removeFriend(String username, String friendName){
        boolean isSuccessful = false;
        
        
        return isSuccessful;
    }
    
    /**
     * this will update user status in user_credentials table
     * @param username
     * @param textStatus
     * @return 
     */
    public boolean setTextStatus(String username, String textStatus){
        boolean isSuccessful = false;
        
        
        return isSuccessful;
    }
    
    /**
     * this will update online status in user_credentials table
     * @param username
     * @param onlineStatus
     * @return 
     */
    public boolean setOnlineStatus(String username, eONLINE_STATUS onlineStatus){
        boolean isSuccessful = false;
        
        
        return isSuccessful;
    }
    
    /**
     * this will update current status in user_credentials table
     * @param username
     * @param currentStatus
     * @return 
     */
    public boolean setCurrentStatus(String username, eCURRENT_STATUS currentStatus){
        boolean isSuccessful = false;
        
        
        return isSuccessful;
    }
    
    /**
     * this will get the clients info as a friend and return it
     * @param username
     * @return 
     */
    public Friend retrieveClientAsFriend(String username){
        Friend tempFriend = null;
        
        String sqlString = processSQLString(SQL_CALLS.GetClientAsFriend, username, null, null);
        ResultSet rs = ExecuteQueryDatabase(sqlString);
        try {
            while (rs.next()) {
                tempFriend = new Friend(
                        rs.getString(1),
                        Boolean.parseBoolean(rs.getString(2)),
                        eCURRENT_STATUS.valueOf(rs.getString(3)),
                        rs.getString(4));
            }
        }
        catch (SQLException e) {
            myServer.writeToConsole("Error generating friends list for " + username + "\n");
        }
        return tempFriend;
    }
    
    /**
     * Retrieve clients friends list from the database
     * @param username
     * @return 
     */
    public FriendsList retrieveFriendsList(String username){
        
        ArrayList<Friend> tempFriendList = new ArrayList<>();
        
        String sqlString = processSQLString(SQL_CALLS.GetFriendsList, username, null, null);
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
     * @param username
     * @return 
     */
    public ArrayList<Friend> retrieveOnlyOnlineFriends(String username){
        ArrayList<Friend> tempFriendList = new ArrayList<>();
        
        String sqlString = processSQLString(SQL_CALLS.GetOnlyOnlineFriends, username, null, null);
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
    
    private boolean ExecuteDatabaseRegisterStoredProcedure(String sqlString, String username, String password){
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

    private String processSQLString(SQL_CALLS calls, String var1, String var2, String var3) {
        String sqlString;

        switch (calls) {
            case Login:
                sqlString = "SELECT username, password FROM user_credentials"
                        + " WHERE username = '" + var1
                        + "' AND password = '" + var2 + "'";
                break;
            case Register:
                sqlString = "{ call register_user(?,?,?)}";
                break;
            case GetFriendsList:
                return "SELECT username, online_status, current_status, text_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE EXISTS ( SELECT username\n"
                        + "FROM " + var1 + "FL\n"
                        + "WHERE username = friend AND blocked = no)";
            case GetOnlyOnlineFriends:
                return "SELECT username, online_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE EXISTS ( SELECT username\n"
                        + "FROM " + var1 + "FL\n"
                        + "WHERE username = friend AND blocked = no) AND online_status = 1";
            case GetClientAsFriend:
                return "SELECT username, online_status, current_status, text_status\n"
                        + "FROM user_credentials\n"
                        + "WHERE username = '" + var1 + "'";
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
        GetClientAsFriend
    }

}
