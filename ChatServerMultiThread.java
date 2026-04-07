import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServerMultiThread {
    private static final int PORT = 12345;

    // Thread-safe set so client handlers can add/remove while others broadcast.
    private static final Set<ClientHandler> clients = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        System.out.println("Multi-thread chat server is running on port " + PORT + "...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                clients.add(handler);
                handler.start();
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage());
        }
    }

    private static void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.send(message);
            }
        }
    }

    private static class ClientHandler extends Thread {
        private static int nextId = 1;

        private final Socket socket;
        private final String clientName;
        private PrintWriter out;

        ClientHandler(Socket socket) {
            this.socket = socket;
            this.clientName = "User-" + getNextId();
        }

        private static synchronized int getNextId() {
            return nextId++;
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                send("Connected as " + clientName);
                broadcast(clientName + " joined the chat.", this);

                String message;
                while ((message = in.readLine()) != null) {
                    String formatted = clientName + ": " + message;
                    System.out.println(formatted);
                    broadcast(formatted, this);
                }
            } catch (IOException e) {
                System.err.println(clientName + " connection error: " + e.getMessage());
            } finally {
                clients.remove(this);
                broadcast(clientName + " left the chat.", this);
                try {
                    socket.close();
                } catch (IOException ignored) {
                    // Ignore close errors.
                }
            }
        }

        void send(String message) {
            if (out != null) {
                out.println(message);
            }
        }
    }
}
