import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

class Transaction {
    private final int id;
    private final String type;
    private final double amount;
    private final String category;
    private final String description;
    private final LocalDate date;

    public Transaction(int id, String type, double amount, String category, String description) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.date = LocalDate.now();
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }
}

class CategoryAnalyticsRow {
    private final String category;
    private final int count;
    private final String totalAmount;
    private final String percentage;

    public CategoryAnalyticsRow(String category, int count, String totalAmount, String percentage) {
        this.category = category;
        this.count = count;
        this.totalAmount = totalAmount;
        this.percentage = percentage;
    }

    public String getCategory() {
        return category;
    }

    public int getCount() {
        return count;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public String getPercentage() {
        return percentage;
    }
}

public class FlowExpenseTrackerFX extends Application {
    private final ObservableList<Transaction> transactions = FXCollections.observableArrayList();
    private final ObservableList<CategoryAnalyticsRow> categoryAnalyticsData = FXCollections.observableArrayList();
    private int transactionId = 1;
    private double totalIncome = 0;
    private double totalExpense = 0;
    private double budget = 0;

    private Label totalIncomeValue;
    private Label totalExpenseValue;
    private Label balanceValue;
    private Label budgetStatusValue;
    private Label budgetWarningLabel;
    private Label budgetCurrentValue;
    private Label budgetSpentValue;
    private ProgressBar budgetProgressBar;
    private TableView<Transaction> transactionTable;
    private TableView<CategoryAnalyticsRow> categoryTable;
    private Label trendsAnalyticsLabel;
    private TextArea reportsInfoArea;
    private Canvas incomeExpenseCanvas;
    private Canvas budgetAnalysisCanvas;
    private Canvas comparisonCanvas;
    private TextField searchField;
    private TableView<Transaction> searchTable;

    @Override
    public void start(Stage stage) {
        stage.setTitle("Flow Expense Tracker - JavaFX Edition");

        TabPane tabs = new TabPane();
        tabs.getTabs().add(new Tab("Dashboard", createDashboardPane()));
        tabs.getTabs().add(new Tab("Add Transaction", createAddTransactionPane()));
        tabs.getTabs().add(new Tab("View All", createViewTransactionPane()));
        tabs.getTabs().add(new Tab("Analytics", createAnalyticsPane()));
        tabs.getTabs().add(new Tab("Budget Manager", createBudgetPane()));
        tabs.getTabs().add(new Tab("Search", createSearchPane()));
        tabs.getTabs().add(new Tab("Reports", createReportsPane()));
        tabs.getTabs().forEach(tab -> tab.setClosable(false));

        Scene scene = new Scene(tabs, 1200, 800);
        stage.setScene(scene);
        stage.show();

        updateAllTabs();
    }

    private VBox createDashboardPane() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        HBox statsRow = new HBox(15);
        statsRow.setAlignment(Pos.CENTER);

        totalIncomeValue = createStatValueLabel();
        totalExpenseValue = createStatValueLabel();
        balanceValue = createStatValueLabel();
        budgetStatusValue = createStatValueLabel();

        statsRow.getChildren().addAll(
            createStatCard("Total Income", totalIncomeValue, Color.web("#4CAF50")),
            createStatCard("Total Expense", totalExpenseValue, Color.web("#F44336")),
            createStatCard("Current Balance", balanceValue, Color.web("#2196F3")),
            createStatCard("Budget Status", budgetStatusValue, Color.web("#FF9800"))
        );

        budgetWarningLabel = new Label();
        budgetWarningLabel.setFont(Font.font("Segoe UI", 14));
        budgetWarningLabel.setWrapText(true);
        budgetWarningLabel.setTextFill(Color.web("#333333"));

        HBox chartPanels = new HBox(15);
        incomeExpenseCanvas = new Canvas(430, 300);
        budgetAnalysisCanvas = new Canvas(430, 300);
        chartPanels.getChildren().addAll(
            createChartWrapper("Income vs Expense", incomeExpenseCanvas),
            createChartWrapper("Budget Analysis", budgetAnalysisCanvas)
        );

        Button refreshButton = new Button("Refresh Data");
        refreshButton.setFont(Font.font("Segoe UI", 13));
        refreshButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        refreshButton.setOnAction(e -> updateAllTabs());

        root.getChildren().addAll(statsRow, budgetWarningLabel, chartPanels, refreshButton);
        return root;
    }

    private VBox createStatCard(String title, Label valueLabel, Color accentColor) {
        VBox card = new VBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setPrefWidth(260);
        card.setStyle("-fx-background-color: white; -fx-border-color: " + toRgbString(accentColor) + "; -fx-border-width: 3; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 12));
        titleLabel.setTextFill(Color.web("#666666"));

        valueLabel.setFont(Font.font("Segoe UI", 24));
        valueLabel.setTextFill(accentColor);

        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }

    private Label createStatValueLabel() {
        Label label = new Label("₹0.00");
        label.setFont(Font.font("Segoe UI", 24));
        return label;
    }

    private StackPane createChartWrapper(String title, Canvas canvas) {
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("Segoe UI", 14));
        titleLabel.setTextFill(Color.web("#333333"));
        StackPane wrapper = new StackPane(canvas);
        wrapper.setPrefSize(430, 300);
        wrapper.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        wrapper.setPadding(new Insets(10));
        wrapper.getChildren().add(titleLabel);
        StackPane.setAlignment(titleLabel, Pos.TOP_LEFT);
        return wrapper;
    }

    private VBox createAddTransactionPane() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label typeLabel = new Label("Transaction Type:");
        typeLabel.setFont(Font.font("Segoe UI", 13));
        ComboBox<String> typeCombo = new ComboBox<>(FXCollections.observableArrayList("Income", "Expense"));
        typeCombo.setValue("Income");
        typeCombo.setPrefWidth(250);

        Label amountLabel = new Label("Amount:");
        amountLabel.setFont(Font.font("Segoe UI", 13));
        TextField amountField = new TextField();
        amountField.setPrefWidth(250);

        Label categoryLabel = new Label("Category:");
        categoryLabel.setFont(Font.font("Segoe UI", 13));
        ComboBox<String> categoryCombo = new ComboBox<>(FXCollections.observableArrayList(
                "Food", "Transport", "Utilities", "Entertainment", "Health", "Shopping", "Salary", "Bonus", "Investment", "Other"));
        categoryCombo.setEditable(true);
        categoryCombo.setValue("Food");
        categoryCombo.setPrefWidth(250);

        Label descLabel = new Label("Description:");
        descLabel.setFont(Font.font("Segoe UI", 13));
        TextArea descField = new TextArea();
        descField.setWrapText(true);
        descField.setPrefRowCount(3);
        descField.setPrefWidth(250);

        form.add(typeLabel, 0, 0);
        form.add(typeCombo, 1, 0);
        form.add(amountLabel, 0, 1);
        form.add(amountField, 1, 1);
        form.add(categoryLabel, 0, 2);
        form.add(categoryCombo, 1, 2);
        form.add(descLabel, 0, 3);
        form.add(descField, 1, 3);

        Button addButton = new Button("Add Transaction");
        addButton.setFont(Font.font("Segoe UI", 13));
        addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        addButton.setOnAction(e -> {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                String category = categoryCombo.getEditor().getText().trim();
                String description = descField.getText().trim();
                String type = typeCombo.getValue();

                if (amount <= 0) {
                    showAlert(Alert.AlertType.ERROR, "Amount must be greater than 0.");
                    return;
                }
                if (category.isEmpty() || description.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Please fill all fields.");
                    return;
                }

                Transaction transaction = new Transaction(transactionId++, type, amount, category, description);
                transactions.add(transaction);
                if (type.equals("Income")) {
                    totalIncome += amount;
                } else {
                    totalExpense += amount;
                }
                amountField.clear();
                descField.clear();
                updateAllTabs();
                showAlert(Alert.AlertType.INFORMATION, "Transaction added successfully.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid numeric amount.");
            }
        });

        Button clearButton = new Button("Clear Form");
        clearButton.setFont(Font.font("Segoe UI", 13));
        clearButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        clearButton.setOnAction(e -> {
            amountField.clear();
            descField.clear();
        });

        HBox buttonBox = new HBox(15, addButton, clearButton);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        root.getChildren().addAll(form, buttonBox);
        return root;
    }

    private VBox createViewTransactionPane() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        transactionTable = new TableView<>();
        transactionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        transactionTable.setPrefHeight(520);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        transactionTable.getColumns().addAll(idCol, typeCol, amountCol, categoryCol, descCol, dateCol);

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        refreshBtn.setOnAction(e -> updateTransactionTable());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        deleteBtn.setOnAction(e -> deleteSelectedTransaction());

        Button detailsBtn = new Button("View Details");
        detailsBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white;");
        detailsBtn.setOnAction(e -> showSelectedTransactionDetails());

        HBox buttonRow = new HBox(15, refreshBtn, detailsBtn, deleteBtn);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        root.getChildren().addAll(transactionTable, buttonRow);
        return root;
    }

    private VBox createAnalyticsPane() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        TabPane analyticsTabs = new TabPane();
        analyticsTabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        analyticsTabs.getTabs().add(new Tab("Category Breakdown", createCategoryAnalyticsPane()));
        analyticsTabs.getTabs().add(new Tab("Monthly Trends", createTrendsAnalyticsPane()));
        analyticsTabs.getTabs().add(new Tab("Expense vs Income", createComparisonAnalyticsPane()));

        root.getChildren().add(analyticsTabs);
        return root;
    }

    private VBox createCategoryAnalyticsPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        categoryTable = new TableView<>(categoryAnalyticsData);
        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<CategoryAnalyticsRow, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<CategoryAnalyticsRow, Integer> countCol = new TableColumn<>("Count");
        countCol.setCellValueFactory(new PropertyValueFactory<>("count"));
        TableColumn<CategoryAnalyticsRow, String> totalCol = new TableColumn<>("Total Amount");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        TableColumn<CategoryAnalyticsRow, String> percCol = new TableColumn<>("Percentage");
        percCol.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        categoryTable.getColumns().addAll(categoryCol, countCol, totalCol, percCol);
        categoryTable.setPrefHeight(540);
        root.getChildren().add(categoryTable);
        return root;
    }

    private VBox createTrendsAnalyticsPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        trendsAnalyticsLabel = new Label();
        trendsAnalyticsLabel.setFont(Font.font("Segoe UI", 14));
        trendsAnalyticsLabel.setWrapText(true);
        trendsAnalyticsLabel.setPrefWidth(1040);

        root.getChildren().add(trendsAnalyticsLabel);
        return root;
    }

    private VBox createComparisonAnalyticsPane() {
        VBox root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        comparisonCanvas = new Canvas(1080, 520);
        StackPane canvasPane = new StackPane(comparisonCanvas);
        canvasPane.setStyle("-fx-background-color: white;");
        root.getChildren().add(canvasPane);
        return root;
    }

    private VBox createBudgetPane() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        GridPane budgetForm = new GridPane();
        budgetForm.setHgap(15);
        budgetForm.setVgap(15);
        budgetForm.setPadding(new Insets(25));
        budgetForm.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");

        Label budgetLabel = new Label("Monthly Budget (₹):");
        budgetLabel.setFont(Font.font("Segoe UI", 13));
        TextField budgetField = new TextField();
        budgetField.setPrefWidth(250);

        Label currentLabel = new Label("Current Budget:");
        currentLabel.setFont(Font.font("Segoe UI", 13));
        budgetCurrentValue = new Label("₹0.00");
        budgetCurrentValue.setFont(Font.font("Segoe UI", 14));
        budgetCurrentValue.setTextFill(Color.web("#2196F3"));

        Label spentLabel = new Label("Amount Spent:");
        spentLabel.setFont(Font.font("Segoe UI", 13));
        budgetSpentValue = new Label("₹0.00");
        budgetSpentValue.setFont(Font.font("Segoe UI", 14));
        budgetSpentValue.setTextFill(Color.web("#F44336"));

        budgetForm.add(budgetLabel, 0, 0);
        budgetForm.add(budgetField, 1, 0);
        budgetForm.add(currentLabel, 0, 1);
        budgetForm.add(budgetCurrentValue, 1, 1);
        budgetForm.add(spentLabel, 0, 2);
        budgetForm.add(budgetSpentValue, 1, 2);

        budgetProgressBar = new ProgressBar(0);
        budgetProgressBar.setPrefWidth(400);
        VBox progressBox = new VBox(10, new Label("Budget Progress:"), budgetProgressBar);
        progressBox.setPadding(new Insets(15, 0, 0, 0));

        Button setBudget = new Button("Set Budget");
        setBudget.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        setBudget.setFont(Font.font("Segoe UI", 13));
        setBudget.setOnAction(e -> {
            try {
                double newBudget = Double.parseDouble(budgetField.getText().trim());
                if (newBudget < 0) {
                    showAlert(Alert.AlertType.ERROR, "Budget cannot be negative.");
                    return;
                }
                budget = newBudget;
                budgetField.clear();
                updateAllTabs();
                showAlert(Alert.AlertType.INFORMATION, "Budget set successfully.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid numeric budget.");
            }
        });

        root.getChildren().addAll(budgetForm, progressBox, setBudget);
        return root;
    }

    private VBox createSearchPane() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        HBox searchRow = new HBox(10);
        searchRow.setPadding(new Insets(15));
        searchRow.setStyle("-fx-background-color: white; -fx-border-color: #C8C8C8; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
        searchRow.setAlignment(Pos.CENTER_LEFT);

        Label searchLabel = new Label("Search by Category or Description:");
        searchLabel.setFont(Font.font("Segoe UI", 13));
        searchField = new TextField();
        searchField.setPrefWidth(350);

        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        searchButton.setOnAction(e -> performSearch());

        Button clearButton = new Button("Clear");
        clearButton.setStyle("-fx-background-color: #9E9E9E; -fx-text-fill: white;");
        clearButton.setOnAction(e -> {
            searchField.clear();
            searchTable.getItems().clear();
        });

        searchRow.getChildren().addAll(searchLabel, searchField, searchButton, clearButton);

        searchTable = new TableView<>();
        searchTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        searchTable.setPrefHeight(590);

        TableColumn<Transaction, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Transaction, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Transaction, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Transaction, String> categoryCol = new TableColumn<>("Category");
        categoryCol.setCellValueFactory(new PropertyValueFactory<>("category"));
        TableColumn<Transaction, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        TableColumn<Transaction, LocalDate> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

        searchTable.getColumns().addAll(idCol, typeCol, amountCol, categoryCol, descCol, dateCol);

        root.getChildren().addAll(searchRow, searchTable);
        return root;
    }

    private VBox createReportsPane() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #F0F2F5;");

        HBox buttonRow = new HBox(15);
        buttonRow.setAlignment(Pos.CENTER_LEFT);

        Button exportCSV = new Button("Export to CSV");
        exportCSV.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        exportCSV.setOnAction(e -> exportToCSV());

        Button printReport = new Button("Print Report");
        printReport.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        printReport.setOnAction(e -> printReport());

        Button summaryReport = new Button("Generate Summary");
        summaryReport.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        summaryReport.setOnAction(e -> showSummaryReport());

        Button clearData = new Button("Clear All Data");
        clearData.setStyle("-fx-background-color: #F44336; -fx-text-fill: white;");
        clearData.setOnAction(e -> clearAllData());

        buttonRow.getChildren().addAll(exportCSV, printReport, summaryReport, clearData);

        reportsInfoArea = new TextArea();
        reportsInfoArea.setEditable(false);
        reportsInfoArea.setWrapText(true);
        reportsInfoArea.setPrefHeight(620);

        root.getChildren().addAll(buttonRow, reportsInfoArea);
        return root;
    }

    private void updateTransactionTable() {
        if (transactionTable != null) {
            transactionTable.getItems().setAll(transactions);
        }
    }

    private void updateCategoryAnalytics() {
        categoryAnalyticsData.clear();
        Map<String, Double> categoryTotal = new HashMap<>();
        Map<String, Integer> categoryCount = new HashMap<>();

        for (Transaction t : transactions) {
            categoryTotal.put(t.getCategory(), categoryTotal.getOrDefault(t.getCategory(), 0.0) + t.getAmount());
            categoryCount.put(t.getCategory(), categoryCount.getOrDefault(t.getCategory(), 0) + 1);
        }

        double total = totalIncome + totalExpense;
        categoryTotal.forEach((category, amount) -> {
            double percentage = total > 0 ? (amount / total) * 100 : 0;
            categoryAnalyticsData.add(new CategoryAnalyticsRow(
                category,
                categoryCount.get(category),
                String.format("₹%.2f", amount),
                String.format("%.1f%%", percentage)
            ));
        });
    }

    private void updateTrendsAnalyticsData() {
        int count = transactions.size();
        double average = count == 0 ? 0 : (totalIncome + totalExpense) / count;
        double highest = transactions.stream().mapToDouble(Transaction::getAmount).max().orElse(0);

        String summary = String.format(
            "Total Transactions: %d\nAverage Transaction: ₹%.2f\nHighest Single Transaction: ₹%.2f",
            count, average, highest
        );
        if (trendsAnalyticsLabel != null) {
            trendsAnalyticsLabel.setText(summary);
        }
    }

    private void updateReportsInfo() {
        if (reportsInfoArea == null) {
            return;
        }
        reportsInfoArea.setText(
            "Reports Panel:\n\n" +
            "• Export CSV: Save all transactions to CSV file\n" +
            "• Print Report: Print current financial summary\n" +
            "• Summary Report: View detailed financial overview\n" +
            "• Clear Data: Remove all transactions (be careful!)\n\n" +
            "Current Status:\n" +
            "Total Transactions: " + transactions.size() + "\n" +
            "Total Income: ₹" + String.format("%.2f", totalIncome) + "\n" +
            "Total Expense: ₹" + String.format("%.2f", totalExpense)
        );
    }

    private void updateDashboard() {
        if (totalIncomeValue != null) {
            totalIncomeValue.setText(String.format("₹%.2f", totalIncome));
        }
        if (totalExpenseValue != null) {
            totalExpenseValue.setText(String.format("₹%.2f", totalExpense));
        }
        double balance = totalIncome - totalExpense;
        if (balanceValue != null) {
            balanceValue.setText(String.format("₹%.2f", balance));
        }
        if (budgetStatusValue != null) {
            budgetStatusValue.setText(String.format("₹%.2f", budget - totalExpense));
        }
        if (budgetCurrentValue != null) {
            budgetCurrentValue.setText(String.format("₹%.2f", budget));
        }
        if (budgetSpentValue != null) {
            budgetSpentValue.setText(String.format("₹%.2f", totalExpense));
        }
        if (budgetProgressBar != null) {
            double progress = budget > 0 ? totalExpense / budget : 0;
            budgetProgressBar.setProgress(Math.min(progress, 1.0));
        }
        updateWarningLabel();
        drawIncomeExpenseChart();
        drawBudgetAnalysisChart();
        drawComparisonChart();
    }

    private void updateWarningLabel() {
        if (budgetWarningLabel == null) {
            return;
        }
        if (budget <= 0) {
            budgetWarningLabel.setText("No budget set. Enter a monthly budget to unlock advanced budget analysis.");
            budgetWarningLabel.setTextFill(Color.web("#2196F3"));
        } else if (totalExpense > budget) {
            budgetWarningLabel.setText("⚠️ Budget exceeded! Expense is above your monthly budget.");
            budgetWarningLabel.setTextFill(Color.web("#F44336"));
        } else if (totalExpense > budget * 0.8) {
            budgetWarningLabel.setText("⚠️ High spending alert. You have used more than 80% of your budget.");
            budgetWarningLabel.setTextFill(Color.web("#F44336"));
        } else if (totalExpense > budget * 0.5) {
            budgetWarningLabel.setText("✔️ On track, but keep an eye on your spending.");
            budgetWarningLabel.setTextFill(Color.web("#FF9800"));
        } else {
            budgetWarningLabel.setText("✔️ Budget health is good. Keep saving!");
            budgetWarningLabel.setTextFill(Color.web("#4CAF50"));
        }
    }

    private void drawIncomeExpenseChart() {
        if (incomeExpenseCanvas == null) {
            return;
        }
        double width = incomeExpenseCanvas.getWidth();
        double height = incomeExpenseCanvas.getHeight();
        GraphicsContext gc = incomeExpenseCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.web("#F0F0F0"));
        gc.fillRect(0, 0, width, height);
        gc.setStroke(Color.web("#C8C8C8"));
        gc.strokeRect(0.5, 0.5, width - 1, height - 1);
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Segoe UI", 16));
        gc.fillText("Income vs Expense", 20, 30);

        double total = totalIncome + totalExpense;
        if (total > 0) {
            double radius = 80;
            double centerX = width / 2;
            double centerY = height / 2 + 20;
            double incomeAngle = totalIncome / total * 360;
            gc.setFill(Color.web("#4CAF50"));
            gc.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, 0, incomeAngle, javafx.scene.shape.ArcType.ROUND);
            gc.setFill(Color.web("#F44336"));
            gc.fillArc(centerX - radius, centerY - radius, radius * 2, radius * 2, incomeAngle, 360 - incomeAngle, javafx.scene.shape.ArcType.ROUND);
        } else {
            gc.setFill(Color.web("#969696"));
            gc.setFont(Font.font("Segoe UI", 12));
            gc.fillText("Add income or expense to see analysis", 90, height / 2);
        }
    }

    private void drawBudgetAnalysisChart() {
        if (budgetAnalysisCanvas == null) {
            return;
        }
        double width = budgetAnalysisCanvas.getWidth();
        double height = budgetAnalysisCanvas.getHeight();
        GraphicsContext gc = budgetAnalysisCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.web("#F0F0F0"));
        gc.fillRect(0, 0, width, height);
        gc.setStroke(Color.web("#C8C8C8"));
        gc.strokeRect(0.5, 0.5, width - 1, height - 1);
        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Segoe UI", 16));
        gc.fillText("Budget Analysis", 20, 30);

        if (budget > 0) {
            double barWidth = 60;
            double chartHeight = height - 110;
            double maxValue = Math.max(budget, totalExpense);
            double budgetHeight = budget <= 0 ? 0 : (budget / maxValue) * chartHeight;
            double expenseHeight = totalExpense <= 0 ? 0 : (totalExpense / maxValue) * chartHeight;
            double remainingHeight = Math.max(0, budget - totalExpense) <= 0 ? 0 : (Math.max(0, budget - totalExpense) / maxValue) * chartHeight;
            double baseY = height - 40;

            gc.setFill(Color.web("#2196F3"));
            gc.fillRect(width / 4 - barWidth / 2, baseY - budgetHeight, barWidth, budgetHeight);
            gc.setFill(Color.web("#F44336"));
            gc.fillRect(width / 2 - barWidth / 2, baseY - expenseHeight, barWidth, expenseHeight);
            gc.setFill(Color.web("#4CAF50"));
            gc.fillRect(3 * width / 4 - barWidth / 2, baseY - remainingHeight, barWidth, remainingHeight);

            gc.setFill(Color.web("#333333"));
            gc.setFont(Font.font("Segoe UI", 12));
            gc.fillText("Budget", width / 4 - 20, baseY + 20);
            gc.fillText("Expense", width / 2 - 30, baseY + 20);
            gc.fillText("Remaining", 3 * width / 4 - 40, baseY + 20);
        } else {
            gc.setFill(Color.web("#969696"));
            gc.setFont(Font.font("Segoe UI", 12));
            gc.fillText("Set a budget to show analysis", 110, height / 2);
        }
    }

    private void drawComparisonChart() {
        if (comparisonCanvas == null) {
            return;
        }
        double width = comparisonCanvas.getWidth();
        double height = comparisonCanvas.getHeight();
        GraphicsContext gc = comparisonCanvas.getGraphicsContext2D();
        gc.clearRect(0, 0, width, height);
        gc.setFill(Color.web("#F0F0F0"));
        gc.fillRect(0, 0, width, height);
        gc.setStroke(Color.web("#505050"));
        gc.setLineWidth(2);
        double margin = 60;
        gc.strokeLine(margin, height - margin, margin, margin);
        gc.strokeLine(margin, height - margin, width - margin, height - margin);

        double maxValue = Math.max(totalIncome, totalExpense);
        if (maxValue > 0) {
            double chartHeight = height - 2 * margin;
            double incomeHeight = (totalIncome / maxValue) * chartHeight * 0.8;
            double expenseHeight = (totalExpense / maxValue) * chartHeight * 0.8;
            gc.setFill(Color.web("#4CAF50"));
            gc.fillRect(margin + 50, height - margin - incomeHeight, 60, incomeHeight);
            gc.setFill(Color.web("#F44336"));
            gc.fillRect(margin + 150, height - margin - expenseHeight, 60, expenseHeight);
        }

        gc.setFill(Color.web("#333333"));
        gc.setFont(Font.font("Segoe UI", 12));
        gc.fillText("Income", margin + 50, height - margin + 30);
        gc.fillText("Expense", margin + 150, height - margin + 30);
        gc.fillText("₹", margin - 25, margin + 10);
    }

    private void updateAllTabs() {
        updateTransactionTable();
        updateDashboard();
        updateCategoryAnalytics();
        updateTrendsAnalyticsData();
        updateReportsInfo();
        if (searchField != null && searchField.getText().trim().isEmpty() && searchTable != null) {
            searchTable.getItems().clear();
        }
    }

    private void deleteSelectedTransaction() {
        if (transactionTable == null) {
            return;
        }
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a transaction to delete.");
            return;
        }
        transactions.remove(selected);
        if (selected.getType().equals("Income")) {
            totalIncome -= selected.getAmount();
        } else {
            totalExpense -= selected.getAmount();
        }
        updateAllTabs();
        showAlert(Alert.AlertType.INFORMATION, "Transaction deleted successfully.");
    }

    private void showSelectedTransactionDetails() {
        if (transactionTable == null) {
            return;
        }
        Transaction selected = transactionTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.ERROR, "Please select a transaction.");
            return;
        }
        String details = String.format(
            "ID: %d\nType: %s\nAmount: ₹%.2f\nCategory: %s\nDescription: %s\nDate: %s",
            selected.getId(), selected.getType(), selected.getAmount(), selected.getCategory(), selected.getDescription(), selected.getDate()
        );
        showAlert(Alert.AlertType.INFORMATION, details);
    }

    private void performSearch() {
        if (searchField == null || searchTable == null) {
            return;
        }
        String term = searchField.getText().trim().toLowerCase();
        if (term.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Please enter a search term.");
            return;
        }
        ObservableList<Transaction> found = FXCollections.observableArrayList();
        for (Transaction t : transactions) {
            if (t.getCategory().toLowerCase().contains(term) || t.getDescription().toLowerCase().contains(term)) {
                found.add(t);
            }
        }
        searchTable.setItems(found);
        if (found.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No transactions found.");
        }
    }

    private void exportToCSV() {
        try (FileWriter writer = new FileWriter("transactions_report.csv")) {
            writer.write("ID,Type,Amount,Category,Description,Date\n");
            for (Transaction t : transactions) {
                writer.write(String.format("%d,%s,%.2f,%s,%s,%s\n",
                        t.getId(), t.getType(), t.getAmount(), t.getCategory(), t.getDescription().replace(",", " "), t.getDate()));
            }
            showAlert(Alert.AlertType.INFORMATION, "Report exported to transactions_report.csv");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error exporting report: " + e.getMessage());
        }
    }

    private void printReport() {
        String report = String.format(
            "=== FLOW EXPENSE TRACKER REPORT ===\n\n" +
            "Generated: %s\n\n" +
            "Total Income: ₹%.2f\n" +
            "Total Expense: ₹%.2f\n" +
            "Balance: ₹%.2f\n" +
            "Budget: ₹%.2f\n\n" +
            "Remaining Budget: ₹%.2f\n",
            LocalDate.now(), totalIncome, totalExpense, totalIncome - totalExpense, budget, budget - totalExpense
        );
        showAlert(Alert.AlertType.INFORMATION, report);
    }

    private void showSummaryReport() {
        String summary = String.format(
            "Total Income: ₹%.2f\n" +
            "Total Expense: ₹%.2f\n" +
            "Net Balance: ₹%.2f\n" +
            "Monthly Budget: ₹%.2f\n" +
            "Spent Percentage: %.1f%%\n" +
            "Total Transactions: %d",
            totalIncome, totalExpense, totalIncome - totalExpense, budget,
            budget > 0 ? (totalExpense / budget) * 100 : 0,
            transactions.size()
        );
        showAlert(Alert.AlertType.INFORMATION, summary);
    }

    private void clearAllData() {
        transactions.clear();
        totalIncome = 0;
        totalExpense = 0;
        transactionId = 1;
        updateAllTabs();
        showAlert(Alert.AlertType.INFORMATION, "All data cleared.");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Flow Expense Tracker");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String toRgbString(Color color) {
        return String.format("rgb(%d, %d, %d)",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }

    public static void main(String[] args) {
        launch(args);
    }
}
