
public class Connecting extends Packet{
    
    private String username;
    
    public Connecting(){
        this.username = null;
    }
    
    public Connecting(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return username;
    }
    
}
