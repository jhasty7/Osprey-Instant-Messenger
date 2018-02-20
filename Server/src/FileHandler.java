
import java.io.IOException;

public class FileHandler implements Runnable{
    
    private SendFilePacket sfp;
    private boolean messageSent = false;
    
    public FileHandler(SendFilePacket sfp){
        this.sfp = sfp;
    }
    
    @Override
    public void run() {
        
        for(int i = 0; i < ClientHandler.connectedUsers.size(); i++){
            if(ClientHandler.connectedUsers.get(i).getUsersUsername().equals(sfp.getSendingTo())){
                try {
                    // im not proud of this
                    ClientHandler.connectedUsers.get(i).getOut().writeObject(sfp);
                    messageSent = true;
                }
                catch (IOException ex) {
                    
                }
            }
        }
        if(messageSent){
            // message successfully sent
        }else{
            // message was unsuccesful
        }
    }
}