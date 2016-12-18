package dk.trustworks.bimanager.dto.numerics;

/**
 * Created by hans on 17/12/2016.
 */
public class NumberWidget {
    public String postfix;
    public Data data;

    public NumberWidget() {
        data = new Data();
    }

    public NumberWidget(String postfix, long data) {
        this.postfix = postfix;
        this.data = new Data(data);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NumberWidget{");
        sb.append("postfix='").append(postfix).append('\'');
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
