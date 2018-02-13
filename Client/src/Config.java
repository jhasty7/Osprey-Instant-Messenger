
import java.io.Serializable;


public class Config implements Serializable{
    private boolean autoLogin;
    private boolean runOnStartup;
    
    public Config(){
        
    }
    
    public void setAutoLogin(boolean b){
        autoLogin = b;
    }
    
    public void setRunOnStartup(boolean b){
        runOnStartup = b;
    }
    
    public boolean getAutoLogin(){
        return autoLogin;
    }
    
    public boolean getRunOnStartup(){
        return runOnStartup;
    }
}
