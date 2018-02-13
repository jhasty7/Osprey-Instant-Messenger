
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ServerInstructions {

    private static final String DB_NAME = "jdbc:mysql://127.0.0.1:3306/unf_im_database";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "l83r6q4E$8io";

    private Connection myConnection = null;

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
        //if false
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
        String sqlString = "";

        switch (calls) {
            case Login:
                sqlString = "SELECT username, password FROM user_credentials"
                        + " WHERE username = '" + var1
                        + "' AND password = '" + var2 + "'";
                break;
            case Register:
                sqlString = "{ call register_user(?,?,?)}";
                break;
            default:
                sqlString = null;
        }

        if (sqlString == null) {
            System.err.println("Error: at ServerInstructions processSQLString; string is null");
        }
        return sqlString;
    }

    private enum SQL_CALLS {
        Login, Register
    }

}
