
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SendFilePacket extends Packet{
    
    private byte[] fileBytes; 
    private String comingFrom;
    private String sendingTo;
    
    public SendFilePacket(String comingFrom, String sendingTo, File file){
        this.comingFrom = comingFrom;
        this.sendingTo = sendingTo;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream out;
            out = new ObjectOutputStream(bos);
            out.writeObject(file);
            fileBytes = bos.toByteArray();
            bos.close();
            out.close();
        } catch(IOException ex){
            System.out.println(ex.toString());
        }
    }
    
    public File getFile(){
        File file;
        try{
            ByteArrayInputStream bis = new ByteArrayInputStream(fileBytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            file = (File)in.readObject();
            bis.close();
            in.close();
            return file;
        }catch(IOException | ClassNotFoundException ex){
            System.out.println(ex.toString());
        }
        return null;
    }

    public String getComingFrom() {
        return comingFrom;
    }

    public String getSendingTo() {
        return sendingTo;
    }
    
    
}
