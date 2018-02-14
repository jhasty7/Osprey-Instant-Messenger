
public class ServerConfirmation extends Packet{
    
    private boolean isSuccessful;
    private String context;
    
    public ServerConfirmation(){
        isSuccessful = false;
        context = null;
    }
    
    public ServerConfirmation(boolean isSuccessful, String context){
        this.isSuccessful = isSuccessful;
        this.context = context;
    }

    public String getContext() {
        return context;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
    
}
