
import java.io.Serializable;

public class RGBValues implements Serializable{
    double red;
    double green;
    double blue;
    
    public RGBValues (double red, double green, double blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public double getRed(){
        return red;
    }
    
    public double getGreen(){
        return green;
    }
    
    public double getBlue(){
        return blue;
    }
    
}
