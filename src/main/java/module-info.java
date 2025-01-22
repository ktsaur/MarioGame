module ru.kpfu.semester_work2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires jdk.jsobject;
    requires com.google.gson;
    requires java.desktop;


    opens ru.kpfu.semester_work2 to javafx.fxml;
    exports ru.kpfu.semester_work2.client;
    exports ru.kpfu.semester_work2.game;
    opens ru.kpfu.semester_work2.game to javafx.fxml;
    exports ru.kpfu.semester_work2;
}