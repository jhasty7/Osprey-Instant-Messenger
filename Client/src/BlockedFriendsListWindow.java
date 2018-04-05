
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class BlockedFriendsListWindow extends Application {
    
    private Stage primaryStage;
    private BorderPane masterPane;
    private BlockedFriendsList bfl;
    private ListView blockedFriends;
    
    public BlockedFriendsListWindow(BlockedFriendsList bfl){
        this.bfl = bfl;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        masterPane = new BorderPane();
        masterPane.setPadding(new Insets(5,5,5,5));
        
        blockedFriends = new ListView(FXCollections.observableArrayList(bfl.getBlockFriendsList()));
        masterPane.setCenter(blockedFriends);
        
        // javaui stage stuff
        Scene scene = new Scene(masterPane, 358, 411, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("Blocked Friends");
        primaryStage.setScene(scene);
        primaryStage.initModality(Modality.APPLICATION_MODAL);
        // show window
        primaryStage.show();
    }
}
