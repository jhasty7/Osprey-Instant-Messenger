
import java.io.IOException;

public class MessageHandler implements Runnable{
    
    private MessagePacket message;
    private boolean messageSent = false;
    
    public MessageHandler(MessagePacket message){
        this.message = message;
    }
    
    @Override
    public void run() {
        
        for(int i = 0; i < ClientHandler.connectedUsers.size(); i++){
            if(ClientHandler.connectedUsers.get(i).getUsersUsername().equals(message.getSendingTo())){
                try {
                    // im not proud of this
                    ClientHandler.connectedUsers.get(i).getOut().writeObject(message);
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