package ru.kpfu.semester_work2.client;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import ru.kpfu.semester_work2.GameApplication;

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
        sendMessage("CREATE_ROOM");
    }

    public void joinRoom(String roomId) {
        sendMessage("JOIN_ROOM " + roomId);
    }

    public void updatePlayerInfo(String playerName, int score, long time,  int playerX) {
        sendMessage("UPDATE_INFO " + playerName + " " + score + " " + time  + " " + playerX);
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
                        String[] parts = message.split("\"roomId\":\"");
                        String roomId = parts[1].split("\"")[0];
                        client.application.showMessage("Room created.", "RoomID: " + roomId, Alert.AlertType.INFORMATION);
                    } else if (message.startsWith("{\"type\":\"JOINED\"")) {
                        client.application.showNotification("Joined the room");
                    } else if (message.startsWith("{\"type\":\"ERROR\"")) {
                        client.application.showNotification("Room is full or doesn't exist");
                    } else if (message.startsWith("{\"type\":\"START\"")) {
                        Platform.runLater(() -> {
                            client.getApplication().getGame().startGameLoop();
                            client.getApplication().startGameTimer();
                            client.getApplication().setView(client.getApplication().getGame());
                        });
                    } else if (message.startsWith("{\"type\":\"UPDATE_INFO\"")) {
                        System.out.println(message);
                        System.out.println("сообщение из гейм клиент");
                        String[] parts = message.split(",", 5);
                        String opponentName = parts[1].split(":")[1].replace("\"", "");
                        int opponentScore = Integer.parseInt(parts[2].split(":")[1]);
                        long opponentTime = (long) Double.parseDouble(parts[3].split(":")[1]);
                        int playerX = Integer.parseInt(parts[4].split(":")[1].replace("}", ""));
                        System.out.println(opponentTime + "opponent time");

                        Platform.runLater(() -> {
                            System.out.println("тут сетю текст на лейбл опонента");
                            client.getApplication().getGame().updateOpponentInfo(opponentName, opponentScore, opponentTime);
                        });
                    } else if (message.startsWith("{\"type\":\"GAME_OVER\"")) {
                        String winner = message.split("\"winner\":\"")[1].split("\"")[0];
                        Platform.runLater(() -> {
                            client.application.showMessage("Game Over", "Winner: " + winner, Alert.AlertType.INFORMATION);
                            client.application.setView(client.application.getPlayerConfigView());
                        });
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
