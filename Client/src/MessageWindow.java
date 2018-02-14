
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javax.imageio.ImageIO;

public class MessageWindow extends Application {
    
    private static final String LOG_PATH = System.getProperty("user.dir") + "/data/logs/";
    private String friendName;
    private TextArea inputTextArea;
    private Button sendMessageButton;
    private Button closeWindowButton;
    private Button sendImageButton;
    private Stage primaryStage;
    private StringProperty messageLog = new SimpleStringProperty();
    private MenuBar mainMenuBar;
    private Menu friendMenuItem;
    private CheckMenuItem ignoreTextMenuItem;
    private MenuItem blockFriendMenuItem;
    private TextFlow textFlow;
    private ScrollPane textFlowScrollPane;
    private VBox textFlowVBox;
    private boolean manualScrolling = false;
    
    private static Color myColor;
    private static Font myFont;
    
    public MessageWindow(String friendName) {
        
        this.friendName = friendName;
        messageLog.set("");
        
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // main pain
        FlowPane pane = new FlowPane();
        
        // menu bar and items
        mainMenuBar = new MenuBar();
        mainMenuBar.prefWidthProperty().bind(pane.widthProperty());
        friendMenuItem = new Menu("Friend");
        ignoreTextMenuItem = new CheckMenuItem("Ignore Font/Color");
        blockFriendMenuItem = new MenuItem("Block");
        blockFriendMenuItem.setOnAction(new BlockFriendMenuItemListener());
        friendMenuItem.getItems().addAll(ignoreTextMenuItem,
                blockFriendMenuItem);
        mainMenuBar.getMenus().add(friendMenuItem);
        
        // text areas and buttons
        inputTextArea = new TextArea("");
        sendMessageButton = new Button("Send");
        closeWindowButton = new Button("Close");
        sendImageButton = new Button("Image");
        textFlow = new TextFlow();
        textFlowScrollPane = new ScrollPane();
        textFlowVBox = new VBox();
        textFlowVBox.getChildren().addAll(textFlowScrollPane, textFlow);
        VBox.setVgrow(textFlowScrollPane, Priority.ALWAYS);
        textFlowScrollPane.setContent(textFlow);
        textFlowScrollPane.vvalueProperty().addListener(new TextFlowScrollPaneVerticalPropertyListener());
        textFlowVBox.setMinSize(350, 200);
        textFlowVBox.prefWidthProperty().bind(pane.widthProperty());
        inputTextArea.setMinSize(180, 110);
        inputTextArea.setMaxSize(180, 110);
        inputTextArea.setWrapText(true);
        inputTextArea.setText("");
        
        /* various component listeners */
        sendMessageButton.setOnAction(new SendButtonActionListener());
        inputTextArea.setOnKeyPressed(new InputTextAreaKeyListener());
        closeWindowButton.setOnAction(new CloseWindowButtonListener());
        sendImageButton.setOnAction(new AttatchImageListener());
        primaryStage.heightProperty().addListener(p -> {
            textFlowVBox.setPrefHeight(primaryStage.getHeight() - 200);
        });
        primaryStage.widthProperty().addListener(p -> {
            inputTextArea.setMaxWidth(primaryStage.getWidth() - 220);
            inputTextArea.setPrefWidth(primaryStage.getWidth() - 220);
        });
        
        pane.setPrefSize(350.0, 350.0);
        pane.setVgap(5);
        pane.setHgap(12);
        pane.getChildren().add(mainMenuBar);
        pane.getChildren().add(textFlowVBox);
        pane.getChildren().add(inputTextArea);
        pane.getChildren().add(sendMessageButton);
        pane.getChildren().add(sendImageButton);
        pane.getChildren().add(closeWindowButton);
        
        Scene scene = new Scene(pane, 350, 350);
        
        primaryStage.setOnCloseRequest(new clickedXToClose());
        primaryStage.setMinWidth(395);
        primaryStage.setMinHeight(390);
        primaryStage.setResizable(true);
        primaryStage.setTitle(friendName);
        primaryStage.setScene(scene);
        
        // check for logs
        if(Config.cfg.isAutoSaveLogs()){
            checkForLog();
        }
        
        primaryStage.show();
    }

    public String getFriendName() {
        return friendName;
    }

    /*
     * when the block friend menu item is clicked
     */
    class BlockFriendMenuItemListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            
        }

    }

    /*
     * when the send button is pressed
     */
    class SendButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null && !inputTextArea.getText().matches("^\\s*$")) {
                String text = inputTextArea.getText();
                sendMessage(text);
                inputTextArea.clear();
                inputTextArea.requestFocus();
            }
        }

    }

    /*
     * when the close button is pressed
     */
    class CloseWindowButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null) {
                closeChatWindow();
            }
        }

    }
    //More dumb Michelle stuff
    class AttatchImageListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null) {
                
                attatchImage();
            }
        }

    }
    //

    /*
     * when the enter button is pressed while the input text area has focus
     */
    class InputTextAreaKeyListener implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                if (inputTextArea.getText() != null && !inputTextArea.getText().matches("^\\s*$")) {
                    String text = inputTextArea.getText();
                    sendMessage(text);
                    inputTextArea.clear();
                }

            }

        }

    }

    /*
     * text flow scroll pane property listener (auto scrolling)
     */
    class TextFlowScrollPaneVerticalPropertyListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            //add more scrolling rules here
        }

    }

    /*
     * build message to display from text message and display it on textflow
     */
    public void displayIncomingText(Message textMessage) {

        String message = textMessage.getComingFrom() + ": " + textMessage.getMessage() + "\r\n";
        /* add message to the message log */
        messageLog.set(messageLog.getValue() + message);
        Text newText = new Text(message);
        if (Config.cfg.isIgnoreFriendTextStyle()) {
            newText.setFill(Config.cfg.getColor());
            newText.setFont(Config.cfg.getFontAsFont());
        } else{
            newText.setFont(textMessage.getFontAsFont());
            newText.setFill(textMessage.getColor());
        }
        textFlow.getChildren().add(newText);
        if (!manualScrolling) {
            textFlowScrollPane.setVvalue(1.0);
        }
    }
    
    void displayMyText(Message textMessage){
        
        String message = textMessage.getComingFrom() + ": " + textMessage.getMessage() + "\r\n";
        /* add message to the message log */
        messageLog.set(messageLog.getValue() + message);
        Text newText = new Text(message);
        newText.setFill(textMessage.getColor());
        newText.setFont(textMessage.getFontAsFont());
        textFlow.getChildren().add(newText);
        
        if (!manualScrolling) {
            textFlowScrollPane.setVvalue(1.0);
        }
        
    }

    /*
     * when the X button is clicked on the window
     */
    class clickedXToClose implements EventHandler<WindowEvent> {

        @Override
        public void handle(WindowEvent event) {
            closeChatWindow();
        }

    }

    /*
     * sending a message
     */
    private void sendMessage(String message) {
        // create Message object and display it locally, then send it to the server
        Message textMessage = new Message(
                DeveloperWindow.USER_NAME,
                friendName,
                message,
                Config.cfg.getFontAsFontValue(),
                Config.cfg.getColor());
        displayMyText(textMessage);
        //TODO: send the packet out to the server
        
    }
    
    //Michelle does more stupid stuff
    private void attatchImage(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("JPGs", "*.JPG"));
        File file = fileChooser.showOpenDialog(null);
        if(file != null){
            Image image = new Image(file.toURI().toString());
            sendImage(image);
        }
    }
    private void sendImage(Image file){     
        
        ImageView imageView = new ImageView(file);
        imageView.setFitHeight(100);
        imageView.setFitWidth(100);
        imageView.setPreserveRatio(true);
        textFlow.getChildren().add(new Text("\n"));
        textFlow.getChildren().add(imageView);
        textFlow.getChildren().add(new Text("\n"));
        
    }
    //

    /*
     * gets the text from the log file, if no log file, creates one
     */
    private void checkForLog() {

        FileReader fileReader;
        BufferedReader bufferedReader;
        String tempStr = "";

        try {
            fileReader = new FileReader(LOG_PATH + friendName + "-chat-log.txt");
            bufferedReader = new BufferedReader(fileReader);

            while (bufferedReader.ready()) {
                tempStr = tempStr + bufferedReader.readLine() + "\r\n";
            }
            bufferedReader.close();
            fileReader.close();
            messageLog.set(tempStr);
            Text beginingText = new Text(tempStr);
            Font beginingFont = Font.font("Times New Roman", FontPosture.ITALIC, 12);
            beginingText.setFont(beginingFont);
            beginingText.setFill(Color.GRAY);
            textFlow.getChildren().add(beginingText);
        }
        catch (FileNotFoundException ex) {

            try {
                File logfile = new File(LOG_PATH + friendName + "-chat-log.txt");
                logfile.getParentFile().mkdirs();
                logfile.createNewFile();
            }
            catch (IOException ex1) {
                DeveloperWindow.displayMessage("failed to create new log file");
            }

        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to open log file");
        }

    }

    /*
     * writes the chat text to the .txt log
     * TODO: remove from the master window list
     */
    private void writeToLog() {

        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter((LOG_PATH + friendName + "-chat-log.txt"), "UTF-8");
            printWriter.write(messageLog.getValue());
            printWriter.close();
        }
        catch (FileNotFoundException ex) {
            DeveloperWindow.displayMessage("couldn't find log file to write to");

        }
        catch (IOException ex) {
            DeveloperWindow.displayMessage("failed to write to log file");
        }

    }
    
    /**
     * closes chat window
     */
    private void closeChatWindow(){
        if(Config.cfg.isAutoSaveLogs()){
            writeToLog();
        }
        primaryStage.close();
    }
    
}
