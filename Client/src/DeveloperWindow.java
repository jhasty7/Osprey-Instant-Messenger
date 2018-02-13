
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 *
 * @author Joshua
 */
public class DeveloperWindow extends Application {

    //global variables
    public static String HOST_NAME = "127.0.0.1";
    public static int PORT_NUMBER = 45566;
    public static String USER_NAME = "Josh";
    public static String FRIENDS_NAME = "Michelle";

    //javafx ui variables
    private static TextArea textArea;
    private TextField textField;

    private TextField sender;
    private TextField receiver;
    private Label senderLabel;
    private Label receiverLabel;
    private Button senderLockButton;
    private Button receiverLockButton;
    private Button connectButton;

    private FlowPane flowPane;
    private Button sendMessageButton;
    private Button quitButton;
    private Button loginWindowButton;
    private Stage primaryStage;
    private Button mainWindowButton;
    private Button messageWindowButton;
    private Button smallGameButton;
    private Button settingsWindow;
    
    //socket variables/objects
    private Socket myConnection;
    private boolean isConnected = false;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String comingFrom;
    private String sendingTo;
    

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        
        flowPane = new FlowPane();
        flowPane.prefWidthProperty().bind(primaryStage.widthProperty());
        flowPane.prefHeightProperty().bind(primaryStage.heightProperty());

        textArea = new TextArea();
        textArea.prefWidthProperty().bind(flowPane.widthProperty());
        textArea.setPrefHeight(500);
        textArea.setEditable(false);

        sender = new TextField();
        senderLabel = new Label("sender");
        senderLockButton = new Button("Lock");
        senderLockButton.setOnAction(new LockSenderFrield());
        receiver = new TextField();
        receiverLabel = new Label("receiver");
        receiverLockButton = new Button("Lock");
        receiverLockButton.setOnAction(new LockReceiverField());

        textField = new TextField();
        textField.prefWidthProperty().bind(flowPane.widthProperty());

        quitButton = new Button("Exit");
        quitButton.setOnAction(new ExitButtonActionListener());
        sendMessageButton = new Button("Send");
        sendMessageButton.setDisable(true);
        sendMessageButton.setOnAction(new SendMessageButtonActionListener());

        connectButton = new Button("Connect");
        connectButton.setDisable(true);
        connectButton.setOnAction(new ConnectButtonActionListener());
        
        loginWindowButton = new Button("Login Window");
        loginWindowButton.setOnAction(new OpenLoginWindowActionListener());
        
        mainWindowButton = new Button("Main Window");
        mainWindowButton.setOnAction(new OpenMainWindowActionListener());
        
        messageWindowButton = new Button("Message Window");
        messageWindowButton.setOnAction(new OpenMessageWindowActionListener());
        
        smallGameButton = new Button("Game");
        smallGameButton.setOnAction(new SmallGameButtonActionListener());
        
        settingsWindow = new Button("Setting Window");
        settingsWindow.setOnAction(new SettingsButtonActionListener());
        
        flowPane.getChildren().add(textArea);
        flowPane.getChildren().add(textField);
        flowPane.getChildren().add(sendMessageButton);
        flowPane.getChildren().add(senderLabel);
        flowPane.getChildren().add(sender);
        flowPane.getChildren().add(senderLockButton);
        flowPane.getChildren().add(receiverLabel);
        flowPane.getChildren().add(receiver);
        flowPane.getChildren().add(receiverLockButton);
        flowPane.getChildren().add(connectButton);
        flowPane.getChildren().add(quitButton);
        flowPane.getChildren().add(loginWindowButton);
        flowPane.getChildren().add(mainWindowButton);
        flowPane.getChildren().add(messageWindowButton);
        flowPane.getChildren().add(smallGameButton);
        flowPane.getChildren().add(settingsWindow);
        
        primaryStage.setTitle("Developer Window");
        primaryStage.setScene(new Scene(flowPane, 600, 600));
        primaryStage.show();
        
        displayMessage("This is the developer window.\n"
                + "This has various components that can be accessed to directly.\n"
                + "This will help us program/debug");
    }

    public static void main(String[] args) {
        Application.launch(args);
    }

    /*
     * javafx ui component action listeners @@@
     */
    private class SendMessageButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            Message textMessage = new Message(comingFrom, sendingTo, textField.getText(), null, null);
            textField.clear();
            // very ugly quick and dirty thread, but it works well.
            new Thread(new sendMessage(textMessage)).start();
        }

    }

    private class ExitButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            System.exit(0);
        }

    }

    private class LockReceiverField implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            sendingTo = receiver.getText();
            FRIENDS_NAME = sendingTo;
            receiver.setDisable(true);
            receiverLockButton.setDisable(true);
            checkLockButtons();
        }

    }

    private class LockSenderFrield implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            comingFrom = sender.getText();
            USER_NAME = comingFrom;
            sender.setDisable(true);
            senderLockButton.setDisable(true);
            checkLockButtons();
        }

    }

    private class ConnectButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            //try to connect
            new Thread(new connect()).start();
            sendMessageButton.setDisable(false);
        }

    }
    
    private class OpenLoginWindowActionListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            Login loginWindow = new Login();
            loginWindow.start(new Stage());
        }
        
    }
    
    private class OpenMainWindowActionListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            MainWindow mainWindow = new MainWindow(USER_NAME);
            mainWindow.start(new Stage());
        }
        
    }
    
    private class OpenMessageWindowActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            MessageWindow messageWindow = new MessageWindow(FRIENDS_NAME);
            messageWindow.start(new Stage());
        }
    }
    
    private class SmallGameButtonActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            Game game = new Game();
            game.start(new Stage());
        }
    }
    
    private class SettingsButtonActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            Settings settings = new Settings();
            settings.start(new Stage());
        }
    }

    public static void displayMessage(String text) {
        javafx.application.Platform.runLater(() -> {
            textArea.appendText(text + "\n");
        });
    }
    
    
    
    
    
    public void checkLockButtons() {
        if (receiverLockButton.isDisable() && senderLockButton.isDisable()) {
            connectButton.setDisable(false);
        }
    }

    private class connect implements Runnable {

        private Message message = new Message();

        @Override
        public void run() {
            displayMessage("Trying to connect...");
            try {

                myConnection = new Socket(HOST_NAME, PORT_NUMBER);
                in = new ObjectInputStream(myConnection.getInputStream());
                out = new ObjectOutputStream(myConnection.getOutputStream());
                isConnected = true;
                displayMessage("Connected");
                // sent login packet
                LoginRegisterPacket p = new LoginRegisterPacket(comingFrom,null);
                out.writeObject(p);
                do {
                    
                    Object obj = in.readObject();
                    displayMessage("Packet received");
                    if (obj.getClass().equals(message.getClass())) {
                        
                        message = (Message) obj;
                        displayMessage(message.getComingFrom() + ": " + message.getMessage());

                    }
                    else {
                        displayMessage("Packet unreadable.");
                    }

                } while (isConnected);

            }
            catch (IOException | ClassNotFoundException ex) {
                System.out.println(ex);
            }

        }

    }

    private class sendMessage implements Runnable {

        private Message message;

        public sendMessage(Message message) {
            this.message = message;
        }

        @Override
        public void run() {

            try {
                out.writeObject(message);
            }
            catch (IOException e) {
                displayMessage("Failed to send packet.");
            }

        }

    }

}
