package ru.kpfu.semester_work2.View;

import javafx.scene.Parent;
import ru.kpfu.semester_work2.GameApplication;

public abstract class BaseView {

    private static GameApplication game;

    public static GameApplication getApplication() {
        if (game != null) {
            return game;
        }
        throw new RuntimeException("application is null");
    }

    public static void setApplication(GameApplication game) {
        BaseView.game = game;
    }

    public abstract Parent getView();
}
