
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 *
 * @author Joshua
 */
public class LoginOrRegisterClient implements Runnable {

    private Socket client;
    private Server myServer;

    private ObjectInputStream in;
    private ObjectOutputStream out;

    private ServerInstructions instruction;
    //Assumed Packet class (might not need this)
    LoginRegisterPacket packet;

    public LoginOrRegisterClient(Socket client, Server myServer) {
        this.myServer = myServer;
        this.client = client;
        instruction = new ServerInstructions();
        try {
            /* initialize input/output streams */
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        }
        catch (IOException e) {
            myServer.writeToConsole("Error: exception in class LoginOrRegisterClient");
        }
    }

    @Override
    public void run() {
        Object obj;
        try {
            
            obj = (Object) in.readObject();
            
            if(obj.getClass().equals(packet.getClass())){
                
                packet = (LoginRegisterPacket)obj;
                
                if(packet.isLogin()){
                    
                    instruction.login(client, packet.getUsername(), packet.getPassword());
                    
                }else if(packet.isRegister()){
                    
                    instruction.register();
                    
                }else{
                    
                    myServer.writeToConsole("Error: first packet was corrupted");
                    
                }
                
            }else{
                
                myServer.writeToConsole("Error: first packet was not login packet");
                
            }
            
        }
        catch (IOException | ClassNotFoundException ex) {
            myServer.writeToConsole("Error: exception in LoginOrRegisterClient");
        }
    }
    
    
}
