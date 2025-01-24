package ru.kpfu.semester_work2.View;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import ru.kpfu.semester_work2.model.PlayerConfig;

import java.util.Objects;

public class PlayerConfigView extends BaseView{

    private AnchorPane pane;
    private VBox mainBox;
    private VBox box;
    private TextField username;
    private TextField host;
    private TextField port;
    private Button start;

    @Override
    public Parent getView() {
        if(pane == null) {
            createView();
        }
        return pane;
    }

    public void createView() {
        pane = new AnchorPane();

        Image backgroundImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/marioStart.png")));
        ImageView background = new ImageView(backgroundImage);

        background.setFitWidth(1200);
        background.setFitHeight(620);
        background.setPreserveRatio(false);
        pane.getChildren().add(background);

        HBox mainBox = new HBox(15);
        mainBox.setLayoutX(400);
        mainBox.setLayoutY(330);

        VBox connectionBox = new VBox(8);
        connectionBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 10; -fx-background-radius: 8;");

        Label usernameLabel = new Label("Username:");
        username = new TextField();
        username.setPromptText("Enter your name");
        username.setStyle("-fx-font-size: 12;");

        Label hostLabel = new Label("Host:");
        host = new TextField("localhost");
        host.setStyle("-fx-font-size: 12;");

        Label portLabel = new Label("Port:");
        port = new TextField("8080");
        port.setStyle("-fx-font-size: 12;");

        Button connectButton = new Button("Connect to Server");
        connectButton.setFont(Font.font(12));
        connectButton.setTextFill(Color.WHITE);
        connectButton.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5;");

        connectButton.setOnAction(event -> {
            PlayerConfig playerConfig = new PlayerConfig();
            playerConfig.setPlayerName(username.getText());
            playerConfig.setHost(host.getText());
            playerConfig.setPort(Integer.parseInt(port.getText()));

            getApplication().setPlayerConfig(playerConfig);
            getApplication().getGameClient().start();
        });

        connectionBox.getChildren().addAll(usernameLabel, username, hostLabel, host, portLabel, port, connectButton);

        VBox createRoomContainer = new VBox(8);
        createRoomContainer.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 10; -fx-background-radius: 8;");

        Button createRoomButton = new Button("Create Room");
        createRoomButton.setFont(Font.font(12));
        createRoomButton.setTextFill(Color.WHITE);
        createRoomButton.setStyle("-fx-background-color: #2196F3; -fx-background-radius: 5;");
        createRoomButton.setOnAction(event -> getApplication().getGameClient().createRoom());

        VBox joinRoomBox = new VBox(8);
        joinRoomBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 10; -fx-background-radius: 8;");
        joinRoomBox.setAlignment(Pos.CENTER);

        TextField roomIdField = new TextField();
        roomIdField.setPromptText("ENTER ROOM ID");
        roomIdField.setStyle("-fx-font-size: 12;");

        Button joinRoomButton = new Button("JOIN ROOM");
        joinRoomButton.setFont(Font.font(12));
        joinRoomButton.setTextFill(Color.WHITE);
        joinRoomButton.setStyle("-fx-background-color: #FF5722; -fx-background-radius: 5;");
        joinRoomButton.setOnAction(event -> getApplication().getGameClient().joinRoom(roomIdField.getText()));

        joinRoomBox.getChildren().addAll(roomIdField, joinRoomButton);

        createRoomContainer.getChildren().addAll(createRoomButton, joinRoomBox);

        mainBox.getChildren().addAll(connectionBox, createRoomContainer);
        pane.getChildren().add(mainBox);
    }
}
