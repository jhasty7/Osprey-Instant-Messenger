
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
public class Client extends Application {

    //global variables
    public static String HOST_NAME = "127.0.0.1";
    public static int PORT_NUMBER = 45566;

    //javafx ui variables
    private TextArea textArea;
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

    //socket variables/objects
    private Socket myConnection;
    private boolean isConnected = false;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String comingFrom;
    private String sendingTo;

    @Override
    public void start(Stage primaryStage) throws Exception {

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

        primaryStage.setTitle("UNF IM");
        primaryStage.setScene(new Scene(flowPane, 600, 600));
        primaryStage.show();
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
            Message textMessage = new Message(comingFrom, sendingTo, textField.getText());
            textField.clear();
            // very ugly quick and dirty thread, but it works well.
            new Thread(new sendMessage(textMessage)).start();
        }

    }

    private class ExitButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            //stop the server somehow
            displayMessage("Not implemented.");
            System.exit(0);
        }

    }

    private class LockReceiverField implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            sendingTo = receiver.getText();
            receiver.setDisable(true);
            receiverLockButton.setDisable(true);
            checkLockButtons();
        }

    }

    private class LockSenderFrield implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            comingFrom = sender.getText();
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

    public void displayMessage(String text) {
        textArea.appendText(text + "\n");
    }
    
    
    
    
    
    public void checkLockButtons() {
        if (receiverLockButton.isDisable() && senderLockButton.isDisable()) {
            connectButton.setDisable(false);
        }
    }

    private class connect implements Runnable {

        private Message message = new Message(null,null,null);

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
