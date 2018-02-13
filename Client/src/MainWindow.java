import com.sun.javafx.stage.StageHelper;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

/**
 * class handles the GUI for the chat program handles
 */
public class MainWindow extends Application {
    
    private String username;
    private USER_STATUS myStatus = USER_STATUS.available;
    private Color myColor;
    private String myFontFamily;
    private Font myFont;
    private String myTextStatus;
    public static ArrayList<MessageWindow> myWindows;
    private ArrayList<Friend> friendsList;
    private FriendsList myFriendsList;
    private Friend mySelf;
    private boolean connected = false;
    private ServerListener serverListener;

    // javafx ui variables/objects
    private Stage primaryStage;
    private FlowPane flowPane;
    private HBox statusHBox;
    private Button availableButton;
    private Button awayButton;
    private Button busyButton;
    private MenuBar menuBar;
    private Menu fileMenu;
    private MenuItem blockListMenuItem;
    private MenuItem disconnectMenuItem;
    private MenuItem settingsMenuItem;
    private MenuItem exitMenuItem;
    private Menu optionsMenu;
    private Menu fontMenu;
    private RadioMenuItem timesNewRomanRadioMenuItem;
    private RadioMenuItem arialRadioMenuItem;
    private ToggleGroup fontGroup;
    private Menu colorMenu;
    private RadioMenuItem blackRadioMenuItem;
    private RadioMenuItem redRadioMenuItem;
    private ToggleGroup colorGroup;
    private TextField statusTextField;
    private ListView onlineFriendsListView;
    private Label onlineFriendsListLabel;
    private ObservableList<Friend> onlineFriendsObservableList;
    private ListView offlineFriendsListView;
    private Label offlineFriendsListLabel;
    private ObservableList<Friend> offlineFriendsObservableList;
    private HBox bottomHBox;
    private String mySelection;
    private Button addFriendButton;
    private Button addFriendSearchButton;
    private ArrayList<Friend> dummyStringList;
    private Rectangle statusRectangle;
    private ContextMenu onlineFriendContextMenu;
    private MenuItem removeOnlineFriend;
    private MenuItem openChat;
    private MenuItem blockOnlineFriend;
    private ContextMenu offlineFriendContextMenu;
    private MenuItem removeOfflineFriend;
    private MenuItem blockOfflineFriend;
    
    public MainWindow(String username){
        this.username = username;
        myColor = new Color(0, 0, 0, 0);
        myFontFamily = "Times New Roman";
        myFont = Font.font(myFontFamily, 12);
        friendsList = new ArrayList<>();
        dummyStringList = new ArrayList<>();
        dummyStringList.add(new Friend("", false, null, ""));
        mySelf = new Friend();
        mySelf = new Friend("butt", true, myStatus, myTextStatus);
        
        // establish lasting connection to the server
        serverListener = new ServerListener(username);
        new Thread(serverListener).start();
    }
    /*
     * Main GUI window
     **************************/
    @Override
    public void start(Stage primaryStage) {
        flowPane = new FlowPane();
        this.primaryStage = primaryStage;
        /* HBox */
        statusHBox = new HBox();
        statusHBox.prefWidthProperty().bind(flowPane.widthProperty());
        statusRectangle = new Rectangle(20, 20, 20, 20);
        statusRectangle.setFill(Color.LIGHTGREEN);
        statusRectangle.widthProperty().bind(flowPane.widthProperty());
        availableButton = new Button("Available");
        availableButton.setDisable(true);
        awayButton = new Button("Away");
        busyButton = new Button("Busy");
        availableButton.prefWidthProperty().bind(statusHBox.widthProperty());
        awayButton.prefWidthProperty().bind(statusHBox.widthProperty());
        busyButton.prefWidthProperty().bind(statusHBox.widthProperty());
        statusHBox.getChildren().addAll(availableButton, awayButton, busyButton);
        /* statusHBox button listeners */
        availableButton.setOnAction(e -> {
            myStatus = USER_STATUS.available;
            statusRectangle.setFill(Color.LIGHTGREEN);
            buttonDisabler();
        });
        awayButton.setOnAction(e -> {
            myStatus = USER_STATUS.away;
            statusRectangle.setFill(Color.YELLOW);
            buttonDisabler();
        });
        busyButton.setOnAction(e -> {
            myStatus = USER_STATUS.busy;
            statusRectangle.setFill(Color.RED);
            buttonDisabler();
        });

        /* menu bars and items */
        menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
        
        blockListMenuItem = new MenuItem("Block List");
        disconnectMenuItem = new MenuItem("Disconnect");
        settingsMenuItem = new MenuItem("Settings");
        exitMenuItem = new MenuItem("Exit");
        fileMenu = new Menu("File");
        fileMenu.getItems().addAll(blockListMenuItem,disconnectMenuItem, settingsMenuItem, exitMenuItem);
        //blockListMenuItem.setOnAction();
        
        fontMenu = new Menu("Font");
        fontGroup = new ToggleGroup();
        timesNewRomanRadioMenuItem = new RadioMenuItem("Times New Roman");
        timesNewRomanRadioMenuItem.setSelected(true);
        arialRadioMenuItem = new RadioMenuItem("Arial");
        timesNewRomanRadioMenuItem.setToggleGroup(fontGroup);
        arialRadioMenuItem.setToggleGroup(fontGroup);
        fontMenu.getItems().addAll(timesNewRomanRadioMenuItem, arialRadioMenuItem);

        colorMenu = new Menu("Color");
        colorGroup = new ToggleGroup();
        blackRadioMenuItem = new RadioMenuItem("Black");
        blackRadioMenuItem.setSelected(true);
        redRadioMenuItem = new RadioMenuItem("Red");
        blackRadioMenuItem.setToggleGroup(colorGroup);
        redRadioMenuItem.setToggleGroup(colorGroup);
        colorMenu.getItems().addAll(blackRadioMenuItem, redRadioMenuItem);

        optionsMenu = new Menu("Options");
        optionsMenu.getItems().addAll(fontMenu, colorMenu);

        menuBar.getMenus().addAll(fileMenu, optionsMenu);

        /* menu items action listeners */
        disconnectMenuItem.setOnAction(e -> {
            serverListener.disconnect();
            primaryStage.close();
            // this probably won't work
            Login newLogin = new Login();
            newLogin.start(primaryStage);
        });
        settingsMenuItem.setOnAction(new SettingsMenuItemActionListener());
        exitMenuItem.setOnAction(e -> {
            exit();
        });
        timesNewRomanRadioMenuItem.setOnAction(new FontRadioMenuItemListener());
        arialRadioMenuItem.setOnAction(new FontRadioMenuItemListener());
        blackRadioMenuItem.setOnAction(new ColorRadioMenuItemListener());
        redRadioMenuItem.setOnAction(new ColorRadioMenuItemListener());
        /* Pressing the X in the corner of the window */
        primaryStage.setOnCloseRequest(e -> {
            exit();
        });

        /* status text field */
        statusTextField = new TextField("Enter your status..");
        statusTextField.setFont(Font.font(myFontFamily, FontPosture.ITALIC, 12));
        statusTextField.setFocusTraversable(false);
        statusTextField.prefWidthProperty().bind(flowPane.widthProperty());
        statusTextField.focusedProperty().addListener(new StatusTextFieldListener());
        statusTextField.setOnKeyPressed(new StatusTextFieldKeyListener());

        /* online friends list */
        onlineFriendsListLabel = new Label("Online Friends");
        onlineFriendsListLabel.prefWidthProperty().bind(flowPane.widthProperty());
        onlineFriendsObservableList = FXCollections.observableArrayList(dummyStringList);
        onlineFriendsListView = new ListView(onlineFriendsObservableList);
        onlineFriendsListView.setDisable(true);
        onlineFriendsListView.prefWidthProperty().bind(flowPane.widthProperty());
        onlineFriendsListView.setPrefHeight(150);
        onlineFriendsListView.setMaxHeight(250);
        /* online friends list context menu */
        onlineFriendContextMenu = new ContextMenu();
        onlineFriendsListView.setContextMenu(onlineFriendContextMenu);
        openChat = new MenuItem("Open Chat");
        removeOnlineFriend = new MenuItem("Remove Friend");
        blockOnlineFriend = new MenuItem("Block Friend");
        onlineFriendContextMenu.getItems().addAll(openChat, removeOnlineFriend, blockOnlineFriend);
        openChat.setOnAction(new OpenChatContextMenuListener());
        removeOnlineFriend.setOnAction(new RemoveOnlineFriendContextMenuListener());
        blockOnlineFriend.setOnAction(new BlockOnlineFriendContextMenuListener());
        /*online friends list listeners */
        onlineFriendsListView.setCellFactory(new OnlineFriendsListFormatCell());
        onlineFriendsListView.setOnMouseClicked(new OnlineFriendsListMouseClickListener());
        onlineFriendsListView.setOnKeyPressed(new OnlineFriendsListKeyListener());
        /* offline friends list */
        offlineFriendsListLabel = new Label("Offline Friends");
        offlineFriendsListLabel.prefWidthProperty().bind(flowPane.widthProperty());
        offlineFriendsObservableList = FXCollections.observableArrayList(dummyStringList);
        offlineFriendsListView = new ListView(offlineFriendsObservableList);
        offlineFriendsListView.setDisable(true);
        offlineFriendsListView.prefWidthProperty().bind(flowPane.widthProperty());
        offlineFriendsListView.setPrefHeight(150);
        offlineFriendsListView.setMaxHeight(250);
        offlineFriendContextMenu = new ContextMenu();
        offlineFriendsListView.setContextMenu(offlineFriendContextMenu);
        removeOfflineFriend = new MenuItem("Remove Friend");
        blockOfflineFriend = new MenuItem("Block Friend");
        offlineFriendContextMenu.getItems().addAll(removeOfflineFriend, blockOfflineFriend);
        removeOfflineFriend.setOnAction(new RemoveOfflineFriendContextMenuListener());
        blockOfflineFriend.setOnAction(new BlockOfflineFriendContextMenuListener());
        /* offline friends list listeners */
        offlineFriendsListView.setCellFactory(new OfflineFriendsListFormatCell());
        /* bottum HBox */
        bottomHBox = new HBox();
        bottomHBox.prefWidthProperty().bind(flowPane.widthProperty());
        addFriendButton = new Button("Add Friend");
        addFriendButton.setOnAction(new AddFriendButtonListener());
        addFriendSearchButton = new Button("Search Friend");
        //addFriendSearchButton.setOnAction();
        bottomHBox.getChildren().add(addFriendButton);
        bottomHBox.getChildren().add(addFriendSearchButton);
        primaryStage.setOnCloseRequest(new clickXToClose());
        // TODO:
        // figure out what to put at the bottombox, time? maybe?
        // 1. time elapsed in online session
        /* add nodes to the flow pane 
        /* it is important in the order they are added */
        flowPane.getChildren().add(menuBar);
        flowPane.getChildren().add(statusRectangle);
        flowPane.getChildren().add(statusHBox);
        flowPane.getChildren().add(statusTextField);
        flowPane.getChildren().add(onlineFriendsListLabel);
        flowPane.getChildren().add(onlineFriendsListView);
        flowPane.getChildren().add(offlineFriendsListLabel);
        flowPane.getChildren().add(offlineFriendsListView);
        flowPane.getChildren().add(bottomHBox);
        /* GUI dimensions */
        Scene scene = new Scene(flowPane, 300, 570, Color.WHITE);
        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setResizable(true);
        primaryStage.setTitle("UNF Instant Messenger");
        primaryStage.setScene(scene);
        primaryStage.show();
        onlineFriendsListLabel.requestFocus();
        /* 
         * END GUI
        *****************/

    }

    /*
     * disables status buttons accordingly
     */
    private void buttonDisabler() {
        serverListener.updateUserStatus(myStatus);
        switch (myStatus) {
            case available:
                availableButton.setDisable(true);
                awayButton.setDisable(false);
                busyButton.setDisable(false);
                onlineFriendsListLabel.requestFocus();
                break;
            case away:
                availableButton.setDisable(false);
                awayButton.setDisable(true);
                busyButton.setDisable(false);
                onlineFriendsListLabel.requestFocus();
                break;
            case busy:
                availableButton.setDisable(false);
                awayButton.setDisable(false);
                busyButton.setDisable(true);
                onlineFriendsListLabel.requestFocus();
                break;
            default:
        }

    }
    
    

    /*
     * exiting the program
     */
    private void exit() {
        serverListener.disconnect();
        closeAllWindows();
        primaryStage.close();
        //Platform.exit();
        //System.exit(0);
    }

    /*
     * close all chat windows
     */
    private void closeAllWindows() {
        
    }

    /*
    * update friends list gui after changes
     */
    private void updateFriendsListGUI() {

        if (!myFriendsList.getOnlineFriends().isEmpty()) {
            if (!myFriendsList.getOnlineFriends().get(0).getUserName().equals("")) {
                onlineFriendsObservableList
                        = FXCollections.observableArrayList(myFriendsList.getOnlineFriends());
                onlineFriendsListView.setItems(onlineFriendsObservableList);
                onlineFriendsListView.setDisable(false);
            }

            else {
                onlineFriendsObservableList
                        = FXCollections.observableArrayList(dummyStringList);
                onlineFriendsListView.setItems(onlineFriendsObservableList);
                onlineFriendsListView.setDisable(true);
            }
        }
        if (!myFriendsList.getOfflineFriends().isEmpty()) {
            if (!myFriendsList.getOfflineFriends().get(0).getUserName().equals("")) {
                offlineFriendsObservableList
                        = FXCollections.observableArrayList(myFriendsList.getOfflineFriends());
                offlineFriendsListView.setItems(offlineFriendsObservableList);
                offlineFriendsListView.setDisable(false);
            }

            else {
                offlineFriendsObservableList
                        = FXCollections.observableArrayList(dummyStringList);
                offlineFriendsListView.setItems(offlineFriendsObservableList);
                offlineFriendsListView.setDisable(true);
            }

        }
    }
    
    /**
     * javafx ui component action listeners @@@
     */
    /*
     * online friends list mouse click listener
     */
    private class OnlineFriendsListMouseClickListener implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                openNewChatWindow();
            }
            else if (event.getButton() == MouseButton.SECONDARY) {

            }
        }

    }

    /*
     * online friends list key listener
     */
    private class OnlineFriendsListKeyListener implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                openNewChatWindow();
            }
        }

    }

    /*
     * online friends list format cell for style
     */
    private class OnlineFriendsListFormatCell extends ListCell<Friend> implements Callback<ListView<Friend>, ListCell<Friend>> {

        @Override
        public ListCell<Friend> call(ListView<Friend> list) {
            return new OnlineFriendsListFormatCell();
        }

        @Override
        protected void updateItem(Friend item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                if (item.getUserName().equals("")) {
                    setText("");
                    setDisable(true);
                }
                else {
                    setText(item.getUserName());
                    setDisable(false);
                }
            }
            else {
                setText("");
                setDisable(true);
            }

        }

    }

    /*
     * offline friends list format cell for style
     */
    private class OfflineFriendsListFormatCell extends ListCell<Friend> implements Callback<ListView<Friend>, ListCell<Friend>> {

        @Override
        public ListCell<Friend> call(ListView<Friend> list) {
            return new OfflineFriendsListFormatCell();
        }

        @Override
        protected void updateItem(Friend item, boolean empty) {
            super.updateItem(item, empty);

            if (item != null) {
                if (item.getUserName().equals("")) {
                    setText("");
                    setDisable(true);
                }
                else {
                    setText(item.getUserName());
                    setDisable(false);
                }
            }
            else {
                setText("");
                setDisable(true);
            }

        }

    }

    /*
     * opens a new chat window after various checks
     */
    private void openNewChatWindow() {
        if (onlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
            mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUserName();
            if (StageHelper.getStages().size() == 1) {
                javafx.application.Platform.runLater(() -> {
                    Stage newStage = new Stage();
                    MessageWindow newWindow = new MessageWindow(mySelection);
                    newWindow.start(newStage);
                });
            }
            else {
                /*
                 * Find a better way to do this,
                 * i couldn't think of another way -Josh
                */
                String tempStr, outerStr;

                Iterator<Stage> itemList = StageHelper.getStages().iterator();
                while (itemList.hasNext()) {
                    outerStr = (itemList.next()).getTitle();
                    if (outerStr.equals(mySelection)) {
                        break;
                    }
                    if (!itemList.hasNext()) {
                        javafx.application.Platform.runLater(() -> {
                            Stage newStage = new Stage();
                            MessageWindow newWindow = new MessageWindow(mySelection);
                            newWindow.start(newStage);
                        });
                    }
                }
            }
        }
    }

    /*
     * event handler for the font group menu item
     */
    private class FontRadioMenuItemListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            RadioMenuItem temp = (RadioMenuItem) event.getSource();
            myFontFamily = temp.getText();
            switch (myFontFamily) {
                case "Times New Roman":
                    myFont = Font.font(myFontFamily, 12);
                    break;
                case "Arial":
                    myFont = Font.font(myFontFamily, 12);
                    break;
                default:
            }
        }
    }

    /*
     * status text field gain/lose focus
     */
    private class StatusTextFieldListener implements ChangeListener<Boolean> {

        @Override
        public void changed(ObservableValue<? extends Boolean> observable,
                Boolean oldValue, Boolean newValue) {
            if (newValue) {
                statusTextField.clear();
                statusTextField.setFont(myFont);
            }
            else {
                if (statusTextField.getText() != null && !statusTextField.getText().matches("^\\s*$")
                        && statusTextField.getText().length() < 40 && statusTextField.getText().length() > 3) {
                    myTextStatus = statusTextField.getText();
                    statusTextField.setText(myTextStatus);
                    serverListener.updateStatusText(myTextStatus);
                }
                else {
                    if (myTextStatus != null) {
                        statusTextField.setText(myTextStatus);
                    }
                    else {
                        statusTextField.setText("Enter your status..");
                    }
                }

                statusTextField.setFont(Font.font(myFontFamily, FontPosture.ITALIC, 12));
            }
        }

    }

    /*
     * pressing enter on status text field
     */
    private class StatusTextFieldKeyListener implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume();
                onlineFriendsListLabel.requestFocus();
            }
        }

    }

    /*
     * when you click add friend
     */
    private class AddFriendButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            TextInputDialog typeFriend = new TextInputDialog();
            typeFriend.setTitle("Add Friend");
            typeFriend.setHeaderText("Add your friend");
            typeFriend.setContentText("Friend's name: ");

            Optional<String> result = typeFriend.showAndWait();
            result.ifPresent(name -> {
                serverListener.addFriend(result.get());
            });
        }
    }

    /*
     * when you click open chat on the online friends list view context menu
     */
    private class OpenChatContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            openNewChatWindow();
        }
    }

    /*
     * when you right click online friends and select remove friend
     */
    private class RemoveOnlineFriendContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (onlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
                mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUserName();

                Alert removeFriendAlert = new Alert(AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Remove friend");
                removeFriendAlert.setHeaderText("Remove " + mySelection + "?");
                removeFriendAlert.setContentText("You can always add them back");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    serverListener.removeFriend(mySelection);
                }
            }
        }

    }

    /*
     * when you right click online friends and select block friend
     */
    private class BlockOnlineFriendContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (onlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
                mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUserName();

                Alert removeFriendAlert = new Alert(AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Goodnight Sweet Prince");
                removeFriendAlert.setHeaderText("Block " + mySelection + "?");
                removeFriendAlert.setContentText("You can always unblock them");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    serverListener.blockFriend(mySelection);
                }
            }
        }

    }

    /*
     * right click on offline friends and select remove friend
     */
    private class RemoveOfflineFriendContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (offlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
                mySelection = ((Friend) offlineFriendsListView.getSelectionModel().getSelectedItem()).getUserName();

                Alert removeFriendAlert = new Alert(AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Remove friend");
                removeFriendAlert.setHeaderText("Remove " + mySelection + "?");
                removeFriendAlert.setContentText("You can always add them back");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    serverListener.removeFriend(mySelection);
                    
                }
            }
        }

    }

    /*
     * when you right click offline friends and select block friend
     */
    private class BlockOfflineFriendContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (offlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
                mySelection = ((Friend) offlineFriendsListView.getSelectionModel().getSelectedItem()).getUserName();

                Alert removeFriendAlert = new Alert(AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Goodnight Sweet Prince");
                removeFriendAlert.setHeaderText("Block " + mySelection + "?");
                removeFriendAlert.setContentText("You can always unblock them");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {

                    serverListener.blockFriend(mySelection);

                }
            }
        }

    }

    /*
     * event handler for the color group menu item
     * TODO: put this in the chat window; then create an options window
     */
    private class ColorRadioMenuItemListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            RadioMenuItem temp = (RadioMenuItem) event.getSource();
            switch (temp.getText()) {
                case "Black":
                    myColor = Color.BLACK;
                    break;
                case "Red":
                    myColor = Color.RED;
                    break;
                default:
            }
        }
    }
    
        /*
     * clicking the X button in the top right corner of the window
    */
    private class clickXToClose implements EventHandler<WindowEvent>{
        
        @Override
        public void handle(WindowEvent event){
            exit();
        }
        
    }
    
    /**
     * opens up the settings window
     */
    private class SettingsMenuItemActionListener implements EventHandler<ActionEvent>{
        @Override
        public void handle(ActionEvent event){
            Settings settings = new Settings();
            settings.start(new Stage());
        }
    }
    
}