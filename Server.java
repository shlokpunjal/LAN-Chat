import java.io.*;
import java.net.*;
import java.util.*;

public class Server {

    private static final int PORT = 9090;
    private static final List<PrintWriter> clientWriters =
            Collections.synchronizedList(new ArrayList<>());

    private DBHelper dbHelper;

    public Server() {
        dbHelper = new DBHelper();
    }

    public void start() {
        System.out.println("[Server] Starting on port " + PORT + " ...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Waiting for clients...");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[Server] New client: "
                        + clientSocket.getInetAddress().getHostAddress());
                Thread t = new Thread(new ClientHandler(clientSocket, dbHelper));
                t.setDaemon(true);
                t.start();
            }
        } catch (IOException e) {
            System.err.println("[Server] Error: " + e.getMessage());
        } finally {
            dbHelper.close();
        }
    }

    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter w : clientWriters) w.println(message);
        }
    }

    public static void addClient(PrintWriter w)    { clientWriters.add(w); }
    public static void removeClient(PrintWriter w) { clientWriters.remove(w); }

    public static void main(String[] args) {
        new Server().start();
    }
}
