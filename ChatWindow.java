import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ChatWindow extends JFrame {

    private JTextPane  chatArea;
    private JTextField inputField;
    private JButton    sendButton;
    private JLabel     statusLabel;

    private Client   client;
    private DBHelper dbHelper;

    // Colours
    private static final Color COLOR_OWN    = new Color(100, 190, 255);
    private static final Color COLOR_OTHER  = new Color(210, 220, 240);
    private static final Color COLOR_SERVER = new Color(255, 210, 80);
    private static final Color COLOR_HIST   = new Color(140, 150, 170);
    private static final Color COLOR_BG     = new Color(18, 20, 35);
    private static final Color COLOR_PANEL  = new Color(25, 28, 50);
    private static final Color COLOR_TOPBAR = new Color(30, 35, 62);

    // Font sizes — bumped up for readability
    private static final int FONT_MSG    = 17;
    private static final int FONT_INPUT  = 17;
    private static final int FONT_TITLE  = 22;
    private static final int FONT_STATUS = 14;

    public ChatWindow(Client client) {
        this.client   = client;
        this.dbHelper = new DBHelper();

        setTitle("💬 LAN Chat  —  " + client.getUsername());
        setSize(950, 680);                          // ← much larger window
        setMinimumSize(new Dimension(700, 500));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        buildUI();
        loadHistory();
        registerClientListener();
        setVisible(true);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                client.disconnect();
                dbHelper.close();
            }
        });
    }

    private void buildUI() {
        setBackground(COLOR_BG);

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(COLOR_TOPBAR);
        topBar.setBorder(BorderFactory.createEmptyBorder(14, 20, 14, 20));

        JLabel titleLbl = new JLabel("💬  LAN Chat");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, FONT_TITLE));
        titleLbl.setForeground(new Color(120, 200, 255));

        statusLabel = new JLabel("● Connected as " + client.getUsername());
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, FONT_STATUS));
        statusLabel.setForeground(new Color(90, 220, 120));

        topBar.add(titleLbl,    BorderLayout.WEST);
        topBar.add(statusLabel, BorderLayout.EAST);

        // ── Chat area ─────────────────────────────────────────────────────────
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(COLOR_BG);
        chatArea.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));

        JScrollPane scroll = new JScrollPane(chatArea);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(COLOR_BG);
        scroll.setBackground(COLOR_BG);

        // ── Input bar ─────────────────────────────────────────────────────────
        JPanel inputBar = new JPanel(new BorderLayout(12, 0));
        inputBar.setBackground(COLOR_PANEL);
        inputBar.setBorder(BorderFactory.createEmptyBorder(12, 18, 14, 18));

        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, FONT_INPUT));
        inputField.setBackground(new Color(38, 42, 68));
        inputField.setForeground(new Color(220, 230, 255));
        inputField.setCaretColor(Color.WHITE);
        inputField.setPreferredSize(new Dimension(0, 46));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(70, 90, 150), 1),
                BorderFactory.createEmptyBorder(8, 14, 8, 14)));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendButton.setBackground(new Color(70, 130, 220));
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setBorderPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setPreferredSize(new Dimension(100, 46));
        sendButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { sendButton.setBackground(new Color(90, 160, 255)); }
            public void mouseExited (MouseEvent e) { sendButton.setBackground(new Color(70, 130, 220)); }
        });

        inputBar.add(inputField, BorderLayout.CENTER);
        inputBar.add(sendButton,  BorderLayout.EAST);

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // ── Assemble ──────────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        add(topBar,  BorderLayout.NORTH);
        add(scroll,  BorderLayout.CENTER);
        add(inputBar, BorderLayout.SOUTH);
    }

    private void loadHistory() {
        List<String> history = dbHelper.loadMessages();
        if (!history.isEmpty()) {
            appendMsg("─────────────  Previous Messages  ─────────────", COLOR_SERVER, true);
            for (String msg : history) appendMsg(msg, COLOR_HIST, false);
            appendMsg("─────────────────  Live Chat  ──────────────────", COLOR_SERVER, true);
        }
    }

    private void registerClientListener() {
        client.setMessageListener(message -> SwingUtilities.invokeLater(() -> {
            boolean isOwn    = message.startsWith(client.getUsername() + ":");
            boolean isServer = message.startsWith("SERVER:");
            Color color = isServer ? COLOR_SERVER : isOwn ? COLOR_OWN : COLOR_OTHER;
            appendMsg(message, color, false);
        }));
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        client.sendMessage(text);
        inputField.setText("");
        inputField.requestFocus();
    }

    private void appendMsg(String message, Color color, boolean italic) {
        StyledDocument doc   = chatArea.getStyledDocument();
        Style          style = chatArea.addStyle("s", null);
        StyleConstants.setForeground(style, color);
        StyleConstants.setFontFamily(style, "Segoe UI");
        StyleConstants.setFontSize(style, FONT_MSG);
        StyleConstants.setItalic(style, italic);
        try {
            doc.insertString(doc.getLength(), message + "\n", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        chatArea.setCaretPosition(doc.getLength());
    }
}
