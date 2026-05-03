import java.io.*;
import java.net.*;

public class Client {

    private Socket         socket;
    private PrintWriter    writer;
    private BufferedReader reader;
    private String         serverIP;
    private int            port;
    private String         username;
    private MessageListener messageListener;

    public Client(String serverIP, int port, String username) {
        this.serverIP = serverIP;
        this.port     = port;
        this.username = username;
    }

    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    public boolean connect() {
        try {
            socket = new Socket(serverIP, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer.println("NAME:" + username);

            Thread t = new Thread(this::listenForMessages);
            t.setDaemon(true);
            t.start();

            System.out.println("[Client] Connected to " + serverIP + ":" + port);
            return true;
        } catch (IOException e) {
            System.err.println("[Client] Connection failed: " + e.getMessage());
            return false;
        }
    }

    private void listenForMessages() {
        try {
            String msg;
            while ((msg = reader.readLine()) != null) {
                if (messageListener != null) messageListener.onMessageReceived(msg);
            }
        } catch (IOException e) {
            if (messageListener != null)
                messageListener.onMessageReceived("SERVER: Disconnected.");
        }
    }

    public void sendMessage(String message) {
        if (writer != null) writer.println(message);
    }

    public void disconnect() {
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[Client] Disconnect error: " + e.getMessage());
        }
    }

    public String getUsername() { return username; }

    public interface MessageListener {
        void onMessageReceived(String message);
    }
}
