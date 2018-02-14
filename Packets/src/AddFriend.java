/**
 *
 * @author Joshua
 */
public class AddFriend extends Packet{
    
    private String friend;
    
    public AddFriend(){
        this.friend = null;
    }
    
    public AddFriend(String friend){
        this.friend = friend;
    }
    
    public String getFriend(){
        return friend;
    }
}
