
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerListener implements Runnable {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket myClient;
    public static final String HOST_NAME = Config.cfg.getHostname();
    public static final int PORT_NUMBER = Config.cfg.getPortNumber();
    private boolean isConnected = false;
    private MainWindow myMainWindow;

    public ServerListener() {
        myClient = null;
        in = null;
        out = null;
    }

    public ServerListener(MainWindow myMainWindow) {
        this.myMainWindow = myMainWindow;
    }

    // incoming messages here on the new thread
    @Override
    public void run() {

        try {
            myClient = new Socket(HOST_NAME, PORT_NUMBER);
            in = new ObjectInputStream(myClient.getInputStream());
            out = new ObjectOutputStream(myClient.getOutputStream());
            isConnected = true;

            //send connectingPacket to let the server you are connecting longterm
            out.writeObject(new Connecting(Config.cfg.getUsername()));

            // get your friends list
            Object inFriendsList = in.readObject();
            FriendsList tempFriendsList = (FriendsList) inFriendsList;
            myMainWindow.setFriendsList(tempFriendsList);
            //after this will just wait for new packets from the server
            do {
                Object obj;
                obj = in.readObject();

                if (obj.getClass().equals(Message.class)) {
                    // find friend window/if not there pop it up and display message
                    myMainWindow.incomingMessage((Message) obj);
                }
                else if (obj.getClass().equals(Friend.class)) {
                    myMainWindow.processFriend((Friend) obj);
                    DeveloperWindow.displayMessage(((Friend) obj).toString());
                }
                else if (obj.getClass().equals(ServerConfirmation.class)) {
                    ServerConfirmation tempSC = (ServerConfirmation) obj;
                    myMainWindow.createAlertFromServer(tempSC.isSuccessful(), tempSC.getContext());
                }

            } while (isConnected);
            myClient.close();
        }
        catch (IOException | ClassNotFoundException ex) {
            DeveloperWindow.displayMessage("Error: in ServerListener at run; receiving data");
        }

    }

    // basic client functions
    public void disconnect() {
        if (isConnected) {
            isConnected = false;
            tellServerToDisconnect();
        }
    }

    public void tellServerToDisconnect() {
        try {
            out.writeObject(new Disconnecting());
            in.close();
            out.close();

        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending disconnecting packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    /**
     * begin outgoing packets
     *
     */

    public void sendMessage(Message textMessage) {
        try {
            out.writeObject(textMessage);
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending message packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void blockFriend(String friendName) {

        try {
            out.writeObject(new BlockFriend(friendName));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending blockFriend packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void addFriend(String friendName) {
        try {
            out.writeObject(new AddFriend(friendName));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending AddFriend packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void updateTextStatus(String statusText) {
        try {
            out.writeObject(new TextStatus(statusText));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending TextStatus packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void updateCurrentStatus(eCURRENT_STATUS currentStatus) {
        try {
            out.writeObject(new CurrentStatus(currentStatus));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending CurrentStatus packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void updateOnlineStatus(eONLINE_STATUS onlineStatus) {
        try {
            out.writeObject(new OnlineStatus(onlineStatus));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending OnlineStatus packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void removeFriend(String friendName) {
        try {
            out.writeObject(new RemoveFriend(friendName));
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: RemoveFriend packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    /**
     * end outgoing packets
     */
    /**
     * this method creates a temporary connection to the server to attempt to
     * login. after the login is validated, the connection will be closed, then
     * reopened more long term. If the validation returns false, no new
     * connection will be open. This won't be split into a new thread because
     * much of the UI changes based of this result.
     *
     * @param username
     * @param password
     * @return
     * @throws java.io.IOException
     */
    public int loginOrRegister(String username, String password, boolean isLogin) throws IOException {
        int loginOrRegisterResponse = 0;
        try {

            myClient = new Socket(HOST_NAME, PORT_NUMBER);
            in = new ObjectInputStream(myClient.getInputStream());
            out = new ObjectOutputStream(myClient.getOutputStream());

            LoginRegisterPacket lrp = new LoginRegisterPacket(username, password);

            // differentiate between a login and registeration request
            if (isLogin) {
                lrp.setLoginTrue();
            }
            else {
                lrp.setRegisterTrue();
            }

            // send packet
            out.writeObject(lrp);
            //set timeout just in case
            myClient.setSoTimeout(8000);
            //wait for serve response
            Object obj = in.readObject();

            if (obj.getClass().equals(lrp.getClass())) {

                lrp = (LoginRegisterPacket) obj;

                if (lrp.isLogin()) {
                    if (lrp.isSuccessful()) {
                        loginOrRegisterResponse = 1;
                    }
                    else {
                        loginOrRegisterResponse = 2;
                    }
                }
                else {
                    if (lrp.isRegister()) {
                        if (lrp.isSuccessful()) {
                            loginOrRegisterResponse = 3;
                        }
                        else {
                            loginOrRegisterResponse = 4;
                        }
                    }
                }

            }
            else {
                loginOrRegisterResponse = 5;
            }

        }
        catch (ClassNotFoundException ex) {
            loginOrRegisterResponse = 0;
        }
        closeIO();
        return loginOrRegisterResponse;

    }

    private void closeIO() {
        try {
            myClient.close();
            in.close();
            out.close();
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: trying to close IO");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

}
