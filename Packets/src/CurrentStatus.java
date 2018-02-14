
public class CurrentStatus extends Packet{
    private eCURRENT_STATUS currentStatus;
    
    public CurrentStatus(){
        currentStatus = null;
    }
    
    public CurrentStatus(eCURRENT_STATUS currentStatus){
        this.currentStatus = currentStatus;
    }
    
    public eCURRENT_STATUS getCurrentStatus(){
        return currentStatus;
    }
}
