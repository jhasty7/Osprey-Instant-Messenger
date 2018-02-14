
import java.io.IOException;
import static java.lang.Character.isLetter;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import static javafx.scene.control.Alert.AlertType.ERROR;
import static javafx.scene.control.Alert.AlertType.INFORMATION;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

public class Login extends Application {
    
    
    private GridPane loginPane;
    private Pane masterPane;
    private TextField username;
    private TextField password;
    private Stage primaryStage;
    private Label usernameLabel;
    private Label passwordLabel;
    private Button login;
    private Button exit;
    private Image loginLogoI;
    private ImageView loginLogo;
    private Button registerButton;
    private CheckBox rememberUsernamePassword;
    private Alert passwordsDoMatch;
    private Alert userAlreadyExistsAlert;
    private Alert passwordsDoNotMatch;
    private String registrationUserName;

    private ServerListener serverListener;

    @Override
    public void start(Stage primaryStage) {
        /*
         * Begin login UI
         */
        masterPane = new Pane();
        loginPane = new GridPane();
        username = new TextField();
        username.setPromptText("Click");
        password = new PasswordField();
        password.setPromptText("Password");
        
        //michelle tryin useless stuff
        loginPane.setPadding(new Insets(10, 10, 10, 10));
        loginPane.setHgap(3);
        //

        usernameLabel = new Label("Username");
        passwordLabel = new Label("Password");
        usernameLabel.getStyleClass().add("outline");
        passwordLabel.getStyleClass().add("outline");

        rememberUsernamePassword = new CheckBox("Remember");
        rememberUsernamePassword.setId("cb");
        
        login = new Button("Login");
        exit = new Button("Exit");
        registerButton = new Button("Register");
        loginLogoI = new Image("white_osprey_blue1.jpg");
        loginLogo = new ImageView(loginLogoI);
        this.primaryStage = primaryStage;
        loginLogo.setOpacity(10);
        login.setDisable(true);
        
        loginPane.add(usernameLabel, 0, 0);
        loginPane.add(username, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(password, 1, 1);
        loginPane.add(login, 2, 0);
        loginPane.add(exit, 2, 1);
        loginPane.add(registerButton, 3, 0);
        loginPane.add(rememberUsernamePassword,0,3);
        loginPane.setAlignment(Pos.CENTER_RIGHT);

        /* component listeners */
        exit.setOnAction(new ExitButtonListener());
        primaryStage.setOnCloseRequest(e -> {
            exit();
        });
        password.setOnKeyPressed(new PasswordKeyListener());
        login.setOnAction(new LoginButtonListener());
        registerButton.setOnAction(new RegisterButtonListener());

        masterPane.getChildren().addAll(loginLogo, loginPane);
        Scene scene = new Scene(masterPane, 395, 470, Color.BLUE);
        scene.getStylesheets().addAll(getClass().getResource("outline.css").toExternalForm());

        primaryStage.getIcons().add(new Image("unf_icon.png"));
        primaryStage.setResizable(false);
        primaryStage.setTitle("UNF Instant Messenger");
        primaryStage.setScene(scene);
        
        primaryStage.show();
        login.requestFocus();
        
        applyConfiguration();
    }
    
    private void applyConfiguration(){
        rememberUsernamePassword.setSelected(Config.cfg.isRememberUsernamePassword());
        if(rememberUsernamePassword.isSelected()){
            if(Config.cfg.getUsername() != null && Config.cfg.getPassword() != null){
                username.setText(Config.cfg.getUsername());
                password.setText(Config.cfg.getPassword());
                if(Config.cfg.isAutoLogin()){
                    attemptToLogin();
                }
            }
        }
    }

    /* login validation */
    private void attemptToLogin() {
        lockLoginUI(true);

        if (!username.getText().equals("") && username.getText() != null && password.getText() != null && !password.getText().matches("^\\s*$")) {

            serverListener = new ServerListener();
            // connect to the server and send login packet
            try {
                processLoginOrRegisterResponse(serverListener.loginOrRegister(username.getText(), password.getText(), true));
            }
            catch (IOException ex) {
                DeveloperWindow.displayMessage("Error: at login class attemptToLogin method");
                DeveloperWindow.displayMessage(ex.toString());
                processLoginOrRegisterResponse(2);
            }

        }

    }

    private void processLoginOrRegisterResponse(int response) {
        Alert alert;
        switch (response) {
            // server sent you gibberish
            case 0:
                alert = new Alert(ERROR);
                alert.setTitle("Hmmm");
                alert.setHeaderText("Unreadble packet");
                alert.setContentText("Something got corrupted on its way here");
                alert.showAndWait();
                break;

            // login successful
            case 1:
                Config.cfg.setRememberUsernamePassword(rememberUsernamePassword.isSelected());
                Config.cfg.setUsername(username.getText());
                Config.cfg.setPassword(password.getText());
                serverListener = null;
                primaryStage.close();
                MainWindow mw = new MainWindow(username.getText());
                mw.start(primaryStage);
                break;

            // login failed; username/password not correct
            case 2:
                password.clear();
                username.clear();
                
                // this checks if the user just newly clicked "Remember" and failed the login.
                // it unchecks remember.
                if(!Config.cfg.isRememberUsernamePassword() && rememberUsernamePassword.isSelected()){
                    rememberUsernamePassword.setSelected(false);
                }
                // god damn wtf did i do
                new Thread(new SleepForLoginEnable()).start();
                alert = new Alert(ERROR);
                alert.setTitle("You suck");
                alert.setHeaderText("Login didn't work");
                alert.setContentText("Why? I don't know");
                alert.showAndWait();
                break;

            // registration succcessful
            case 3:
                username.setText(registrationUserName);
                alert = new Alert(INFORMATION);
                alert.setTitle("Register Successful");
                alert.setHeaderText("You did it");
                alert.setContentText("You can now login");
                alert.showAndWait();
                break;

            // registration failed; not sure why
            case 4:
                alert = new Alert(ERROR);
                alert.setTitle("You suck");
                alert.setHeaderText("Registration failed");
                alert.setContentText("this really shouldn't fail");
                alert.showAndWait();
                break;

            // server didn't send the right packet
            case 5:
                alert = new Alert(ERROR);
                alert.setTitle("Well..");
                alert.setHeaderText("Server sent a packet you can read");
                alert.setContentText("it just wasn't the right one");
                alert.showAndWait();
                break;
        }
    }

    /*
     * clicking the register button
     */
    private class RegisterButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            password.clear();
            /*
             username registration
             */
            TextField username = new TextField();
            UsernameFieldKeyListener usernameListener = new UsernameFieldKeyListener();
            usernameListener.setTextFieldReference(username);
            username.focusedProperty().addListener(usernameListener);

            /*
             password registration
             */
            passwordsDoMatch = new Alert(AlertType.INFORMATION);
            passwordsDoMatch.setTitle("Success");
            passwordsDoMatch.setHeaderText("Registration Complete");
            passwordsDoMatch.setContentText("email josh is you forget your password");
            userAlreadyExistsAlert = new Alert(AlertType.INFORMATION);
            userAlreadyExistsAlert.setTitle("Success?");
            userAlreadyExistsAlert.setHeaderText("Username is taken");
            userAlreadyExistsAlert.setContentText("that's probably what it is");
            passwordsDoNotMatch = new Alert(AlertType.ERROR);
            passwordsDoNotMatch.setTitle("Failure");
            passwordsDoNotMatch.setHeaderText("Passwords do not match");
            passwordsDoNotMatch.setContentText("give it another shot");
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Register");
            dialog.setHeaderText("Type in a unique username and complex password");

            ButtonType loginButtonType = new ButtonType("Regiser", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            PasswordField password = new PasswordField();
            password.setPromptText("Password");
            PasswordFieldKeyListener passwordListener = new PasswordFieldKeyListener();
            passwordListener.setTextFieldReference(password);
            password.focusedProperty().addListener(passwordListener);

            PasswordField retypePassword = new PasswordField();
            retypePassword.setPromptText("Retype Password");

            grid.add(new Label("Username:"), 0, 0);
            grid.add(username, 1, 0);
            grid.add(new Label("Password:"), 0, 1);
            grid.add(password, 1, 1);
            grid.add(new Label("Re-Type Password:"), 0, 2);
            grid.add(retypePassword, 1, 2);

            Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
            loginButton.setDisable(true);

            password.textProperty().addListener((observable, oldValue, newValue) -> {
                loginButton.setDisable(newValue.trim().isEmpty());
            });

            dialog.getDialogPane().setContent(grid);
            username.requestFocus();

            // Convert the result to a password-retypepassword-pair when the register button is clicked.
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == loginButtonType) {
                    return new Pair<>(password.getText(), retypePassword.getText());
                }
                return null;
            });

            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(usernamePassword -> {
                registrationUserName = username.getText();
                String tempPass;
                String tempRetypePass;
                tempPass = usernamePassword.getKey();
                tempRetypePass = usernamePassword.getValue();
                if (registrationUserName.equals("")) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("No username");
                    alert.setContentText("Enter a username");
                    alert.setTitle("You suck");
                    alert.showAndWait();
                    handle(event);
                }
                else if (tempPass.equals(tempRetypePass)) {
                    serverListener = new ServerListener();
                    try {
                        processLoginOrRegisterResponse(serverListener.loginOrRegister(registrationUserName, tempPass, false));
                    }
                    catch (IOException ex) {
                        processLoginOrRegisterResponse(4);
                    }
                }
                else {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("Passwords didn't match");
                    alert.setContentText("try again");
                    alert.setTitle("Don't give up");
                    alert.showAndWait();
                    handle(event);
                }
            });

        }

    }

    /*
     * password registration input validation
     */
    private class UsernameFieldKeyListener implements ChangeListener<Boolean> {

        private TextField username;

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
            }
            else {
                if (!username.getText().equals("")) {
                    String temp = username.getText();
                    username.setText(temp.replaceAll(" ", ""));
                    if (username.getText().length() > 20) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Too long");
                        alert.setContentText("username is way too long");
                        alert.setTitle("Bad");
                        alert.showAndWait();
                        username.setText("");
                        username.requestFocus();
                    }
                    else if (!isLetter(username.getText().charAt(0))) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Start with letters");
                        alert.setContentText("Thanks");
                        alert.setTitle("&%^$");
                        alert.showAndWait();
                        username.setText("");
                        username.requestFocus();
                    }
                    else if (username.getText().length() < 4) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Make the username longer");
                        alert.setContentText("It's too short");
                        alert.setTitle("Longer");
                        alert.showAndWait();
                        username.setText("");
                        username.requestFocus();
                    }
                }
            }
        }

        public void setTextFieldReference(TextField tf) {
            username = tf;
        }

    }

    /*
     * password registration input validation
     */
    private class PasswordFieldKeyListener implements ChangeListener<Boolean> {

        private TextField password;

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (newValue) {
            }
            else {
                if (!password.getText().equals("")) {
                    String temp = password.getText();
                    password.setText(temp.trim());
                    if (password.getText().length() > 20) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Too long");
                        alert.setContentText("password is way too long");
                        alert.setTitle("Bad");
                        alert.showAndWait();
                        password.setText("");
                        password.requestFocus();
                    }
                    else if (!isLetter(password.getText().charAt(0))) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Start with letters");
                        alert.setContentText("Thanks");
                        alert.setTitle("&%^$");
                        alert.showAndWait();
                        password.setText("");
                        password.requestFocus();
                    }
                    else if (password.getText().length() < 4) {
                        Alert alert = new Alert(AlertType.ERROR);
                        alert.setHeaderText("Make it longer");
                        alert.setContentText("password is too short");
                        alert.setTitle("Longer");
                        alert.showAndWait();
                        password.setText("");
                        password.requestFocus();
                    }
                }
            }
        }

        public void setTextFieldReference(TextField tf) {
            password = tf;
        }

    }

    /*
     * clicking the login button
     */
    private class LoginButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            attemptToLogin();
        }

    }

    /*
     * clicking the exit button
     */
    private class ExitButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            exit();
        }

    }

    /*
     * pressing enter while password text field is focused
     */
    private class PasswordKeyListener implements EventHandler<KeyEvent> {

        @Override
        public void handle(KeyEvent event) {
            if (password.getText() != null && !password.getText().matches("^\\s*$")) {
                login.setDisable(false);
                if (event.getCode() == KeyCode.ENTER) {
                    event.consume();
                    attemptToLogin();
                }
            }
            else {
                login.setDisable(true);
            }
        }

    }

    /* locks and unlocks sensitive UI components */
    private void lockLoginUI(boolean lock) {
        if (lock) {
            password.setDisable(true);
            username.setDisable(true);
            login.setDisable(true);
            registerButton.setDisable(true);
            exit.setDisable(true);
        }
        else {
            password.setDisable(false);
            username.setDisable(false);
            login.setDisable(false);
            registerButton.setDisable(false);
            exit.setDisable(false);
        }
    }

    private void exit() {
        primaryStage.close();
        //Platform.exit();
        //System.exit(0);
    }

    // this is the ugliest thing ive ever done
    private class SleepForLoginEnable implements Runnable {

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
                javafx.application.Platform.runLater(() -> {
                    lockLoginUI(false);
                });
            }
            catch (InterruptedException ex) {
                DeveloperWindow.displayMessage("Error: in login at SleepForLoginEnable");
            }
        }
    }
}
