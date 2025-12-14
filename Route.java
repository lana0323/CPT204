

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a route from start to destination
 */
public class Route {
    private List<String> cities;  // Ordered list of cities
    private int totalDistance;    // Total distance

    public Route() {
        this.cities = new ArrayList<>();
        this.totalDistance = 0;
    }
    
    public Route(List<String> cities, int totalDistance) {
        this.cities = new ArrayList<>(cities);
        this.totalDistance = totalDistance;
    }
    
    /**
     * Copy constructor
     */
    public Route(Route other) {
        this.cities = new ArrayList<>(other.cities);
        this.totalDistance = other.totalDistance;
    }
    
    /**
     * Add a city to the route
     * @param city city name
     */
    public void addCity(String city) {
        cities.add(city);
    }
    
    /**
     * Set the total distance of the route
     * @param distance total distance
     */
    public void setTotalDistance(int distance) {
        this.totalDistance = distance;
    }
    
    /**
     * Get the list of cities in the route
     * @return list of cities
     */
    public List<String> getCities() {
        return cities;
    }
    
    /**
     * Get the total distance of the route
     * @return total distance
     */
    public int getTotalDistance() {
        return totalDistance;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ");
        
        for (int i = 0; i < cities.size(); i++) {
            sb.append(cities.get(i));
            if (i < cities.size() - 1) {
                sb.append(" -> ");
            }
        }
        
        sb.append(", Distance: ").append(totalDistance).append(" mile");
        return sb.toString();
    }
} 