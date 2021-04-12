package tourGuide.DTO;

import tourGuide.model.Location;

public class NearAttractionsDTO {
    private double distance;
    private String nameAttraction;
    private double latitude;
    private double longitude;
    private Location location;
    private int reward;

    public NearAttractionsDTO(String nameAttraction, double distance, double latitude, double longitude, Location location, int reward) {
        this.distance = distance;
        this.nameAttraction = nameAttraction;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.reward = reward;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


    public String getNameAttraction() {
        return nameAttraction;
    }

    public void setNameAttraction(String nameAttraction) {
        this.nameAttraction = nameAttraction;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
