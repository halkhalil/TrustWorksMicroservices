package dk.trustworks.adminportal.domain;

import java.util.Comparator;

/**
 * Created by hans on 29/12/15.
 */
public class AmountPerItem implements  Comparable<AmountPerItem> {
    public String uuid;
    public String description;
    public double amount;

    public AmountPerItem() {
    }

    public AmountPerItem(String uuid, String description, double amount) {
        this.uuid = uuid;
        this.description = description;
        this.amount = amount;
    }

    public int compareTo(Object obj1, Object obj2) {
        double p1 = ((AmountPerItem) obj1).amount;
        double p2 = ((AmountPerItem) obj2).amount;

        if (p1 > p2) {
            return 1;
        } else if (p1 < p2){
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public int compareTo(AmountPerItem o) {
        return Double.compare(o.amount, this.amount);
    }
}
