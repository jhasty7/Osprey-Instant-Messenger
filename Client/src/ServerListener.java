
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

                if (obj.getClass().equals(MessagePacket.class)) {
                    // find friend window/if not there pop it up and display message
                    myMainWindow.incomingMessage((MessagePacket) obj);
                }
                else if(obj.getClass().equals(ImagePacket.class)){
                    myMainWindow.incomingMessage((ImagePacket) obj);
                }
                else if(obj.getClass().equals(SendFilePacket.class)){
                    myMainWindow.incomingMessage((SendFilePacket) obj);
                }
                else if (obj.getClass().equals(Friend.class)) {
                    myMainWindow.processFriend((Friend) obj);
                    DeveloperWindow.displayMessage(((Friend) obj).toString());
                }
                else if (obj.getClass().equals(ServerConfirmation.class)) {
                    ServerConfirmation tempSC = (ServerConfirmation) obj;
                    myMainWindow.createAlertFromServer(tempSC.isSuccessful(), tempSC.getContext());
                }
                else if (obj.getClass().equals(BlockFriend.class)){
                    BlockFriend blockFriend = (BlockFriend) obj;
                    myMainWindow.processBeingBlocked(blockFriend.getFriend());
                }
                else if (obj.getClass().equals(FriendsList.class)){
                    FriendsList temp = (FriendsList) inFriendsList;
                    myMainWindow.setFriendsList(temp);
                }
                else if(obj.getClass().equals(BlockedFriendsList.class)){
                    myMainWindow.openBlockedFriendsListWindow((BlockedFriendsList)obj);
                }
                else if(obj.getClass().equals(GetAllUsers.class)){
                    myMainWindow.openAllUsersWindow((GetAllUsers)obj);
                }

            } while (isConnected);
            myClient.close();
        }
        catch (IOException | ClassNotFoundException ex) {
            DeveloperWindow.displayMessage("Error: in ServerListener at run; receiving data");
            DeveloperWindow.displayMessage(ex.toString());
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
    public void sendMessage(MessagePacket textMessage) {
        try {
            out.writeObject(textMessage);
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending image packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }
    
    public void sendImage(ImagePacket ip) {
        try {
            out.writeObject(ip);
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending message packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }
    
    public void sendFile(SendFilePacket sfp){
        try{
            out.writeObject(sfp);
        }catch(IOException ex){
            DeveloperWindow.displayMessage("Error: sending file packet");
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
    
    public void getBlockedFriendsList(){
        try {
            out.writeObject(new BlockedFriendsList());
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending blockFriendsList packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }
    
    public void getAllUsers(){
        try {
            out.writeObject(new GetAllUsers());
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending blockFriendsList packet");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void addFriend(String friendName) {
        try {
            PendingFriendRequest temp = new PendingFriendRequest(friendName);
            temp.setAsRequest();
            out.writeObject(temp);
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("Error: sending PendingFriendRequest packet");
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
    
    public void acceptFriendRequest(String friendName){
        try {
            PendingFriendRequest pfr = new PendingFriendRequest(friendName);
            pfr.acceptRequest();
            out.writeObject(pfr);
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
