

import java.util.*;

/**
 * Class for planning optimal routes
 */
public class RoutePlanner {
    private Graph graph;
    private DataLoader dataLoader;
    
    public RoutePlanner(Graph graph) {
        this.graph = graph;
    }
    
    public RoutePlanner(Graph graph, DataLoader dataLoader) {
        this.graph = graph;
        this.dataLoader = dataLoader;
    }
    
    /**
     * Find the optimal route from start to end city, visiting all specified attractions
     * 
     * @param startCity starting city
     * @param endCity destination city
     * @param attractions list of attractions to visit
     * @return optimal route
     */
    public Route findOptimalRoute(String startCity, String endCity, List<String> attractions) {
        // Validate input
        if (!graph.hasNode(startCity)) {
            throw new IllegalArgumentException("The starting city " + startCity + " is not in the map");
        }
        if (!graph.hasNode(endCity)) {
            throw new IllegalArgumentException("The destination city " + endCity + " is not in the map");
        }
        
        // If no attractions to visit, just find the shortest path
        if (attractions == null || attractions.isEmpty() || (attractions.size() == 1 && attractions.get(0).isEmpty())) {
            return findShortestPath(startCity, endCity);
        }
        
        // Convert attractions to corresponding cities
        Set<String> attractionCities = new HashSet<>();
        Map<String, String> attractionMap = new HashMap<>();  // Map from attraction to its location
        
        for (String attraction : attractions) {
            if (attraction != null && !attraction.trim().isEmpty()) {
                String location = dataLoader.getLocationForAttraction(attraction);
                if (location != null) {
                    attractionCities.add(location);
                    attractionMap.put(attraction, location);
                } else {
                    throw new IllegalArgumentException("The location of the scenic spot " + attraction + " can't be found");
                }
            }
        }
        
        // All cities to visit (including start and end)
        Set<String> allCities = new HashSet<>(attractionCities);
        allCities.add(startCity);
        allCities.add(endCity);
        List<String> citiesToVisit = new ArrayList<>(allCities);
        
        // Build distance matrix between cities
        int n = citiesToVisit.size();
        int[][] distances = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    Route shortestPath = findShortestPath(citiesToVisit.get(i), citiesToVisit.get(j));
                    distances[i][j] = shortestPath.getTotalDistance();
                }
            }
        }
        
        // Use dynamic programming to solve a variant of the TSP problem
        // Since we have fixed start and end points, this is a path problem rather than a cycle problem
        
        // Find indices of start and end in citiesToVisit
        int startIndex = citiesToVisit.indexOf(startCity);
        int endIndex = citiesToVisit.indexOf(endCity);
        
        // Use greedy algorithm to find an approximate optimal solution
        List<Integer> path = new ArrayList<>();
        path.add(startIndex);
        
        // Mark visited cities
        boolean[] visited = new boolean[n];
        visited[startIndex] = true;
        
        // Keep selecting the nearest unvisited city
        int currentIndex = startIndex;
        while (path.size() < n - 1) {  // All cities except the end
            int nearestIndex = -1;
            int minDistance = Integer.MAX_VALUE;
            
            for (int i = 0; i < n; i++) {
                if (!visited[i] && i != endIndex && distances[currentIndex][i] < minDistance) {
                    nearestIndex = i;
                    minDistance = distances[currentIndex][i];
                }
            }
            
            if (nearestIndex != -1) {
                path.add(nearestIndex);
                visited[nearestIndex] = true;
                currentIndex = nearestIndex;
            } else {
                break;
            }
        }
        
        // Add end point
        path.add(endIndex);
        
        // Build final route
        Route finalRoute = new Route();
        int totalDistance = 0;
        
        // Build route segment by segment
        for (int i = 0; i < path.size() - 1; i++) {
            int fromIndex = path.get(i);
            int toIndex = path.get(i + 1);
            String fromCity = citiesToVisit.get(fromIndex);
            String toCity = citiesToVisit.get(toIndex);
            
            Route segment = findShortestPath(fromCity, toCity);
            
            // First segment is added completely
            if (i == 0) {
                finalRoute.getCities().addAll(segment.getCities());
            } else {
                // For subsequent segments, skip the first city (already at end of previous segment)
                for (int j = 1; j < segment.getCities().size(); j++) {
                    finalRoute.getCities().add(segment.getCities().get(j));
                }
            }
            
            totalDistance += segment.getTotalDistance();
        }
        
        finalRoute.setTotalDistance(totalDistance);
        return finalRoute;
    }
    
    /**
     * Use Dijkstra's algorithm to find the shortest path between two cities
     * 
     * @param start starting city
     * @param end destination city
     * @return Route object containing path and distance
     */
    private Route findShortestPath(String start, String end) {
        // Validate input
        if (!graph.hasNode(start)) {
            throw new IllegalArgumentException("The starting city " + start + " is not in the map");
        }
        if (!graph.hasNode(end)) {
            throw new IllegalArgumentException("The destination city " + end + " is not in the map");
        }
        
        // Start and end are the same
        if (start.equals(end)) {
            Route route = new Route();
            route.addCity(start);
            route.setTotalDistance(0);
            return route;
        }
        
        // Priority queue for Dijkstra's algorithm
        PriorityQueue<Node> priorityQueue = new PriorityQueue<>(Comparator.comparingInt(Node::getDistance));
        
        // Distance and predecessor tables
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        
        // Initialize all cities' distances to infinity
        for (String city : graph.getNodes()) {
            distances.put(city, Integer.MAX_VALUE);
        }
        
        // Start distance is 0
        distances.put(start, 0);
        priorityQueue.add(new Node(start, 0));
        
        // Main loop of Dijkstra's algorithm
        while (!priorityQueue.isEmpty()) {
            Node current = priorityQueue.poll();
            String currentCity = current.getCity();
            int currentDistance = current.getDistance();
            
            // If current distance is greater than known shortest distance, skip
            if (currentDistance > distances.get(currentCity)) {
                continue;
            }
            
            // If destination is reached, end search
            if (currentCity.equals(end)) {
                break;
            }
            
            // Check all neighbors
            for (String neighbor : graph.getNeighbors(currentCity)) {
                int edgeDistance = graph.getDistance(currentCity, neighbor);
                int newDistance = currentDistance + edgeDistance;
                
                // If a shorter path is found, update
                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentCity);
                    priorityQueue.add(new Node(neighbor, newDistance));
                }
            }
        }
        
        // If no path to destination
        if (!predecessors.containsKey(end) && !start.equals(end)) {
            throw new IllegalStateException("There is no path from " + start + " to " + end);
        }
        
        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = end;
        
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }
        
        // Create Route object
        Route route = new Route();
        route.getCities().addAll(path);
        route.setTotalDistance(distances.get(end));
        
        return route;
    }
    
    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
    
    // Internal class: Node for Dijkstra's algorithm
    private static class Node {
        private String city;
        private int distance;
        
        public Node(String city, int distance) {
            this.city = city;
            this.distance = distance;
        }
        
        public String getCity() {
            return city;
        }
        
        public int getDistance() {
            return distance;
        }
    }
} 