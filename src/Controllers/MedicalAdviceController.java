package Controllers;

import AI.GeminiApiClient;
import Utils.Server;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import models.SharedDataModel;

/**
 * FXML Controller class
 *
 * @author berek
 */
public class MedicalAdviceController implements Initializable {

    Stage stage;
    Scene scene;

    @FXML
    private Button homeBtn;
    @FXML
    private Button doctorBtn;
    @FXML
    private Button firstAidBtn;
    @FXML
    private Button chatbotBtn;
    @FXML
    private Button healthtipsBtn;
    @FXML
    private Pane healthtipsPane;
    @FXML
    private Pane chatbotPane;
    @FXML
    private Pane firstAidPane;
    @FXML
    private Pane doctorPane;
    @FXML
    private Pane homePane;
    @FXML
    private Button doctorBtn2;
    @FXML
    private Button healthtipsBtn2;
    @FXML
    private Button firstAidBtn2;
    @FXML
    private Button chatbotBtn2;
    @FXML
    private TextField userInputField;
    @FXML
    private Button sendQuestion;
    @FXML
    private VBox chatVBox;
    @FXML
    private ScrollPane chatScrollPane;
    @FXML
    private WebView webview1;
    @FXML
    private WebView webview2;
    @FXML
    private WebView webview3;
    @FXML
    private WebView webview4;
    @FXML
    private WebView webview5;
    @FXML
    private WebView webview6;
    @FXML
    private Label youremail;
    @FXML
    private Button button_send;
    @FXML
    private TextField tf_message;
    @FXML
    private ScrollPane sp_main;
    @FXML
    private VBox vbox_messages;

    private Server server;

    @FXML
    public void handleClicks(ActionEvent event) {
        if (event.getSource().equals(homeBtn)) {
            homePane.toFront();
        }
        if (event.getSource().equals(doctorBtn)) {
            doctorPane.toFront();
        }
        if (event.getSource().equals(doctorBtn2)) {
            doctorPane.toFront();
        }
        if (event.getSource().equals(firstAidBtn)) {
            firstAidPane.toFront();
        }
        if (event.getSource().equals(firstAidBtn2)) {
            firstAidPane.toFront();
        }
        if (event.getSource().equals(chatbotBtn)) {
            chatbotPane.toFront();
        }
        if (event.getSource().equals(chatbotBtn2)) {
            chatbotPane.toFront();
        }
        if (event.getSource().equals(healthtipsBtn)) {
            healthtipsPane.toFront();
        }
        if (event.getSource().equals(healthtipsBtn2)) {
            healthtipsPane.toFront();
        }

    }

    @FXML
    private void signOut(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/SignInSignup.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handleSendQuestion(ActionEvent event) {
        sendMessage();
    }

    private void sendMessage() {
        GeminiApiClient geminiApiClient = new GeminiApiClient();
        String userMessage = userInputField.getText();
        addMessage("You", userMessage); // Add user message to the chat
        String botResponse = geminiApiClient.sendMessage(userMessage);
        addMessage("Bot", botResponse); // Add bot response to the chat
        userInputField.clear();
    }

    private void addMessage(String sender, String message) {
        Text text = new Text(sender + ": " + message);
        text.setFont(Font.font("Arial", 14));

        // Set wrapping width to fit VBox
        text.wrappingWidthProperty().bind(chatVBox.widthProperty().subtract(80)); // Adjust spacing

        // Set a maximum width to prevent infinite growth
        text.maxWidth(Double.MAX_VALUE);

        // Create a VBox to contain the text and apply padding and styling
        VBox messageContainer = new VBox();
        messageContainer.getChildren().add(text);
        messageContainer.setPadding(new Insets(8)); // Adjust padding as needed

        // Apply different styles based on the sender
        if (sender.equals("You")) {
            messageContainer.setAlignment(Pos.TOP_LEFT);
            messageContainer.setStyle("-fx-background-color: rgb(15,125,242); -fx-background-radius: 10px; -fx-padding: 8px;");
        } else {
            messageContainer.setAlignment(Pos.TOP_RIGHT);
            messageContainer.setStyle("-fx-background-color: rgb(233,233,235); -fx-background-radius: 10px; -fx-padding: 8px;");
        }

        chatVBox.getChildren().add(messageContainer);
        chatScrollPane.setVvalue(1.0); // Scroll to the bottom
    }

    // Method to toggle full-screen for WebView
    private void toggleFullScreen(WebView webView) {
        Stage fullScreenStage = new Stage();
        StackPane fullScreenPane = new StackPane();
        fullScreenPane.setStyle("-fx-background-color: black;");

        WebView fullScreenWebView = new WebView();
        fullScreenWebView.getEngine().load(webView.getEngine().getLocation());

        fullScreenPane.getChildren().add(fullScreenWebView);
        fullScreenStage.setScene(new Scene(fullScreenPane));
        fullScreenStage.setFullScreen(true);
        fullScreenStage.show();
    }

    private void loadYouTubeVideo(WebView webView, String videoId) {
        webView.getEngine().load("https://www.youtube.com/embed/" + videoId);
        webView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) { // Detect double-click
                toggleFullScreen(webView);
            }
        });
    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233,233,235);"
                + " -fx-background-radius: 20px;");
        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize chat area with a welcome message
        addMessage("Bot", "Hello! What can I assist you with today?");
        // Initialize Gemini API client
        GeminiApiClient geminiApiClient = new GeminiApiClient();

        // Event listener for sending messages when user presses Enter
        userInputField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessage();
            }
        });
        // Event listener for sendQuestion button
        sendQuestion.setOnAction(this::handleSendQuestion);

        String[] videoIds = {
            "yvxX0XfqJtw", // YouTube video IDs
            "HAmeeWuQWx0",
            "5OKFljZ2GQE",
            "5BwlDW0CAuU",
            "_HEnohs6yYw",
            "fk8cqO4meKo"
        };

        loadYouTubeVideo(webview1, videoIds[0]);
        loadYouTubeVideo(webview2, videoIds[1]);
        loadYouTubeVideo(webview3, videoIds[2]);
        loadYouTubeVideo(webview4, videoIds[3]);
        loadYouTubeVideo(webview5, videoIds[4]);
        loadYouTubeVideo(webview6, videoIds[5]);

        // get user email and set it to the label
        if (SharedDataModel.text.equals("")) {
            youremail.setText("Error");
        } else {
            youremail.setText(SharedDataModel.text);
        }
// Listner for the scroll pane to grow
        chatVBox.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                chatScrollPane.setVvalue((Double) newValue);
            }
        });

// Messaging feature 
        try {
            server = new Server(new ServerSocket(1234));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error creating server.");
        }
        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });
        server.receiveMessageFromClient(vbox_messages);
        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String messageToSend = tf_message.getText();
                if (!messageToSend.isEmpty()) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239,242,255); "
                            + "-fx-background-color: rgb(15,125,242);"
                            + " -fx-background-radius: 20px;");

                    textFlow.setPadding(new Insets(5, 10, 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hBox.getChildren().add(textFlow);
                    vbox_messages.getChildren().add(hBox);

                    server.sendMessageToClient(messageToSend);
                    tf_message.clear();
                }
            }
        });
    }

}
