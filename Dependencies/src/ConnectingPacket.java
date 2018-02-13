
public class ConnectingPacket extends Packet{
    
    private String username;
    
    public ConnectingPacket(){
        this.username = null;
    }
    
    public ConnectingPacket(String username){
        this.username = username;
    }
    
    public String getUsername(){
        return username;
    }
    
}
