package Controllers;

import animatefx.animation.ZoomIn;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import models.SharedDataModel;

/**
 * FXML Controller class
 *
 * @author berek
 */
public class SignInController implements Initializable {

    Stage stage;
    Scene scene;

    @FXML
    private Pane signInPane;
    @FXML
    private Pane signUpPane;
    @FXML
    private StackPane stackPane;
    @FXML
    private ImageView btnBack;
    @FXML
    private TextField emailLabel;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button btnLogin;
    @FXML
    private Button btnSignup;

    @FXML
    private TextField signInEmailLabel;
    @FXML
    private PasswordField signInPasswordField;
    @FXML
    private Button getStartedBtn;

    // DATABASE TOOLS
    private Connection connect;
    private PreparedStatement prepare;
    private ResultSet result;

    // CONNECT DATABASE
    public Connection connectDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
// LINKING THE DATABASE AND PUTING OUR USERNAME AND PASSWORD'S DATABASE
            Connection Connect = DriverManager.getConnection("jdbc:mysql://localhost/autentication_system1", "root", ""); // Root is our default username while NULL or EMPTY for DEFAULT PASSWOED
            return Connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void logIn() {
        // Ensure connection is established
        connect = connectDB();
        
        String email = emailLabel.getText();
        String password = passwordField.getText();
        String sql = "SELECT * FROM userlist WHERE Email = ? AND Password = ?";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, email);
            prepare.setString(2, password);
            result = prepare.executeQuery();
            if (result.next()) {
                // Successful login
                SharedDataModel.text = emailLabel.getText();
                showHome();
                showConfirmationAlert("Login Successful", "Welcome back, " + email + "!");
            } else {
                // Failed login
                showAlert("Login Error", "Invalid email or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Database Error", "An error occurred while trying to log in");
        }
    }

    private void showConfirmationAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Load the image resource as an input stream
        try (InputStream inputStream = getClass().getResourceAsStream("/Resources/icons8_ok_60px_1.png")) {
            if (inputStream != null) {
                // Create an image from the input stream
                Image image = new Image(inputStream);
                // Create an image view with the image
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(48);
                imageView.setFitHeight(48);
                alert.setGraphic(imageView);
            } else {
                System.out.println("Image resource not found!");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Customize the buttons to show only OK button
        alert.getButtonTypes().setAll(ButtonType.OK);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showHome() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void signUp() {
        // Ensure connection is established
        connect = connectDB();

        String email = signInEmailLabel.getText();
        String password = signInPasswordField.getText();

        // Check if email and password fields are empty
        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Sign Up Error", "Please fill in all fields.");
            return; // Exit the method if fields are empty
        }
        // Check if email already exists in the database
        String checkEmailQuery = "SELECT * FROM userlist WHERE Email = ?";
        try {
            prepare = connect.prepareStatement(checkEmailQuery);
            prepare.setString(1, email);
            ResultSet resultSet = prepare.executeQuery();
            if (resultSet.next()) {
                showAlert("Sign Up Error", "Email already registered.");
                return; // Exit the method if email already exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Sign Up Error", "An error occurred while checking email availability.");
            return; // Exit the method if error occurs
        }
        // If email is not registered, proceed with user registration
        String sql = "INSERT INTO userlist (Email, Password) VALUES (?, ?)";
        try {
            prepare = connect.prepareStatement(sql);
            prepare.setString(1, email);
            prepare.setString(2, password);
            prepare.executeUpdate();
            showConfirmationAlert("Sign Up Success", "You have successfully signed up!");
            // Navigate to basic info page after successful sign up
            gotoHomePage();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Sign Up Error", "An error occurred while trying to sign up");
        }
    }

    private void gotoHomePage() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
            Stage stage = (Stage) ((Node) getStartedBtn).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void signupPane(ActionEvent event) {
        if (event.getSource().equals(btnSignup)) {
            new ZoomIn(signUpPane).play();
            signUpPane.toFront();
        }
    }

    @FXML
    private void gotoSignIn(MouseEvent event) {
        if (event.getSource().equals(btnBack)) {
            new ZoomIn(signInPane).play();
            signInPane.toFront();
        }
    }

    @FXML
    private void gotoHome(ActionEvent event) throws IOException {
        logIn();
    }

    @FXML
    private void gotoHome2(ActionEvent event) {
        signUp(); // Register the user when getStartedBtn is clicked
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
}
