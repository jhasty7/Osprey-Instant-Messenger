
import javafx.application.Application;
import javafx.event.EventType;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Settings extends Application{
    private Pane masterPane;
    private CheckBox autoLogin;
    private Button saveAndClose;
    private Button cancel;
    
    @Override
    public void start(Stage primaryStage){
        masterPane = new Pane();
        
        autoLogin = new CheckBox("Auto Login");
        
        
        saveAndClose = new Button("Save and Close");
        cancel = new Button("Cancel");
        
        masterPane.getChildren().add(autoLogin);
        
        Scene scene = new Scene(masterPane, 395, 470, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("UNF Instant Messenger");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
