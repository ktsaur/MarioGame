package ru.kpfu.semester_work2.game;

import javafx.animation.AnimationTimer;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import ru.kpfu.semester_work2.View.BaseView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Game extends BaseView{
    public static ArrayList<Block> platforms = new ArrayList<>(); //платформы
    public HashMap<KeyCode, Boolean> keys = new HashMap<>(); //коды кнопок
    public static ArrayList<Monster> monsters = new ArrayList<>();
    private Text scoreText;
    public static int score = 0; // Счет игрока

    Image backgroundImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/background.png")));
    public static final int BLOCK_SIZE = 45;
    public static final int MARIO_SIZE = 40;

    private Pane appRoot;
    private Pane gameRoot;

    public Character player;
    int levelNumber = 0;
    private int levelWidth;

    public Game(Pane appRoot, Pane gameRoot) {
        this.appRoot = appRoot;
        this.gameRoot = gameRoot;
    }

    public void initContent(){
        ImageView backgroundIV = new ImageView(backgroundImg);
        backgroundIV.setFitHeight(14*BLOCK_SIZE);
        backgroundIV.setFitWidth(212*BLOCK_SIZE);

        levelWidth = LevelData.levels[levelNumber][0].length()*BLOCK_SIZE;
        for(int i = 0; i < LevelData.levels[levelNumber].length; i++){
            String line = LevelData.levels[levelNumber][i];
            for(int j = 0; j < line.length();j++){
                switch (line.charAt(j)){
                    case 'M': // символ для монстра
                        Monster monster = new Monster(j * BLOCK_SIZE, i * BLOCK_SIZE);
                        monsters.add(monster); // Already added to gameRoot inside the Monster constructor
                        for (int k = 1; k <= monsters.size(); k++) {
                            System.out.println(k+1 + " монстр " + monsters.get(k).toString());
                        }
                        break;
                    case '0':
                        break;
                    case '1':
                        Block platformFloor = new Block(Block.BlockType.PLATFORM, j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '2':
                        Block brick = new Block(Block.BlockType.BRICK,j*BLOCK_SIZE,i*BLOCK_SIZE);
                        break;
                    case '3':
                        Block bonus = new Block(Block.BlockType.BONUS,j*BLOCK_SIZE,i*BLOCK_SIZE);
                        break;
                    case '4':
                        Block stone = new Block(Block.BlockType.STONE,j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '5':
                        Block PipeTopBlock = new Block(Block.BlockType.PIPE_TOP,j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '6':
                        Block PipeBottomBlock = new Block(Block.BlockType.PIPE_BOTTOM,j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                    case '*':
                        Block InvisibleBlock = new Block(Block.BlockType.INVISIBLE_BLOCK,j * BLOCK_SIZE, i * BLOCK_SIZE);
                        break;
                }
            }

        }
        player =new Character();
        player.setTranslateX(0);
        player.setTranslateY(400);
        player.translateXProperty().addListener((obs,old,newValue)->{
            int offset = newValue.intValue();
            if(offset>640 && offset<levelWidth-640){
                gameRoot.setLayoutX(-(offset-640));
                backgroundIV.setLayoutX(-(offset-640));
            }
        });
        gameRoot.getChildren().add(player);
        appRoot.getChildren().addAll(backgroundIV,gameRoot);

        scoreText = new Text("Score: 0");
        scoreText.setTranslateX(10); // Расположение по X
        scoreText.setTranslateY(20); // Расположение по Y
        appRoot.getChildren().add(scoreText);
    }

    private void update(){
        for (int i = 0; i < monsters.size(); i++) {
            Monster monster = monsters.get(i);
            System.out.println("создали нового монстра " + monster.toString());
            if (player.getBoundsInParent().intersects(monster.getBoundsInParent())) {
                player.handleMonsterCollision(monster, monsters, gameRoot, scoreText);
                break; // Прерываем, чтобы избежать ошибок из-за изменения списка
            }
        }
        if(isPressed(KeyCode.UP) && player.getTranslateY()>=5){
            player.jumpPlayer();
        }
        if(isPressed(KeyCode.LEFT) && player.getTranslateX()>=5){
            player.setScaleX(-1);
            player.animation.play();
            player.moveX(-5);
        }
        if(isPressed(KeyCode.RIGHT) && player.getTranslateX()+40 <=levelWidth-5){
            player.setScaleX(1);
            player.animation.play();
            player.moveX(5);
        }
        if(player.playerVelocity.getY()<10){
            player.playerVelocity = player.playerVelocity.add(0,1);
        }
        player.moveY((int)player.playerVelocity.getY());
        scoreText.setText("Score: " + score);
    }

    private boolean isPressed(KeyCode key){
        return keys.getOrDefault(key,false);
    }

    public void startGameLoop() {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();
    }

    @Override
    public Parent getView() {
        return appRoot;
    }
}

