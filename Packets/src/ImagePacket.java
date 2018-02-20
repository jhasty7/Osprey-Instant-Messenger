
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.imageio.ImageIO;


public class ImagePacket extends Packet{
    
    
    private byte[] serializedBytes;
    private String sendingTo;
    private String comingFrom;
    
    public ImagePacket(String comingFrom, String sendingTo, BufferedImage image){
        this.comingFrom = comingFrom;
        this.sendingTo = sendingTo;
        
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            ImageIO.write(image, "png", out);
            serializedBytes = bos.toByteArray();
            bos.close();
            out.close();
        } catch(IOException ex){
            System.out.println(ex.toString());
        }
        
    }
    
    public BufferedImage getImage(){
        BufferedImage bi;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(serializedBytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            bi = ImageIO.read(in);
            bis.close();
            in.close();
            return bi;
        }catch(IOException ex){
            System.out.println(ex.toString());
        }
        return null;
    }

    public String getSendingTo() {
        return sendingTo;
    }

    public String getComingFrom() {
        return comingFrom;
    }
    
    
}
