package ru.kpfu.semester_work2.game;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import ru.kpfu.semester_work2.GameApplication;

import java.util.ArrayList;
import java.util.Objects;

import static ru.kpfu.semester_work2.GameApplication.appRoot;
import static ru.kpfu.semester_work2.GameApplication.gameRoot;

public class Character extends Pane {

    Image marioImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/mario.png")));
    ImageView imageView = new ImageView(marioImg);
    int count = 3;
    int columns = 3;
    int offsetX = 96;
    int offsetY =33;
    int width = 16;
    int height = 16;
    public SpriteAnimation animation;
    public Point2D playerVelocity = new Point2D(0, 0);
    private boolean canJump = true;

    public Character() {
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
        animation = new SpriteAnimation(imageView, Duration.millis(200), count, columns, offsetX, offsetY, width, height);
        getChildren().addAll(this.imageView);
    }

    public void moveY(int value) { //х - число пикселей, на которое мы смещаем наш объект
       boolean movingDown = value > 0;
        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform: Game.platforms) {
                if (platform instanceof Monster && !gameRoot.getChildren().contains(platform)) {
                    continue; // Пропускаем, если объект уже удалён
                }
                if(this.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if(movingDown) {
                        if(this.getTranslateY() + Game.MARIO_SIZE == platform.getTranslateY()) {
                            this.setTranslateY(this.getTranslateY() - 1);
                            canJump = true;
                            return;
                        }
                    } else {
                        if(this.getTranslateY() == platform.getTranslateY() + Game.BLOCK_SIZE) {
                            this.setTranslateY(this.getTranslateY() + 1);
                            playerVelocity = new Point2D(0, 10);
                            return;
                        }
                    }
                }
            }
            this.setTranslateY(this.getTranslateY() + (movingDown ? 1 : -1));
            if(this.getTranslateY()> 640) {
                this.setTranslateX(0);
                this.setTranslateY(400);
                gameRoot.setLayoutX(0);
            }
        }
    }

    public void moveX(int value) {
        boolean movingRight = value > 0;
        for (int i = 0; i < Math.abs(value); i++) {
            for (Node platform: Game.platforms) {
                if (platform instanceof Monster && !gameRoot.getChildren().contains(platform)) {
                    continue; // Пропускаем, если объект уже удалён
                }
                if(this.getBoundsInParent().intersects(platform.getBoundsInParent())) {
                    if(movingRight) {
                        if(this.getTranslateX() + Game.MARIO_SIZE == platform.getTranslateX()) {
                            this.setTranslateX(this.getTranslateX() - 1);
                            return;
                        }
                    } else {
                        if(this.getTranslateX() == platform.getTranslateX() + Game.BLOCK_SIZE) {
                            this.setTranslateX(this.getTranslateX() + 1);
                            return;
                        }
                    }
                }
            }
            this.setTranslateX(this.getTranslateX() + (movingRight ? 1 : -1));
        }
    }
    public void jumpPlayer(){
        if(canJump){
            playerVelocity = playerVelocity.add(0,-30);
            canJump = false;
        }
    }

    public void handleMonsterCollision(Monster monster, ArrayList<Monster> monsters, Pane gameRoot, Text scoreText) {
        double playerBottom = this.getTranslateY() + Game.MARIO_SIZE;
        double monsterTop = monster.getTranslateY();
        double playerLeft = this.getTranslateX();
        double playerRight = this.getTranslateX() + Game.MARIO_SIZE;
        double monsterLeft = monster.getTranslateX();
        double monsterRight = monster.getTranslateX() + Game.BLOCK_SIZE;

        if (playerBottom >= monsterTop && playerVelocity.getY() > 0 &&
                playerRight > monsterLeft && playerLeft < monsterRight) {
            // Игрок прыгает на монстра
            gameRoot.getChildren().remove(monster);
            System.out.println("количество монтров до удаления : " + monsters.size());
            monsters.remove(monster);
            System.out.println("количество монтров после удаления : " + monsters.size());
            Game.score += 100;
            System.out.println("Score: " + Game.score);

            // Имитация прыжка вверх от монстра
            this.setTranslateY(this.getTranslateY() - 20);
            this.playerVelocity = new Point2D(0, -10);
        } else if (playerRight > monsterLeft && playerLeft < monsterRight) {
            // Игрок касается монстра с боку
            System.out.println("Game Over!");
            this.setTranslateX(0);
            this.setTranslateY(400);
            gameRoot.setLayoutX(0);
            Game.score = 0;
        }
    }


}
