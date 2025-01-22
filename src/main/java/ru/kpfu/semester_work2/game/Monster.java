package ru.kpfu.semester_work2.game;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import ru.kpfu.semester_work2.GameApplication;

import java.util.Objects;


public class Monster extends Pane {
    Image monsterImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/image23.png")));
    ImageView monster;
    private int direction = 1; // 1 - вправо, -1 - влево
    private final int movementRange = 40; // диапазон движения в пикселях
    private final int speed = 1; // скорость движения (пиксели за один шаг)
    private int movedDistance = 0;

    public Monster(int x, int y) {
        super(); // Вызов конструктора суперкласса
        monster = new ImageView(monsterImg);

        monster.setFitWidth(60); // Устанавливаем размер изображения
        monster.setFitHeight(60);
        setTranslateX(x); // Устанавливаем позицию
        setTranslateY(y);

        monster.setViewport(new Rectangle2D(256, 0, 16, 16));
        getChildren().add(monster);

        if (!GameApplication.gameRoot.getChildren().contains(this)) {
            Game.monsters.add(this);
            GameApplication.gameRoot.getChildren().add(this);
        }

        Timeline movement = new Timeline(new KeyFrame(Duration.millis(30), e -> move()));
        movement.setCycleCount(Timeline.INDEFINITE);
        movement.play();
    }

    private void move() {
        if (movedDistance >= movementRange) {
            direction *= -1; // Меняем направление
            movedDistance = 0; // Сбрасываем пройденное расстояние
        }
        setTranslateX(getTranslateX() + direction * speed);
        movedDistance += speed;
    }
}
