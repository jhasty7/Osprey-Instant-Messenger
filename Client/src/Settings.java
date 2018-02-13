
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Settings extends Application {
    
    private static String configFilePath = System.getProperty("user.dir") + "/data/config.cfg";
    private Config config;
    
    //javafx ui variables/objects
    private Stage primaryStage;
    private BorderPane masterPane;
    private Label autoLoginLabel;
    private CheckBox autoLoginCheckBox;
    private Label runOnStartupLabel;
    private CheckBox runOnStartupCheckBox;
    private Button saveAndCloseButton;
    private Button cancelButton;
    private HBox bottomHBox;
    private VBox leftVBox;
    private VBox centerPaneGeneral;
    private VBox centerPaneChat;
    private VBox centerPaneNetwork;
    private ListView tabs;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        config = new Config();
        
        masterPane = new BorderPane();
        masterPane.setPadding(new Insets(5,5,5,5));
        centerPaneGeneral = new VBox();
        centerPaneGeneral.setPadding(new Insets(0,0,0,5));
        centerPaneGeneral.setSpacing(5);
        bottomHBox = new HBox();
        bottomHBox.setSpacing(3);
        bottomHBox.setPadding(new Insets(2,2,2,2));
        leftVBox = new VBox();
        centerPaneChat = new VBox();
        centerPaneNetwork = new VBox();
        
        //left side menu changer
        tabs = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList (
        "General","Chat","Network");
        tabs.setItems(items);
        tabs.getSelectionModel().select(0);
        tabs.setOnMouseClicked(new SectionChangingListener());
        leftVBox.getChildren().add(tabs);
        leftVBox.setPrefWidth(100);
        tabs.prefHeightProperty().bind(leftVBox.heightProperty());
        
        // General stuff
        autoLoginCheckBox = new CheckBox("Auto Login");
        runOnStartupCheckBox = new CheckBox("Run on startup");
        autoLoginLabel = new Label("Automatically attempt to login when the program starts");
        autoLoginLabel.setWrapText(true);
        runOnStartupLabel = new Label("Program will start when your OS starts");
        runOnStartupLabel.setWrapText(true);
        centerPaneGeneral.getChildren().add(autoLoginCheckBox);
        centerPaneGeneral.getChildren().add(autoLoginLabel);
        centerPaneGeneral.getChildren().add(runOnStartupCheckBox);
        centerPaneGeneral.getChildren().add(runOnStartupLabel);
        // chat stuff
        
        // networking stuff
        
        //rest
        saveAndCloseButton = new Button("Save and Close");
        saveAndCloseButton.setOnAction(new SaveAndCloseButtonActionListener());
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new CancelButtonActionListener());
        
        bottomHBox.getChildren().add(saveAndCloseButton);
        bottomHBox.getChildren().add(cancelButton);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        
        
        
        masterPane.setLeft(leftVBox);
        masterPane.setCenter(centerPaneGeneral);
        masterPane.setBottom(bottomHBox);
        
        Scene scene = new Scene(masterPane, 358, 411, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("Settings");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new ClosingPrimaryStageWindowEventHandler());
        readConfigFile();
        //change things before the window is shown
        primaryStage.show();
    }
    
    
        
    private void applyConfiguration(){
        if(config.getAutoLogin()){
            autoLoginCheckBox.setSelected(true);
        }
        if(config.getRunOnStartup()){
            runOnStartupCheckBox.setSelected(true);
        }
    }
    
    public void setConfiguation(){
        config.setAutoLogin(autoLoginCheckBox.isSelected());
        config.setRunOnStartup(runOnStartupCheckBox.isSelected());
        writeConfigFile();
    }

    private void readConfigFile() {
        try {
            FileInputStream fin = new FileInputStream(configFilePath);
            ObjectInputStream oin = new ObjectInputStream(fin);
            config = (Config) oin.readObject();
            fin.close();
            oin.close();
            // apply the configuration to the form
            applyConfiguration();
        }
        catch (FileNotFoundException | ClassNotFoundException ex) {

            try {
                File cfgfile = new File(configFilePath);
                cfgfile.getParentFile().mkdirs();
                cfgfile.createNewFile();
            }
            catch (IOException ex1) {
                DeveloperWindow.displayMessage("failed to create new config file");
            }

        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to open config file");
        }
    }

    public void writeConfigFile() {
        try {
            FileOutputStream fout = new FileOutputStream(configFilePath);
            ObjectOutputStream oout = new ObjectOutputStream(fout);
            oout.writeObject(config);
            fout.close();
            oout.close();
        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to write config file");
            DeveloperWindow.displayMessage(ex.toString());
        }

    }
    
    private class ClosingPrimaryStageWindowEventHandler implements EventHandler<WindowEvent>{

        @Override
        public void handle(WindowEvent event) {
            // ignore the setting changes
            primaryStage.close();
        }
    
    }
    
    private class CancelButtonActionListener implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            // ignore the setting changes
            primaryStage.close();
        }
        
    }
    
    private class SaveAndCloseButtonActionListener implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            // overwrite the config file
            setConfiguation();
            primaryStage.close();
        }
    }
    
    private class SectionChangingListener implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                String temp = tabs.getSelectionModel().getSelectedItem().toString();
                if(temp.equals("General")){
                    masterPane.setCenter(centerPaneGeneral);
                }else if(temp.equals("Chat")){
                    masterPane.setCenter(centerPaneChat);
                }else if(temp.equals("Network")){
                    masterPane.setCenter(centerPaneNetwork);
                }
            }
            else if (event.getButton() == MouseButton.SECONDARY) {

            }
        }

    }

}
