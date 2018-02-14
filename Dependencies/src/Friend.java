
import java.io.Serializable;

public class Friend implements Serializable{

    private String username;
    private boolean onlineStatus;
    private eCURRENT_STATUS currentStatus;
    private String textStatus;
    private boolean isUpdate = false;
    private boolean isAdd = false;
    private boolean isRemove = false;
    private boolean isBlock = false;
    
    public Friend(){
        username = null;
        onlineStatus = false;
        currentStatus = null;
        textStatus = null;
    }
    
    public Friend(String userName, boolean onlineStatus,eCURRENT_STATUS currentStatus,String textStatus){
        this.username = userName;
        this.onlineStatus = onlineStatus;
        this.currentStatus = currentStatus;
        this.textStatus = textStatus;
    }
    
    public String getUsername(){
        return username;
    }
    
    public boolean getOnlineStatus(){
        return onlineStatus;
    }
    
    public eCURRENT_STATUS getCurrentStatus(){
        return currentStatus;
    }
    
    public eONLINE_STATUS getOnlineStatusAsEnum(){
        if(onlineStatus){
            return eONLINE_STATUS.online;
        }else{
            return eONLINE_STATUS.offline;
        }
    }
    
    public String setTextStatus(){
        return textStatus;
    }
    
    public void setCurrentStatus(eCURRENT_STATUS currentStatus){
        this.currentStatus = currentStatus;
    }
    
    public void setTextStatus(String textStatus){
        this.textStatus = textStatus;
    }
    
    public void setOnlineStatus(boolean onlineStatus){
        this.onlineStatus = onlineStatus;
    }
    
    public void setUsername(String username){
        this.username = username;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setIsUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean isAdd() {
        return isAdd;
    }

    public void setIsAdd(boolean isAdd) {
        this.isAdd = isAdd;
    }

    public boolean isRemove() {
        return isRemove;
    }

    public void setIsRemove(boolean isRemove) {
        this.isRemove = isRemove;
    }

    public boolean isBlock() {
        return isBlock;
    }

    public void setIsBlock(boolean isBlock) {
        this.isBlock = isBlock;
    }
    
    
    
    @Override
    public String toString(){
        return username + "," + onlineStatus + "," + currentStatus + "," + textStatus;
    }
}
