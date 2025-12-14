import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.List;

public class RoutePlannerUI extends JFrame {
    private JTextField startField = new JTextField(20);
    private JTextField endField = new JTextField(20);
    private JTextField attractionsField = new JTextField(30);
    private JTextArea resultArea = new JTextArea(15, 50);
    private RoutePlanner planner;
    private DataLoader dataLoader;

    public RoutePlannerUI() {
        super("Intelligent Travel Route Planner");
        initializeData();
        setupUI();
    }

    private void initializeData() {
        try {
            dataLoader = new DataLoader();
            dataLoader.loadAttractions("CW3_Data_Files/attractions.csv");
            dataLoader.loadRoads("CW3_Data_Files/roads.csv");
            Graph graph = dataLoader.buildGraph();
            planner = new RoutePlanner(graph, dataLoader);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Data loading failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    private void setupUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 1, 5, 5));

        inputPanel.add(createLabeledField("Start City:", startField));
        inputPanel.add(createLabeledField("Destination City:", endField));
        inputPanel.add(createLabeledField("Attractions (comma-separated):", attractionsField));

        JButton planButton = new JButton("Plan Route");
        planButton.addActionListener(this::handlePlanRoute);
        inputPanel.add(planButton);

        // Result area
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        // Main layout
        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createLabeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(label));
        panel.add(field);
        return panel;
    }

    private void handlePlanRoute(ActionEvent e) {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                try {
                    String start = startField.getText().trim();
                    String end = endField.getText().trim();
                    List<String> attractions = Arrays.asList(attractionsField.getText().split("\\s*,\\s*"));

                    Route route = planner.findOptimalRoute(start, end, attractions);

                    SwingUtilities.invokeLater(() -> {
                        resultArea.setText("");
                        resultArea.append("Planning Result:\n");
                        resultArea.append("Start City: " + start + "\n");
                        resultArea.append("Destination: " + end + "\n");
                        resultArea.append("Attractions: " + attractions + "\n");
                        resultArea.append("Optimal Route: " + route.getCities() + "\n");
                        resultArea.append("Total Distance: " + route.getTotalDistance() + " miles\n");
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() ->
                            JOptionPane.showMessageDialog(RoutePlannerUI.this,
                                    "Error: " + ex.getMessage(), "Planning Failed", JOptionPane.ERROR_MESSAGE));
                }
                return null;
            }
        }.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RoutePlannerUI());
    }
}
