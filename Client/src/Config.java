
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;


public class Config implements Serializable{
    private transient static final String CONFIG_FILE_PATH = System.getProperty("user.dir") + "/data/config.cfg";
    public static Config cfg = new Config();
    // General
    private boolean rememberUsernamePassword = false;
    private boolean autoLogin = false;
    private boolean runOnStartup = false;
    
    // Chat
    private RGBValues color = new RGBValues(0,0,0);
    private String fontStyle = "Times New Roman";
    private String fontWeight = "Regular";
    private String fontPosture = "Regular";
    private int fontSize = 12;
    private boolean ignoreFriendTextStyle = false;
    private boolean autoSaveLogs = true;
    
    // Network
    private String hostname = "127.0.0.1";
    private int portNumber = 45566;
    
    // extra infomation no one needs to know about
    private String username = null;
    private String password = null;
    
    public Config(){
        readConfigFile();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isRememberUsernamePassword() {
        return rememberUsernamePassword;
    }

    public void setRememberUsernamePassword(boolean rememberUsernamePassword) {
        this.rememberUsernamePassword = rememberUsernamePassword;
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
    }

    public boolean isRunOnStartup() {
        return runOnStartup;
    }

    public void setRunOnStartup(boolean runOnStartup) {
        this.runOnStartup = runOnStartup;
    }

    public Color getColor() {
        return new Color(color.getR(),color.getG(),color.getB(),1);
    }

    public void setColor(Color inColor) {
        this.color = new RGBValues(inColor.getRed(),inColor.getGreen(),inColor.getBlue());
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public void setFontStyle(String fontStyle) {
        this.fontStyle = fontStyle;
    }

    public String getFontPosture() {
        return fontPosture;
    }

    public void setFontPosture(String fontPosture) {
        this.fontPosture = fontPosture;
    }

    public boolean isIgnoreFriendTextStyle() {
        return ignoreFriendTextStyle;
    }

    public void setIgnoreFriendTextStyle(boolean ignoreFriendTextStyle) {
        this.ignoreFriendTextStyle = ignoreFriendTextStyle;
    }

    public boolean isAutoSaveLogs() {
        return autoSaveLogs;
    }

    public void setAutoSaveLogs(boolean autoSaveLogs) {
        this.autoSaveLogs = autoSaveLogs;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    public String getFontWeight() {
        return fontWeight;
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

    public void setFontWeight(String fontWeight) {
        this.fontWeight = fontWeight;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
    
    public Font getFontAsFont(){
        return Font.font(fontStyle,getFontWeightEnum(),getFontPostureEnum(),fontSize);
    }
    
    public FontValues getFontAsFontValue(){
        return new FontValues(fontStyle,fontWeight,fontPosture,fontSize);
    }
    
    private void readConfigFile() {
        try {
            FileInputStream fin = new FileInputStream(CONFIG_FILE_PATH);
            ObjectInputStream oin = new ObjectInputStream(fin);
            cfg = (Config) oin.readObject();
            fin.close();
            oin.close();
        }
        catch (FileNotFoundException | ClassNotFoundException ex) {

            try {
                File cfgfile = new File(CONFIG_FILE_PATH);
                cfgfile.getParentFile().mkdirs();
                cfgfile.createNewFile();
            }
            catch (IOException ex1) {
                DeveloperWindow.displayMessage("failed to create new config file");
                DeveloperWindow.displayMessage(ex1.toString());
            }

        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to open config file");
            DeveloperWindow.displayMessage(ex.toString());
        }
    }

    public void writeConfigFile() {
        try {
            FileOutputStream fout = new FileOutputStream(CONFIG_FILE_PATH);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(cfg);
            fout.close();
            oout.close();
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to write config file");
            DeveloperWindow.displayMessage(ex.toString());
        }

    }
    
    
}
