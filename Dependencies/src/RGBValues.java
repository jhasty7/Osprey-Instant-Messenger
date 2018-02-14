
import java.io.Serializable;

public class RGBValues implements Serializable{
    private double red;
    private double green;
    private double blue;
    
    public RGBValues (double red, double green, double blue){
        this.red = red;
        this.green = green;
        this.blue = blue;
    }
    
    public double getR(){
        return red;
    }
    
    public double getG(){
        return green;
    }
    
    public double getB(){
        return blue;
    }
    
}
