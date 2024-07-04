package Controllers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import models.SharedDataModel;

/**
 * FXML Controller class
 *
 * @author berek
 */
public class TrackAmbulanceController implements Initializable {

    Stage stage;
    Scene scene;
    @FXML
    private Label hospitalSelectedLabel;
    @FXML
    private WebView webview;
    @FXML
    private Button goToBasicInfoButton;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        WebEngine webEngine = webview.getEngine();
        try {
            File htmlFile = new File("C:/Users/berek/OneDrive/Documents/Java/App1.x/src/html/googlemaps.html");
            BufferedReader reader = new BufferedReader(new FileReader(htmlFile));
            StringBuilder htmlContent = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                htmlContent.append(line);
                htmlContent.append("\n");
            }
            reader.close();
            webEngine.loadContent(htmlContent.toString());
        } catch (IOException e) {
            System.err.println("Error loading HTML file: " + e.getMessage());
        }

        // get user hospital Selected and set it to the label
        if (SharedDataModel.hospitalselectedlabel.equals("")) {
            hospitalSelectedLabel.setText("Error");
        } else {
            hospitalSelectedLabel.setText(SharedDataModel.hospitalselectedlabel);
        }
    }

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
    private void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/CallPage.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}
