package ru.kpfu.semester_work2.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private ServerSocket serverSocket;
    private List<Client> clients = new ArrayList<>();
    private boolean gameStarted = false;

    static class Client implements Runnable {
        private GameServer server;
        private BufferedReader in;
        private BufferedWriter out;
        private String clientName;

        public Client(GameServer server, BufferedReader in, BufferedWriter out, String clientName) {
            this.server = server;
            this.in = in;
            this.out = out;
            this.clientName = clientName;
        }

        public static void main(String[] args) {
            GameServer server = new GameServer();
            server.start();
        }

        @Override
        public void run() {
            try{
                while(true) {
                    String message = in.readLine(); //прием сообщений от подключенного клиента (ЭТО НЕ ВВОД ИМЕНИ)
                    server.sendMessage(message, this); //пересылка сообщения клиенту.
                    // мы получили сообщение от текущего клиента и отправляем его всем
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void start(){
        try {
            serverSocket = new ServerSocket(8080);
            while (clients.size() < 2) {
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                String playerName = in.readLine();  // Здесь читаем имя игрока
                Client client = new Client(this, in, out, playerName);
                clients.add(client);
                new Thread(client).start();
                System.out.println("Player connected: " + playerName);
            }
            startGame();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized void startGame() throws IOException {
        if (clients.size() == 2 && !gameStarted) {
            gameStarted = true;
            for (Client client : clients) {
                client.out.write("READY\n");
                client.out.flush();
            }

            //оэидаем подтверждение от обоих игроков
            for (Client client : clients) {
                String response = client.in.readLine();
                if (!"READY".equals(response)) {
                    gameStarted = false;
                    return;
                }
            }

            for (int i = 0; i < clients.size(); i++) {
                Client client = clients.get(i);
                sendMessage("{\"type\":\"START\", \"player\":\"Player" + (i + 1) + "\"}", client);
            }
            System.out.println("Game started for both players.");
        }
    }

    private void sendMessage(String message, Client client) {
        for (Client c : clients) {
            if (c.equals(client)) {
                continue;
            }
            try {
                c.out.write(message + "\n");
                c.out.newLine();
                c.out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
