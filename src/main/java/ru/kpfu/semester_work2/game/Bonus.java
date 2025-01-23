package ru.kpfu.semester_work2.game;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import ru.kpfu.semester_work2.GameApplication;

import java.util.Objects;


public class Bonus extends Pane {
    Image bonusImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/image23.png")));
    ImageView bonus;

    public Bonus(int x, int y) {
        bonus = new ImageView(bonusImg);
        bonus.setFitWidth(Game.BLOCK_SIZE);
        bonus.setFitHeight(Game.BLOCK_SIZE);
        setTranslateX(x);
        setTranslateY(y);
        bonus.setViewport(new Rectangle2D(384, 16, 16, 16));
        getChildren().add(bonus);
        Game.bonuses.add(this);
    }
}
