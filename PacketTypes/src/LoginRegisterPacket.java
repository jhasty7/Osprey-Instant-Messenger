
/**
 *
 * @author Joshua
 */
public class LoginRegisterPacket extends Packet{
    private boolean isLogin = false;
    private boolean isRegister = false;
    private String username;
    private String password;
    
    public LoginRegisterPacket(String username, String password){
        this.username = username;
        this.password = password;
    }
    
    public boolean isLogin(){
        return isLogin;
    }
    
    public boolean isRegister(){
        return isRegister;
    }
    
    public void setLoginTrue(){
        isLogin = true;
        isRegister = false;
    }
    
    public void setRegisterTrue(){
        isRegister = true;
        isLogin = false;
    }
    
    public String getUsername(){
        return username;
    }
    
    public String getPassword(){
        return password;
    }
}
