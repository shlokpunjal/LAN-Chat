import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginWindow extends JFrame {

    private JTextField usernameField;
    private JTextField serverIPField;
    private JButton    connectButton;

    public LoginWindow() {
        setTitle("LAN Chat – Connect");
        setSize(520, 380);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        buildUI();
        setVisible(true);
    }

    private void buildUI() {
        JPanel bg = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(20, 20, 35), 0, getHeight(), new Color(35, 40, 65));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bg.setLayout(new GridBagLayout());
        setContentPane(bg);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 20, 12, 20);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("💬  LAN Chat", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 34));
        title.setForeground(new Color(120, 200, 255));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        bg.add(title, gbc);

        JLabel sub = new JLabel("Connect to your local network", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        sub.setForeground(new Color(150, 160, 180));
        gbc.gridy = 1;
        bg.add(sub, gbc);

        // Username row
        gbc.gridwidth = 1;
        gbc.gridy = 2; gbc.gridx = 0;
        bg.add(makeLabel("Username"), gbc);
        usernameField = makeTextField("Enter your name");
        gbc.gridx = 1;
        bg.add(usernameField, gbc);

        // Server IP row
        gbc.gridy = 3; gbc.gridx = 0;
        bg.add(makeLabel("Server IP"), gbc);
        serverIPField = makeTextField("e.g. 192.168.1.5");
        serverIPField.setText("localhost");
        gbc.gridx = 1;
        bg.add(serverIPField, gbc);

        // Connect button
        connectButton = new JButton("Connect  →");
        connectButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        connectButton.setBackground(new Color(70, 130, 220));
        connectButton.setForeground(Color.WHITE);
        connectButton.setFocusPainted(false);
        connectButton.setBorderPainted(false);
        connectButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        connectButton.setPreferredSize(new Dimension(200, 50));
        connectButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { connectButton.setBackground(new Color(90, 160, 255)); }
            public void mouseExited (MouseEvent e) { connectButton.setBackground(new Color(70, 130, 220)); }
        });

        gbc.gridy = 4; gbc.gridx = 0; gbc.gridwidth = 2;
        gbc.fill   = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        bg.add(connectButton, gbc);

        connectButton.addActionListener(e -> attemptConnect());
        getRootPane().setDefaultButton(connectButton);
    }

    private void attemptConnect() {
        String username = usernameField.getText().trim();
        String ip       = serverIPField.getText().trim();

        if (username.isEmpty()) { showError("Please enter a username."); return; }
        if (ip.isEmpty())       { showError("Please enter the server IP."); return; }

        Client client = new Client(ip, 9090, username);
        if (client.connect()) {
            new ChatWindow(client);
            dispose();
        } else {
            showError("Could not connect to " + ip + ":9090\nMake sure the server is running.");
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private JLabel makeLabel(String text) {
        JLabel lbl = new JLabel(text + ":");
        lbl.setForeground(new Color(180, 190, 210));
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        return lbl;
    }

    private JTextField makeTextField(String tooltip) {
        JTextField tf = new JTextField(16);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 17));
        tf.setBackground(new Color(45, 50, 75));
        tf.setForeground(new Color(220, 230, 255));
        tf.setCaretColor(Color.WHITE);
        tf.setPreferredSize(new Dimension(220, 42));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 100, 160), 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        tf.setToolTipText(tooltip);
        return tf;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginWindow::new);
    }
}
