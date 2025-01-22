package ru.kpfu.semester_work2.game;

import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.ImageView;
import javafx.util.Duration;


public class SpriteAnimation extends Transition {
    /* класс, содержащий основные функции, необходимые для всех анимаций на основе переходов
    Этот класс предлагает структура для определения анимации
    Метод interpolate() вызывается в каждом кадре во время выполнения перехода
     */
    private final ImageView imageView;
    private final int columns;
    private final int count; //количество кадров в анимации
    private int offsetX; //смещение первого кадра по иксу
    private int offsetY; //смешение первого кадра по игрику
    private final int width; // размер кадра (одного квадратика)
    private final int height; //размер кадра (одного квадратика)

    public SpriteAnimation(
            ImageView imageView,
            Duration duration,
            int columns, int count,
            int offsetX, int offsetY,
            int width, int height) {
        this.imageView = imageView;
        this.columns = columns;
        this.count = count;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        setCycleDuration(duration); //устанавливаем продолжительность анимации
        setCycleCount(Animation.INDEFINITE); //длительность нашей анимации (у нас не определено, поэтому она будет двигаться, пока мы ее не остановим)
        setInterpolator(Interpolator.LINEAR); //анимация будет идти линейно, без замедления и тп
        this.imageView.setViewport(new Rectangle2D(offsetX, offsetY, width, height));
    }

    public void setOffsetX(int offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(int offsetY) {
        this.offsetY = offsetY;
    }

    @Override
    protected void interpolate(double v) { //в этом методе определяем поведение анимации
        final int index = Math.min((int) Math.floor(v * count), count - 1); //определяем константу индекса, которая равна 0, 1 либо 2
        /*
        с помощью индекс мы будем определять координаты x и y
         */
        final int x = (index % columns) * width + offsetX;
        final int y = (index / columns) * height + offsetY;
        imageView.setViewport(new Rectangle2D(x, y, width, height));
    }


}
