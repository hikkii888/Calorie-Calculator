import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * CalorieTrackerGUI
 * A small Swing app for logging food and tracking daily calories against a goal.
 * OOP demo: abstraction (FoodItem), inheritance (Breakfast/Lunch/Dinner/Snack),
 * polymorphism (getCategory() override), encapsulation (private fields + accessors).
 */
abstract class FoodItem {
    private String name;
    private int calories;

    public FoodItem(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public abstract String getCategory();

    /** Rebuilds a FoodItem of the right subtype from a saved category label. */
    public static FoodItem forCategory(String category, String name, int calories) {
        switch (category) {
            case "Breakfast": return new Breakfast(name, calories);
            case "Lunch":     return new Lunch(name, calories);
            case "Dinner":    return new Dinner(name, calories);
            case "Snack":     return new Snack(name, calories);
            default:          return new Breakfast(name, calories);
        }
    }
}

class Breakfast extends FoodItem {
    public Breakfast(String name, int calories) { super(name, calories); }
    @Override public String getCategory() { return "Breakfast"; }
}

class Lunch extends FoodItem {
    public Lunch(String name, int calories) { super(name, calories); }
    @Override public String getCategory() { return "Lunch"; }
}

class Dinner extends FoodItem {
    public Dinner(String name, int calories) { super(name, calories); }
    @Override public String getCategory() { return "Dinner"; }
}

class Snack extends FoodItem {
    public Snack(String name, int calories) { super(name, calories); }
    @Override public String getCategory() { return "Snack"; }
}

public class CalorieTrackerGUI extends JFrame {

    private static final String[] CATEGORIES = {"Breakfast", "Lunch", "Dinner", "Snack"};

    // Input row
    private JTextField txtFoodName;
    private JTextField txtCalories;
    private JComboBox<String> cmbCategory;
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnSave;
    private JButton btnLoad;

    // Table
    private JTable table;
    private DefaultTableModel tableModel;

    // Dashboard
    private JLabel lblTotal;
    private JProgressBar goalBar;
    private JSpinner goalSpinner;
    private Map<String, JLabel> categoryLabels = new LinkedHashMap<>();
    private Map<String, JProgressBar> categoryBars = new LinkedHashMap<>();

    private ArrayList<FoodItem> foodList;

    public CalorieTrackerGUI() {
        foodList = new ArrayList<>();
        initComponents();
    }

    private void initComponents() {
        setTitle("Calorie Tracker");
        setSize(820, 620);
        setMinimumSize(new Dimension(680, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));

        add(buildInputPanel(), BorderLayout.NORTH);
        add(buildTablePanel(), BorderLayout.CENTER);
        add(buildDashboardPanel(), BorderLayout.EAST);

        btnAdd.addActionListener(e -> addFood());
        btnUpdate.addActionListener(e -> updateSelectedFood());
        btnDelete.addActionListener(e -> deleteFood());
        btnSave.addActionListener(e -> saveToCsv());
        btnLoad.addActionListener(e -> loadFromCsv());
        table.getSelectionModel().addListSelectionListener(e -> populateFieldsFromSelection());

        updateDashboard();
    }

    private JPanel buildInputPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Add / Edit Food"));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(4, 4, 4, 4);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; panel.add(new JLabel("Food Name:"), c);
        txtFoodName = new JTextField(14);
        c.gridx = 1; c.weightx = 1; panel.add(txtFoodName, c);

        c.gridx = 2; c.weightx = 0; panel.add(new JLabel("Calories:"), c);
        txtCalories = new JTextField(6);
        c.gridx = 3; panel.add(txtCalories, c);

        c.gridx = 4; panel.add(new JLabel("Category:"), c);
        cmbCategory = new JComboBox<>(CATEGORIES);
        c.gridx = 5; panel.add(cmbCategory, c);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        btnAdd = new JButton("Add Food");
        btnUpdate = new JButton("Update Selected");
        btnDelete = new JButton("Delete Selected");
        btnSave = new JButton("Save to CSV\u2026");
        btnLoad = new JButton("Load from CSV\u2026");
        buttonRow.add(btnAdd);
        buttonRow.add(btnUpdate);
        buttonRow.add(btnDelete);
        buttonRow.add(new JSeparator(SwingConstants.VERTICAL));
        buttonRow.add(btnSave);
        buttonRow.add(btnLoad);

        c.gridx = 0; c.gridy = 1; c.gridwidth = 6;
        panel.add(buttonRow, c);

        return panel;
    }

    private JScrollPane buildTablePanel() {
        String[] columns = {"Food Name", "Category", "Calories"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true);
        return new JScrollPane(table);
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Dashboard"));
        panel.setPreferredSize(new Dimension(260, 0));

        lblTotal = new JLabel("Total: 0 kcal");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 18));
        lblTotal.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(lblTotal);
        panel.add(Box.createVerticalStrut(8));

        JPanel goalRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        goalRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        goalRow.add(new JLabel("Daily goal:"));
        goalSpinner = new JSpinner(new SpinnerNumberModel(2000, 0, 20000, 50));
        goalSpinner.addChangeListener(e -> updateDashboard());
        goalRow.add(goalSpinner);
        panel.add(goalRow);

        goalBar = new JProgressBar(0, 100);
        goalBar.setStringPainted(true);
        goalBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        goalBar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        panel.add(Box.createVerticalStrut(4));
        panel.add(goalBar);

        panel.add(Box.createVerticalStrut(14));
        JLabel byCategory = new JLabel("By category");
        byCategory.setFont(new Font("Arial", Font.BOLD, 13));
        byCategory.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(byCategory);
        panel.add(Box.createVerticalStrut(6));

        for (String cat : CATEGORIES) {
            JLabel label = new JLabel(cat + ": 0 kcal");
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            JProgressBar bar = new JProgressBar(0, 100);
            bar.setAlignmentX(Component.LEFT_ALIGNMENT);
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            categoryLabels.put(cat, label);
            categoryBars.put(cat, bar);
            panel.add(label);
            panel.add(bar);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ---------- Actions ----------

    private void addFood() {
        Integer calories = readValidatedInput();
        if (calories == null) return;

        String name = txtFoodName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        FoodItem food = FoodItem.forCategory(category, name, calories);

        foodList.add(food);
        tableModel.addRow(new Object[]{food.getName(), food.getCategory(), food.getCalories()});

        clearInputs();
        updateDashboard();
    }

    private void updateSelectedFood() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            showWarning("Please select a row to update.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);

        Integer calories = readValidatedInput();
        if (calories == null) return;

        String name = txtFoodName.getText().trim();
        String category = (String) cmbCategory.getSelectedItem();
        FoodItem updated = FoodItem.forCategory(category, name, calories);

        foodList.set(modelRow, updated);
        tableModel.setValueAt(updated.getName(), modelRow, 0);
        tableModel.setValueAt(updated.getCategory(), modelRow, 1);
        tableModel.setValueAt(updated.getCalories(), modelRow, 2);

        clearInputs();
        table.clearSelection();
        updateDashboard();
    }

    private void deleteFood() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            showWarning("Please select a row to delete.");
            return;
        }
        int modelRow = table.convertRowIndexToModel(viewRow);
        foodList.remove(modelRow);
        tableModel.removeRow(modelRow);
        updateDashboard();
    }

    private void populateFieldsFromSelection() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;
        int modelRow = table.convertRowIndexToModel(viewRow);
        FoodItem item = foodList.get(modelRow);
        txtFoodName.setText(item.getName());
        txtCalories.setText(String.valueOf(item.getCalories()));
        cmbCategory.setSelectedItem(item.getCategory());
    }

    private void saveToCsv() {
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("calorie-log.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (PrintWriter out = new PrintWriter(new FileWriter(chooser.getSelectedFile()))) {
            out.println("name,category,calories");
            for (FoodItem item : foodList) {
                out.println(escapeCsv(item.getName()) + "," + item.getCategory() + "," + item.getCalories());
            }
            JOptionPane.showMessageDialog(this, "Saved " + foodList.size() + " item(s).");
        } catch (IOException ex) {
            showError("Could not save file: " + ex.getMessage());
        }
    }

    private void loadFromCsv() {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        try (BufferedReader in = new BufferedReader(new FileReader(chooser.getSelectedFile()))) {
            String line = in.readLine(); // header
            foodList.clear();
            tableModel.setRowCount(0);
            while ((line = in.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;
                String name = parts[0];
                String category = parts[1];
                int calories = Integer.parseInt(parts[2].trim());
                FoodItem item = FoodItem.forCategory(category, name, calories);
                foodList.add(item);
                tableModel.addRow(new Object[]{item.getName(), item.getCategory(), item.getCalories()});
            }
            updateDashboard();
        } catch (IOException | NumberFormatException ex) {
            showError("Could not load file: " + ex.getMessage());
        }
    }

    // ---------- Helpers ----------

    /** Validates the input row and returns the parsed calorie value, or null (with a dialog) if invalid. */
    private Integer readValidatedInput() {
        String name = txtFoodName.getText().trim();
        if (name.isEmpty()) {
            showWarning("Please enter food name");
            return null;
        }
        String caloriesText = txtCalories.getText().trim();
        if (caloriesText.isEmpty()) {
            showWarning("Please enter calories");
            return null;
        }
        try {
            int calories = Integer.parseInt(caloriesText);
            if (calories <= 0) {
                showWarning("Calories must be positive");
                return null;
            }
            return calories;
        } catch (NumberFormatException ex) {
            showWarning("Please enter a valid number for calories");
            return null;
        }
    }

    private void clearInputs() {
        txtFoodName.setText("");
        txtCalories.setText("");
    }

    private void updateDashboard() {
        int total = 0;
        Map<String, Integer> byCategory = new LinkedHashMap<>();
        for (String cat : CATEGORIES) byCategory.put(cat, 0);

        for (FoodItem food : foodList) {
            total += food.getCalories();
            byCategory.merge(food.getCategory(), food.getCalories(), Integer::sum);
        }

        lblTotal.setText("Total: " + total + " kcal");

        int goal = (Integer) goalSpinner.getValue();
        if (goal > 0) {
            int pct = Math.min(100, (int) Math.round(total * 100.0 / goal));
            goalBar.setValue(pct);
            goalBar.setString(total + " / " + goal + " kcal (" + pct + "%)");
            goalBar.setForeground(total > goal ? new Color(178, 58, 46) : new Color(60, 110, 99));
        } else {
            goalBar.setValue(0);
            goalBar.setString("Set a goal above");
        }

        for (String cat : CATEGORIES) {
            int catTotal = byCategory.get(cat);
            int pctOfTotal = total > 0 ? (int) Math.round(catTotal * 100.0 / total) : 0;
            categoryLabels.get(cat).setText(cat + ": " + catTotal + " kcal");
            categoryBars.get(cat).setValue(pctOfTotal);
            categoryBars.get(cat).setString(pctOfTotal + "%");
            categoryBars.get(cat).setStringPainted(true);
        }
    }

    private String escapeCsv(String value) {
        if (value.contains(",") || value.contains("\"")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this, message, "Input Error", JOptionPane.WARNING_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new CalorieTrackerGUI().setVisible(true);
        });
    }
}
