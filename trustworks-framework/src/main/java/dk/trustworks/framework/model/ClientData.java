package dk.trustworks.framework.model;

/**
 * Created by hans on 13/11/2016.
 */
public class ClientData {

    public String uuid;
    public String clientuuid;
    public String clientname;
    public String city;
    public String contactperson;
    public String cvr;
    public String ean;
    public String otheraddressinfo;
    public int postalcode;
    public String streetnamenumber;

    public ClientData() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClientData{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", clientuuid='").append(clientuuid).append('\'');
        sb.append(", clientname='").append(clientname).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", contactperson='").append(contactperson).append('\'');
        sb.append(", cvr='").append(cvr).append('\'');
        sb.append(", ean='").append(ean).append('\'');
        sb.append(", otheraddressinfo='").append(otheraddressinfo).append('\'');
        sb.append(", postalcode=").append(postalcode);
        sb.append(", streetnamenumber='").append(streetnamenumber).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
