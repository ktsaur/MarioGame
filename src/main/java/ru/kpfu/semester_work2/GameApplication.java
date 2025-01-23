package ru.kpfu.semester_work2;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
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
    }

   /* public void startGame() {
        game.startGameLoop();
        setView(game);
    }*/

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

    public void showErrorMessage(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
