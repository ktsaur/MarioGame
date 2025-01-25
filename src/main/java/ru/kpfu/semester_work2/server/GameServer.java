package ru.kpfu.semester_work2.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class GameServer {
    private ServerSocket serverSocket;
    private Map<String, GameRoom> rooms = new HashMap<>();

    public static void main(String[] args) {
        GameServer server = new GameServer();
        server.start();
    }

    static class GameRoom {
        private String roomId;
        private List<Client> clients = new ArrayList<>();
        private boolean gameEnded = false;

        public GameRoom(String roomId) {
            this.roomId = roomId;
        }

        public String getRoomId() {
            return roomId;
        }

        public void addClient(Client client) {
            clients.add(client);
            System.out.println("Добавили клиента. количество клиентов  = " + clients.size());
        }

        public List<Client> getClients() {
            return clients;
        }

        public boolean isFull() {
            System.out.println("Количество клиентов в комнате два штуки");
            return clients.size() == 2;
        }

        public void startGame() {
            for (Client client : clients) {
                client.sendMessage("{\"type\":\"START\"}");
            }
        }

        public boolean checkGameEnd() {
            boolean allPlayersFinished = clients.stream().allMatch(Client::hasReachedEndOfLevel);
            if (allPlayersFinished) {
                Client winner = null;
                double minTime = Double.MAX_VALUE;

                for (Client client : clients) {
                    double adjustedTime = client.getAdjustedTime();
                    if (adjustedTime < minTime) {
                        minTime = adjustedTime;
                        winner = client;
                    }
                }

                gameEnded = true;
                for (Client client : clients) {
                    client.sendMessage("{\"type\":\"GAME_OVER\",\"winner\":\"" + winner.getPlayerName() + "\",\"time\":" + minTime + "}");
                }
                System.out.println("Игра завершена! Победитель: " + winner.getPlayerName());
            }
            return gameEnded;
        }

        public boolean isGameEnded() {
            return gameEnded;
        }

    }

    static class Client implements Runnable {
        private GameServer server;
        private BufferedReader in;
        private BufferedWriter out;
        private GameRoom room;
        private String playerName;
        private int score;
        private double time;
        private int playerX;  // Координата X игрока
        private static final int LEVEL_END_X = 9474; // Примерное значение конца уровня

        public Client(GameServer server, BufferedReader in, BufferedWriter out) {
            this.server = server;
            this.in = in;
            this.out = out;
        }

        public int getScore() {
            return score;
        }

        public String getPlayerName() {
            return playerName;
        }

        public void updatePlayerPosition(int x) {
            this.playerX = x;
        }

        public boolean hasReachedEndOfLevel() {
            return playerX >= LEVEL_END_X;
        }

        public double getAdjustedTime() {
            // За каждые 100 очков вычитается 3 секунды
            int bonusTimeReduction = (score / 100) * 3;
            return time - bonusTimeReduction;
        }

        @Override
        public void run() { // метод для обработки входящих сообщений от клиента
            try{
                String message;
                while((message= in.readLine()) != null) {
                    if (message.startsWith("CREATE_ROOM")) {
                        String roomId = Integer.toString((int)(Math.random() * 9000) + 1000);
                        GameRoom room = new GameRoom(roomId);
                        server.rooms.put(roomId, room);
                        room.addClient(this);
                        this.room = room;
                        sendMessage("{\"type\":\"ROOM_CREATED\", \"roomId\":\"" + roomId + "\"}");
                    } else if (message.startsWith("JOIN_ROOM")) {
                        String roomId = message.split(" ")[1];
                        GameRoom room = server.rooms.get(roomId);
                        if (room != null && !room.isFull()) {
                            room.addClient(this);
                            System.out.println("Добавили клиента");
                            this.room = room;
                            sendMessage("{\"type\":\"JOINED\", \"roomId\":\"" + roomId + "\"}");
                            if (room.isFull()) {
                                System.out.println("Комната полная");
                                room.startGame();
                                System.out.println("Отправил сообщение о старте игры");
                            }
                        } else {
                            sendMessage("{\"type\":\"ERROR\", \"message\":\"Room is full or doesn't exist\"}");
                        }
                    } else if (message.startsWith("UPDATE_INFO")) {
                        System.out.println(message);
                        String[] parts = message.split(" ", 5);
                        this.playerName = parts[1];
                        this.score = Integer.parseInt(parts[2]);
                        this.time = (long) Double.parseDouble(parts[3]);
                        this.playerX = Integer.parseInt(parts[4]);

                        if (room != null) {
                            if (room.checkGameEnd()) break;
                            for (Client client : room.getClients()) {
                                if (client != this) {
                                    client.sendMessage("{\"type\":\"UPDATE_INFO\",\"playerName\":\"" + playerName +
                                            "\",\"score\":" + score + ",\"time\":" + time + ",\"x\":" + playerX + "}");
                                    System.out.println("{\"type\":\"UPDATE_INFO\",\"playerName\":\"" + playerName  +
                                            "\",\"score\":" + score + ",\"time\":" + time + ",\"x\":" + playerX + "}");
                                }
                            }
                        }
                    }
                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public void sendMessage(String message) {
            try {
                out.write(message + "\n");
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public void start(){
        try {
            serverSocket = new ServerSocket(8080);
            while (true) {
                Socket clientSocket = serverSocket.accept(); //начинает слушать подключения
                System.out.println("подключение принято от: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                Client client = new Client(this, in, out);
                new Thread(client).start();
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
