
public class ServerListener implements Runnable{
    
    public ServerListener(){
        
    }
    
    // incoming messages here on the new thread
    @Override
    public void run() {
    }
    
    // basic client functions
    public boolean disconnect(){
        return false;
    }
    
    public boolean connect(){
        return false;
    }
    
    // outgoing message *** spawn a new thread to handle these
    public void blockFriend(String friendName){
        
    }
    
    public void addFriend(String friendName){
        
    }
    
    public void updateStatusText(String statusText){
        
    }
    
    public void updateUserStatus(USER_STATUS currentStatus){
        
    }
    
    public void removeFriend(String friendName){
        
    }
    
}
