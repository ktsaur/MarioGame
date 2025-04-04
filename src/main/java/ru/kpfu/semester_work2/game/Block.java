package ru.kpfu.semester_work2.game;

import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import ru.kpfu.semester_work2.GameApplication;

import java.util.Objects;

public class Block extends Pane {
    Image blocksImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/ru/kpfu/semester_work2/image23.png")));
    ImageView block;

    public enum BlockType {
        PLATFORM, BRICK, BLOCK_BONUS, PIPE_TOP, PIPE_BOTTOM, INVISIBLE_BLOCK, STONE
    }

    public Block(BlockType blockType, int x, int y) {
        block = new ImageView(blocksImg);
        block.setFitWidth(Game.BLOCK_SIZE);
        block.setFitHeight(Game.BLOCK_SIZE);
        setTranslateX(x);
        setTranslateY(y);

        switch (blockType) {
            case PLATFORM:
                block.setViewport(new Rectangle2D(0, 0, 16, 16));
                break;
            case BLOCK_BONUS:
                block.setViewport(new Rectangle2D(384, 0, 16, 16));
                break;
            case BRICK:
                block.setViewport(new Rectangle2D(16, 0, 16, 16));
                break;
            case PIPE_TOP:
                block.setViewport(new Rectangle2D(0, 128, 32, 16));
                block.setFitWidth(Game.BLOCK_SIZE * 2);
                break;
            case PIPE_BOTTOM:
                block.setViewport(new Rectangle2D(0, 145, 32, 14));
                block.setFitWidth(Game.BLOCK_SIZE * 2);
                break;
            case INVISIBLE_BLOCK:
                block.setViewport(new Rectangle2D(0, 0, 16, 16));
                block.setOpacity(0);
                break;
            case STONE:
                block.setViewport(new Rectangle2D(0, 16, 16, 16));
                break;
        }
        getChildren().add(block);
        Game.platforms.add(this);
        GameApplication.gameRoot.getChildren().add(this);
    }

}
