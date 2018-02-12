
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

/**
 * 
 * Since we're already making a basic client-server,
 * we might as well implement a game that two people chatting can play together.
 * Something basic? like pong or tetris? i don't know
 */
public class Game extends Application{
    private FlowPane flowPane;
    
    @Override
    public void start(Stage primaryStage){
        flowPane = new FlowPane();
        flowPane.prefWidthProperty().bind(primaryStage.widthProperty());
        flowPane.prefHeightProperty().bind(primaryStage.heightProperty());
        
        primaryStage.setTitle("Game");
        primaryStage.setScene(new Scene(flowPane, 600, 600));
        primaryStage.show();
    }
    
}
