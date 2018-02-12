
import static java.lang.Character.isLetter;
import java.util.Optional;
import javafx.application.Application;
import javafx.application.Platform;
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
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
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

    private static GridPane loginPane;
    private static Pane masterPane;
    private static TextField username;
    private static TextField password;
    private static Stage primaryStage;
    private static Label usernameLabel;
    private static Label passwordLabel;
    private static Button login;
    private static Button exit;
    private static Image loginLogoI;
    private static ImageView loginLogo;
    private static Button registerButton;
    private static Alert passwordsDoMatch;
    private static Alert userAlreadyExistsAlert;
    private static Alert passwordsDoNotMatch;
    private static String registrationUserName;

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
        
        usernameLabel = new Label("Username");
        passwordLabel = new Label("Password");
        usernameLabel.getStyleClass().add("outline");
        passwordLabel.getStyleClass().add("outline");
        
        login = new Button("Login");
        exit = new Button("Exit");
        registerButton = new Button("Register");
        loginLogoI = new Image("white_osprey_blue.jpg");
        loginLogo = new ImageView(loginLogoI);
        this.primaryStage = primaryStage;
        loginLogo.setOpacity(10);
        //username.setDisable(true);
        login.setDisable(true);
        loginPane.add(usernameLabel, 0, 0);
        loginPane.add(username, 1, 0);
        loginPane.add(passwordLabel, 0, 1);
        loginPane.add(password, 1, 1);
        loginPane.add(login, 2, 0);
        loginPane.add(exit, 2, 1);
        loginPane.add(registerButton, 3, 0);
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
    }

    /* login validation */
    public void attemptToLogin() {
        lockLoginUI(true);
        
        if (username.getText() != null && password.getText() != null && !password.getText().matches("^\\s*$")) {

            /* open connection to the server */
            if (true) {
                //TODO: send username/password to server to verify login
            }
            else {
                /* connection to the server unavailable */
                loginFailed();
            }

        }
        password.clear();

    }

    /*
     * on login success
     */
    public static void loginSuccessful() {

        primaryStage.close();
        MainWindow mw = new MainWindow(username.getText());
        mw.start(primaryStage);

    }

    /*
     * on login failed
     */
    public void loginFailed() {
        lockLoginUI(false);
        password.clear();
        login.setDisable(true);
    }

    /*
     * clicking the register button
     */
    class RegisterButtonListener implements EventHandler<ActionEvent> {

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
                if(registrationUserName.equals("")){
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setHeaderText("No username");
                    alert.setContentText("Enter a username");
                    alert.setTitle("You suck");
                    alert.showAndWait();
                    handle(event);
                }
                else if (tempPass.equals(tempRetypePass)) {
                    // TODO: Query database for registration here
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
    class LoginButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            attemptToLogin();
        }

    }

    /*
     * clicking the exit button
     */
    class ExitButtonListener implements EventHandler<ActionEvent> {

        @Override
        public void handle(ActionEvent event) {
            exit();
        }

    }

    /*
     * pressing enter while password text field is focused
     */
    class PasswordKeyListener implements EventHandler<KeyEvent> {

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
    public void lockLoginUI(boolean lock) {
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
        Platform.exit();
        System.exit(0);
    }

}
