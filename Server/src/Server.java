
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

public class Server extends Application {
    //server variables and objects
    private ClientListener clientListener;
    public Server myServer = this;
    //database variables
    public static final String DATABASE_PATH = "jdbc:ucanaccess://C:/Projects/users.accdb";
    public static final String DB_CLASS_BY_NAME = "net.ucanaccess.jdbc.UcanaccessDriver";

    //javafx gui objects
    private TextArea textArea;
    private FlowPane flowPane;
    private Button stopServerButton;
    private Button startServerButton;

    /**
     *
     * @param primaryStage
     * @exception IOException
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        //initialize javafx user interface
        flowPane = new FlowPane();
        flowPane.prefWidthProperty().bind(primaryStage.widthProperty());
        flowPane.prefHeightProperty().bind(primaryStage.heightProperty());
        textArea = new TextArea();
        textArea.prefWidthProperty().bind(flowPane.widthProperty());
        textArea.setPrefHeight(500);
        textArea.setEditable(false);
        stopServerButton = new Button("Stop");
        stopServerButton.setDisable(true);
        stopServerButton.setOnAction(new StopServerButtonActionListener());
        startServerButton = new Button("Start");
        startServerButton.setOnAction(new StartServerButtonActionListener());
        
        flowPane.getChildren().add(textArea);
        flowPane.getChildren().add(startServerButton);
        flowPane.getChildren().add(stopServerButton);

        primaryStage.setScene(new Scene(flowPane, 600, 600));
        primaryStage.show();

        startUpText();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     *
     * @param text
     */
    public void writeToConsole(String text) {
        javafx.application.Platform.runLater(()
                -> textArea.appendText(text + "\n")
        );
    }

    /**
     * @param none text written to console on startup
     */
    private void startUpText() {
        writeToConsole("Welcome to UNF Chat server. Version 1.0\n");
    }

    /**
     * the server crashed somewhere, and whichever class crashed
     * the server used the server object to relay the message
     */
    public void stopServer() {

    }

    /*
     * javafx ui component action listeners @@@
     */
    
    private class StartServerButtonActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            // very ugly quick and dirty thread, but it works well.
            clientListener = new ClientListener(myServer);
            Thread mainThread = new Thread(clientListener);
            mainThread.start();
            startServerButton.setDisable(true);
            stopServerButton.setDisable(false);
        }

    }
    
    private class StopServerButtonActionListener implements EventHandler<ActionEvent>{

        @Override
        public void handle(ActionEvent event) {
            //stop the server somehow
            writeToConsole("Not implemented.");
            System.exit(0);
        }
        
    }
    
}
