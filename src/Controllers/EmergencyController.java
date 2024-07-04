package Controllers;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import javafx.geometry.Pos;
import org.controlsfx.control.Notifications;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import models.FacilityRow;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableCell;
import javafx.stage.Stage;
import models.SharedDataModel;

/**
 *
 * @author berek
 */
public class EmergencyController implements Initializable {

    private Node parentNode;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void setParentNode(Node parentNode) {
        this.parentNode = parentNode;
    }

    @FXML
    private ComboBox<String> City;
    @FXML
    private ComboBox<String> Sub_city;
    @FXML
    private ComboBox<String> Woreda;
    @FXML
    private Button check;
    @FXML
    private TableView<FacilityRow> showfacility;
    @FXML
    private TableColumn<FacilityRow, String> facilities;
    @FXML
    private TableColumn<FacilityRow, String> availableBeds;
    @FXML
    private ComboBox<String> emergencyType;

    @FXML
    private void goBack(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Home.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
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
            Connection Connect = DriverManager.getConnection("jdbc:mysql://localhost/facilitiesdb", "root", ""); // Root is our default username while NULL or EMPTY for DEFAULT PASSWOED
            return Connect;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void ComboBoxEmergencyType() {
        String selectEmergencyTypes = "SELECT EmergencyType FROM emergencytype";
        try {
            PreparedStatement statement = connect.prepareStatement(selectEmergencyTypes);
            ResultSet resultSet = statement.executeQuery();
            ObservableList<String> emergencyTypes = FXCollections.observableArrayList();
            while (resultSet.next()) {
                emergencyTypes.add(resultSet.getString("EmergencyType"));
            }
            emergencyType.setItems(emergencyTypes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void comboBoxCity() {
        String selectAllData = "SELECT * FROM `facilities location`";
        connect = connectDB();
        try {
            prepare = connect.prepareStatement(selectAllData);
            result = prepare.executeQuery();
            ObservableList listData = FXCollections.observableArrayList();
            while (result.next()) {
                String item = result.getString("City");
                listData.add(item);
            }
            City.setItems(listData);
            City.setOnAction(event -> {
                String selectedCity = City.getValue();
                if (selectedCity != null) {
                    String selectSubCityQuery = "SELECT Sub_City FROM `sub city` WHERE userid=?";
                    try {
                        prepare = connect.prepareStatement(selectSubCityQuery);
                        prepare.setString(1, selectedCity);
                        ResultSet subCityResult = prepare.executeQuery();
                        ObservableList<String> subCityData = FXCollections.observableArrayList();
                        while (subCityResult.next()) {
                            subCityData.add(subCityResult.getString("Sub_City"));
                        }
                        Sub_city.setItems(subCityData);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    Sub_city.setOnAction(subCityEvent -> {
                        String selectedSubCity = Sub_city.getValue();
                        if (selectedSubCity != null) {
                            String selectWoredaQuery = "SELECT woreda FROM `woreda` WHERE userid = ?";
                            try {
                                prepare = connect.prepareStatement(selectWoredaQuery);
                                prepare.setString(1, selectedSubCity);
                                ResultSet woredaResult = prepare.executeQuery();
                                ObservableList<String> woredaData = FXCollections.observableArrayList();
                                while (woredaResult.next()) {
                                    woredaData.add(woredaResult.getString("Woreda"));
                                }
                                Woreda.setItems(woredaData);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onButtonClick(ActionEvent event) {
        onClick1();
        onClick2();
    }

    public void onClick1() {
        // Show temporary notification in the middle of the GUI
        Notifications.create()
                .title("Locating nearby facilities")
                .hideAfter(Duration.seconds(0.2))
                .owner(parentNode) // Set the owner node
                .position(Pos.CENTER) // Set the position to the center
                .show();
    }

    public void onClick2() {
        // Retrieve selected values from ComboBoxes
        String city = City.getValue();
        String subCity = Sub_city.getValue();
        String woreda = Woreda.getValue();

        // Construct SQL query based on selected values
        String query = "SELECT nf.facility_name, nf.available_beds, fl.City, sc.Sub_City "
                + "FROM nearbyfacilities nf "
                + "JOIN woreda w ON nf.woreda_id = w.woreda "
                + "JOIN `sub city` sc ON w.userid = sc.Sub_City "
                + "JOIN `facilities location` fl ON sc.userid = fl.City "
                + "WHERE fl.City = ? AND sc.Sub_City = ? AND w.woreda = ?";

        // Connect to the database and execute the query
        try (Connection connection = connectDB(); PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, city);
            statement.setString(2, subCity);
            statement.setString(3, woreda);

            ResultSet resultSet = statement.executeQuery();

            // Populate the result table with the retrieved data
            ObservableList<FacilityRow> facilityRows = FXCollections.observableArrayList();
            while (resultSet.next()) {
                // Retrieve data from the result set
                String facilityName = resultSet.getString("facility_name");
                String availableBed = resultSet.getString("available_beds");

                // Add the data to the table
                facilityRows.add(new FacilityRow(facilityName, availableBed));
                showfacility.setItems(facilityRows);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setupTableViewCallBtn() {
        // Define column
        TableColumn<FacilityRow, Button> actionCol = new TableColumn<>("Call");
        actionCol.setCellValueFactory(data -> null);
        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button button = new Button("Call");

            {
                button.setStyle("-fx-background-color: transparent; "
                        + "-fx-text-fill: #EA2624; "
                        + "-fx-font-weight: bold;"
                        + "-fx-font-size: 12px;"
                        + "-fx-background-radius: 3px; "
                        + "-fx-border-color: #EA2624; "
                        + "-fx-border-width: 1px; "
                        + "-fx-border-style: solid; "
                        + "-fx-border-radius: 3px;");
                button.setOnMouseEntered(event -> {
                    button.setStyle("-fx-background-color: #EA2624; "
                            + "-fx-text-fill: #FFF;"
                            + "-fx-font-weight: bold;"
                            + "-fx-font-size: 14px;"
                            + "-fx-background-radius: 3px;"
                            + "-fx-border-color: #EA2624; "
                            + "-fx-border-width: 1px; "
                            + "-fx-border-style: solid; "
                            + "-fx-border-radius: 3px;");
                });
                button.setOnMouseExited(event -> {
                    button.setStyle("-fx-background-color: transparent; "
                            + "-fx-text-fill: #EA2624;"
                            + "-fx-font-weight: bold;"
                            + "-fx-font-size: 12px;"
                            + "-fx-background-radius: 3px; "
                            + "-fx-border-color: #EA2624; "
                            + "-fx-border-width: 1px; "
                            + "-fx-border-style: solid; "
                            + "-fx-border-radius: 3px;");
                });
                button.setOnAction(event -> {
                    FacilityRow row = getTableView().getItems().get(getIndex());
                    // Show message
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Calling for Help");
                    alert.setHeaderText(null);
                    alert.setContentText("Calling for an ambulance...");
                    alert.showAndWait();

                    // Assign facilityName to another hospitalselectedlabel
                    SharedDataModel.hospitalselectedlabel = row.getFacilityName();
                    
                    // Delay for 0.5 second
                    Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), evt -> {
                        try {
                            root = FXMLLoader.load(getClass().getResource("/fxml/CallPage.fxml"));
                            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                            scene = new Scene(root);
                            stage.setScene(scene);
                            stage.show();
                        } catch (IOException ex) {
                            Logger.getLogger(EmergencyController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }));
                    timeline.play();
                });
            }

            @Override
            protected void updateItem(Button item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });

        // Add columns to TableView
        showfacility.getColumns().addAll(actionCol);
    }

    @FXML
    public void setEmergencyType(ActionEvent event) throws IOException {
        SharedDataModel.emergencylabel = emergencyType.getValue();
//        SharedDataModel.hospitalselectedlabel = emergencyType.getValue();
    }
//    private void updateSubmitButtonState() {
//        String city = City.getValue();
//        String subCity = Sub_city.getValue();
//        String woreda = Woreda.getValue();
//        boolean isDisabled = (city == null || city.isEmpty())
//                || (subCity == null || subCity.isEmpty())
//                || (woreda == null || woreda.isEmpty());
//        check.setDisable(isDisabled);
//    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboBoxCity();
        ComboBoxEmergencyType();
        setupTableViewCallBtn();
// Initialize the result table with columns
        facilities.setCellValueFactory(new PropertyValueFactory<>("facilityName"));
        availableBeds.setCellValueFactory(new PropertyValueFactory<>("availableBed"));

    }

}
