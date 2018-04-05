
public class BlockFriend extends Packet{
    private String friend;
    public BlockFriend(){
        this.friend = null;
    }
    
    public BlockFriend(String friend){
        this.friend = friend;
    }
    
    public String getFriend(){
        return friend;
    }
}
