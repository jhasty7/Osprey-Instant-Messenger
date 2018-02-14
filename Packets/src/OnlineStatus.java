
public class OnlineStatus extends Packet{
    private boolean isOnline;
    private eONLINE_STATUS onlineStatus;
    
    public OnlineStatus(){
        isOnline = false;
        onlineStatus = null;
    }
    
    public OnlineStatus(boolean isOnline){
        
        this.isOnline = isOnline;
        
        if(isOnline){
            onlineStatus = eONLINE_STATUS.online;
        }else{
            onlineStatus = eONLINE_STATUS.offline;
        }
        
    }
    
    public OnlineStatus(eONLINE_STATUS onlineStatus){
        
        this.onlineStatus = onlineStatus;
        
        if(onlineStatus == eONLINE_STATUS.online){
            isOnline = true;
        }else{
            isOnline = false;
        }
    }
    
    public eONLINE_STATUS getOnlineStatusEnum(){
        return onlineStatus;
    }
    
    public boolean getOnlineStatusBoolean(){
        return isOnline;
    }
}
