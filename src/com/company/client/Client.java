package com.company.client;

import com.company.Connection;
import com.company.ConsoleHelper;
import com.company.Message;
import com.company.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }

    /**
     * main run
     **/
    public void run() {

        // Создавать новый сокетный поток с помощью метода getSocketThread
        SocketThread socketThread = getSocketThread();

        // Помечать созданный поток как daemon, это нужно для того, чтобы при выходе
        // из программы вспомогательный поток прервался автоматически.
        socketThread.setDaemon(true);

        // Запустить вспомогательный поток
        socketThread.start();

        // Заставить текущий поток ожидать, пока он не получит нотификацию из другого потока
        try {
            synchronized (this) {
                this.wait();
                System.out.println("After wait");
            }
        } catch (InterruptedException e) {
            ConsoleHelper.writeMessage("Ошибка");
            return;
        }


        if (clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");

            // сообщения с консоли пока клиент подключен. Если будет введена команда 'exit', то выход
            while (clientConnected) {
                String message = ConsoleHelper.readString();
                if (message.equals("exit")) break;

                if (shouldSendTextFromConsole()) {
                    sendTextMessage(message);
                }


            }
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }
    }

    /**
     * Должен запросить ввод адреса сервера и вернуть введенное значение
     **/
    protected String getServerAddress() {

        ConsoleHelper.writeMessage("Введите адрес сервера: ");
        return ConsoleHelper.readString();
    }


    /**
     * Должен запрашивать ввод порта сервера и возвращать его
     **/
    protected int getServerPort() {

        ConsoleHelper.writeMessage("Введите порт сервера: ");
        return ConsoleHelper.readInt();
    }


    /**
     * Должен запрашивать и возвращать имя пользователя
     **/
    protected String getUserName() {

        ConsoleHelper.writeMessage("Введите имя пользователя: ");
        return ConsoleHelper.readString();
    }


    protected boolean shouldSendTextFromConsole() {
        return true;
    }


    /**
     * должен создавать и возвращать новый объект класса SocketThread
     **/
    protected SocketThread getSocketThread() {

        return new SocketThread();
    }


    /**
     * создает новое текстовое сообщение, используя переданный текст и отправляет его серверу через соединение connection
     **/
    protected void sendTextMessage(String text) {

        try {
            connection.send(new Message(MessageType.TEXT, text));

        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка отправки");
            clientConnected = false;
        }
    }


    /**
     * class thread
     **/
    public class SocketThread extends Thread {

        public void run() {

            try {
                // Создай новый объект класса java.net.Socket c запросом сервера и порта
                Socket socket = new Socket(getServerAddress(), getServerPort());

                Client.this.connection = new Connection(socket);


                clientHandshake();
                clientMainLoop();


            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
            }

        }


        /**
         * clientHandshake
         **/
        protected void clientHandshake() throws IOException, ClassNotFoundException {

            while (true) {

                Message message = connection.receive();

                switch (message.getType()) {

                    // 	Если тип полученного сообщения NAME_REQUEST (сервер запросил имя)
                    case NAME_REQUEST:

                        String userName = getUserName();
                        connection.send(new Message(MessageType.USER_NAME, userName));
                        break;


                    case NAME_ACCEPTED:
                        notifyConnectionStatusChanged(true);
                        return;


                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }


        /**
         * Этот метод будет реализовывать главный цикл обработки сообщений сервера
         **/
        protected void clientMainLoop() throws IOException, ClassNotFoundException {

            while (true) {

                Message message = connection.receive();

                switch (message.getType()) {

                    // Если это текстовое сообщение (тип TEXT), processIncomingMessage()
                    case TEXT:
                        processIncomingMessage(message.getData());
                        break;

                    // Если это сообщение с типом USER_ADDED, informAboutAddingNewUser()
                    case USER_ADDED:
                        informAboutAddingNewUser(message.getData());
                        break;

                    // Если это сообщение с типом USER_REMOVED, informAboutDeletingNewUser()
                    case USER_REMOVED:
                        informAboutDeletingNewUser(message.getData());
                        break;

                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }


        /**
         * выводит текст message в консоль
         **/
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }


        /**
         * выводит в консоль информацию о том, что участник с именем userName присоединился к чату
         **/
        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("участник " + userName + " присоединился к чату");
        }


        /**
         * выводит в консоль, что участник с именем userName покинул чат
         **/
        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("участник " + userName + " покинул чат");
        }


        /**
         * Устанавливать значение поля clientConnected класса Client в соответствии с
         * переданным параметром.
         * Оповещать (пробуждать ожидающий) основной поток класса Client
         **/
        protected void notifyConnectionStatusChanged(boolean clientConnected) {

            Client.this.clientConnected = clientConnected;

            synchronized (Client.this) {
                Client.this.notify();
            }
        }
    }


}