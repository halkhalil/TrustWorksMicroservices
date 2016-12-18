package dk.trustworks.bimanager.dto.numerics;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 17/12/2016.
 */
public class GraphWidget {

    public String postfix;

    public List<Data> data;

    public GraphWidget() {
        data = new ArrayList<>();
    }

    public GraphWidget(String postfix) {
        data = new ArrayList<>();
        this.postfix = postfix;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("GraphWidget{");
        sb.append("postfix='").append(postfix).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
