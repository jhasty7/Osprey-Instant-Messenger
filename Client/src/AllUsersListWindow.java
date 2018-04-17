
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class AllUsersListWindow extends Application {

    private Stage primaryStage;
    private BorderPane masterPane;
    private GetAllUsers gau;
    private TableView<Row> allUsers;
    private ArrayList<String> columnNames;
    private List<Row> rows;
    private HBox bottomPanel;
    private Button addFriendButton;
    private ServerListener serverListener;

    public AllUsersListWindow(GetAllUsers gau, ServerListener serverListener) {
        this.gau = gau;
        this.serverListener = serverListener;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        masterPane = new BorderPane();
        masterPane.setPadding(new Insets(5, 5, 5, 5));

        bottomPanel = new HBox(5);
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        allUsers = new TableView();
        columnNames = initializeColumnNames();
        rows = createRows();
        makeColumns(allUsers);
        allUsers.getItems().addAll(rows);
        allUsers.setEditable(false);
        
        addFriendButton = new Button("Add Friend");
        addFriendButton.setOnAction(new AddFriendButtonActionListener());
        bottomPanel.getChildren().addAll(addFriendButton);
        
        masterPane.setCenter(allUsers);
        masterPane.setBottom(bottomPanel);
        // javaui stage stuff
        Scene scene = new Scene(masterPane, 370, 401, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("All users");
        primaryStage.setScene(scene);
        primaryStage.initModality(Modality.APPLICATION_MODAL);

        // show window
        primaryStage.show();
    }
    
    private void makeColumns(TableView<Row> tableView) {
        for (int m = 0; m < columnNames.size(); m++) {
            TableColumn<Row, String> column = new TableColumn<>(columnNames.get(m));
            column.setCellValueFactory(param -> {
                int index = param.getTableView().getColumns().indexOf(param.getTableColumn());
                List<Cell> cells = param.getValue().getCells();
                return new SimpleStringProperty(cells.size() > index ? cells.get(index).toString() : null);
            });
            tableView.getColumns().add(column);
        }
    }
    
    private List<Row> createRows() {
        List<Row> rows = new ArrayList<>();
        for (int i = 0; i < gau.getAllUsersList().size(); i++) {
            Row e = new Row();
            e.getCells().add(new Cell(gau.getAllUsersList().get(i).getUsername()));
            e.getCells().add(new Cell(String.valueOf(gau.getAllUsersList().get(i).getOnlineStatus())));
            e.getCells().add(new Cell(gau.getAllUsersList().get(i).getCurrentStatus().toString()));
            e.getCells().add(new Cell(gau.getAllUsersList().get(i).getTextStatus()));
            rows.add(e);
        }
        return rows;
    }

    private static class Row {
        private final List<Cell> list = new ArrayList<>();
        public List<Cell> getCells() {
            return list;
        }
    }

    private static class Cell {

        private final String value;

        public Cell(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return value;
        }
    }
    
    private class AddFriendButtonActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            Row tempRow = allUsers.getSelectionModel().getSelectedItem();
            if(tempRow != null){
                if(MyUtils.ShowConfirmationDialog("Friend", "Send Friend Request", "Send \"" + tempRow.getCells().get(0).toString() +"\" a friend request?")){
                    serverListener.addFriend(tempRow.getCells().get(0).toString());
                    MyUtils.ShowInformationDialog("Complete", "Friend Request Sent", "It's done.", "Friends are cool");
                    primaryStage.close();
                }
            }
        }
    }
    
    private ArrayList<String> initializeColumnNames(){
        ArrayList<String> temp = new ArrayList();
        temp.add("Friend Name");
        temp.add("Online");
        temp.add("Current Status");
        temp.add("Text Status");
        return temp;
    }
}
