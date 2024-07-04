package Controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.SharedDataModel;

/**
 * FXML Controller class
 *
 * @author berek
 */
public class BasicInfoController implements Initializable {

    Stage stage;
    Scene scene;

    private Scene previousScene;
    @FXML
    private Button goBackButton;
    @FXML
    private Button signOut;
    @FXML
    private TextField firstNameP;
    @FXML
    private TextField phNumberP;
    @FXML
    private TextField address;
    @FXML
    private DatePicker birthday;
    @FXML
    private TextField lastNameP;
    @FXML
    private TextField fullNameE;
    @FXML
    private TextField phNumberE;
    @FXML
    private TextArea alergies;
    @FXML
    private TextArea chronicIllness;
    @FXML
    private Button submitBtn;
    @FXML
    private Label emergencyTypeLabel;
    @FXML
    private ComboBox<String> bloodType;
    @FXML
    private ComboBox<String> gender;
    @FXML
    private ComboBox<String> bloodTypeE;
    @FXML
    private ComboBox<String> relationwPatient;

    public void setPreviousScene(Scene scene) {
        this.previousScene = scene;
    }
    // DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // CONNECT DATABASE
    public Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            // LINKING THE DATABASE AND PUTING OUR USERNAME AND PASSWORD'S DATABASE
            Connection Connect = DriverManager.getConnection("jdbc:mysql://localhost/patient_general_info", "root", ""); // Root is our default username while NULL or EMPTY for DEFAULT PASSWOED
            return Connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    ObservableList<String> bloodtype = FXCollections.observableArrayList("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    ObservableList<String> Gender = FXCollections.observableArrayList("Male", "Female");
    ObservableList<String> relationwpatient = FXCollections.observableArrayList("Spouse", "Children", "Parent", "Sibling", "Relative", "Friend");

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Stage stage = (Stage) goBackButton.getScene().getWindow();
        stage.setScene(previousScene);
    }

    @FXML
    private void submit(ActionEvent event) {
        if (validateFields()) {
            try {
                // Connect to the database
                connect = connectDB();

                // Check if patient already exists
                if (patientExists()) {
                    // Show alert if patient already exists
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Warning");
                    alert.setHeaderText(null);
                    alert.setContentText("Patient with the provided information already exists.");
                    alert.showAndWait();
                    return; // Exit the method without inserting duplicate record
                }
                // Prepare the SQL statement for insertion
                String query = "INSERT INTO basicinfo (FirstnameP, LastnameP, Gender, Birthday, Address, PhonenumberP, BloodtypeP, Allergies, ChronicIllness, FullnameE, BloodtypeE, PhonenumberE, RelationE) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                prepare = connect.prepareStatement(query);

                // Set parameters from the JavaFX controls
                prepare.setString(1, firstNameP.getText());
                prepare.setString(2, lastNameP.getText());
                prepare.setString(3, gender.getValue());
                prepare.setString(4, formatDate(birthday.getValue()));
                prepare.setString(5, address.getText());
                prepare.setString(6, phNumberP.getText());
                prepare.setString(7, bloodType.getValue());
                prepare.setString(8, alergies.getText());
                prepare.setString(9, chronicIllness.getText());
                prepare.setString(10, fullNameE.getText());
                prepare.setString(11, bloodTypeE.getValue());
                prepare.setString(12, phNumberE.getText());
                prepare.setString(13, relationwPatient.getValue());

                // Execute the SQL statement
                prepare.executeUpdate();

                // Show confirmation alert
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Confirmation");
                alert.setHeaderText(null);
                alert.setContentText("Data has been sent to the Patient Admission System.");
                alert.showAndWait();

            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions, show error alert if necessary
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("An error occurred while sending data to the Patient Admission System.");
                alert.showAndWait();
            } finally {
                // Close resources
                try {
                    if (prepare != null) {
                        prepare.close();
                    }
                    if (connect != null) {
                        connect.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            // Show alert if any fields are empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please fill in all fields.");
            alert.showAndWait();
        }
    }

    private boolean patientExists() throws SQLException {
        // Prepare the SQL statement to check if the patient already exists
        String query = "SELECT COUNT(*) FROM basicinfo WHERE FirstnameP = ? AND LastnameP = ? AND Birthday = ?";
        prepare = connect.prepareStatement(query);
        prepare.setString(1, firstNameP.getText());
        prepare.setString(2, lastNameP.getText());
        prepare.setString(3, formatDate(birthday.getValue()));

        // Execute the query
        result = prepare.executeQuery();
        if (result.next()) {
            int count = result.getInt(1);
            return count > 0;
        }
        return false;
    }

    private String formatDate(LocalDate date) {
        // Format the date using DateTimeFormatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return date.format(formatter);
    }

    private boolean validateFields() {
        return !firstNameP.getText().isEmpty()
                && !lastNameP.getText().isEmpty()
                && !phNumberP.getText().isEmpty()
                && !address.getText().isEmpty()
                && birthday.getValue() != null
                && gender.getValue() != null
                && bloodType.getValue() != null
                && !alergies.getText().isEmpty()
                && !chronicIllness.getText().isEmpty()
                && !fullNameE.getText().isEmpty()
                && !phNumberE.getText().isEmpty()
                && relationwPatient.getValue() != null
                && bloodTypeE.getValue() != null;
    }

    @FXML
    private void signOut(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignInSignup.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bloodType.setItems(bloodtype);
        bloodTypeE.setItems(bloodtype);
        gender.setItems(Gender);
        relationwPatient.setItems(relationwpatient);
        
        // get user emergency type and set it to the label
        if (SharedDataModel.emergencylabel.equals("")) {
            emergencyTypeLabel.setText("Error");
        } else {
            emergencyTypeLabel.setText(SharedDataModel.emergencylabel);
        }
    }

}
