
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *
 * @author Joshua
 */
public class ClientHandler implements Runnable {

    public static List<User> connectedUsers = new CopyOnWriteArrayList<User>();
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean remainsListening = false;
    private Socket client;
    private ServerInstructions serverInstructions;
    private Server myServer;
    private String clientsUsername = null;
    private Friend clientAsFriend;

    public ClientHandler(Socket client, Server myServer) throws IOException {
        this.client = client;
        this.myServer = myServer;

        serverInstructions = new ServerInstructions(myServer);
        out = new ObjectOutputStream(client.getOutputStream());
        in = new ObjectInputStream(client.getInputStream());

        clientAsFriend = new Friend();
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
            if (obj.getClass().equals(LoginRegisterPacket.class)) {

                LoginRegisterPacket lrp = (LoginRegisterPacket) obj;

                // check to see if it's for loging in
                if (lrp.isLogin()) {
                    if (serverInstructions.login(lrp.getUsername(), lrp.getPassword())) {
                        // success
                        lrp = new LoginRegisterPacket();
                        lrp.setLoginTrue();
                        lrp.setSuccessful(true);
                    }
                    else {
                        // failure
                        lrp = new LoginRegisterPacket();
                        lrp.setLoginTrue();
                        lrp.setSuccessful(false);
                    }
                }
                // check to see if it's for registering
                else if (lrp.isRegister()) {
                    if (serverInstructions.register(lrp.getUsername(), lrp.getPassword())) {
                        // success
                        lrp = new LoginRegisterPacket();
                        lrp.setRegisterTrue();
                        lrp.setSuccessful(true);
                    }
                    else {
                        // failure
                        lrp = new LoginRegisterPacket();
                        lrp.setRegisterTrue();
                        lrp.setSuccessful(false);
                    }
                }

                // tell the client the login/registration was validated (or not)
                out.writeObject(lrp);
                // and drop its connection allowing it to reconnect more long term
                remainsListening = false;

            }
            else if (obj.getClass().equals(Connecting.class)) {

                Connecting connectingPacket = (Connecting) obj;

                // add user to the userlist
                connectedUsers.add(new User(client, connectingPacket.getUsername(), in, out));

                // bind username to this thread
                clientsUsername = connectingPacket.getUsername();
                myServer.writeToConsole(connectingPacket.getUsername() + " added to connected user list");
                // set user online
                serverInstructions.setOnlineStatus(clientsUsername, eONLINE_STATUS.online);
                
                // generate friends list
                FriendsList fl = serverInstructions.retrieveFriendsList(clientsUsername);
                // send user their friends list
                OutgoingPacketHandler.SendFriendsList(out, fl);
                // update clientAsFriend
                clientAsFriend = serverInstructions.getFriendInfoFromDatabase(clientsUsername);
                OutgoingPacketHandler.SendFriendUpdateComingOnline(out, fl.getOnlineFriends(), clientAsFriend);

                // drop reference to friends list (it will quickly be out of date)
                fl = null;

            }
            else if (obj.getClass().equals(Message.class)) {
                Message message = (Message) obj;
                myServer.writeToConsole("From " + message.getComingFrom() + " to " + message.getSendingTo() + " > " + message.getMessage());
                new Thread(new MessageHandler(message)).start();
            }
            else if (obj.getClass().equals(AddFriend.class)) {
                boolean isSuccessful;
                AddFriend addFriend = (AddFriend) obj;

                isSuccessful = serverInstructions.addFriend(clientsUsername, addFriend.getFriend());
                OutgoingPacketHandler.SendConfirmation(out, isSuccessful, "AddFriend");
                if (isSuccessful) {
                    Friend tempfriend = serverInstructions.getFriendInfoFromDatabase(addFriend.getFriend());
                    tempfriend.setIsAdd(true);
                    OutgoingPacketHandler.sendFriendToClient(out,tempfriend);
                }
            }
            else if (obj.getClass().equals(RemoveFriend.class)) {

                RemoveFriend removeFriend = (RemoveFriend) obj;
                OutgoingPacketHandler.SendConfirmation(out, serverInstructions.removeFriend(clientsUsername, removeFriend.getFriend()), "RemoveFriend");

            }
            else if (obj.getClass().equals(TextStatus.class)) {

                TextStatus textStatus = (TextStatus) obj;
                clientAsFriend.setTextStatus(textStatus.getTextStatus());
                serverInstructions.setTextStatus(clientsUsername, textStatus.getTextStatus());

            }
            else if (obj.getClass().equals(OnlineStatus.class)) {

                OnlineStatus onlineStatus = (OnlineStatus) obj;
                clientAsFriend.setOnlineStatus(onlineStatus.getOnlineStatusBoolean());
                serverInstructions.setOnlineStatus(clientsUsername, onlineStatus.getOnlineStatusEnum());

            }
            else if (obj.getClass().equals(CurrentStatus.class)) {

                CurrentStatus currentStatus = (CurrentStatus) obj;
                clientAsFriend.setCurrentStatus(currentStatus.getCurrentStatus());
                serverInstructions.setCurrentStatus(clientsUsername, currentStatus.getCurrentStatus());

            }
            else if (obj.getClass().equals(Disconnecting.class)) {
                remainsListening = false;
                serverInstructions.setOnlineStatus(clientsUsername, eONLINE_STATUS.offline);
                OutgoingPacketHandler.SendFriendUpdateGoingOffline(serverInstructions.retrieveOnlyOnlineFriends(clientsUsername), clientAsFriend);
                myServer.writeToConsole(clientsUsername + " has disconnected. Removing from the list and closing thread.");
                dropConnection();
            }
            else {
                //packet deserialization problem
                remainsListening = false;
            }
        } while (remainsListening);
        //dropConnection();
    }

    private void dropConnection() throws IOException {
        serverInstructions.setOnlineStatus(clientsUsername, eONLINE_STATUS.offline);
        // remove the user from the server owned connected userlist if possible
        if (clientsUsername != null) {
            for (User thisUser : connectedUsers) {
                if (thisUser.getUsersUsername().equals(clientsUsername)) {
                    connectedUsers.remove(thisUser);
                }
            }
        }

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
            myServer.writeToConsole(ex.toString());
        }
    }

}
