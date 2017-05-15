package lab6;

import com.oracle.javafx.jmx.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.server.ExportException;

/**
 * Created by alexandr on 14.05.17.
 */
public class Server {
    private ServerSocket serverSocket;
    private ServerListener serverListener;
    private Database database;
    private Logger logger;

    public Server(Logger logger) {
        this.logger = logger;
    }

    public void start(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            this.database = new Database();
            logger.addEvent("SERVER", "STARTED");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        if (serverListener != null) {
            serverListener.isWorking = false;
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            serverListener = null;
        }
    }

    public void listen() {
        if (serverListener != null)
            return;
        this.serverListener = new ServerListener(serverSocket);
        (new Thread(serverListener)).start();
        logger.addEvent("SERVER", "LISTENING");
    }


    private class ServerListener implements Runnable {
        private boolean isWorking;
        private ServerSocket serverSocket;

        public ServerListener(ServerSocket serverSocket) {
            this.isWorking = true;
            this.serverSocket = serverSocket;
        }

        @Override
        public void run() {
            while (isWorking) {
                try {
                    Socket socket = serverSocket.accept();
                    new ServerWorker(socket);
                } catch (IOException i) {

                }
            }
        }
    }

    private class ServerWorker implements Runnable {
        private PrintWriter writer;
        private BufferedReader reader;
        private Socket socket;
        private boolean authenticated;
        private String login;

        public ServerWorker(Socket socket) {
            this.socket = socket;
            try {
                logger.addEvent("SERVER", "NEW CONNECTION ESTABILISHED");
                this.writer = new PrintWriter(socket.getOutputStream(), false);
                this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                (new Thread(this)).start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private Message read() {
            try {
                String content = reader.readLine();
                try {
                    JSONObject jsonObject = (JSONObject) new JSONParser().parse(content);
                    logger.addEvent("CLIENT", "FROM " + (authenticated ? login : "anonymus") + ": " + content);
                    Message message = new Message(jsonObject.toJSONString());
                    return message;
                } catch (ParseException p) {
                    p.printStackTrace();
                    logger.addError("CLIENT", "FROM " + (authenticated ? login : "anonymus") + " (NOT JSON): " + content);
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.addError("CLIENT", "FROM " + (authenticated ? login : "anonymus") + " (NOT JSON): null");
            }
            return null;
        }

        private void send(Message message) {
            if (message.getType().equals("error"))
                logger.addError("SERVER", "SENT TO CLIENT " + message.toString());
            else
                logger.addEvent("SERVER", "SENT TO CLIENT " + message.toString());
            writer.println(message);
            writer.flush();
        }

        @Override
        public void run() {
            try {
                Message message = read();
                if (message == null) {
                    message = new Message.Builder("error")
                            .addParam("info", "bad request")
                            .addParam("code", "400")
                            .build();
                    socket.close();
                    send(message);
                    return;
                }
                login = message.getParam("login");
                String password = message.getParam("password");
                synchronized (database) {
                    boolean result = database.checkCredentials(login, password);
                    if (!result) {
                        message = new Message.Builder("error")
                                .addParam("info", "access denied")
                                .addParam("code", "403")
                                .build();
                        send(message);
                        writer.flush();
                        socket.close();
                        return;
                    }
                }
                logger.addEvent("SERVER", login + " AUTHENTICATED SUCCESSFULLY");
                message = new Message.Builder("success")
                        .addParam("info", "success")
                        .addParam("code", "200")
                        .build();
                send(message);
                while (!socket.isClosed() && socket.isConnected()) {
                    try {
                        System.out.println("TRYING...");
                        Message request = read();
                        if (request == null)
                            send(new Message.Builder("error")
                                    .addParam("info", "bad request")
                                    .addParam("code", "400")
                                    .build());
                        else
                            switch (request.getType()) {
                                case "exp":
                                    send(new Message.Builder("exp")
                                            .addParam("info", "success")
                                            .addParam("code", "200")
                                            .addParam("result", String.valueOf(Math.exp(Double.parseDouble(request.getParam("value")))))
                                            .build());
                                    break;
                            }
                    } catch (Exception e){
                        socket.close();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException ex) {

                }
            }
        }
    }
}
