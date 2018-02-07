
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

/**
 *
 * @author Joshua
 */
public class ClientHandler implements Runnable {

    public static ArrayList<User> connectedUsers = new ArrayList<>();
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean remainsListening = false;
    private Packet packet;
    private Message message;
    private LoginRegisterPacket loginPacket;
    private AddFriend addFriend;
    private Socket client;
    private Server myServer;
    
    // testing variables
    private boolean addOneTime = false;
    // end testing variables

    public ClientHandler(Socket client, String username, Server myServer) throws IOException {

        connectedUsers.add(new User(client, username, in, out));

        /* initialize input/output streams */
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());

    }

    // this constructor is to bypass login/registration for testing
    public ClientHandler(Socket client, Server myServer) throws IOException {
        this.client = client;
        this.myServer = myServer;
        loginPacket = new LoginRegisterPacket(null,null);
        message = new Message(null,null,null);
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }
    // end testing constructor

    public void clientListening() throws IOException, ClassNotFoundException {

        remainsListening = true;

        do {
            /* receiving new packet after login */
            Object obj = in.readObject();
            myServer.writeToConsole("Receieved packet from " + client.toString());
            if(obj.getClass().equals(loginPacket.getClass())){
                // this is for testing
                loginPacket = (LoginRegisterPacket) obj;
                myServer.writeToConsole(loginPacket.getUsername() + " added to connected user list");
                connectedUsers.add(new User(client, loginPacket.getUsername(),in,out));
                // end testing
            }
            else if (obj.getClass().equals(message.getClass())) {
                message = (Message) obj;
                myServer.writeToConsole("From " + message.getComingFrom() + " to " + message.getSendingTo() + " > " + message.getMessage());
                new Thread(new MessageHandler(message)).start();
            }
            else if (obj instanceof AddFriend) {

                addFriend = (AddFriend) obj;

            }
            else {
                //packet coversion problem
            }
        } while (remainsListening);

    }

    /**
     * Starting threader here instead of login for testing purposes
     */
    @Override
    public void run() {
        try {
            clientListening();
        }
        catch (IOException | ClassNotFoundException ex) {
        }
    }
    // end testing thread
    
}
