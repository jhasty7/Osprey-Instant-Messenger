
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

public class PendingFriendRequestWindow extends Application {

    private Stage primaryStage;
    private BorderPane masterPane;
    private ArrayList<Friend> pendingFriends;
    private TableView<Row> tableView;
    private ArrayList<String> columnNames;
    private List<Row> rows;
    private HBox bottomPanel;
    private Button addFriendButton;
    private ServerListener serverListener;

    public PendingFriendRequestWindow(ArrayList<Friend> pendingFriends, ServerListener serverListener) {
        this.pendingFriends = pendingFriends;
        this.serverListener = serverListener;
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        masterPane = new BorderPane();
        masterPane.setPadding(new Insets(5, 5, 5, 5));

        bottomPanel = new HBox(5);
        bottomPanel.setPadding(new Insets(5, 5, 5, 5));
        
        tableView = new TableView();
        columnNames = initializeColumnNames();
        rows = createRows();
        makeColumns(tableView);
        tableView.getItems().addAll(rows);
        tableView.setEditable(false);
        
        addFriendButton = new Button("Accept Friend Request");
        addFriendButton.setOnAction(new AcceptFriendRequestActionListener());
        bottomPanel.getChildren().addAll(addFriendButton);
        
        masterPane.setCenter(tableView);
        masterPane.setBottom(bottomPanel);
        // javaui stage stuff
        Scene scene = new Scene(masterPane, 370, 401, Color.BLUE);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setTitle("Friend Requests");
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
        for (int i = 0; i < pendingFriends.size(); i++) {
            Row e = new Row();
            e.getCells().add(new Cell(pendingFriends.get(i).getUsername()));
            e.getCells().add(new Cell(String.valueOf(pendingFriends.get(i).getOnlineStatus())));
            e.getCells().add(new Cell(pendingFriends.get(i).getCurrentStatus().toString()));
            e.getCells().add(new Cell(pendingFriends.get(i).getTextStatus()));
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
    
    private class AcceptFriendRequestActionListener implements EventHandler<ActionEvent>{
        
        @Override
        public void handle(ActionEvent event){
            Row tempRow = tableView.getSelectionModel().getSelectedItem();
            if(tempRow != null){
                if(MyUtils.ShowConfirmationDialog("Friend", "Accept Friend Request", "Accept " + tempRow.getCells().get(0).toString() +"'s friend request?")){
                    serverListener.acceptFriendRequest(tempRow.getCells().get(0).toString());
                    MyUtils.ShowInformationDialog("Complete", "You have a new friend", "It's done.", "I'm not alone");
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
