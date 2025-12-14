

public class Road {
    private String cityA;
    private String cityB;
    private int distance;
    
    public Road(String cityA, String cityB, int distance) {
        this.cityA = cityA;
        this.cityB = cityB;
        this.distance = distance;
    }
    
    public String getCityA() {
        return cityA;
    }
    
    public String getCityB() {
        return cityB;
    }
    
    public int getDistance() {
        return distance;
    }
    
    @Override
    public String toString() {
        return cityA + " -> " + cityB + " (" + distance + " mile)";
    }
} 