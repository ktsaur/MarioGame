package ru.kpfu.semester_work2;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import ru.kpfu.semester_work2.View.BaseView;
import ru.kpfu.semester_work2.View.PlayerConfigView;
import ru.kpfu.semester_work2.client.GameClient;
import ru.kpfu.semester_work2.game.Game;
import ru.kpfu.semester_work2.model.PlayerConfig;


public class GameApplication extends Application {

    private GameClient client;
    private PlayerConfig playerConfig;
    private PlayerConfigView playerConfigView;
    private Game game;
    private BorderPane root;
    private Label playerInfo;
    private long startTime;
    private boolean gameStarted = false;

    public static Pane appRoot = new Pane();
    public static Pane gameRoot = new Pane();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        root = new BorderPane();
        game = new Game(appRoot, gameRoot);
        game.initContent();

        primaryStage.setTitle("Mini Mario");
        primaryStage.setOnCloseRequest(e -> System.exit(0));
        BaseView.setApplication(this);

        playerConfigView = new PlayerConfigView();
        client = new GameClient(this);

        Scene scene = new Scene(root,1200,620);
        scene.setOnKeyPressed(event-> game.keys.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> {
            game.keys.put(event.getCode(), false);
            game.player.animation.stop();
        });

        primaryStage.setScene(scene);
        root.setCenter(appRoot);
        primaryStage.show();

        setView(playerConfigView);
    }

    public void setPlayerConfig(PlayerConfig playerConfig) {
        this.playerConfig = playerConfig;
        System.out.println(playerConfig != null);
        System.out.println("name is = " + playerConfig.getPlayerName());
        if (playerConfig != null && playerConfig.getPlayerName() != null) {
            addPlayerInfoCard(playerConfig.getPlayerName());
        }
    }

    public PlayerConfigView getPlayerConfigView() {
        return playerConfigView;
    }

    public Game getGame() {
        return game;
    }

    public void setView(BaseView view) {
        root.setCenter(view.getView());
    }

    public PlayerConfig getPlayerConfig() {
        return playerConfig;
    }

    public GameClient getGameClient() {
        return client;
    }

    public void showMessage(String title, String message, Alert.AlertType alertType ) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    public void showNotification(String message) {
        Platform.runLater(() -> {
            Label notification = new Label(message);
            notification.setStyle("-fx-background-color: rgb(0,134,0); -fx-text-fill: white; -fx-padding: 10; -fx-font-size: 14; -fx-background-radius: 5;");

            notification.setMinWidth(500);
            notification.setMaxWidth(Double.MAX_VALUE);
            notification.setWrapText(true);

            double windowWidth = root.getWidth();
            double notificationWidth = notification.getWidth();

            double centerX = (windowWidth - notificationWidth) / 2;

            notification.setTranslateY(40);
            notification.setTranslateX(centerX);

            StackPane overlay = new StackPane(notification);
            overlay.setMouseTransparent(true);
            overlay.setPrefSize(root.getWidth(), root.getHeight());
            root.getChildren().add(overlay);

            PauseTransition delay = new PauseTransition(Duration.seconds(5));
            delay.setOnFinished(event -> root.getChildren().remove(overlay));
            delay.play();
        });
    }

    public void addPlayerInfoCard(String playerName) {
        playerInfo = new Label();
        playerInfo.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-padding: 10; -fx-font-size: 14;");
        playerInfo.setLayoutX(10);
        playerInfo.setLayoutY(10);

        Platform.runLater(() -> appRoot.getChildren().add(playerInfo));

        new Thread(() -> {
            while (true) {
                if (gameStarted) {
                    long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
                    int currentScore = Game.getScore();
                    Platform.runLater(() -> {
                        playerInfo.setText(String.format("Player: %s\nScore: %d\nTime: %d sec \n playerX: %d", playerName, currentScore, elapsedTime, (int) game.player.getTranslateX()));
                    });
                    client.updatePlayerInfo(playerName, currentScore, elapsedTime, (int) game.player.getTranslateX());
                    System.out.println("отправили сообщение клиенту");
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void startGameTimer() {
        startTime = System.currentTimeMillis();
        gameStarted = true;
    }

}
