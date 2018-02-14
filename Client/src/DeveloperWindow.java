
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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

    //javafx ui variables
    private static TextArea textArea;
    private FlowPane flowPane;
    private Button loginWindowButton;
    private Button mainWindowButton;
    private Button messageWindowButton;
    private Button smallGameButton;
    private Button settingsWindow;

    @Override
    public void start(Stage primaryStage) throws Exception {
        
        flowPane = new FlowPane();
        flowPane.prefWidthProperty().bind(primaryStage.widthProperty());
        flowPane.prefHeightProperty().bind(primaryStage.heightProperty());

        textArea = new TextArea();
        textArea.prefWidthProperty().bind(flowPane.widthProperty());
        textArea.setPrefHeight(500);
        textArea.setEditable(false);
        
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
        Config cfg = new Config();
        Application.launch(args);
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
            MainWindow mainWindow = new MainWindow("TESTER");
            mainWindow.start(new Stage());
        }
        
    }
    
    private class OpenMessageWindowActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            MessageWindow messageWindow = new MessageWindow("TESTER", new ServerListener());
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

}
