
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.embed.swing.SwingFXUtils;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

public class MessageWindow extends Application {

    private static final String LOG_PATH = System.getProperty("user.dir") + "/data/logs/";
    private ServerListener serverListener;
    private String friendName;
    private TextArea inputTextArea;
    private Button sendMessageButton;
    private Button sendImageButton;
    private Stage primaryStage;
    private StringProperty messageLog = new SimpleStringProperty();
    private MenuBar mainMenuBar;
    private Menu friendMenuItem;
    private CheckMenuItem ignoreTextMenuItem;
    private MenuItem blockFriendMenuItem;
    private MenuItem exitConvoMenuItem;
    private MenuItem settingsMenuItem;
    private Menu other;
    private TextFlow textFlow;
    private ScrollPane textFlowScrollPane;
    private VBox textFlowVBox;
    private BorderPane controlHBox;
    private boolean manualScrolling = false;

    public MessageWindow(String friendName, ServerListener serverListener) {
        this.serverListener = serverListener;
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
        other = new Menu("Conversation");
        ignoreTextMenuItem = new CheckMenuItem("Ignore Font/Color");
        ignoreTextMenuItem.selectedProperty().addListener(new IgnoreTextFontListener());
        blockFriendMenuItem = new MenuItem("Block");
        exitConvoMenuItem = new MenuItem("Exit Conversation");
        settingsMenuItem = new MenuItem("Settings");
        exitConvoMenuItem.setOnAction(new CloseWindowButtonListener());
        blockFriendMenuItem.setOnAction(new BlockFriendMenuItemListener());
        settingsMenuItem.setOnAction(new SettingsMenuItemListener());
        friendMenuItem.getItems().addAll(ignoreTextMenuItem);
        other.getItems().addAll(settingsMenuItem, exitConvoMenuItem);
        mainMenuBar.getMenus().addAll(friendMenuItem, other);
        ignoreTextMenuItem.setSelected(Config.cfg.isIgnoreFriendTextStyle());
        
        // text areas and buttons
        inputTextArea = new TextArea("");
        sendMessageButton = new Button("Send");
        sendImageButton = new Button("+");
        textFlow = new TextFlow();
        textFlowScrollPane = new ScrollPane();
        textFlowVBox = new VBox();
        textFlowVBox.getChildren().addAll(textFlowScrollPane, textFlow);
        textFlow.setLineSpacing(10);
        VBox.setVgrow(textFlowScrollPane, Priority.ALWAYS);
        textFlowScrollPane.setContent(textFlow);
        textFlowScrollPane.vvalueProperty().addListener(new TextFlowScrollPaneVerticalPropertyListener());
        textFlowVBox.setMinSize(350, 200);
        textFlowVBox.prefWidthProperty().bind(pane.widthProperty());
        inputTextArea.setMinSize(180, 110);
        inputTextArea.setMaxSize(180, 110);
        inputTextArea.setWrapText(true);
        inputTextArea.setText("");
        //Michelle's wrapping changes and textflow padding
        textFlowScrollPane.setFitToWidth(true);
        textFlow.setPadding(new Insets(0, 10, 0, 10));

        //
        controlHBox = new BorderPane();
        /* various component listeners */
        sendMessageButton.setOnAction(new SendButtonActionListener());
        inputTextArea.setOnKeyPressed(new InputTextAreaKeyListener());
        //closeWindowButton.setOnAction(new CloseWindowButtonListener());
        sendImageButton.setOnAction(new AttatchImageListener());
        primaryStage.heightProperty().addListener(p -> {
            textFlowVBox.setPrefHeight(primaryStage.getHeight() - 200);
        });
        primaryStage.widthProperty().addListener(p -> {
            inputTextArea.setMaxWidth(primaryStage.getWidth() - 100);
            inputTextArea.setPrefWidth(primaryStage.getWidth() - 100);
        });
        textFlow.getChildren().addListener(
                (ListChangeListener<Node>) ((Change) -> {
                    if (!manualScrolling) {
                        textFlow.layout();
                        textFlowScrollPane.layout();
                        textFlowScrollPane.setVvalue(1.0f);
                    }
                }));

        controlHBox.setTop(sendImageButton);
        controlHBox.setBottom(sendMessageButton);

        pane.setPrefSize(350.0, 350.0);
        pane.setVgap(5);
        pane.setHgap(12);
        pane.getChildren().add(mainMenuBar);
        pane.getChildren().add(textFlowVBox);
        pane.getChildren().add(inputTextArea);
        pane.getChildren().add(controlHBox);

        Scene scene = new Scene(pane, 350, 350);

        primaryStage.setOnCloseRequest(new clickedXToClose());
        primaryStage.setMinWidth(395);
        primaryStage.setMinHeight(390);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setResizable(true);
        primaryStage.setTitle(friendName);
        primaryStage.setScene(scene);

        // check for logs
        if (Config.cfg.isAutoSaveLogs()) {
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
    private class BlockFriendMenuItemListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
                Alert removeFriendAlert = new Alert(Alert.AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Goodnight Sweet Prince");
                removeFriendAlert.setHeaderText("Block " + friendName + "?");
                removeFriendAlert.setContentText("You can always unblock them");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    serverListener.blockFriend(friendName);
                    closeChatWindow();
                }
        }

    }
    
    private class IgnoreTextFontListener implements ChangeListener<Boolean>{
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if(newValue){
                Config.cfg.setIgnoreFriendTextStyle(newValue);
            }else{
                Config.cfg.setIgnoreFriendTextStyle(newValue);
            }
        }
    }

    /*
     * when the send button is pressed
     */
    private class SendButtonActionListener implements EventHandler<ActionEvent> {

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
    private class CloseWindowButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null) {
                closeChatWindow();
            }
        }

    }

    private class SettingsMenuItemListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null) {
                Settings settings = new Settings();
                settings.start(new Stage());
            }
        }

    }

    //More dumb Michelle stuff
    private class AttatchImageListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (inputTextArea.getText() != null) {
                attachFile();
            }
        }

    }
    //

    /*
     * when the enter button is pressed while the input text area has focus
     */
    private class InputTextAreaKeyListener implements EventHandler<KeyEvent> {

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
            else {
                if (inputTextArea.getText() != null) {
                    inputTextArea.setStyle("-fx-text-fill: #" + Integer.toHexString(Config.cfg.getColor().hashCode()));
                    inputTextArea.setFont(Config.cfg.getFontAsFont());
                }

            }

        }

    }

    /*
     * text flow scroll pane property listener (auto scrolling)
     */
    private class TextFlowScrollPaneVerticalPropertyListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<? extends Object> observable, Object oldValue, Object newValue) {
            textFlowScrollPane.layout();
            if(textFlowScrollPane.getVvalue() != 1.0){
                manualScrolling = true;
            }else{
                manualScrolling = false;
            }
        }

    }

    /*
     * build message to display from text message and display it on textflow
     */
    public void displayIncomingText(MessagePacket textMessage) {

        String message = textMessage.getComingFrom() + ":\n" + textMessage.getMessage() + "\r\n";
        /* add message to the message log */
        messageLog.set(messageLog.getValue() + message);
        Text newText = new Text(message);
        if (Config.cfg.isIgnoreFriendTextStyle()) {
            newText.setFill(Config.cfg.getColor());
            newText.setFont(Config.cfg.getFontAsFont());
        }
        else {
            newText.setFont(textMessage.getFontAsFont());
            newText.setFill(textMessage.getColor());
        }
        textFlow.getChildren().add(newText);
    }

    void displayMyText(MessagePacket textMessage) {

        String message = textMessage.getComingFrom() + ":\n" + textMessage.getMessage() + "\r\n";
        /* add message to the message log */
        messageLog.set(messageLog.getValue() + message);
        Text newText = new Text(message);
        newText.setFill(textMessage.getColor());
        newText.setFont(textMessage.getFontAsFont());
        
        textFlow.getChildren().add(newText);
    }

    /*
     * when the X button is clicked on the window
     */
    private class clickedXToClose implements EventHandler<WindowEvent> {

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
        MessagePacket textMessage = new MessagePacket(
                MainWindow.username,
                friendName,
                message,
                Config.cfg.getFontAsFontValue(),
                Config.cfg.getColor());
        displayMyText(textMessage);
        serverListener.sendMessage(textMessage);

    }

    //Michelle does more stupid stuff
    private void attachFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {

            String fileExtension = FilenameUtils.getExtension(file.toString());
            DeveloperWindow.displayMessage(fileExtension);
            switch (fileExtension) {
                case "jpg":
                case "png":
                case "bmp":
                case "tiff":
                case "jpeg":
                case "gif":
                    // if image
                    Image image = new Image(file.toURI().toString());
                    displayImage(image, MainWindow.username);
                    try {
                        sendOutgoingImage(ImageIO.read(file));
                    }
                    catch (IOException ex) {
                        DeveloperWindow.displayMessage("Error: in message window class at attachImage; cannot ImageIO.read");
                        DeveloperWindow.displayMessage(ex.toString());
                    }
                    break;
                default:
                    // if file
                    sendFile(file);
            }

        }

    }

    private void displayImage(Image file, String comfingFrom) {

        ImageView imageView = new ImageView(file);
        imageView.setFitHeight(200);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);
        textFlow.getChildren().add(new Text(comfingFrom + ":\n"));
        textFlow.getChildren().add(imageView);
        textFlow.getChildren().add(new Text("\n"));

    }

    public void displayIncomingImage(final ImagePacket ip) {
        Hyperlink acceptHyperlink = new Hyperlink("Accept");
        Hyperlink rejectHyperlink = new Hyperlink("Reject");
        Text askForImage = new Text("Incoming image from " + ip.getComingFrom() + "\n");
        Text spacing = new Text("  ");
        textFlow.getChildren().addAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);
        acceptHyperlink.setOnAction(e -> {
            textFlow.getChildren().removeAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);
            displayImage(SwingFXUtils.toFXImage(ip.getImage(), null), ip.getComingFrom());
        });
        rejectHyperlink.setOnAction(e -> {
            textFlow.getChildren().removeAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);
        });
    }

    public void displayIncomingFileDownloadPrompt(SendFilePacket sfp) {

        Hyperlink acceptHyperlink = new Hyperlink("Accept");
        Hyperlink rejectHyperlink = new Hyperlink("Reject");
        Text askForImage = new Text("Save file from " + sfp.getComingFrom() + "\n");
        Text spacing = new Text("  ");
        textFlow.getChildren().addAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);

        acceptHyperlink.setOnAction(e -> {
            textFlow.getChildren().removeAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save file");
            fileChooser.setInitialFileName(FilenameUtils.getName(sfp.getFile().toString()));
            fileChooser.getExtensionFilters().add(new ExtensionFilter(FilenameUtils.getExtension(sfp.getFile().toString()),
                    FilenameUtils.getExtension(sfp.getFile().toString())));
            try {
                File tempFile = fileChooser.showSaveDialog(null);
                FileUtils.copyFile(sfp.getFile(), tempFile, false);
                String newPath = FilenameUtils.getFullPath(tempFile.toString());
                Hyperlink openFolderHyperlink = new Hyperlink("Open Folder");
                textFlow.getChildren().add(new Text("File Saved."));
                textFlow.getChildren().add(openFolderHyperlink);
                textFlow.getChildren().add(new Text("\n"));
                openFolderHyperlink.setOnAction(ea -> {
                    try {
                        Desktop.getDesktop().open(new File(newPath));
                    }
                    catch (IOException egh) {
                    }
                });
            }
            catch (IOException ex) {
                DeveloperWindow.displayMessage("Error: in messagewindow class at displayIncomingFileDownloadPrompt");
            }
        });

        rejectHyperlink.setOnAction(e -> {
            textFlow.getChildren().removeAll(askForImage, acceptHyperlink, spacing, rejectHyperlink);
        });
    }

    public void sendOutgoingImage(BufferedImage ic) {

        serverListener.sendImage(new ImagePacket(MainWindow.username, friendName, ic));

    }

    public void sendFile(File file) {

        serverListener.sendFile(new SendFilePacket(MainWindow.username, friendName, file));

    }

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
    public void closeChatWindow() {
        if (Config.cfg.isAutoSaveLogs()) {
            writeToLog();
        }
        MainWindow.myMessageWindows.remove(this);
        primaryStage.close();
    }

    /**
     * allows the MainWindow to access the Message window stage
     *
     * @return
     */
    public Stage getStage() {
        return primaryStage;
    }

}
