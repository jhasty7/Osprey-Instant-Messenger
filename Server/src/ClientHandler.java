
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
            else if (obj.getClass().equals(MessagePacket.class)) {

                MessagePacket message = (MessagePacket) obj;
                myServer.writeToConsole("From " + message.getComingFrom() + " to " + message.getSendingTo() + " > " + message.getMessage());
                new Thread(new MessageHandler(message)).start();

            }
            else if (obj.getClass().equals(ImagePacket.class)) {

                ImagePacket ip = (ImagePacket) obj;
                myServer.writeToConsole("From " + ip.getComingFrom() + " to " + ip.getSendingTo() + " * user is sending Image.");
                new Thread(new ImageHandler(ip)).start();

            }
            else if (obj.getClass().equals(SendFilePacket.class)) {

                SendFilePacket sfp = (SendFilePacket) obj;
                myServer.writeToConsole("From " + sfp.getComingFrom() + " to " + sfp.getSendingTo() + " * user is sending a file.");
                new Thread(new FileHandler(sfp)).start();

            }
            else if (obj.getClass().equals(PendingFriendRequest.class)) {

                PendingFriendRequest pfr = (PendingFriendRequest) obj;
                if (pfr.isRequesting()) {
                    if (serverInstructions.addPendingFriendRequest(clientsUsername, pfr.getFriendname())) {
                        for (User u : connectedUsers) {
                            if (u.getUsersUsername().equals(pfr.getFriendname())) {
                                Friend tempFriend = serverInstructions.getFriendInfoFromDatabase(clientsUsername);
                                tempFriend.setPendingAdd(true);
                                OutgoingPacketHandler.sendFriendToClient(u.getOut(), tempFriend);
                            }
                        }
                    }
                }
                else {
                    if (pfr.isAccepting()) {
                        if (serverInstructions.updatePendingFriend(clientsUsername, pfr.getFriendname())) {
                            if (serverInstructions.addFriend(pfr.getFriendname(), clientsUsername)) {
                                Friend tempFriend = serverInstructions.getFriendInfoFromDatabase(pfr.getFriendname());
                                tempFriend.setAcceptedFriendRequest(true);
                                OutgoingPacketHandler.sendFriendToClient(out, tempFriend);
                                for (User u : connectedUsers) {
                                    if (u.getUsersUsername().equals(pfr.getFriendname())) {
                                        Friend tempFriendInner = serverInstructions.getFriendInfoFromDatabase(clientsUsername);
                                        tempFriendInner.setIsAdd(true);
                                        OutgoingPacketHandler.sendFriendToClient(u.getOut(), tempFriendInner);
                                    }
                                }
                            }
                        }
                    }
                    else {
                        if (pfr.isBlock()) {
                            serverInstructions.blockFriend(clientsUsername, pfr.getFriendname());
                        }
                        else {
                            serverInstructions.removeFriend(clientsUsername, pfr.getFriendname());
                        }
                    }
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
            else if (obj.getClass().equals(BlockFriend.class)) {
                BlockFriend blockFriend = (BlockFriend) obj;
                if (serverInstructions.blockFriend(clientsUsername, blockFriend.getFriend())) {
                    OutgoingPacketHandler.SendConfirmation(out, true, blockFriend.getFriend() + " is now blocked.");
                    OutgoingPacketHandler.SendFriendsList(out, serverInstructions.retrieveFriendsList(clientsUsername));
                    for (User u : connectedUsers) {
                        if (u.getUsersUsername().equals(blockFriend.getFriend())) {
                            OutgoingPacketHandler.sendBlockFriendPacket(u.getOut(), new BlockFriend(clientsUsername));
                            OutgoingPacketHandler.SendFriendsList(u.getOut(), serverInstructions.retrieveFriendsList(blockFriend.getFriend()));
                        }
                    }
                }
                else {
                    OutgoingPacketHandler.SendConfirmation(out, false, blockFriend.getFriend() + " is NOT blocked, something went wrong.");
                }
            }
            else if (obj.getClass().equals(GetAllUsers.class)) {
                OutgoingPacketHandler.sendAllUsers(out, new GetAllUsers(serverInstructions.getAllUsers(clientsUsername)));
            }
            else if (obj.getClass().equals(BlockedFriendsList.class)) {
                OutgoingPacketHandler.sendBlockedFriendsList(out, new BlockedFriendsList(serverInstructions.getBlockedFriendsList(clientsUsername)));
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
