import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FraudDetection {

    static final Color BG      = new Color(30, 30, 30);
    static final Color BG2     = new Color(45, 45, 45);
    static final Color BG3     = new Color(60, 60, 60);
    static final Color FG      = new Color(220, 220, 220);
    static final Color FG_DIM  = new Color(150, 150, 150);
    static final Color RED     = new Color(200,  60,  60);
    static final Color GREEN   = new Color( 60, 180,  80);
    static final Color RED_ROW = new Color( 70,  20,  20);
    static final Color GRN_ROW = new Color( 20,  55,  20);
    static final Color BORDER  = new Color( 80,  80,  80);

    static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("hh:mm:ss a");

    static final String[] STATES = {
        "Andhra Pradesh", "Arunachal Pradesh", "Assam", "Bihar",
        "Chhattisgarh", "Goa", "Gujarat", "Haryana", "Himachal Pradesh",
        "Jharkhand", "Karnataka", "Kerala", "Madhya Pradesh", "Maharashtra",
        "Manipur", "Meghalaya", "Mizoram", "Nagaland", "Odisha",
        "Punjab", "Rajasthan", "Sikkim", "Tamil Nadu", "Telangana",
        "Tripura", "Uttar Pradesh", "Uttarakhand", "West Bengal",
        "Andaman & Nicobar", "Chandigarh", "Dadra & Nagar Haveli",
        "Daman & Diu", "Delhi", "Jammu & Kashmir", "Ladakh",
        "Lakshadweep", "Puducherry"
    };

    static class Transaction {
        String cardNumber; double amount; String location; int hour;
        boolean isFraud; String reason; String timestamp;
        Transaction(String c, double a, String l, int h) {
            cardNumber = c; amount = a; location = l; hour = h;
            isFraud = false; reason = "OK";
            // Real wall-clock time when the transaction object is created
            timestamp = LocalTime.now().format(TIME_FMT);
        }
    }

    static List<Transaction>       allTransactions  = new ArrayList<>();
    static HashMap<String, String> cardHomeLocation = new HashMap<>();
    static final int VELOCITY_LIMIT = 3;
    static double amountThreshold = 10000.0;

    static Transaction analyze(String card, double amount, String loc, int hour) {
        Transaction t = new Transaction(card, amount, loc, hour);

        if (amount > amountThreshold) {
            t.isFraud = true;
            t.reason = "High amount (Rs." + String.format("%.0f", amount)
                     + " > Rs." + String.format("%.0f", amountThreshold) + ")";
        }
        if (hour >= 0 && hour <= 5) {
            t.isFraud = true;
            t.reason = (t.reason.equals("OK") ? "" : t.reason + " | ")
                     + "Suspicious hour (" + hour + ":00)";
        }
        if (!cardHomeLocation.containsKey(card)) cardHomeLocation.put(card, loc);
        String home = cardHomeLocation.get(card);
        if (!loc.equalsIgnoreCase(home)) {
            t.isFraud = true;
            t.reason = (t.reason.equals("OK") ? "" : t.reason + " | ")
                     + "Wrong state (" + loc + ", home=" + home + ")";
        }
        int count = 0;
        for (Transaction old : allTransactions)
            if (old.cardNumber.equals(card)) count++;
        if (count >= VELOCITY_LIMIT) {
            t.isFraud = true;
            t.reason = (t.reason.equals("OK") ? "" : t.reason + " | ")
                     + "Too many transactions (" + (count + 1) + " total)";
        }
        allTransactions.add(t);
        return t;
    }

    static void clearAll() { allTransactions.clear(); cardHomeLocation.clear(); }

    public static void main(String[] args) { SwingUtilities.invokeLater(() -> buildGUI()); }

    static void buildGUI() {
        JFrame frame = new JFrame("Card Fraud Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1100, 700);
        frame.getContentPane().setBackground(BG);
        frame.setLayout(new BorderLayout(8, 8));

        // ---- INPUT PANEL ----
        JPanel inputPanel = new JPanel(new BorderLayout(16, 0));
        inputPanel.setBackground(BG2);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));

        // LEFT: Card, Amount, State, buttons
        JPanel leftInputs = new JPanel(new GridBagLayout());
        leftInputs.setBackground(BG2);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(3, 5, 3, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;

        JTextField tfCard   = darkField("4111-1111-1111-1111");
        JTextField tfAmount = darkField("500");
        JComboBox<String> cbState = new JComboBox<>(STATES);
        styleCombo(cbState);

        gc.gridy = 0;
        gc.gridx = 0; leftInputs.add(darkLabel("Card Number"), gc);
        gc.gridx = 1; leftInputs.add(darkLabel("Amount (Rs.)"), gc);
        gc.gridx = 2; leftInputs.add(darkLabel("Home State"), gc);

        gc.gridy = 1;
        gc.gridx = 0; leftInputs.add(tfCard, gc);
        gc.gridx = 1; leftInputs.add(tfAmount, gc);
        gc.gridx = 2; leftInputs.add(cbState, gc);

        JButton btnSubmit = darkButton("Check Transaction", new Color(50, 100, 160));
        JButton btnClear  = darkButton("Clear History",     new Color(80,  40,  40));
        gc.gridy = 2;
        gc.gridx = 0; leftInputs.add(btnSubmit, gc);
        gc.gridx = 1; leftInputs.add(btnClear, gc);
        gc.gridx = 2;
        JLabel lblHint = darkLabel("First txn per card registers its home state");
        lblHint.setForeground(FG_DIM);
        leftInputs.add(lblHint, gc);

        // RIGHT: Slider + Simulate
        JPanel rightInputs = new JPanel(new GridBagLayout());
        rightInputs.setBackground(BG2);
        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(3, 5, 3, 5);
        gr.fill = GridBagConstraints.HORIZONTAL;
        gr.anchor = GridBagConstraints.WEST;
        gr.gridx = 0;

        JLabel lblThresh = darkLabel("Threshold: Rs. 10,000");
        lblThresh.setFont(new Font("SansSerif", Font.BOLD, 12));

        JSlider slider = new JSlider(1000, 100000, 10000);
        slider.setBackground(BG2);
        slider.setForeground(FG);
        slider.setMajorTickSpacing(25000);
        slider.setPaintTicks(true);
        slider.setPreferredSize(new Dimension(220, 45));
        slider.addChangeListener(e -> {
            amountThreshold = slider.getValue();
            lblThresh.setText("Threshold: Rs. " + String.format("%,.0f", amountThreshold));
        });

        JButton btnSimulate = darkButton("Simulate Transactions", new Color(80, 60, 130));

        gr.gridy = 0; rightInputs.add(lblThresh, gr);
        gr.gridy = 1; rightInputs.add(slider, gr);
        gr.gridy = 2; rightInputs.add(btnSimulate, gr);

        inputPanel.add(leftInputs,  BorderLayout.WEST);
        inputPanel.add(rightInputs, BorderLayout.EAST);

        // ---- TABLE ----
        // Status is col 5 now (time inserted at col 1, shifting everything right)
        String[] cols = {"Card Number", "Time", "Amount (Rs.)", "State", "Home State", "Status", "Reason"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setBackground(BG2);
        table.setForeground(FG);
        table.setGridColor(BORDER);
        table.setShowGrid(true);
        table.setSelectionBackground(BG3);

        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(160); // Card Number
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Time
        table.getColumnModel().getColumn(2).setPreferredWidth(100); // Amount
        table.getColumnModel().getColumn(3).setPreferredWidth(120); // State
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Home State
        table.getColumnModel().getColumn(5).setPreferredWidth(60);  // Status
        table.getColumnModel().getColumn(6).setPreferredWidth(340); // Reason

        JTableHeader th = table.getTableHeader();
        th.setBackground(BG3);
        th.setForeground(FG);
        th.setFont(new Font("SansSerif", Font.BOLD, 12));
        th.setPreferredSize(new Dimension(0, 30));

        // Status is now col 5
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object val,
                    boolean sel, boolean foc, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, foc, row, col);
                boolean fraud = "FRAUD".equals(t.getModel().getValueAt(row, 5));
                c.setBackground(fraud ? RED_ROW : GRN_ROW);
                c.setForeground(col == 5 ? (fraud ? RED : GREEN) : FG);
                if (sel) c.setBackground(BG3);
                setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(BG2);
        scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ---- CRITERIA PANEL (bottom half info box) ----
        JPanel criteriaPanel = new JPanel(new GridLayout(1, 4, 0, 0));
        criteriaPanel.setBackground(new Color(35, 35, 35));
        criteriaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        criteriaPanel.add(criteriaBox(
            "Rule 1 — High Amount",
            "Transaction exceeds the threshold set by the slider.\nDefault is Rs. 10,000. Drag the slider to change it.",
            new Color(180, 80, 50)
        ));
        criteriaPanel.add(criteriaBox(
            "Rule 2 — Odd Hours",
            "Transaction made between 12:00 AM and 5:00 AM.\nActive only during simulation (manual txns use 10 AM).",
            new Color(160, 130, 40)
        ));
        criteriaPanel.add(criteriaBox(
            "Rule 3 — Wrong State",
            "Transaction state doesn't match the card's home state.\nThe very first transaction registers the home state.",
            new Color(50, 100, 170)
        ));
        criteriaPanel.add(criteriaBox(
            "Rule 4 — Too Many Txns",
            "Same card used more than 3 times in total.\nEach additional transaction after 3 is flagged.",
            new Color(100, 60, 160)
        ));

        // ---- CENTER: table + criteria stacked ----
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BG);
        centerPanel.add(scroll,         BorderLayout.CENTER);
        centerPanel.add(criteriaPanel,  BorderLayout.SOUTH);

        // ---- STATUS BAR ----
        JLabel statusBar = new JLabel("  Ready.");
        statusBar.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusBar.setForeground(FG_DIM);
        statusBar.setBackground(BG2);
        statusBar.setOpaque(true);
        statusBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER),
            BorderFactory.createEmptyBorder(5, 12, 5, 12)
        ));

        // ---- SUBMIT ----
        btnSubmit.addActionListener(e -> {
            String card = tfCard.getText().trim();
            String loc  = (String) cbState.getSelectedItem();
            double amount;
            try {
                amount = Double.parseDouble(tfAmount.getText().trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Amount must be a number.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (amount <= 0) {
                JOptionPane.showMessageDialog(frame, "Amount must be greater than 0.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (card.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Card number cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Transaction t = analyze(card, amount, loc, 10);
            String home   = cardHomeLocation.getOrDefault(card, loc);
            model.addRow(new Object[]{
                t.cardNumber, t.timestamp, String.format("%.2f", t.amount),
                t.location, home, t.isFraud ? "FRAUD" : "SAFE", t.reason
            });
            scrollToBottom(table);
            updateStatus(statusBar, t, home);
        });

        // ---- CLEAR ----
        btnClear.addActionListener(e -> {
            model.setRowCount(0);
            clearAll();
            statusBar.setText("  History cleared.");
            statusBar.setForeground(FG_DIM);
        });

        // ---- SIMULATE ----
        Object[][] sim = {
            { "4111-1111-1111-1111",  500.0,   "Tamil Nadu",    10 },
            { "4111-1111-1111-1111",  250.0,   "Tamil Nadu",    12 },
            { "5200-8282-8282-8210",  15000.0, "Maharashtra",   15 },
            { "4111-1111-1111-1111",  300.0,   "Tamil Nadu",    16 },
            { "4111-1111-1111-1111",  800.0,   "Tamil Nadu",    18 },
            { "3714-496353-98431",    200.0,   "Delhi",          9 },
            { "3714-496353-98431",    100.0,   "Maharashtra",    3 },
            { "5200-8282-8282-8210",  50.0,    "Karnataka",      2 },
            { "6011-1111-1111-1117",  999.0,   "West Bengal",   12 },
            { "6011-1111-1111-1117", 25000.0,  "Rajasthan",      1 },
        };

        int[] idx = { 0 };
        Timer simTimer = new Timer(900, null);
        simTimer.addActionListener(e -> {
            if (idx[0] >= sim.length) {
                simTimer.stop();
                btnSimulate.setText("Simulate Transactions");
                btnSimulate.setEnabled(true);
                statusBar.setText("  Simulation complete — " + sim.length + " transactions processed.");
                statusBar.setForeground(FG_DIM);
                idx[0] = 0;
                return;
            }
            Object[] row  = sim[idx[0]];
            Transaction t = analyze((String)row[0], (double)row[1], (String)row[2], (int)row[3]);
            String home   = cardHomeLocation.getOrDefault((String)row[0], (String)row[2]);
            model.addRow(new Object[]{
                t.cardNumber, t.timestamp, String.format("%.2f", t.amount),
                t.location, home, t.isFraud ? "FRAUD" : "SAFE", t.reason
            });
            scrollToBottom(table);
            updateStatus(statusBar, t, home);
            idx[0]++;
        });

        btnSimulate.addActionListener(e -> {
            model.setRowCount(0);
            clearAll();
            idx[0] = 0;
            btnSimulate.setText("Simulating...");
            btnSimulate.setEnabled(false);
            statusBar.setText("  Running simulation...");
            statusBar.setForeground(FG_DIM);
            simTimer.start();
        });

        frame.add(inputPanel,   BorderLayout.NORTH);
        frame.add(centerPanel,  BorderLayout.CENTER);
        frame.add(statusBar,    BorderLayout.SOUTH);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // Builds one criteria info box with a colored left border
    static JPanel criteriaBox(String title, String body, Color accent) {
        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(new Color(38, 38, 38));
        box.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
            BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblTitle.setForeground(accent.brighter());

        // Multi-line body using HTML
        JLabel lblBody = new JLabel("<html>" + body.replace("\n", "<br>") + "</html>");
        lblBody.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblBody.setForeground(FG_DIM);

        box.add(lblTitle, BorderLayout.NORTH);
        box.add(lblBody,  BorderLayout.CENTER);

        // Wrap in a panel with a small gap on the right
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(new Color(35, 35, 35));
        wrapper.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
        wrapper.add(box);
        return wrapper;
    }

    static void scrollToBottom(JTable t) {
        int last = t.getRowCount() - 1;
        if (last >= 0) t.scrollRectToVisible(t.getCellRect(last, 0, true));
    }

    static void updateStatus(JLabel bar, Transaction t, String home) {
        if (t.isFraud) {
            bar.setText("  FRAUD: " + t.reason);
            bar.setForeground(new Color(200, 80, 80));
        } else {
            bar.setText("  SAFE — " + t.cardNumber + " | home state: " + home);
            bar.setForeground(new Color(80, 180, 80));
        }
    }

    static JLabel darkLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(new Color(180, 180, 180));
        l.setFont(new Font("SansSerif", Font.PLAIN, 12));
        return l;
    }

    static JTextField darkField(String text) {
        JTextField tf = new JTextField(text);
        tf.setBackground(new Color(55, 55, 55));
        tf.setForeground(new Color(220, 220, 220));
        tf.setCaretColor(Color.WHITE);
        tf.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        tf.setFont(new Font("Monospaced", Font.PLAIN, 13));
        return tf;
    }

    static void styleCombo(JComboBox<String> cb) {
        cb.setBackground(new Color(55, 55, 55));
        cb.setForeground(new Color(220, 220, 220));
        cb.setFont(new Font("SansSerif", Font.PLAIN, 12));
    }

    static JButton darkButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
