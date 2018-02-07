/**
 *
 * @author Joshua
 */
public class AddFriend {
    private String sender;
    private String friend;
    
    public AddFriend(String sender, String friend){
        this.sender = sender;
        this.friend = friend;
    }
    
    public String getSender(){
        return sender;
    }
    
    public String getFriend(){
        return friend;
    }
}
