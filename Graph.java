

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Graph representing the connections between cities
 */
public class Graph {
    // Adjacency list representation of the graph
    private Map<String, Map<String, Integer>> adjacencyList = new HashMap<>();
    
    /**
     * Add a node to the graph
     * @param node name of the node to add
     */
    public void addNode(String node) {
        if (!adjacencyList.containsKey(node)) {
            adjacencyList.put(node, new HashMap<>());
        }
    }
    
    /**
     * Add an edge to the graph (undirected graph)
     * @param source source node
     * @param destination destination node
     * @param distance distance
     */
    public void addEdge(String source, String destination, int distance) {
        // Ensure nodes exist
        addNode(source);
        addNode(destination);
        
        // Add bidirectional edge
        adjacencyList.get(source).put(destination, distance);
        adjacencyList.get(destination).put(source, distance);
    }
    
    /**
     * Get the distance between two nodes
     * @param source source node
     * @param destination destination node
     * @return distance, or -1 if no connection
     */
    public int getDistance(String source, String destination) {
        if (!adjacencyList.containsKey(source) || !adjacencyList.containsKey(destination)) {
            return -1;
        }
        
        Map<String, Integer> neighbors = adjacencyList.get(source);
        return neighbors.getOrDefault(destination, -1);
    }
    
    /**
     * Get all neighbors of a node
     * @param node node name
     * @return list of neighbor names
     */
    public List<String> getNeighbors(String node) {
        if (!adjacencyList.containsKey(node)) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(adjacencyList.get(node).keySet());
    }
    
    /**
     * Get all nodes
     * @return list of node names
     */
    public List<String> getNodes() {
        return new ArrayList<>(adjacencyList.keySet());
    }
    
    /**
     * Check if a node exists
     * @param node node name
     * @return whether it exists
     */
    public boolean hasNode(String node) {
        return adjacencyList.containsKey(node);
    }
    
    /**
     * Check if an edge exists between two nodes
     * @param source source node
     * @param destination destination node
     * @return whether the edge exists
     */
    public boolean hasEdge(String source, String destination) {
        if (!adjacencyList.containsKey(source)) {
            return false;
        }
        
        return adjacencyList.get(source).containsKey(destination);
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph with ").append(adjacencyList.size()).append(" nodes:\n");
        
        for (String node : adjacencyList.keySet()) {
            sb.append(node).append(" -> ");
            Map<String, Integer> neighbors = adjacencyList.get(node);
            
            for (Map.Entry<String, Integer> entry : neighbors.entrySet()) {
                sb.append(entry.getKey()).append("(").append(entry.getValue()).append(") ");
            }
            
            sb.append("\n");
        }
        
        return sb.toString();
    }
} 