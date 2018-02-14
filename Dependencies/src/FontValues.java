
import java.io.Serializable;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class FontValues implements Serializable{
    private String fontStyle;
    private String fontWeight;
    private String fontPosture;
    private int fontSize;
    
    public FontValues(String fontStyle, String fontWeight, String fontPosture, int fontSize){
        this.fontStyle = fontStyle;
        this.fontWeight = fontWeight;
        this.fontPosture = fontPosture;
        this.fontSize = fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontWeight() {
        return fontWeight;
    }

    public String getFontPosture() {
        return fontPosture;
    }

    public int getFontSize() {
        return fontSize;
    }
    
    public FontWeight getFontWeightEnum(){
        
        switch(fontWeight.toUpperCase()){
            case "NORMAL":
                return FontWeight.NORMAL;
            case "BLACK":
                return FontWeight.BLACK;
            case "BOLD":
                return FontWeight.BOLD;
            case "EXTRA BOLD":
                return FontWeight.EXTRA_BOLD;
            case "EXTRA LIGHT":
                return FontWeight.EXTRA_LIGHT;
            case "LIGHT":
                return FontWeight.LIGHT;
            case "MEDIUM":
                return FontWeight.MEDIUM;
            case "SEMI BOLD":
                return FontWeight.SEMI_BOLD;
            case "THIN":
                return FontWeight.THIN;
            default:
                return FontWeight.NORMAL;
        }
        
    }
    
    public FontPosture getFontPostureEnum(){
        
        switch(fontPosture.toUpperCase()){
            case "REGULAR":
                return FontPosture.REGULAR;
            case "ITALIC":
                return FontPosture.ITALIC;
            default:
                return FontPosture.REGULAR;
        }
        
    }
    
}
