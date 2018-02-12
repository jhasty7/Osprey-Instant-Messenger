

public class Friend extends Packet {

    private String userName;
    private boolean online;
    private USER_STATUS userStatus;
    private String statusText;
    
    public Friend(){
        userName = null;
        online = false;
    }
    
    public Friend(String userName, boolean online,USER_STATUS userStatus,String statusText){
        this.userName = userName;
        this.online = online;
        this.userStatus = userStatus;
        this.statusText = statusText;
    }
    
    public String getUserName(){
        return userName;
    }
    
    public boolean getOnlineStatus(){
        return online;
    }
    
    public USER_STATUS getUserStatus(){
        return userStatus;
    }
    
    public String getStatusText(){
        return statusText;
    }
    
    public void setUserStats(USER_STATUS userStatus){
        this.userStatus = userStatus;
    }
    
    public void setStatusString(String statusText){
        this.statusText = statusText;
    }
    
    public void setOnline(boolean onlineStatus){
        this.online = onlineStatus;
    }
    
    @Override
    public String toString(){
        return userName + "," + online + "," + userStatus + "," + statusText;
    }
}
