import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

// Car class
class Car {
    private String carId;
    private String brand;
    private String model;
    private double pricePerDay;
    private boolean available = true;
    private String rentedBy = "";

    public Car(String carId, String brand, String model, double pricePerDay) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.pricePerDay = pricePerDay;
    }

    public String getCarId() {
        return carId;
    }

    public String getDetails() {
        return carId + " - " + brand + " " + model + " (\u20B9" + pricePerDay + "/day)";
    }

    public double calculatePrice(int days) {
        return pricePerDay * days;
    }

    public boolean isAvailable() {
        return available;
    }

    public void rent(String customerName) {
        available = false;
        rentedBy = customerName;
    }

    public void returnCar() {
        available = true;
        rentedBy = "";
    }

    public String getRentedBy() {
        return rentedBy;
    }

    @Override
    public String toString() {
        return getDetails() + " [" + (available ? "Available" : "Rented by " + rentedBy) + "]";
    }
}

// Rental history class
class RentalHistory {
    private String customerName;
    private String carDetails;
    private int days;
    private double totalCost;
    private boolean isReturn;

    public RentalHistory(String customerName, String carDetails, int days, double totalCost) {
        this.customerName = customerName;
        this.carDetails = carDetails;
        this.days = days;
        this.totalCost = totalCost;
        this.isReturn = false;
    }

    public RentalHistory(String customerName, String carDetails) {
        this.customerName = customerName;
        this.carDetails = carDetails;
        this.isReturn = true;
    }

    @Override
    public String toString() {
        if (isReturn) {
            return customerName + " returned " + carDetails;
        } else {
            // Conditional check for singular/plural days
            String dayLabel = (days == 1) ? "day" : "days";
            return customerName + " rented " + carDetails + " for " + days + " " + dayLabel + ". Total: \u20B9" + totalCost;
        }
    }
}

// GUI Class
public class CarRentalGUI extends JFrame {
    private List<Car> carList = new ArrayList<>();
    private List<RentalHistory> rentalHistoryList = new ArrayList<>();

    private JComboBox<String> rentDropdown;
    private JComboBox<String> returnDropdown;
    private JTextField nameField;
    private JTextField daysField;
    private JTextArea displayArea;
    private JTextArea historyArea;

    public CarRentalGUI() {
        setTitle("Car Rental System");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Add sample cars
        carList.add(new Car("C101", "Maruti Suzuki", "Alto K10", 2600));
        carList.add(new Car("C102", "Tata", "Punch", 2800));
        carList.add(new Car("C103", "Tata", "Nexon", 3100));
        carList.add(new Car("C104", "Mahindra", "XUV700", 8000));
        carList.add(new Car("C105", "Mahindra", "Thar", 5000));
        carList.add(new Car("C106", "Renault", "Kwid", 2300));

        // Top Panel
        JPanel inputPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Rental Section"));

        nameField = new JTextField();
        daysField = new JTextField();
        rentDropdown = new JComboBox<>();
        returnDropdown = new JComboBox<>();

        inputPanel.add(new JLabel("Customer Name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Rental Days:"));
        inputPanel.add(daysField);
        inputPanel.add(new JLabel("Select Car to Rent:"));
        inputPanel.add(rentDropdown);
        inputPanel.add(new JLabel("Select Car to Return:"));
        inputPanel.add(returnDropdown);

        JButton rentButton = new JButton("Rent");
        JButton returnButton = new JButton("Return");

        inputPanel.add(rentButton);
        inputPanel.add(returnButton);

        // Center Area - Tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Car Status Tab
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane statusScroll = new JScrollPane(displayArea);
        tabbedPane.add("Car Status", statusScroll);

        // Rental History Tab
        historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane historyScroll = new JScrollPane(historyArea);
        tabbedPane.add("Rental History", historyScroll);

        // Add to Frame
        add(inputPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        // Button Listeners
        rentButton.addActionListener(e -> handleRent());
        returnButton.addActionListener(e -> handleReturn());

        updateDropdowns();
        refreshCarStatus();
        refreshHistory();
    }

    private void updateDropdowns() {
        rentDropdown.removeAllItems();
        returnDropdown.removeAllItems();

        for (Car car : carList) {
            if (car.isAvailable()) {
                rentDropdown.addItem(car.getCarId());
            } else {
                returnDropdown.addItem(car.getCarId());
            }
        }
    }

    private void refreshCarStatus() {
        displayArea.setText("Current Car Status:\n\n");
        for (Car car : carList) {
            displayArea.append(car.toString() + "\n");
        }
        displayArea.append("\n");
    }

    private void refreshHistory() {
        historyArea.setText("Rental History:\n\n");
        for (RentalHistory record : rentalHistoryList) {
            historyArea.append(record.toString() + "\n");
        }
        historyArea.append("\n");
    }

    private void handleRent() {
        String name = nameField.getText().trim();
        String carId = (String) rentDropdown.getSelectedItem();
        int days;

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name.");
            return;
        }

        if (carId == null) {
            JOptionPane.showMessageDialog(this, "No car selected to rent.");
            return;
        }

        try {
            days = Integer.parseInt(daysField.getText().trim());
            if (days <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of days.");
            return;
        }

        String formattedName = capitalizeName(name);

        for (Car car : carList) {
            if (car.getCarId().equals(carId) && car.isAvailable()) {
                double total = car.calculatePrice(days);
                car.rent(formattedName);

                rentalHistoryList.add(new RentalHistory(formattedName, car.getDetails(), days, total));

                JOptionPane.showMessageDialog(this, formattedName + " rented " + car.getDetails() + " for " + days + " " + (days == 1 ? "day" : "days") + ".\nTotal: \u20B9" + total);
                updateDropdowns();
                refreshCarStatus();
                refreshHistory();

                nameField.setText("");
                daysField.setText("");
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Car not available.");
    }

    private void handleReturn() {
        String name = nameField.getText().trim();
        String carId = (String) returnDropdown.getSelectedItem();

        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your name to return the car.");
            return;
        }

        if (carId == null) {
            JOptionPane.showMessageDialog(this, "No car selected to return.");
            return;
        }

        for (Car car : carList) {
            if (car.getCarId().equals(carId) && !car.isAvailable()) {
                if (!car.getRentedBy().equalsIgnoreCase(name)) {
                    JOptionPane.showMessageDialog(this, "This car was not rented by " + name + ". It was rented by " + car.getRentedBy() + ".");
                    return;
                }

                String formattedName = capitalizeName(name);
                String details = car.getDetails();
                car.returnCar();

                rentalHistoryList.add(new RentalHistory(formattedName, details)); // Add return entry

                JOptionPane.showMessageDialog(this, "Car " + details + " has been returned by " + formattedName + ".");
                updateDropdowns();
                refreshCarStatus();
                refreshHistory();
                return;
            }
        }

        JOptionPane.showMessageDialog(this, "Please select a rented car to return.");
    }

    private String capitalizeName(String name) {
        if (name == null || name.isEmpty()) return name;
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CarRentalGUI gui = new CarRentalGUI();
            gui.setVisible(true);
        });
    }
}
