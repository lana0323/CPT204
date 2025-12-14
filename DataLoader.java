import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataLoader {
    private Map<String, String> attractionToLocation = new HashMap<>();
    private Map<String, List<String>> locationToAttractions = new HashMap<>();
    private List<Road> roads = new ArrayList<>();
    private List<String> cities = new ArrayList<>();
    
    /**
     * Load attraction data
     */
    public void loadAttractions(String filename) throws IOException {
        // Try different paths to load the file
        BufferedReader reader = getBufferedReader(filename);
        String line;
        boolean isHeader = true;
        
        while ((line = reader.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            
            String[] parts = line.split(",");
            if (parts.length == 2) {
                String attraction = parts[0].trim();
                String location = parts[1].trim();
                
                attractionToLocation.put(attraction, location);
                
                // Maintain mapping from cities to attractions
                if (!locationToAttractions.containsKey(location)) {
                    locationToAttractions.put(location, new ArrayList<>());
                }
                locationToAttractions.get(location).add(attraction);
                
                // Ensure the city is in the cities list
                if (!cities.contains(location)) {
                    cities.add(location);
                }
            }
        }
        
        reader.close();
        System.out.println(attractionToLocation.size() + " scenic spots have been loaded ");
    }
    
    /**
     * Load road data
     */
    public void loadRoads(String filename) throws IOException {
        // Try different paths to load the file
        BufferedReader reader = getBufferedReader(filename);
        String line;
        boolean isHeader = true;
        
        while ((line = reader.readLine()) != null) {
            if (isHeader) {
                isHeader = false;
                continue;
            }
            
            String[] parts = line.split(",");
            if (parts.length == 3) {
                String cityA = parts[0].trim();
                String cityB = parts[1].trim();
                int distance = Integer.parseInt(parts[2].trim());
                
                roads.add(new Road(cityA, cityB, distance));
                
                // Ensure cities are in the cities list
                if (!cities.contains(cityA)) {
                    cities.add(cityA);
                }
                if (!cities.contains(cityB)) {
                    cities.add(cityB);
                }
            }
        }
        
        reader.close();
        System.out.println(roads.size() + " roads have been loaded ");
    }
    
    /**
     * Get BufferedReader, try multiple possible file paths
     */
    private BufferedReader getBufferedReader(String filename) throws IOException {
        // Try different path combinations
        String[] pathsToTry = {
            filename,                      // Direct use of provided path
            "src/" + filename,             // src/CW3/file.csv
            "../" + filename,              // ../CW3/file.csv
            "../../" + filename,           // ../../CW3/file.csv
            "./src/" + filename,           // ./src/CW3/file.csv
            "./CW3/" + filename.substring(filename.lastIndexOf("/") + 1)  // ./CW3/file.csv
        };
        
        for (String path : pathsToTry) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("File found at: " + file.getAbsolutePath());
                return new BufferedReader(new FileReader(file));
            }
        }
        
        // If none of the above paths exist, throw an exception with detailed information
        StringBuilder errorMsg = new StringBuilder();
        errorMsg.append("Cannot find file: ").append(filename).append("\n");
        errorMsg.append("Current working directory: ").append(new File(".").getAbsolutePath()).append("\n");
        errorMsg.append("Failed to try following paths:\n");
        for (String path : pathsToTry) {
            errorMsg.append(" - ").append(path).append("\n");
        }
        
        throw new IOException(errorMsg.toString());
    }
    
    /**
     * Build graph
     */
    public Graph buildGraph() {
        Graph graph = new Graph();
        
        // Add all cities as nodes
        for (String city : cities) {
            graph.addNode(city);
        }
        
        // Add all roads as edges
        for (Road road : roads) {
            graph.addEdge(road.getCityA(), road.getCityB(), road.getDistance());
        }
        
        return graph;
    }
    
    public String getLocationForAttraction(String attraction) {
        return attractionToLocation.get(attraction);
    }
    
    public List<String> getAttractionsForLocation(String location) {
        return locationToAttractions.getOrDefault(location, new ArrayList<>());
    }
    
    public List<Road> getRoads() {
        return roads;
    }
    
    public List<String> getCities() {
        return cities;
    }
} 