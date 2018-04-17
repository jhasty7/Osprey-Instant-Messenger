
import java.util.Optional;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;


public final class MyUtils {

    public static boolean ShowConfirmationDialog(String title, String header, String context) {
        boolean isOkay = false;
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        
        ButtonType buttonTypeOne = new ButtonType("Yes");
        ButtonType buttonTypeTwo = new ButtonType("No Way");
        
        alert.getButtonTypes().setAll(buttonTypeOne,buttonTypeTwo);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == buttonTypeOne) {
            isOkay = true;
        }
        return isOkay;
    }
    
    public static void ShowInformationDialog(String title, String header, String context, String buttonText){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(context);
        ButtonType buttontypeOne = new ButtonType(buttonText);
        alert.getButtonTypes().clear();
        alert.getButtonTypes().addAll(buttontypeOne);
        alert.showAndWait();
    }

}
