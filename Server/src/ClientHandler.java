
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
    private Socket client;
    private ServerInstructions serverInstructions;
    private Server myServer;

    // dependency packet objects
    private AddFriend addFriend;
    private Packet packet;
    private Message message;
    private LoginRegisterPacket loginPacket;
    private ConnectingPacket connectingPacket;

    public ClientHandler(Socket client, Server myServer) throws IOException {
        this.client = client;
        this.myServer = myServer;

        addFriend = new AddFriend();
        packet = new Packet();
        connectingPacket = new ConnectingPacket();
        loginPacket = new LoginRegisterPacket();
        message = new Message();

        serverInstructions = new ServerInstructions();
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());
    }

    public void clientListening() throws IOException, ClassNotFoundException {

        remainsListening = true;

        do {
            Object obj = in.readObject();
            myServer.writeToConsole("Receieved packet from " + client.toString());

            /**
             * if a login packet is received, the connection will be short
             * lived. The connection will drop after validating login. Then the
             * client will reconnect on a more long term basis. However; it will
             * still check any incoming packets if they are login first.
             */
            if (obj.getClass().equals(loginPacket.getClass())) {

                loginPacket = (LoginRegisterPacket) obj;
                
                // check to see if it's for loging in
                if (loginPacket.isLogin()) {
                    if (serverInstructions.login(loginPacket.getUsername(), loginPacket.getPassword())) {
                        // success
                        loginPacket = new LoginRegisterPacket();
                        loginPacket.setLoginTrue();
                        loginPacket.setSuccessful(true);
                    }
                    else {
                        // failure
                        loginPacket = new LoginRegisterPacket();
                        loginPacket.setLoginTrue();
                        loginPacket.setSuccessful(false);
                    }
                }
                // check to see if it's for registering
                else if (loginPacket.isRegister()) {
                    if (serverInstructions.register(loginPacket.getUsername(), loginPacket.getPassword())) {
                        // success
                        loginPacket = new LoginRegisterPacket();
                        loginPacket.setRegisterTrue();
                        loginPacket.setSuccessful(true);
                    }
                    else {
                        // failure
                        loginPacket = new LoginRegisterPacket();
                        loginPacket.setRegisterTrue();
                        loginPacket.setSuccessful(false);
                    }
                }
                
                // tell the client the login/registration was validated (or not)
                out.writeObject(loginPacket);
                // and drop its connection allowing it to reconnect more long term
                remainsListening = false;

            }
            else if (obj.getClass().equals(connectingPacket.getClass())) {

                connectingPacket = (ConnectingPacket) obj;
                connectedUsers.add(new User(client, connectingPacket.getUsername(), in, out));
                myServer.writeToConsole(connectingPacket.getUsername() + " added to connected user list");

            }
            else if (obj.getClass().equals(message.getClass())) {
                message = (Message) obj;
                myServer.writeToConsole("From " + message.getComingFrom() + " to " + message.getSendingTo() + " > " + message.getMessage());
                new Thread(new MessageHandler(message)).start();
            }
            else if (obj.getClass().equals(addFriend.getClass())) {

                addFriend = (AddFriend) obj;

            }
            else {
                //packet deserialization problem
                remainsListening = false;
            }
        } while (remainsListening);
        dropConnection();
    }

    private void dropConnection() throws IOException {
        client.close();
        in.close();
        out.close();
        client = null;
        in = null;
        out = null;
    }

    /**
     * thread frame (makes it more neat to reduce try/catches)
     */
    @Override
    public void run() {
        try {
            clientListening();
        }
        catch (IOException | ClassNotFoundException ex) {
            myServer.writeToConsole("Error: in clientHandler class run method");
        }
    }

}
