
import java.util.ArrayList;

public class GetAllUsers extends Packet{
    private ArrayList<Friend> allUsers;
    private boolean sendingToServer = false;
    
    public GetAllUsers() {
        sendingToServer = true;
        allUsers = null;
    }

    public GetAllUsers(ArrayList<Friend> allUsers) {
        this.allUsers = allUsers;
    }

    public ArrayList<Friend> getAllUsersList() {
        return allUsers;
    }

    public boolean isSendingToServer() {
        return sendingToServer;
    }
}
