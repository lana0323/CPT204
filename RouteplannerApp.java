

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

public class RouteplannerApp {
    public static void main(String[] args) {
        try {
            // Load data
            System.out.println("Data is being loaded...");
            DataLoader dataLoader = new DataLoader();
            dataLoader.loadAttractions("attractions.csv");
            dataLoader.loadRoads("roads.csv");
            
            Graph graph = dataLoader.buildGraph();
            RoutePlanner planner = new RoutePlanner(graph, dataLoader);
            
            // User interaction
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            
            while (true) {
                try {
                    System.out.print("Enter the starting city (For example, New York NY): ");
                    String startCity = reader.readLine().trim();
                    
                    System.out.print("Enter the destination city (For example, Chicago IL): ");
                    String endCity = reader.readLine().trim();
                    
                    System.out.print("Enter scenic spots (Comma separation, For example, Hollywood Sign): ");
                    String attractionsInput = reader.readLine().trim();
                    List<String> attractions = Arrays.asList(attractionsInput.split(","));
                    
                    // Clean data
                    for (int i = 0; i < attractions.size(); i++) {
                        attractions.set(i, attractions.get(i).trim());
                    }
                    
                    // Plan route
                    Route route = planner.findOptimalRoute(startCity, endCity, attractions);
                    
                    // Display results
                    System.out.println("\nresult:");
                    System.out.println("Starting point: " + startCity);
                    System.out.println("destination: " + endCity);
                    System.out.println("scenic spot: " + attractions);
                    System.out.println("Optimal route: " + route.getCities());
                    System.out.println("Total distance: " + route.getTotalDistance() + " Mile");
                    
                    System.out.print("\nWhether to continue or not (y/n)? ");
                    String continueResponse = reader.readLine().trim().toLowerCase();
                    if (!continueResponse.equals("y")) {
                        break;
                    }
                } catch (Exception e) {
                    System.out.println("An error occurred: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            reader.close();
        } catch (IOException e) {
            System.out.println("The input cannot be read: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 