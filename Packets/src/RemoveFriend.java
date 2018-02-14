public class RemoveFriend extends Packet{
    
    private String friend;
    
    public RemoveFriend(){
        this.friend = null;
    }
    
    public RemoveFriend(String friend){
        this.friend = friend;
    }
    
    public String getFriend(){
        return friend;
    }
    
}
