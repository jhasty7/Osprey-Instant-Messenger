
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public final class OutgoingPacketHandler {
    
    public static void SendConfirmation(ObjectOutputStream out, boolean isSuccessful, String context) throws IOException{
        out.reset();
        out.writeObject(new ServerConfirmation(isSuccessful,context));
    }
    
    public static void SendFriendsList(ObjectOutputStream out, FriendsList friendsList) throws IOException{
        out.reset();
        out.writeObject(friendsList);
    }
    
    public static void sendFriendToClient(ObjectOutputStream out, Friend friend) throws IOException{
        out.reset();
        out.writeObject(friend);
    }
    
    public static void sendBlockFriendPacket(ObjectOutputStream out, BlockFriend blockFriend) throws IOException{
        out.reset();
        out.writeObject(blockFriend);
    }
    
    public static void sendBlockedFriendsList(ObjectOutputStream out, BlockedFriendsList bfl) throws IOException{
        out.reset();
        out.writeObject(bfl);
    }
    
    public static void sendAllUsers(ObjectOutputStream out, GetAllUsers gau) throws IOException{
        out.reset();
        out.writeObject(gau);
    }
    
    public static void SendFriendUpdateComingOnline(ObjectOutputStream out, ArrayList<Friend> onlineFriends, Friend tempFriend) throws IOException{
        out.reset();
        tempFriend.setIsYourself(true);
        out.writeObject(tempFriend);
        tempFriend.setIsYourself(false);
        tempFriend.setIsUpdate(true);
        
        // send your friend ID to anyone that's online
        for (Friend onlineFriend : onlineFriends) {
            for(User user : ClientHandler.connectedUsers){
                if(onlineFriend.getUsername().equals(user.getUsersUsername())){
                    user.getOut().reset();
                    user.getOut().writeObject(tempFriend);
                }
            }
        }
    }
    
    public static void SendFriendUpdateGoingOffline(ArrayList<Friend> onlineFriends, Friend tempFriend) throws IOException{
        tempFriend.setIsUpdate(true);
        // send your friend ID to anyone that's online
        tempFriend.setOnlineStatus(false);
        for (Friend onlineFriend : onlineFriends) {
            for(User user : ClientHandler.connectedUsers){
                if(onlineFriend.getUsername().equals(user.getUsersUsername())){
                    user.getOut().reset();
                    user.getOut().writeObject(tempFriend);
                }
            }
        }
    }
    
    
}
