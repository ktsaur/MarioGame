package ru.kpfu.semester_work2.client;

import javafx.application.Platform;
import javafx.stage.Stage;
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
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            clientThread = new ClientThread(in, out, this);

            new Thread(clientThread).start(); // запускаем поток для обработки входящих сообщений
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static class ClientThread implements Runnable {

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
                String message; //message - входящее сообщение
                while ((message = in.readLine()) != null) {
                    if (message.equals("READY")) {
                        System.out.println("Server is ready. Sending confirmation...");
                        out.write("READY\n");
                        out.flush();
                    } else if (message.startsWith("{\"type\":\"START\"")) {
                        Platform.runLater(() -> client.getApplication().getGame().startGameLoop());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
