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

    }

    static class Client implements Runnable {
        private GameServer server;
        private BufferedReader in;
        private BufferedWriter out;
        private GameRoom room;

        public Client(GameServer server, BufferedReader in, BufferedWriter out) {
            this.server = server;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() { // метод для обработки входящих сообщений от клиента
            try{
                String message;
                while((message= in.readLine()) != null) {//прием сообщений от подключенного клиента (ЭТО НЕ ВВОД ИМЕНИ)
                    if (message.startsWith("create_room")) {
                        String roomId = UUID.randomUUID().toString();
                        GameRoom room = new GameRoom(roomId);
                        server.rooms.put(roomId, room);
                        room.addClient(this);
                        this.room = room;
                        sendMessage("{\"type\":\"ROOM_CREATED\", \"roomId\":\"" + roomId + "\"}");
                    } else if (message.startsWith("join_room")) {
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
