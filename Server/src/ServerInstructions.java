
import java.io.IOException;
import java.net.Socket;

/**
 *
 * @author Joshua
 */
public class ServerInstructions{
    
    public boolean login(Socket client, String username, String password){
        // perform login with database (check username/password)
            
        //if true
        //new ClientHandler(client,username, myServer);

        //if false
        return false;
    }
    
    /**
     * This method will register a username and password in
     * the database
     * @return 
     */
    public boolean register(){
        return false;
    }
    
}
