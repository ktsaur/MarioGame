package ru.kpfu.semester_work2.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import ru.kpfu.semester_work2.GameApplication;
import ru.kpfu.semester_work2.game.Game;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class GameClient {

    private GameApplication application;
    private Socket socket;
    private ClientThread clientThread;

    public GameClient(GameApplication application) {
        this.application = application;
    }

    public GameApplication getApplication() {
        return application;
    }

    public void sendMessage(String message) {
        try {
            clientThread.out.write(message);
            clientThread.out.newLine();
            clientThread.out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() { //этот метод для ПОДКЛЮЧЕНИЯ К СЕРВЕРУ
        String host = application.getPlayerConfig().getHost();
        int port = application.getPlayerConfig().getPort();

        System.out.println("Connecting to " + host + ":" + port);

        BufferedReader in;
        BufferedWriter out;

        try {
            socket = new Socket(host, port);
            System.out.println("Подключение к серверу установлено: " + host + ":" + port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            clientThread = new ClientThread(in, out, this);

            new Thread(clientThread).start(); // запускаем поток для обработки входящих сообщений
            application.showNotification("Connection to the server was successful");
        } catch (IOException e) {
            System.err.println("Невозможно подключиться к серверу. Проверьте хост и порт.");
            Platform.runLater(() -> {
                application.showMessage("Ошибка подключения",
                        "Невозможно подключиться к серверу. Проверьте хост и порт.", Alert.AlertType.ERROR);
            });
        }
    }

    public void createRoom() {
        sendMessage("create_room");
    }

    public void joinRoom(String roomId) {
        sendMessage("join_room " + roomId);
    }

    static class ClientThread implements Runnable { //это поток для обработки сообщений
        private BufferedReader in;
        private BufferedWriter out;
        private GameClient client;

        public ClientThread(BufferedReader in, BufferedWriter out, GameClient client) {
            this.in = in;
            this.out = out;
            this.client = client;
        }

        @Override
        public void run() { //должен быть метод для обработки входящих сообщений от сервера
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("{\"type\":\"ROOM_CREATED\"")) {
                        System.out.println("Комната создана: " + message);
                        client.application.showMessage("Room created.", "RoomID: " + message, Alert.AlertType.INFORMATION);
                    } else if (message.startsWith("{\"type\":\"JOINED\"")) {
                        System.out.println("Присоединился к комнате: " + message);
                        client.application.showNotification("Joined the room");
                    } else if (message.startsWith("{\"type\":\"ERROR\"")) {
                        System.out.println("Ошибка: " + message);
                        client.application.showNotification(message);
                    } else if (message.startsWith("{\"type\":\"START\"")) {
                        Platform.runLater(() -> {
                            System.out.println("Запуск игрового цикла...");
                            client.getApplication().getGame().startGameLoop();
                            client.getApplication().setView(client.getApplication().getGame());
                        });
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
