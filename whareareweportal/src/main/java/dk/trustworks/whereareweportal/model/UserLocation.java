package dk.trustworks.whereareweportal.model;

/**
 * Created by hans on 19/12/2016.
 */
public class UserLocation {

    public String username;
    public String clientname;
    public double latitude;
    public double longitude;

    public UserLocation() {
    }

    public UserLocation(String username, String clientname, double latitude, double longitude) {
        this.username = username;
        this.clientname = clientname;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserLocation{");
        sb.append("username='").append(username).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", latitude=").append(latitude);
        sb.append(", longitude=").append(longitude);
        sb.append('}');
        return sb.toString();
    }
}
