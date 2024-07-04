package Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author berek
 */
public class CallPageController implements Initializable {

    Stage stage;
    Scene scene;
    @FXML
    private AnchorPane scenePane;
    @FXML
    private Button goToBasicInfoButton;

    @FXML
    public void basicInfoPage(ActionEvent event) throws IOException {
//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/BasicInfo.fxml"));
//        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//        scene = new Scene(root);
//        stage.setScene(scene);
//        stage.show();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/BasicInfo.fxml"));
            Scene basicInfoScene = new Scene(loader.load());
            BasicInfoController controller = loader.getController();
            controller.setPreviousScene(goToBasicInfoButton.getScene());

            Stage stage = (Stage) goToBasicInfoButton.getScene().getWindow();
            stage.setScene(basicInfoScene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void cancelRequest(ActionEvent event) throws IOException {

        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Cancel Request");
        alert.setHeaderText("You're about to cancel for the ambulance you requested!");
        alert.setContentText("Are you sure you want to cancel? ");

        if (alert.showAndWait().get() == ButtonType.OK) {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    public void trackPage(javafx.event.ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/TrackAmbulance.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb
    ) {
        // TODO
    }

}
