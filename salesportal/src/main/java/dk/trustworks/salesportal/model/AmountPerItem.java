package dk.trustworks.salesportal.model;

/**
 * Created by hans on 29/12/15.
 */
public class AmountPerItem implements  Comparable<AmountPerItem> {
    public String uuid;
    public String description;
    public double amount;
    public String status;

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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AmountPerItem{");
        sb.append("uuid='").append(uuid).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", amount=").append(amount);
        sb.append('}');
        return sb.toString();
    }
}
