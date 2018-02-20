
import java.io.IOException;

public class ImageHandler implements Runnable{
    
    private ImagePacket ip;
    private boolean messageSent = false;
    
    public ImageHandler(ImagePacket ip){
        this.ip = ip;
    }
    
    @Override
    public void run() {
        
        for(int i = 0; i < ClientHandler.connectedUsers.size(); i++){
            if(ClientHandler.connectedUsers.get(i).getUsersUsername().equals(ip.getSendingTo())){
                try {
                    // im not proud of this
                    ClientHandler.connectedUsers.get(i).getOut().writeObject(ip);
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