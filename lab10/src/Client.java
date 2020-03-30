import javafx.scene.control.TextArea;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Runnable {
    final private int PORT = 8000;

    public Socket socket;
    static PrintWriter toServer;
    BufferedReader fromServer;
    private TextArea chatMessages;
    private static String username;

    // Initialize the connection
    public Client() throws IOException {
        try {
            socket = new Socket(InetAddress.getLocalHost(), PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }

        toServer = new PrintWriter(socket.getOutputStream(), true);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

        chatMessages = new TextArea();
    }

    public static void sendText(String text) {
        toServer.write("(" + username + "): " + text + "\n");
        toServer.flush();
        return;
    }

    public void logout() throws IOException {
        System.out.println("Closing socket");
        toServer.close();
        fromServer.close();
        socket.close();
    }

    @Override
    public void run() {
        try {
            while (true) {
                String message = fromServer.readLine();
                if (!message.equals("")) {
                    chatMessages.appendText(message + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Getters and setters
    public TextArea getchatMessages() {
        return chatMessages;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }


}
