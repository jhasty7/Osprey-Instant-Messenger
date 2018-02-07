/**
 *
 * @author Joshua
 */
public class Message extends Packet{
    private String message;
    private String comingFrom;
    private String sendingTo;
    
    public Message(String comingFrom, String sendingTo, String message){
        this.message = message;
        this.comingFrom = comingFrom;
        this.sendingTo = sendingTo;
    }
    
    public String getMessage(){
        return message;
    }
    
    public String getComingFrom(){
        return comingFrom;
    }
    
    public String getSendingTo(){
        return sendingTo;
    }
}
