package dk.trustworks.clientmanager.numericsmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by hans on 01/12/2016.
 */
public class NumericsNumber {

    public String postfix;

    @JsonProperty("data")
    public NumericsData data;

    public NumericsNumber() {
    }

    public NumericsNumber(String postfix) {
        this.postfix = postfix;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NumericsNumber{");
        sb.append("postfix='").append(postfix).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
