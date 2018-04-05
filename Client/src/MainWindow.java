
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
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
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

/**
 * class handles the GUI for the chat program handles
 */
public class MainWindow extends Application {

    // very important variables/objects
    private FriendsList myFriendsList;
    public static List<MessageWindow> myMessageWindows;
    private ServerListener serverListener;
    public static String username;

    // extra variables/objects for niche functionality
    private String myTextStatus;

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

    public MainWindow(String usernameTemp) {
        dummyStringList = new ArrayList<>();
        dummyStringList.add(new Friend("", false, null, ""));
        myMessageWindows = new CopyOnWriteArrayList<>();
        username = usernameTemp;
        // establish lasting connection to the server
        serverListener = new ServerListener(this);
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
            currentStatusChanger(eCURRENT_STATUS.available, true);
        });
        awayButton.setOnAction(e -> {
            currentStatusChanger(eCURRENT_STATUS.away, true);
        });
        busyButton.setOnAction(e -> {
            currentStatusChanger(eCURRENT_STATUS.busy, true);
        });

        /* menu bars and items */
        menuBar = new MenuBar();
        menuBar.prefWidthProperty().bind(primaryStage.widthProperty());

        blockListMenuItem = new MenuItem("Block List");
        disconnectMenuItem = new MenuItem("Disconnect");
        settingsMenuItem = new MenuItem("Settings");
        exitMenuItem = new MenuItem("Exit");
        fileMenu = new Menu("File");
        fileMenu.getItems().addAll(blockListMenuItem, disconnectMenuItem, settingsMenuItem, exitMenuItem);
        //blockListMenuItem.setOnAction();

        menuBar.getMenus().addAll(fileMenu);

        /* menu items action listeners */
        disconnectMenuItem.setOnAction(new DisconnectMenuItemActionListener());
        settingsMenuItem.setOnAction(new SettingsMenuItemActionListener());
        exitMenuItem.setOnAction(e -> {
            exit();
        });

        /* Pressing the X in the corner of the window */
        primaryStage.setOnCloseRequest(e -> {
            exit();
        });

        /* status text field */
        statusTextField = new TextField("Enter your status..");
        statusTextField.setFont(Font.font("Times New Roman", FontWeight.LIGHT, FontPosture.REGULAR, 10));
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
     * changes status color
     */
    private void currentStatusChanger(eCURRENT_STATUS currentStatus, boolean sendToServer) {
        switch (currentStatus) {
            case available:
                statusRectangle.setFill(Color.LIGHTGREEN);
                availableButton.setDisable(true);
                awayButton.setDisable(false);
                busyButton.setDisable(false);
                onlineFriendsListLabel.requestFocus();
                break;
            case away:
                statusRectangle.setFill(Color.YELLOW);
                availableButton.setDisable(false);
                awayButton.setDisable(true);
                busyButton.setDisable(false);
                onlineFriendsListLabel.requestFocus();
                break;
            case busy:
                statusRectangle.setFill(Color.RED);
                availableButton.setDisable(false);
                awayButton.setDisable(false);
                busyButton.setDisable(true);
                onlineFriendsListLabel.requestFocus();
                break;
            default:
        }
        if (sendToServer) {
            serverListener.updateCurrentStatus(currentStatus);
        }
    }

    /*
     * exiting the program
     */
    private void exit() {
        closeAllWindows();
        serverListener.disconnect();
        primaryStage.close();
        serverListener = null;
        Platform.exit();
        System.exit(0);
    }

    private void disconnectFromServer() {
        closeAllWindows();
        serverListener.disconnect();
        serverListener = null;
        primaryStage.close();
        Login login = new Login();
        login.start(new Stage());
    }

    /*
     * close all chat windows
     */
    private void closeAllWindows() {
        myMessageWindows.forEach((temp) -> {
            temp.closeChatWindow();
        });
    }

    /*
    * update friends list gui after changes
     */
    private void updateFriendsListGUI() {

        if (!myFriendsList.getOnlineFriends().isEmpty()) {
            if (!myFriendsList.getOnlineFriends().get(0).getUsername().equals("")) {
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
            if (!myFriendsList.getOfflineFriends().get(0).getUsername().equals("")) {
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
                outgoingMessage();
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
                outgoingMessage();
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
                setText(item.getUsername());
                setDisable(false);
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
                setText(item.getUsername());
                setDisable(false);
            }
            else {
                setText("");
                setDisable(true);
            }

        }

    }

    /*
     * outgoing message to open message window
     */
    private void outgoingMessage() {
        boolean foundWindow = false;
        if (onlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
            mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUsername();
            if (!myMessageWindows.isEmpty()) {

                // does window exist already
                for (MessageWindow window : myMessageWindows) {
                    if (window.getFriendName().equals(mySelection)) {
                        // if you found a window, this means the user is clicking
                        // to open an already opened chat window. just request focus.
                        window.getStage().requestFocus();
                        foundWindow = true;
                    }
                }
                // if you didn't find the window create one
                if (!foundWindow) {
                    MessageWindow newWindow = new MessageWindow(mySelection, serverListener);
                    myMessageWindows.add(newWindow);
                    newWindow.start(new Stage());
                }
            }
            // this list was empty
            else {
                MessageWindow newWindow = new MessageWindow(mySelection, serverListener);
                myMessageWindows.add(newWindow);
                newWindow.start(new Stage());
            }
        }
    }

    /*
     * incoming message to open message window
     */
    public void incomingMessage(MessagePacket textMessage) {
        javafx.application.Platform.runLater(() -> {
            String friendName = textMessage.getComingFrom();
            boolean foundWindow = false;

            if (!myMessageWindows.isEmpty()) {

                // does window exist already
                for (MessageWindow window : myMessageWindows) {
                    if (window.getFriendName().equals(friendName)) {
                        // find window, display message, and request focus
                        window.displayIncomingText(textMessage);
                        window.getStage().requestFocus();
                        foundWindow = true;
                    }
                }
                // if window wasn't found, open up new window
                if (!foundWindow) {
                    MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                    myMessageWindows.add(newWindow);
                    newWindow.start(new Stage());
                    newWindow.displayIncomingText(textMessage);
                }
            }
            // if the list was empty
            else {
                MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                myMessageWindows.add(newWindow);
                newWindow.start(new Stage());
                newWindow.displayIncomingText(textMessage);
            }
        });
    }

    /*
     * overloaded incomingMessage with ImagePacket
     */
    public void incomingMessage(ImagePacket ip) {
        javafx.application.Platform.runLater(() -> {
            String friendName = ip.getComingFrom();
            boolean foundWindow = false;

            if (!myMessageWindows.isEmpty()) {

                // does window exist already
                for (MessageWindow window : myMessageWindows) {
                    if (window.getFriendName().equals(friendName)) {
                        // find window, display message, and request focus
                        window.displayIncomingImage(ip);
                        window.getStage().requestFocus();
                        foundWindow = true;
                    }
                }
                // if window wasn't found, open up new window
                if (!foundWindow) {
                    MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                    myMessageWindows.add(newWindow);
                    newWindow.start(new Stage());
                    newWindow.displayIncomingImage(ip);
                }
            }
            // if the list was empty
            else {
                MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                myMessageWindows.add(newWindow);
                newWindow.start(new Stage());
                newWindow.displayIncomingImage(ip);
            }
        });
    }

    public void incomingMessage(SendFilePacket sfp) {
        javafx.application.Platform.runLater(() -> {
            String friendName = sfp.getComingFrom();
            boolean foundWindow = false;

            if (!myMessageWindows.isEmpty()) {

                // does window exist already
                for (MessageWindow window : myMessageWindows) {
                    if (window.getFriendName().equals(friendName)) {
                        // find window, display message, and request focus
                        window.displayIncomingFileDownloadPrompt(sfp);
                        window.getStage().requestFocus();
                        foundWindow = true;
                    }
                }
                // if window wasn't found, open up new window
                if (!foundWindow) {
                    MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                    myMessageWindows.add(newWindow);
                    newWindow.start(new Stage());
                    newWindow.displayIncomingFileDownloadPrompt(sfp);
                }
            }
            // if the list was empty
            else {
                MessageWindow newWindow = new MessageWindow(friendName, serverListener);
                myMessageWindows.add(newWindow);
                newWindow.start(new Stage());
                newWindow.displayIncomingFileDownloadPrompt(sfp);
            }
        });
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
            }
            else {
                if (statusTextField.getText() != null && !statusTextField.getText().matches("^\\s*$")
                        && statusTextField.getText().length() < 40 && statusTextField.getText().length() > 3) {
                    myTextStatus = statusTextField.getText();
                    statusTextField.setText(myTextStatus);

                    serverListener.updateTextStatus(myTextStatus);
                }
                else {
                    if (myTextStatus != null) {
                        statusTextField.setText(myTextStatus);
                    }
                    else {
                        statusTextField.setText("Enter your status..");
                    }
                }

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
    private class friendRequestsButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            
        }
    }

    /*
     * when you click open chat on the online friends list view context menu
     */
    private class OpenChatContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            outgoingMessage();
        }
    }

    /*
     * when you right click online friends and select remove friend
     */
    private class RemoveOnlineFriendContextMenuListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            if (onlineFriendsListView.getSelectionModel().getSelectedItem() != null) {
                mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUsername();

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
                mySelection = ((Friend) onlineFriendsListView.getSelectionModel().getSelectedItem()).getUsername();

                Alert removeFriendAlert = new Alert(AlertType.CONFIRMATION);
                removeFriendAlert.setTitle("Goodnight Sweet Prince");
                removeFriendAlert.setHeaderText("Block " + mySelection + "?");
                removeFriendAlert.setContentText("You can always unblock them");
                Optional<ButtonType> result = removeFriendAlert.showAndWait();
                if (result.get() == ButtonType.OK) {
                    for(MessageWindow w : myMessageWindows){
                        if(w.getFriendName().equals(mySelection)){
                            w.closeChatWindow();
                        }
                    }
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
                mySelection = ((Friend) offlineFriendsListView.getSelectionModel().getSelectedItem()).getUsername();

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
                mySelection = ((Friend) offlineFriendsListView.getSelectionModel().getSelectedItem()).getUsername();

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
     * when you click disconnect in the menu items
     */
    private class DisconnectMenuItemActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            disconnectFromServer();
        }
    }

    /**
     * opens up the settings window
     */
    private class SettingsMenuItemActionListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            Settings settings = new Settings();
            settings.start(new Stage());
        }
    }

    public void setFriendsList(FriendsList myFriendsList) {
        javafx.application.Platform.runLater(() -> {
            this.myFriendsList = myFriendsList;
            updateFriendsListGUI();
        });
    }

    public void processFriend(Friend friend) {
        javafx.application.Platform.runLater(() -> {
            if (friend.isUpdate()) {
                this.myFriendsList.updateFriend(friend);
                updateFriendsListGUI();
            }
            else if (friend.isYourself()) {
                currentStatusChanger(friend.getCurrentStatus(), false);
                myTextStatus = friend.getTextStatus();
                statusTextField.setText(myTextStatus);
            }
            else if (friend.isAdd()) {
                this.myFriendsList.addFriend(friend);
                updateFriendsListGUI();
            }
            else if (friend.isBlock()) {

            }
            else if (friend.isRemove()) {

            }
        });
    }
    
    public void processBeingBlocked(String personThatsBlockingYou){
        javafx.application.Platform.runLater(() -> {
            for(MessageWindow w : myMessageWindows){
                if(w.getFriendName().equals(personThatsBlockingYou)){
                    w.closeChatWindow();
                }
            }
        });
    }

    public void createAlertFromServer(boolean isSuccessful, String context) {
        javafx.application.Platform.runLater(() -> {
            Alert serverAlert;
            if (isSuccessful) {
                serverAlert = new Alert(AlertType.CONFIRMATION);
                serverAlert.setTitle("Success");
                serverAlert.setHeaderText(context);
                serverAlert.setContentText("It worked.");
                serverAlert.showAndWait();
            }
            else {
                serverAlert = new Alert(AlertType.ERROR);
                serverAlert.setTitle("Failed");
                serverAlert.setHeaderText(context);
                serverAlert.setContentText("It failed for some reason.");
                serverAlert.showAndWait();
            }
            updateFriendsListGUI();
        });
    }
}
