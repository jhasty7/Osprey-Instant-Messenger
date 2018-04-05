
import java.util.ArrayList;

public class BlockedFriendsList extends Packet{

    private ArrayList<String> blockFriendsList;
    private boolean sendingToServer = false;
    
    public BlockedFriendsList() {
        sendingToServer = true;
        blockFriendsList = null;
    }

    public BlockedFriendsList(ArrayList<String> blockFriendsList) {
        this.blockFriendsList = blockFriendsList;
    }

    public ArrayList<String> getBlockFriendsList() {
        return blockFriendsList;
    }

    public boolean isSendingToServer() {
        return sendingToServer;
    }
    
}
