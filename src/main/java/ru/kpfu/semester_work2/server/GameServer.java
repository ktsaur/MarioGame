package ru.kpfu.semester_work2.server;

import org.w3c.dom.ls.LSOutput;

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

        public Client(GameServer server, BufferedReader in, BufferedWriter out) {
            this.server = server;
            this.in = in;
            this.out = out;
        }

        public static void main(String[] args) {
            GameServer server = new GameServer();
            server.start();
        }

        @Override
        public void run() { // метод для обработки входящих сообщений от клиента
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

                Socket clientSocket = serverSocket.accept(); //начинает слушать подключения
                System.out.println("подключение принято от: " + clientSocket.getInetAddress());

                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                Client client = new Client(this, in, out);
                clients.add(client);
                System.out.println("Клиент добавлен. Всего клиентов: " + clients.size());

                new Thread(client).start();
            }

            if (clients.size() == 2) {
                System.out.println("Два клиента подключены. Начинаем игру...");
                for (Client client : clients) { sendMessage("{\"type\":\"START\"}", client); }
                //мы отправили сообщения клиентам о том что игра началась
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
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
                System.out.println("Сообщение отправлено клиенту: " + message);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        gameStarted = true;
    }
}
