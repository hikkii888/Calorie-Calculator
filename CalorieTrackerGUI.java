
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

abstract class FoodItem {
    private String name;
    private int calories;
    
    public FoodItem(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public int getCalories() {
        return calories;
    }
    
    public void setCalories(int calories) {
        this.calories = calories;
    }
    
    public abstract String getCategory();
}

class Breakfast extends FoodItem {
    public Breakfast(String name, int calories) {
        super(name, calories);
    }
    
    // Polymorphism - method overriding
    @Override
    public String getCategory() {
        return "Breakfast";
    }
}

// Lunch.java - Inheritance
class Lunch extends FoodItem {
    public Lunch(String name, int calories) {
        super(name, calories);
    }
    
    // Polymorphism - method overriding
    @Override
    public String getCategory() {
        return "Lunch";
    }
}

// Dinner.java - Inheritance
class Dinner extends FoodItem {
    public Dinner(String name, int calories) {
        super(name, calories);
    }
    
    // Polymorphism - method overriding
    @Override
    public String getCategory() {
        return "Dinner";
    }
}

// Snack.java - Inheritance
class Snack extends FoodItem {
    public Snack(String name, int calories) {
        super(name, calories);
    }
    
    // Polymorphism - method overriding
    @Override
    public String getCategory() {
        return "Snack";
    }
}

// Main GUI Class
public class CalorieTrackerGUI extends JFrame {
    // GUI components
    private JTextField txtFoodName;
    private JTextField txtCalories;
    private JComboBox<String> cmbCategory;
    private JButton btnAdd;
    private JButton btnDelete;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    
    // Data storage
    private ArrayList<FoodItem> foodList;
    
    public CalorieTrackerGUI() {
        foodList = new ArrayList<>();
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Calorie Tracker");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Top panel for input
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(4, 2, 5, 5));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        topPanel.add(new JLabel("Food Name:"));
        txtFoodName = new JTextField();
        topPanel.add(txtFoodName);
        
        topPanel.add(new JLabel("Calories:"));
        txtCalories = new JTextField();
        topPanel.add(txtCalories);
        
        topPanel.add(new JLabel("Category:"));
        String[] categories = {"Breakfast", "Lunch", "Dinner", "Snack"};
        cmbCategory = new JComboBox<>(categories);
        topPanel.add(cmbCategory);
        
        btnAdd = new JButton("Add Food");
        topPanel.add(new JLabel()); // Empty cell
        topPanel.add(btnAdd);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Center panel for table
        String[] columns = {"Food Name", "Category", "Calories"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        btnDelete = new JButton("Delete Selected");
        bottomPanel.add(btnDelete, BorderLayout.WEST);
        
        lblTotal = new JLabel("Total Calories: 0");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(lblTotal, BorderLayout.EAST);
        
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Event handlers
        btnAdd.addActionListener(e -> addFood());
        btnDelete.addActionListener(e -> deleteFood());
    }
    
    private void addFood() {
        try {
            // Data validation
            String name = txtFoodName.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter food name", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String caloriesText = txtCalories.getText().trim();
            if (caloriesText.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Please enter calories", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int calories = Integer.parseInt(caloriesText);
            if (calories <= 0) {
                JOptionPane.showMessageDialog(this, 
                    "Calories must be positive", 
                    "Input Error", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            String category = (String) cmbCategory.getSelectedItem();
            FoodItem food;
            
            switch (category) {
                case "Breakfast":
                    food = new Breakfast(name, calories);
                    break;
                case "Lunch":
                    food = new Lunch(name, calories);
                    break;
                case "Dinner":
                    food = new Dinner(name, calories);
                    break;
                case "Snack":
                    food = new Snack(name, calories);
                    break;
                default:
                    food = new Breakfast(name, calories);
            }
            
            // Add to list and table
            foodList.add(food);
            tableModel.addRow(new Object[]{
                food.getName(), 
                food.getCategory(), 
                food.getCalories()
            });
            
            // Clear input fields
            txtFoodName.setText("");
            txtCalories.setText("");
            
            // Update total
            updateTotal();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Please enter valid number for calories", 
                "Input Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteFood() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a row to delete", 
                "Selection Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        foodList.remove(selectedRow);
        tableModel.removeRow(selectedRow);
        updateTotal();
    }
    
    private void updateTotal() {
        int total = 0;
        for (FoodItem food : foodList) {
            total += food.getCalories();
        }
        lblTotal.setText("Total Calories: " + total);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CalorieTrackerGUI gui = new CalorieTrackerGUI();
            gui.setVisible(true);
        });
    }
}