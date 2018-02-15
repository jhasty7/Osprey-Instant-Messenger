
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Settings extends Application {
    
    // javafx ui variables/objects
    private Stage primaryStage;
    private BorderPane masterPane;
    private ListView tabs;
    private VBox leftVBox;
    
    // General tab
    private VBox centerPaneGeneral;
    private CheckBox rememberUsernamePasswordCheckBox;
    private Label rememberUsernamePasswordLabel;
    private CheckBox autoLoginCheckBox;
    private Label autoLoginLabel;
    private CheckBox runOnStartupCheckBox;
    private Label runOnStartupLabel;
    private Label dangerZoneLabel;
    private Button deleteAccountButton;
    private Label deleteAccountLabel;
    
    // Chat tab
    private GridPane centerPaneChat;
    private Label colorLabel;
    private ColorPicker colorChangePicker;
    private Label fontStyleLabel;
    private ComboBox fontStyleComboBox;
    private ObservableList fontStyleObservableList;
    private Label fontWeightLabel;
    private ComboBox fontWeightComboBox;
    private Label fontSizeLabel;
    private ComboBox fontSizeComboBox;
    private Label fontPostureLabel;
    private ComboBox fontPostureComboBox;
    private CheckBox ignoreFriendTextStyleCheckBox;
    private Label ignoreFriendTextStyleLabel;
    private CheckBox autoSaveLogCheckBox;
    private Label autoSaveLogLabel;
    private Button resetChatLogButton;
    private Label resetChatLogLabel;
    
    // Network tab
    private VBox centerPaneNetwork;
    private Image warningImage;
    private ImageView warningImageView;
    private Label warningLabel;
    private Button bypassWarningButton;
    private Label hostNameLabel;
    private TextField hostNameTextField;
    private Label portLabel;
    private TextField portTextField;
    private Label willRequireRestartLabel;
            
    // bottom buttons
    private HBox bottomHBox;
    private Button saveAndCloseButton;
    private Button cancelButton;
    
    // variables
    private boolean emptyHostName = false;
    private boolean emptyPortNumber = false;
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // main pane
        masterPane = new BorderPane();
        masterPane.setPadding(new Insets(5,5,5,5));  
        
        //left side menu changer
        leftVBox = new VBox();
        tabs = new ListView<>();
        ObservableList<String> items = FXCollections.observableArrayList (
        "General","Chat","Network");
        tabs.setItems(items);
        tabs.getSelectionModel().select(0);
        tabs.setOnMouseClicked(new SectionChangingListener());
        leftVBox.getChildren().add(tabs);
        leftVBox.setPrefWidth(100);
        tabs.prefHeightProperty().bind(leftVBox.heightProperty());
        
        // General tab
        centerPaneGeneral = new VBox(5);
        centerPaneGeneral.setPadding(new Insets(0,0,0,5));
        rememberUsernamePasswordCheckBox = new CheckBox("Remember Username and Password");
        rememberUsernamePasswordCheckBox.setOnAction(new RememberUsernameAndPasswordChangeListener());
        rememberUsernamePasswordLabel = new Label("Your Username and Password will appear auto-filled on startup.");
        rememberUsernamePasswordLabel.setWrapText(true);
        autoLoginCheckBox = new CheckBox("Auto Login");
        autoLoginCheckBox.setDisable(true);
        autoLoginLabel = new Label("Automatically attempt to login when the program starts");
        autoLoginLabel.setWrapText(true);
        runOnStartupCheckBox = new CheckBox("Run on startup");
        runOnStartupLabel = new Label("Program will start when your OS starts");
        runOnStartupLabel.setWrapText(true);
        
        dangerZoneLabel = new Label("Danger Zone");
        deleteAccountButton = new Button("Delete Account");
        deleteAccountLabel = new Label("This can not be undone. Your deleted username can not be re-used.");
        deleteAccountLabel.setWrapText(true);
        centerPaneGeneral.getChildren().addAll(rememberUsernamePasswordCheckBox, rememberUsernamePasswordLabel,
                autoLoginCheckBox, autoLoginLabel ,runOnStartupCheckBox,
                runOnStartupLabel,dangerZoneLabel,deleteAccountButton,deleteAccountLabel);
        
        // chat tab
        centerPaneChat = new GridPane();
        centerPaneChat.setPadding(new Insets(0,0,0,5));
        centerPaneChat.setVgap(5);
        colorLabel = new Label("Color");
        colorChangePicker = new ColorPicker();
        fontStyleLabel = new Label("Font Style");
        fontStyleObservableList = FXCollections.observableArrayList(Font.getFamilies());
        fontStyleComboBox = new ComboBox(fontStyleObservableList);
        fontStyleComboBox.setPrefWidth(150);
        fontWeightLabel = new Label("Font Weight");
        fontWeightComboBox = new ComboBox(FXCollections.observableArrayList("Normal","Black","Light","Extra Light","Medium","Bold","Extra Bold","Semi Bold","Thin"));
        fontWeightComboBox.setPrefWidth(150);
        fontPostureLabel = new Label("Font Posture");
        fontPostureComboBox = new ComboBox(FXCollections.observableArrayList("Regular","Italic"));
        fontPostureComboBox.setPrefWidth(150);
        fontSizeLabel = new Label("Font Size");
        fontSizeComboBox = new ComboBox(FXCollections.observableArrayList("10","12","14","16"));
        fontSizeComboBox.setPrefWidth(150);
        ignoreFriendTextStyleCheckBox = new CheckBox("Ignore Friend Text Style");
        ignoreFriendTextStyleLabel = new Label("Ignores your friends text color, style, and posture. It will use your choice instead");
        ignoreFriendTextStyleLabel.setWrapText(true);
        autoSaveLogCheckBox = new CheckBox("Auto-Save logs");
        autoSaveLogLabel = new Label("Automatically saves friends conversation and displays them in the message window.");
        autoSaveLogLabel.setWrapText(true);
        resetChatLogButton = new Button("Reset Chat Logs");
        resetChatLogLabel = new Label("This cannot be undone. This delete all chat history. This will also delete everything in the chat folder.");
        resetChatLogLabel.setWrapText(true);
        centerPaneChat.addRow(0, colorLabel,colorChangePicker);
        centerPaneChat.addRow(1,fontStyleLabel, fontStyleComboBox);
        centerPaneChat.addRow(2,fontWeightLabel, fontWeightComboBox);
        centerPaneChat.addRow(3,fontPostureLabel, fontPostureComboBox);
        centerPaneChat.addRow(4,fontSizeLabel, fontSizeComboBox);
        centerPaneChat.addRow(5,ignoreFriendTextStyleCheckBox);
        centerPaneChat.setColumnSpan(ignoreFriendTextStyleCheckBox,3);
        centerPaneChat.addRow(6,ignoreFriendTextStyleLabel);
        centerPaneChat.setColumnSpan(ignoreFriendTextStyleLabel,3);
        centerPaneChat.addRow(7,autoSaveLogCheckBox);
        centerPaneChat.setColumnSpan(autoSaveLogCheckBox,3);
        centerPaneChat.addRow(8,autoSaveLogLabel);
        centerPaneChat.setColumnSpan(autoSaveLogLabel,3);
        centerPaneChat.addRow(9,resetChatLogButton);
        centerPaneChat.setColumnSpan(resetChatLogButton,3);
        centerPaneChat.addRow(10,resetChatLogLabel);
        centerPaneChat.setColumnSpan(resetChatLogLabel,3);
        
        // networking tab
        centerPaneNetwork = new VBox(5);
        centerPaneNetwork.setPadding(new Insets(0,0,0,5));
        warningImage = new Image("warning.png");
        warningImageView = new ImageView(warningImage);
        warningLabel = new Label("Don't mess with this section unless you know what you're doing.");
        warningLabel.setWrapText(true);
        bypassWarningButton = new Button("I'm Smart");
        bypassWarningButton.setOnAction(new BypassNetworkWarningActionListener());
        hostNameLabel = new Label("Hostname or IP");
        hostNameTextField = new TextField(ServerListener.HOST_NAME);
        hostNameTextField.setDisable(true);
        portLabel = new Label("Port Number");
        portTextField = new TextField(String.valueOf(ServerListener.PORT_NUMBER));
        portTextField.setDisable(true);
        willRequireRestartLabel = new Label(" * Changes here will require restart.");
        willRequireRestartLabel.setWrapText(true);
        centerPaneNetwork.getChildren().addAll(warningImageView, warningLabel,
                bypassWarningButton, hostNameLabel, hostNameTextField,
                portLabel, portTextField);
        
        // bottom menu
        bottomHBox = new HBox();
        bottomHBox.setSpacing(3);
        bottomHBox.setPadding(new Insets(2,2,2,2));
        saveAndCloseButton = new Button("Save and Close");
        saveAndCloseButton.setOnAction(new SaveAndCloseButtonActionListener());
        cancelButton = new Button("Cancel");
        cancelButton.setOnAction(new CancelButtonActionListener());
        bottomHBox.getChildren().addAll(saveAndCloseButton,cancelButton);
        bottomHBox.setAlignment(Pos.CENTER_RIGHT);
        
        // add starting panes to the main pane
        masterPane.setLeft(leftVBox);
        masterPane.setCenter(centerPaneGeneral);
        masterPane.setBottom(bottomHBox);
        
        // javaui stage stuff
        Scene scene = new Scene(masterPane, 358, 411, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("Settings");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(new ClosingPrimaryStageWindowEventHandler());
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        applyConfiguration();
        // show window
        primaryStage.show();
    }
    
    
        
    private void applyConfiguration(){
        // General
        rememberUsernamePasswordCheckBox.setSelected(Config.cfg.isRememberUsernamePassword());
        if(rememberUsernamePasswordCheckBox.isSelected()){
            autoLoginCheckBox.setDisable(false);
        }
        autoLoginCheckBox.setSelected(Config.cfg.isAutoLogin());
        runOnStartupCheckBox.setSelected(Config.cfg.isRunOnStartup());
        
        // Chat
        colorChangePicker.setValue(Config.cfg.getColor());
        fontStyleComboBox.getSelectionModel().select(Config.cfg.getFontStyle());
        fontWeightComboBox.getSelectionModel().select(Config.cfg.getFontWeight());
        fontPostureComboBox.getSelectionModel().select(Config.cfg.getFontPosture());
        fontSizeComboBox.getSelectionModel().select(String.valueOf(Config.cfg.getFontSize()));
        ignoreFriendTextStyleCheckBox.setSelected(Config.cfg.isIgnoreFriendTextStyle());
        autoSaveLogCheckBox.setSelected(Config.cfg.isAutoSaveLogs());
        
        // Network
        hostNameTextField.setText(Config.cfg.getHostname());
        portTextField.setText(String.valueOf(Config.cfg.getPortNumber()));
        
    }
    
    public void setConfiguation(){
        // General
        Config.cfg.setRememberUsernamePassword(rememberUsernamePasswordCheckBox.isSelected());
        Config.cfg.setAutoLogin(autoLoginCheckBox.isSelected());
        Config.cfg.setRunOnStartup(runOnStartupCheckBox.isSelected());
        
        // chat
        Config.cfg.setColor(colorChangePicker.getValue());
        Config.cfg.setFontStyle(fontStyleComboBox.getSelectionModel().getSelectedItem().toString());
        Config.cfg.setFontWeight(fontWeightComboBox.getSelectionModel().getSelectedItem().toString());
        Config.cfg.setFontPosture(fontPostureComboBox.getSelectionModel().getSelectedItem().toString());
        Config.cfg.setFontSize(Integer.parseInt(fontSizeComboBox.getSelectionModel().getSelectedItem().toString()));
        Config.cfg.setIgnoreFriendTextStyle(ignoreFriendTextStyleCheckBox.isSelected());
        Config.cfg.setAutoSaveLogs(autoSaveLogCheckBox.isSelected());
        
        // network
        if(!emptyHostName){
            Config.cfg.setHostname(hostNameTextField.getText());
        }
        if(!emptyPortNumber){
            Config.cfg.setPortNumber(Integer.parseInt(portTextField.getText()));
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
            String tempText;
            tempText = hostNameTextField.getText();
            if(tempText == null || tempText.length() < 1){
                emptyHostName = true;
            }
            tempText = portTextField.getText();
            if(tempText == null || tempText.length() < 1){
                emptyPortNumber = true;
            }
            
            setConfiguation();
            Config.cfg.writeConfigFile();
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
    
    private class BypassNetworkWarningActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            bypassWarningButton.setDisable(true);
            hostNameTextField.setDisable(false);
            portTextField.setDisable(false);
            centerPaneNetwork.getChildren().add(willRequireRestartLabel);
        }
    }
    
    private class RememberUsernameAndPasswordChangeListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            if(event.getSource() instanceof CheckBox){
                CheckBox temp = (CheckBox) event.getSource();
                if(temp.isSelected()){
                    autoLoginCheckBox.setDisable(false);
                }else if(!temp.isSelected()){
                    autoLoginCheckBox.setSelected(false);
                    autoLoginCheckBox.setDisable(true);
                }
            }
        }
        
    }

}
