
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
/**
 *
 * @author Joshua
 */
public class MessagePacket extends Packet{
    private String message;
    private String comingFrom;
    private String sendingTo;
    private FontValues myFont;
    private RGBValues color;
    
    public MessagePacket(){
        this.message = null;
        this.comingFrom = null;
        this.sendingTo = null;
        this.myFont = null;
        this.color = null;
    }
    
    public MessagePacket(String comingFrom, String sendingTo, String message, FontValues myFont, Color myColor){
        this.message = message;
        this.comingFrom = comingFrom;
        this.sendingTo = sendingTo;
        this.myFont = myFont;
        this.color = new RGBValues(myColor.getRed(),myColor.getGreen(),myColor.getBlue());
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
    
    public FontValues getFontAsFontValues(){
        return myFont;
    }
    
    public Font getFontAsFont(){
        return Font.font(myFont.getFontStyle(),myFont.getFontWeightEnum(),myFont.getFontPostureEnum(),myFont.getFontSize());
    }
    
    public Color getColor(){
        return new Color(color.getR(),color.getG(),color.getB(), 1);
    }
    
}
