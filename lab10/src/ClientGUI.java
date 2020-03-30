import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientGUI extends Application {
    private Client client;

    public ClientGUI() throws IOException {
        client = new Client();
        Thread t = new Thread(client);
        t.start();
    }

    @Override
    public void start(Stage primaryStage) {

        // Create the register ui first then switch later.
        VBox regvBox = new VBox();

        HBox userRegister = new HBox(10);
        TextField userField = new TextField();
        userRegister.getChildren().addAll(new Label("Enter username"), userField);

        Button btRegister = new Button("Register");

        // Register the name and then switch scenes.
        btRegister.setOnAction(e -> {
            client.setUsername(userField.getText());
            primaryStage.setScene(setMainGUI());
            primaryStage.setTitle("Instance for " + client.getUsername());
            primaryStage.show();
        });

        regvBox.getChildren().addAll(userRegister, btRegister);

        Scene gui = new Scene(regvBox);

        primaryStage.setScene(gui);
        primaryStage.show();
    }

    // create
    private Scene setMainGUI() {
        VBox vBox = new VBox();

        VBox chatPane = createChatPane();
        HBox bottomBar = createStatusBar();

        vBox.setVgrow(chatPane, Priority.ALWAYS);
        vBox.getChildren().addAll(chatPane, bottomBar);

        Scene scene = new Scene(vBox, 900, 600);

        return scene;
    }

    // Functions to create the main chat window
    private VBox createChatPane() {
        VBox chatPane = new VBox();

        client.getchatMessages().setWrapText(true);
        ScrollPane chatArea = new ScrollPane(client.getchatMessages());
        chatArea.setFitToHeight(true);
        chatArea.setFitToWidth(true);

        HBox chatInput = createTextInput();
        chatInput.setMaxHeight(100);

        VBox.setVgrow(chatArea, Priority.ALWAYS);
        chatPane.getChildren().addAll(chatArea, chatInput);

        return chatPane;
    }

    private HBox createTextInput() {
        HBox textArea = new HBox();

        TextField userText = new TextField();
        userText.setPromptText("Enter message");
        userText.setPrefSize(500,50);
        userText.setMinSize(100,50);

        // Allow user to send text if they press send or if they press the enter key
        userText.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                client.sendText(userText.getText());
                userText.clear();
            }
        });

        Button sendButton = new Button("Send");
        sendButton.setMinSize(100,50);
        sendButton.setMaxSize(100,50);
        sendButton.setOnAction(e -> {
            client.sendText(userText.getText());
            userText.clear();
        });

        HBox.setHgrow(userText, Priority.ALWAYS);
        textArea.getChildren().addAll(userText, sendButton);

        return textArea;
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(0);
        statusBar.maxHeight(50);

        Button btLogout = new Button("Log out");
        btLogout.setMinWidth(80);

        btLogout.setOnMouseClicked(e -> {
            System.out.println("mouse clicked");
            try {
                logout();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        statusBar.getChildren().add(btLogout);
        return statusBar;

    }

    private void logout() throws IOException {
        System.out.println("Stage is closing");
        Platform.exit();
        System.out.println("client is closing");
        client.logout();
        System.out.println("Program is exiting");
        System.exit(0);
    }
}