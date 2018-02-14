
public class TextStatus extends Packet{
    
    private String textStatus;
    
    public TextStatus(){
        textStatus = null;
    }
    
    public TextStatus(String textStatus){
        this.textStatus = textStatus;
    }
    
    public String getTextStatus(){
        return textStatus;
    }
}
