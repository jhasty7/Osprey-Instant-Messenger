
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ServerListener implements Runnable {

    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String username;
    private Socket myClient;
    private String HOST_NAME = "127.0.0.1";
    private int PORT_NUMBER = 45566;
    private boolean isConnected = false;

    public ServerListener(String username) {
        this.username = username;
        myClient = null;
        in = null;
        out = null;
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
            out.writeObject(new ConnectingPacket(username));

            //after this will just wait for new packets from the server
            do {
                Object obj;
                obj = in.readObject();
                // deciefer the object

            } while (isConnected);

        }
        catch (IOException | ClassNotFoundException ex) {
            System.err.println("Error: in ServerListener at run; receiving data");
        }

    }

    // basic client functions
    public boolean disconnect() {
        return false;
    }

    public boolean connect() {

        return false;
    }

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
        myClient.close();
        in.close();
        out.close();
        return loginOrRegisterResponse;

    }

    // outgoing message *** spawn a new thread to handle these (or maybe not i dunno)
    public void blockFriend(String friendName) {

    }

    public void addFriend(String friendName) {

    }

    public void updateStatusText(String statusText) {

    }

    public void updateUserStatus(USER_STATUS currentStatus) {

    }

    public void removeFriend(String friendName) {

    }

}
