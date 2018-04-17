public class PendingFriendRequest extends Packet{
    
    private String friendname;
    private boolean isAccepting = false;
    private boolean isRequesting = false;
    private boolean isBlock = false;
    
    public PendingFriendRequest(String friendname){
        this.friendname = friendname;
    }

    public String getFriendname() {
        return friendname;
    }

    public boolean isAccepting() {
        return isAccepting;
    }
    
    public void acceptRequest(){
        isAccepting = true;
    }
    
    public boolean isRequesting(){
        return isRequesting;
    }
    
    public void setAsRequest(){
        isRequesting = true;
    }
    
    public void blockRequest(){
        isBlock = true;
    }
    
    public boolean isBlock(){
        return isBlock;
    }
    
}
