import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    final private int PORT = 8000;
    private ServerSocket serverSocket;
    private static ArrayList<Socket> clientList;

    public Server() {
        // Create the server
        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("running");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() throws IOException {
        clientList = new ArrayList<>();

        // Infinite loop for listening to client connections
        while (true) {
            Socket client =  serverSocket.accept();
            clientList.add(client);

            // Assign the client to a thread
            ClientHandler clientHandler = new ClientHandler(client);
            Thread t = new Thread(clientHandler);
            t.start();
        }
    }

    // synchronized method to make sure only one client is allowed to send text at a time
    public static synchronized void sendMessage(String text) throws IOException {
        // iterate over items in the list and write to the socket
        for (Socket client : clientList) {
            if (!client.isClosed()) {
                PrintWriter output = new PrintWriter(client.getOutputStream());
                output.println(text + "\n");
                output.flush();
            }

        }
        return;
    }

    public static void main(String[] args) throws IOException {
        new Server().startListening();
    }

    // Handler class to listen for input
    class ClientHandler implements Runnable {
        Socket clientSocket;

        ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                BufferedReader clientInput = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                while (true) {
                    String message = clientInput.readLine();

                    if (!message.equals("")) {
                        Server.sendMessage(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
