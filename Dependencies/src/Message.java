
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
/**
 *
 * @author Joshua
 */
public class Message extends Packet{
    private String message;
    private String comingFrom;
    private String sendingTo;
    private RGBValues color;
    private FontValues font;
    
    public Message(){
        this.message = null;
        this.comingFrom = null;
        this.sendingTo = null;
        this.font = null;
        this.color = null;
    }
    
    public Message(String comingFrom, String sendingTo, String message, Font myFont, Color myColor){
        this.message = message;
        this.comingFrom = comingFrom;
        this.sendingTo = sendingTo;
        this.font = new FontValues(myFont.getFamily(),myFont.getSize(),myFont.getStyle());
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
    
        public Font getFont(){
        Font newFont = null;
        switch(font.getStyle()){
            case "Italic":
                newFont = Font.font(font.getFamily(),FontPosture.ITALIC,font.getSize());
                break;
            case "Regular":
                newFont = Font.font(font.getFamily(),font.getSize());
                break;
            default:
        }
        
        return newFont;
    }
    
    public Color getColor(){
        return new Color(color.getRed(),color.getGreen(),color.getBlue(), 1);
    }
    
    public void setColor(Color thisColor){
        this.color = new RGBValues(thisColor.getRed(), thisColor.getGreen(), thisColor.getBlue());
    }
    
    public void setFont(Font thisFont){
        this.font = new FontValues(thisFont.getFamily(),thisFont.getSize(),thisFont.getStyle());
    }
}
