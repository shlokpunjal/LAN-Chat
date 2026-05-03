import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBHelper {

    private static final String URL      = System.getProperty("db.url");
    private static final String USER     = System.getProperty("db.user");
    private static final String PASSWORD = System.getProperty("db.password");

    private Connection connection;

    public DBHelper() {
        connect();
        createTableIfNotExists();
    }

    private void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("[DB] Connected to MySQL successfully.");
        } catch (Exception e) {
            System.err.println("[DB] Connection failed: " + e.getMessage());
        }
    }

    private void createTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS messages (" +
                     "id INT AUTO_INCREMENT PRIMARY KEY," +
                     "sender VARCHAR(50) NOT NULL," +
                     "message TEXT NOT NULL," +
                     "timestamp DATETIME DEFAULT CURRENT_TIMESTAMP)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("[DB] Table ready.");
        } catch (SQLException e) {
            System.err.println("[DB] Table creation error: " + e.getMessage());
        }
    }

    public void saveMessage(String sender, String message) {
        String sql = "INSERT INTO messages (sender, message) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB] Save error: " + e.getMessage());
        }
    }

    public List<String> loadMessages() {
        List<String> history = new ArrayList<>();
        String sql = "SELECT sender, message, timestamp FROM messages ORDER BY timestamp ASC";
        try (Statement stmt = connection.createStatement();
             ResultSet  rs  = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String sender    = rs.getString("sender");
                String message   = rs.getString("message");
                String timestamp = rs.getTimestamp("timestamp").toString().substring(0, 16);
                history.add("[" + timestamp + "] " + sender + ": " + message);
            }
        } catch (SQLException e) {
            System.err.println("[DB] Load error: " + e.getMessage());
        }
        return history;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Close error: " + e.getMessage());
        }
    }
}
