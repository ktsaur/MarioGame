package ru.kpfu.semester_work2.View;

import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import ru.kpfu.semester_work2.model.PlayerConfig;

public class PlayerConfigView extends BaseView{

    private AnchorPane pane;
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
        box = new VBox();

        Label usernameLabel = new Label("Username:");
        username = new TextField();
        Label hostLabel = new Label("Host:");
        host = new TextField();
        host.setText("localhost");
        Label portLabel = new Label("Port:");
        port = new TextField();
        port.setText("8080");
        start = new Button("Start");

        start.setOnAction(event -> {
            if (event.getSource() == start) {
                PlayerConfig playerConfig = new PlayerConfig();
                playerConfig.setPlayerName(username.getText());
                playerConfig.setHost(host.getText());
                playerConfig.setPort(Integer.parseInt(port.getText()));

                getApplication().setPlayerConfig(playerConfig);

                Label waitingLabel = new Label("Waiting for another player...");
                box.getChildren().add(waitingLabel);

                getApplication().startGame();

            }
        });
        box.getChildren().addAll(
                usernameLabel, username, hostLabel,
                host, portLabel, port, start
        );
        pane.getChildren().addAll(box);
    }
}
