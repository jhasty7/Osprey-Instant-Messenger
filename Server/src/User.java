
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class User {
    private Socket client;
    private String username;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    public User(Socket client, String username, ObjectInputStream in, ObjectOutputStream out){
        this.client = client;
        this.username = username;
        this.in = in;
        this.out = out;
    }
    
    public Socket getUserConnection(){
        return client;
    }
    
    public String getUsersUsername(){
        return username;
    }
    
    public ObjectOutputStream getOut(){
        return out;
    }
    
    public ObjectInputStream getIn(){
        return in;
    }
}
