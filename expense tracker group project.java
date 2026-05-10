import java.awt.*;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;

class Transaction {
    int id;
    String type;
    double amount;
    String category;
    String description;
    LocalDate date;

    public Transaction(int id, String type, double amount,
                       String category, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = LocalDate.now();
    }

    public void display() {
        System.out.println("--------------------------------");
        System.out.println("ID          : " + id);
        System.out.println("Type        : " + type);
        System.out.println("Amount      : " + amount);
        System.out.println("Category    : " + category);
        System.out.println("Description : " + description);
    }
}

class FlowExpenseTracker extends JFrame {

    static ArrayList<Transaction> transactions = new ArrayList<>();
    static Scanner input = new Scanner(System.in);

    static int transactionId = 1;
    static double totalIncome = 0;
    static double totalExpense = 0;
    static double budget = 0;
    
    private final JTabbedPane tabbedPane;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JLabel dashboardLabel;
    private JLabel totalIncomeValue;
    private JLabel totalExpenseValue;
    private JLabel balanceValue;
    private JLabel budgetStatusValue;
    private JLabel budgetWarningLabel;
    private JLabel budgetCurrentValue;
    private JLabel budgetSpentValue;
    private JProgressBar budgetProgressBar;
    private JPanel incomeExpenseChartPanel;
    private JPanel budgetAnalysisChartPanel;
    private DefaultTableModel categoryAnalyticsModel;
    private JLabel trendsAnalyticsLabel;
    private JPanel comparisonAnalyticsPanel;
    private JTextArea reportsInfoArea;

    // Constructor for GUI
    public FlowExpenseTracker() {
        setTitle("💰 Flow Expense Tracker - Advanced Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(true);
        
        // Set modern look
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            System.err.println("Look and feel error: " + e.getMessage());
        }
        
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        
        tabbedPane.addTab("📊 Dashboard", createAdvancedDashboardPanel());
        tabbedPane.addTab("➕ Add Transaction", createAddTransactionPanel());
        tabbedPane.addTab("📋 View All", createViewTransactionPanel());
        tabbedPane.addTab("📈 Analytics", createAnalyticsPanel());
        tabbedPane.addTab("💾 Budget Manager", createSetBudgetPanel());
        tabbedPane.addTab("🔍 Search", createSearchPanel());
        tabbedPane.addTab("📥 Reports", createReportsPanel());
        
        add(tabbedPane);
        setVisible(true);
    }
    
    private JPanel createAdvancedDashboardPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        
        totalIncomeValue = new JLabel("₹0.00");
        totalExpenseValue = new JLabel("₹0.00");
        balanceValue = new JLabel("₹0.00");
        budgetStatusValue = new JLabel("₹0.00");
        
        statsPanel.add(createStatCard("Total Income", totalIncomeValue, new Color(76, 175, 80)));
        statsPanel.add(createStatCard("Total Expense", totalExpenseValue, new Color(244, 67, 54)));
        statsPanel.add(createStatCard("Current Balance", balanceValue, new Color(33, 150, 243)));
        statsPanel.add(createStatCard("Budget Status", budgetStatusValue, new Color(255, 152, 0)));
        
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setOpaque(false);
        topPanel.add(statsPanel);
        
        budgetWarningLabel = new JLabel(" ");
        budgetWarningLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        budgetWarningLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(budgetWarningLabel);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);
        
        // Chart panel
        JPanel chartPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        chartPanel.setOpaque(false);
        incomeExpenseChartPanel = createChartPanel("Income vs Expense", true);
        budgetAnalysisChartPanel = createChartPanel("Budget Analysis", false);
        chartPanel.add(incomeExpenseChartPanel);
        chartPanel.add(budgetAnalysisChartPanel);
        
        JScrollPane scrollPane = new JScrollPane(chartPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("🔄 Refresh Data");
        refreshBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshBtn.setBackground(new Color(33, 150, 243));
        refreshBtn.setForeground(Color.WHITE);
        styleButton(refreshBtn);
        refreshBtn.addActionListener(e -> updateAllTabs());
        mainPanel.add(refreshBtn, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private void styleButton(JButton button) {
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
    }
    
    private JPanel createStatCard(String title, JLabel valueLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 3),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(new Color(100, 100, 100));
        
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createChartPanel(String title, boolean isIncomeExpense) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                g2.setColor(new Color(240, 240, 240));
                g2.fillRect(0, 0, width, height);

                g2.setColor(new Color(200, 200, 200));
                g2.setStroke(new BasicStroke(1));
                g2.drawRect(0, 0, width - 1, height - 1);

                g2.setColor(new Color(100, 100, 100));
                g2.setFont(new Font("Segoe UI", Font.BOLD, 14));
                g2.drawString(title, 15, 30);

                if (isIncomeExpense) {
                    if (totalIncome + totalExpense > 0) {
                        int centerX = width / 2;
                        int centerY = height / 2 + 10;
                        int radius = 60;

                        double incomeAngle = (totalIncome / (totalIncome + totalExpense)) * 360;
                        g2.setColor(new Color(76, 175, 80));
                        g2.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, (int) incomeAngle);

                        g2.setColor(new Color(244, 67, 54));
                        g2.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, (int) incomeAngle, 360 - (int) incomeAngle);
                    } else {
                        g2.setColor(new Color(150, 150, 150));
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        g2.drawString("Add income or expense to see analysis", width / 2 - 100, height / 2);
                    }
                } else {
                    if (budget > 0) {
                        int barWidth = 80;
                        int maxBarHeight = height - 120;
                        double maxValue = Math.max(budget, totalExpense);
                        double expenseHeight = (totalExpense / maxValue) * maxBarHeight;
                        double remaining = Math.max(0, budget - totalExpense);
                        double remainingHeight = (remaining / maxValue) * maxBarHeight;
                        double budgetHeight = (budget / maxValue) * maxBarHeight;

                        int baseY = height - 40;

                        g2.setColor(new Color(33, 150, 243));
                        g2.fillRect(width / 4 - barWidth / 2, baseY - (int) budgetHeight, barWidth, (int) budgetHeight);
                        g2.setColor(new Color(244, 67, 54));
                        g2.fillRect(width / 2 - barWidth / 2, baseY - (int) expenseHeight, barWidth, (int) expenseHeight);
                        g2.setColor(new Color(76, 175, 80));
                        g2.fillRect(3 * width / 4 - barWidth / 2, baseY - (int) remainingHeight, barWidth, (int) remainingHeight);

                        g2.setColor(Color.BLACK);
                        g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        g2.drawString("Budget", width / 4 - 25, baseY + 20);
                        g2.drawString("Expense", width / 2 - 30, baseY + 20);
                        g2.drawString("Remaining", 3 * width / 4 - 45, baseY + 20);
                    } else {
                        g2.setColor(new Color(150, 150, 150));
                        g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                        g2.drawString("Set a budget to show analysis", width / 2 - 80, height / 2);
                    }
                }
            }
        };
        panel.setBackground(Color.WHITE);
        panel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.setPreferredSize(new Dimension(300, 250));
        return panel;
    }
    
    private JPanel createAnalyticsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JTabbedPane analyticsTabs = new JTabbedPane();
        analyticsTabs.addTab("Category Breakdown", createCategoryAnalytics());
        analyticsTabs.addTab("Monthly Trends", createTrendsAnalytics());
        analyticsTabs.addTab("Expense vs Income", createComparisonAnalytics());
        
        mainPanel.add(analyticsTabs, BorderLayout.CENTER);
        return mainPanel;
    }
    
    private JPanel createCategoryAnalytics() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columnNames = {"Category", "Count", "Total Amount", "Percentage"};
        categoryAnalyticsModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        
        for (Transaction t : transactions) {
            categoryMap.put(t.category, categoryMap.getOrDefault(t.category, 0.0) + t.amount);
            categoryCount.put(t.category, categoryCount.getOrDefault(t.category, 0) + 1);
        }
        
        double total = totalIncome + totalExpense;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            double percentage = total > 0 ? (entry.getValue() / total) * 100 : 0;
            categoryAnalyticsModel.addRow(new Object[]{entry.getKey(), categoryCount.get(entry.getKey()), 
                                      String.format("₹%.2f", entry.getValue()), String.format("%.1f%%", percentage)});
        }
        
        JTable table = new JTable(categoryAnalyticsModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTrendsAnalytics() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        trendsAnalyticsLabel = new JLabel();
        trendsAnalyticsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        updateTrendsAnalyticsData();
        
        panel.add(trendsAnalyticsLabel, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createComparisonAnalytics() {
        comparisonAnalyticsPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int width = getWidth();
                int height = getHeight();
                int margin = 60;
                int chartHeight = height - 2*margin;
                
                // Draw axes
                g2.setColor(new Color(80, 80, 80));
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(margin, height-margin, margin, margin);
                g2.drawLine(margin, height-margin, width-margin, height-margin);
                
                double maxValue = Math.max(totalIncome, totalExpense);
                if (maxValue > 0) {
                    int incomeBarHeight = (int)((totalIncome / maxValue) * chartHeight * 0.8);
                    int expenseBarHeight = (int)((totalExpense / maxValue) * chartHeight * 0.8);

                    g2.setColor(new Color(76, 175, 80));
                    g2.fillRect(margin+50, (int)(height-margin-incomeBarHeight), 60, incomeBarHeight);
                    
                    g2.setColor(new Color(244, 67, 54));
                    g2.fillRect(margin+150, (int)(height-margin-expenseBarHeight), 60, expenseBarHeight);
                }
                
                // Labels
                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                g2.drawString("Income", margin+50, height-margin+30);
                g2.drawString("Expense", margin+150, height-margin+30);
                g2.drawString("₹", margin-25, margin+10);
            }
        };
        comparisonAnalyticsPanel.setBackground(Color.WHITE);
        comparisonAnalyticsPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        return comparisonAnalyticsPanel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        
        JButton exportCSV = new JButton("📊 Export to CSV");
        exportCSV.setFont(new Font("Segoe UI", Font.BOLD, 12));
        exportCSV.setBackground(new Color(33, 150, 243));
        exportCSV.setForeground(Color.WHITE);
        styleButton(exportCSV);
        exportCSV.addActionListener(e -> exportToCSV());
        
        JButton printReport = new JButton("🖨️ Print Report");
        printReport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        printReport.setBackground(new Color(33, 150, 243));
        printReport.setForeground(Color.WHITE);
        styleButton(printReport);
        printReport.addActionListener(e -> printReport());
        
        JButton summaryReport = new JButton("📄 Generate Summary");
        summaryReport.setFont(new Font("Segoe UI", Font.BOLD, 12));
        summaryReport.setBackground(new Color(33, 150, 243));
        summaryReport.setForeground(Color.WHITE);
        styleButton(summaryReport);
        summaryReport.addActionListener(e -> showSummaryReport());
        
        JButton clearData = new JButton("🗑️ Clear All Data");
        clearData.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearData.setBackground(new Color(244, 67, 54));
        clearData.setForeground(Color.WHITE);
        styleButton(clearData);
        clearData.addActionListener(e -> clearAllData());
        
        buttonPanel.add(exportCSV);
        buttonPanel.add(printReport);
        buttonPanel.add(summaryReport);
        buttonPanel.add(clearData);
        
        panel.add(buttonPanel, BorderLayout.NORTH);
        
        reportsInfoArea = new JTextArea();
        reportsInfoArea.setText("Reports Panel:\n\n" +
            "• Export CSV: Save all transactions to CSV file\n" +
            "• Print Report: Print current financial summary\n" +
            "• Summary Report: View detailed financial overview\n" +
            "• Clear Data: Remove all transactions (be careful!)\n\n" +
            "Current Status:\n" +
            "Total Transactions: " + transactions.size() + "\n" +
            "Total Income: ₹" + String.format("%.2f", totalIncome) + "\n" +
            "Total Expense: ₹" + String.format("%.2f", totalExpense));
        reportsInfoArea.setEditable(false);
        reportsInfoArea.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        panel.add(new JScrollPane(reportsInfoArea), BorderLayout.CENTER);
        return panel;
    }
    
    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("transactions_report.csv")) {
            writer.write("ID,Type,Amount,Category,Description,Date\n");
            for (Transaction t : transactions) {
                writer.write(t.id + "," + t.type + "," + t.amount + "," + t.category + "," + t.description + "," + t.date + "\n");
            }
            JOptionPane.showMessageDialog(this, "Report exported to transactions_report.csv", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void printReport() {
        StringBuilder report = new StringBuilder();
        report.append("\n=== FLOW EXPENSE TRACKER REPORT ===\n\n");
        report.append("Generated: ").append(LocalDate.now()).append("\n\n");
        report.append("FINANCIAL SUMMARY:\n");
        report.append("Total Income: ₹").append(String.format("%.2f", totalIncome)).append("\n");
        report.append("Total Expense: ₹").append(String.format("%.2f", totalExpense)).append("\n");
        report.append("Balance: ₹").append(String.format("%.2f", totalIncome - totalExpense)).append("\n");
        report.append("Budget: ₹").append(String.format("%.2f", budget)).append("\n\n");
        report.append("Remaining Budget: ₹").append(String.format("%.2f", budget - totalExpense)).append("\n\n");
        
        System.out.println(report.toString());
        JOptionPane.showMessageDialog(this, report.toString(), "Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showSummaryReport() {
        String summary = "<html><body style='padding: 20px; font-family: Segoe UI;'>" +
            "<h2>📊 Financial Summary Report</h2>" +
            "<p><b>Generated:</b> " + LocalDate.now() + "</p>" +
            "<hr>" +
            "<p><b>Total Income:</b> ₹" + String.format("%.2f", totalIncome) + "</p>" +
            "<p><b>Total Expense:</b> ₹" + String.format("%.2f", totalExpense) + "</p>" +
            "<p><b>Net Balance:</b> <span style='color: " + (totalIncome-totalExpense >= 0 ? "green" : "red") + ";'>" +
            "₹" + String.format("%.2f", totalIncome - totalExpense) + "</span></p>" +
            "<p><b>Monthly Budget:</b> ₹" + String.format("%.2f", budget) + "</p>" +
            "<p><b>Spent Percentage:</b> " + String.format("%.1f%%", budget > 0 ? (totalExpense/budget)*100 : 0) + "</p>" +
            "<p><b>Total Transactions:</b> " + transactions.size() + "</p>" +
            "</body></html>";
        
        JLabel label = new JLabel(summary);
        JOptionPane.showMessageDialog(this, label, "Summary Report", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearAllData() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete all transactions? This cannot be undone!", 
            "Confirm Clear", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            transactions.clear();
            totalIncome = 0;
            totalExpense = 0;
            transactionId = 1;
            updateAllTabs();
            JOptionPane.showMessageDialog(this, "All data cleared!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private JPanel createAddTransactionPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Type
        JLabel typeLabel = new JLabel("🏷️ Transaction Type:");
        typeLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        String[] types = {"Income", "Expense"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Amount
        JLabel amountLabel = new JLabel("💵 Amount:");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField amountField = new JTextField();
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Category
        JLabel categoryLabel = new JLabel("📂 Category:");
        categoryLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JComboBox<String> categoryCombo = new JComboBox<>(new String[]{
            "Food", "Transport", "Utilities", "Entertainment", "Health", "Shopping", "Salary", "Bonus", "Investment", "Other"
        });
        categoryCombo.setEditable(true);
        categoryCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        // Description
        JLabel descLabel = new JLabel("📝 Description:");
        descLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextArea descField = new JTextArea(2, 20);
        descField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descField.setLineWrap(true);
        descField.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descField);
        
        formPanel.add(typeLabel);
        formPanel.add(typeCombo);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryCombo);
        formPanel.add(descLabel);
        formPanel.add(descScroll);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton addButton = new JButton("✅ Add Transaction");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addButton.setBackground(new Color(76, 175, 80));
        addButton.setForeground(Color.WHITE);
        styleButton(addButton);
        addButton.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());
                String category = (String) categoryCombo.getSelectedItem();
                String description = descField.getText();
                
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be greater than 0!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (category == null || category.isEmpty() || description.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Please fill all fields!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                Transaction t = new Transaction(transactionId, type, amount, category, description);
                transactions.add(t);
                
                if (type.equals("Income")) {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
                
                transactionId++;
                
                JOptionPane.showMessageDialog(this, "✅ Transaction Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                amountField.setText("");
                descField.setText("");
                updateAllTabs();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid amount!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton clearButton = new JButton("🔄 Clear Form");
        clearButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clearButton.setBackground(new Color(158, 158, 158));
        clearButton.setForeground(Color.WHITE);
        styleButton(clearButton);
        clearButton.addActionListener(e -> {
            amountField.setText("");
            descField.setText("");
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(clearButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createViewTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columnNames = {"ID", "Type", "Amount", "Category", "Description", "Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        transactionTable.setRowHeight(25);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setOpaque(false);
        
        JButton refreshButton = new JButton("🔄 Refresh");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        refreshButton.setBackground(new Color(33, 150, 243));
        refreshButton.setForeground(Color.WHITE);
        styleButton(refreshButton);
        refreshButton.addActionListener(e -> updateTransactionTable());
        
        JButton deleteButton = new JButton("🗑️ Delete Selected");
        deleteButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.WHITE);
        styleButton(deleteButton);
        deleteButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                int confirm = JOptionPane.showConfirmDialog(this, "Delete transaction ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    deleteTransactionById(id);
                    updateTransactionTable();
                    updateAllTabs();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JButton editButton = new JButton("✏️ View Details");
        editButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        editButton.setBackground(new Color(255, 152, 0));
        editButton.setForeground(Color.WHITE);
        styleButton(editButton);
        editButton.addActionListener(e -> {
            int selectedRow = transactionTable.getSelectedRow();
            if (selectedRow >= 0) {
                int id = (int) tableModel.getValueAt(selectedRow, 0);
                for (Transaction t : transactions) {
                    if (t.id == id) {
                        JOptionPane.showMessageDialog(this, 
                            "ID: " + t.id + "\nType: " + t.type + "\nAmount: ₹" + t.amount + 
                            "\nCategory: " + t.category + "\nDescription: " + t.description + "\nDate: " + t.date,
                            "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
                        break;
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please select a transaction!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        updateTransactionTable();
        return panel;
    }
    
    private JPanel createSetBudgetPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBackground(new Color(240, 242, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        JPanel formPanel = new JPanel(new GridLayout(4, 2, 15, 15));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(25, 25, 25, 25)
        ));
        
        JLabel titleLabel = new JLabel("💾 Budget Manager");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        
        JLabel budgetLabel = new JLabel("Monthly Budget (₹):");
        budgetLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField budgetField = new JTextField();
        budgetField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        JLabel currentLabel = new JLabel("Current Budget:");
        currentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetCurrentValue = new JLabel("₹" + String.format("%.2f", budget));
        budgetCurrentValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        budgetCurrentValue.setForeground(new Color(33, 150, 243));
        
        JLabel spentLabel = new JLabel("Amount Spent:");
        spentLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        budgetSpentValue = new JLabel("₹" + String.format("%.2f", totalExpense));
        budgetSpentValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        budgetSpentValue.setForeground(new Color(244, 67, 54));
        
        formPanel.add(titleLabel);
        formPanel.add(new JLabel());
        formPanel.add(budgetLabel);
        formPanel.add(budgetField);
        formPanel.add(currentLabel);
        formPanel.add(budgetCurrentValue);
        formPanel.add(spentLabel);
        formPanel.add(budgetSpentValue);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        JPanel progressPanel = new JPanel(new BorderLayout(10, 10));
        progressPanel.setOpaque(false);
        progressPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel progressLabel = new JLabel("Budget Progress:");
        progressLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        budgetProgressBar = new JProgressBar(0, 100);
        int progress = budget > 0 ? (int)((totalExpense / budget) * 100) : 0;
        budgetProgressBar.setValue(Math.min(progress, 100));
        budgetProgressBar.setStringPainted(true);
        budgetProgressBar.setString(progress + "% Spent");
        budgetProgressBar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        
        progressPanel.add(progressLabel, BorderLayout.NORTH);
        progressPanel.add(budgetProgressBar, BorderLayout.CENTER);
        
        mainPanel.add(progressPanel, BorderLayout.SOUTH);
        
        JButton setBudgetButton = new JButton("💾 Set Budget");
        setBudgetButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        setBudgetButton.setBackground(new Color(76, 175, 80));
        setBudgetButton.setForeground(Color.WHITE);
        styleButton(setBudgetButton);
        setBudgetButton.addActionListener(e -> {
            try {
                double newBudget = Double.parseDouble(budgetField.getText());
                if (newBudget < 0) {
                    JOptionPane.showMessageDialog(this, "Budget cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                budget = newBudget;
                budgetCurrentValue.setText("₹" + String.format("%.2f", budget));
                int p = budget > 0 ? (int)((totalExpense / budget) * 100) : 0;
                budgetProgressBar.setValue(Math.min(p, 100));
                budgetProgressBar.setString(p + "% Spent");
                JOptionPane.showMessageDialog(this, "💾 Budget Set Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                budgetField.setText("");
                updateAllTabs();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter a valid budget!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(setBudgetButton);
        mainPanel.add(buttonPanel, BorderLayout.PAGE_END);
        
        return mainPanel;
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(240, 242, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JPanel searchPanel = new JPanel();
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel searchLabel = new JLabel("🔍 Search by Category:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        String[] columnNames = {"ID", "Type", "Amount", "Category", "Description", "Date"};
        DefaultTableModel searchTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable searchTable = new JTable(searchTableModel);
        searchTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchTable.setRowHeight(25);
        
        JScrollPane scrollPane = new JScrollPane(searchTable);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        
        JButton searchButton = new JButton("🔍 Search");
        searchButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchButton.setBackground(new Color(33, 150, 243));
        searchButton.setForeground(Color.WHITE);
        styleButton(searchButton);
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText();
            if (searchTerm.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a search term!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            searchTableModel.setRowCount(0);
            boolean found = false;
            
            for (Transaction t : transactions) {
                if (t.category.equalsIgnoreCase(searchTerm) || t.description.toLowerCase().contains(searchTerm.toLowerCase())) {
                    searchTableModel.addRow(new Object[]{t.id, t.type, t.amount, t.category, t.description, t.date});
                    found = true;
                }
            }
            
            if (!found) {
                JOptionPane.showMessageDialog(this, "No transactions found!", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        JButton clearSearch = new JButton("🗑️ Clear");
        clearSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        clearSearch.setBackground(new Color(158, 158, 158));
        clearSearch.setForeground(Color.WHITE);
        styleButton(clearSearch);
        clearSearch.addActionListener(e -> {
            searchField.setText("");
            searchTableModel.setRowCount(0);
        });
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(clearSearch);
        
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void updateTransactionTable() {
        tableModel.setRowCount(0);
        for (Transaction t : transactions) {
            tableModel.addRow(new Object[]{t.id, t.type, t.amount, t.category, t.description, t.date});
        }
    }

    private void updateCategoryAnalytics() {
        if (categoryAnalyticsModel == null) {
            return;
        }
        categoryAnalyticsModel.setRowCount(0);
        Map<String, Double> categoryMap = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();
        for (Transaction t : transactions) {
            categoryMap.put(t.category, categoryMap.getOrDefault(t.category, 0.0) + t.amount);
            categoryCount.put(t.category, categoryCount.getOrDefault(t.category, 0) + 1);
        }
        double total = totalIncome + totalExpense;
        for (Map.Entry<String, Double> entry : categoryMap.entrySet()) {
            double percentage = total > 0 ? (entry.getValue() / total) * 100 : 0;
            categoryAnalyticsModel.addRow(new Object[]{entry.getKey(), categoryCount.get(entry.getKey()),
                    String.format("₹%.2f", entry.getValue()), String.format("%.1f%%", percentage)});
        }
    }

    private void updateTrendsAnalyticsData() {
        if (trendsAnalyticsLabel == null) {
            return;
        }
        String text = "<html><body style='font-size: 14px;'><b>Monthly Trends:</b><br>" +
            "Total Transactions: " + transactions.size() + "<br>" +
            "Avg Transaction: ₹" + String.format("%.2f", transactions.isEmpty() ? 0 : (totalIncome + totalExpense) / transactions.size()) + "<br>" +
            "Highest Single Transaction: ₹" + (transactions.isEmpty() ? "0.00" :
            String.format("%.2f", transactions.stream().mapToDouble(t -> t.amount).max().orElse(0))) +
            "</body></html>";
        trendsAnalyticsLabel.setText(text);
    }

    private void updateReportsInfo() {
        if (reportsInfoArea == null) {
            return;
        }
        reportsInfoArea.setText("Reports Panel:\n\n" +
            "• Export CSV: Save all transactions to CSV file\n" +
            "• Print Report: Print current financial summary\n" +
            "• Summary Report: View detailed financial overview\n" +
            "• Clear Data: Remove all transactions (be careful!)\n\n" +
            "Current Status:\n" +
            "Total Transactions: " + transactions.size() + "\n" +
            "Total Income: ₹" + String.format("%.2f", totalIncome) + "\n" +
            "Total Expense: ₹" + String.format("%.2f", totalExpense));
    }

    private void updateAnalyticsPanels() {
        updateCategoryAnalytics();
        updateTrendsAnalyticsData();
        if (comparisonAnalyticsPanel != null) {
            comparisonAnalyticsPanel.repaint();
        }
    }
    
    private void updateDashboard() {
        totalIncomeValue.setText("₹" + String.format("%.2f", totalIncome));
        totalExpenseValue.setText("₹" + String.format("%.2f", totalExpense));
        double balance = totalIncome - totalExpense;
        balanceValue.setText("₹" + String.format("%.2f", balance));
        double remainingBudget = budget - totalExpense;
        budgetStatusValue.setText("₹" + String.format("%.2f", remainingBudget));
        
        if (budgetCurrentValue != null) {
            budgetCurrentValue.setText("₹" + String.format("%.2f", budget));
        }
        if (budgetSpentValue != null) {
            budgetSpentValue.setText("₹" + String.format("%.2f", totalExpense));
        }
        if (budgetProgressBar != null) {
            int progress = budget > 0 ? (int)((totalExpense / budget) * 100) : 0;
            budgetProgressBar.setValue(Math.min(progress, 100));
            budgetProgressBar.setString(progress + "% Spent");
        }
        if (incomeExpenseChartPanel != null) {
            incomeExpenseChartPanel.repaint();
        }
        if (budgetAnalysisChartPanel != null) {
            budgetAnalysisChartPanel.repaint();
        }
        if (budgetWarningLabel != null) {
            if (budget <= 0) {
                budgetWarningLabel.setText("No budget set. Enter a monthly budget to unlock advanced budget analysis.");
                budgetWarningLabel.setForeground(new Color(33, 150, 243));
            } else if (totalExpense > budget) {
                budgetWarningLabel.setText("⚠️ Budget exceeded! Expense is above your monthly budget.");
                budgetWarningLabel.setForeground(new Color(244, 67, 54));
            } else if (totalExpense > budget * 0.8) {
                budgetWarningLabel.setText("⚠️ High spending alert. You have used more than 80% of your budget.");
                budgetWarningLabel.setForeground(new Color(244, 67, 54));
            } else if (totalExpense > budget * 0.5) {
                budgetWarningLabel.setText("✔️ On track, but keep an eye on your spending.");
                budgetWarningLabel.setForeground(new Color(255, 152, 0));
            } else {
                budgetWarningLabel.setText("✔️ Budget health is good. Keep saving!");
                budgetWarningLabel.setForeground(new Color(76, 175, 80));
            }
        }
        if (dashboardLabel != null) {
            double remaining = budget - totalExpense;
            String statusColor = remaining >= 0 ? "green" : "red";
            String dashboard = "<html><body style='padding: 20px;'>";
            dashboard += "<h2>DASHBOARD</h2>";
            dashboard += "<p><b>Total Income:</b> ₹" + String.format("%.2f", totalIncome) + "</p>";
            dashboard += "<p><b>Total Expense:</b> ₹" + String.format("%.2f", totalExpense) + "</p>";
            dashboard += "<p><b>Balance:</b> ₹" + String.format("%.2f", balance) + "</p>";
            dashboard += "<p><b>Budget:</b> ₹" + String.format("%.2f", budget) + "</p>";
            if (budget > 0) {
                dashboard += "<p><b>Remaining Budget:</b> <span style='color: " + statusColor + ";'>₹" + String.format("%.2f", remaining) + "</span></p>";
            }
            dashboard += "</body></html>";
            dashboardLabel.setText(dashboard);
        }
    }
    
    private void updateAllTabs() {
        updateTransactionTable();
        updateDashboard();
        updateAnalyticsPanels();
        updateReportsInfo();
    }
    
    private void deleteTransactionById(int id) {
        for (int i = 0; i < transactions.size(); i++) {
            Transaction t = transactions.get(i);
            if (t.id == id) {
                if (t.type.equals("Income")) {
                    totalIncome -= t.amount;
                } else {
                    totalExpense -= t.amount;
                }
                transactions.remove(i);
                JOptionPane.showMessageDialog(this, "Transaction Deleted!", "Success", JOptionPane.INFORMATION_MESSAGE);
                break;
            }
        }
    }

    // Add Transaction (Console version for backward compatibility)
        public static void addTransaction() {
        // Console method - kept for backward compatibility
    }

    // View Transactions
    public static void viewTransactions() {

        if (transactions.isEmpty()) {
            System.out.println("No Transactions Found!");
            return;
        }

        for (Transaction t : transactions) {
            t.display();
        }
    }

    // Delete Transaction
    public static void deleteTransaction() {

        System.out.print("Enter Transaction ID to Delete: ");
        int id = input.nextInt();

        boolean found = false;

        for (int i = 0; i < transactions.size(); i++) {

            Transaction t = transactions.get(i);

            if (t.id == id) {

                if (t.type.equals("Income")) {
                    totalIncome -= t.amount;
                } else {
                    totalExpense -= t.amount;
                }

                transactions.remove(i);

                found = true;

                System.out.println("Transaction Deleted!");
                break;
            }
        }

        if (!found) {
            System.out.println("Transaction Not Found!");
        }
    }

    // Set Budget
    public static void setBudget() {

        System.out.print("Enter Monthly Budget: ");
        budget = input.nextDouble();

        System.out.println("Budget Set Successfully!");
    }

    // Dashboard
    public static void showDashboard() {

        double balance = totalIncome - totalExpense;

        System.out.println("\n========== DASHBOARD ==========");
        System.out.println("Total Income  : " + totalIncome);
        System.out.println("Total Expense : " + totalExpense);
        System.out.println("Balance       : " + balance);
        System.out.println("Budget        : " + budget);

        if (budget > 0) {
            System.out.println("Remaining Budget : " + (budget - totalExpense));
        }

        System.out.println("================================");
    }

    // Search Transaction
    public static void searchTransaction() {

        input.nextLine();

        System.out.print("Enter Category to Search: ");
        String search = input.nextLine();

        boolean found = false;

        for (Transaction t : transactions) {

            if (t.category.equalsIgnoreCase(search)) {
                t.display();
                found = true;
            }
        }

        if (!found) {
            System.out.println("No Matching Transaction Found!");
        }
    }

    // Main Method - Launches GUI
    public static void main(String[] args) {
        SwingUtilities.invokeLater(FlowExpenseTracker::new);
    }
}