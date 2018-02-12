
import java.io.Serializable;


public class FontValues implements Serializable{
    private String family;
    private Double size;
    private String style;
    
    public FontValues(String family, Double size, String style){
        this.family = family;
        this.size = size;
        this.style = style;
    }
    
    public String getFamily(){
        return family;
    }
    
    public Double getSize(){
        return size;
    }
    
    public String getStyle(){
        return style;
    }
}
