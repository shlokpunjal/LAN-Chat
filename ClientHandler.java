import java.io.*;
import java.net.*;

public class ClientHandler implements Runnable {

    private Socket      socket;
    private PrintWriter writer;
    private DBHelper    dbHelper;
    private String      clientName = "Unknown";

    public ClientHandler(Socket socket, DBHelper dbHelper) {
        this.socket   = socket;
        this.dbHelper = dbHelper;
    }

    @Override
    public void run() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()))) {

            writer = new PrintWriter(socket.getOutputStream(), true);
            Server.addClient(writer);

            String message;
            while ((message = reader.readLine()) != null) {
                if (message.startsWith("NAME:")) {
                    clientName = message.substring(5).trim();
                    System.out.println("[Server] " + clientName + " joined.");
                    Server.broadcast("SERVER: " + clientName + " has joined the chat!");
                } else {
                    String full = clientName + ": " + message;
                    System.out.println("[Server] " + full);
                    Server.broadcast(full);
                    dbHelper.saveMessage(clientName, message);
                }
            }
        } catch (IOException e) {
            System.out.println("[Server] Client disconnected: " + clientName);
        } finally {
            if (writer != null) Server.removeClient(writer);
            Server.broadcast("SERVER: " + clientName + " has left the chat.");
            try { socket.close(); } catch (IOException ignored) {}
        }
    }
}
