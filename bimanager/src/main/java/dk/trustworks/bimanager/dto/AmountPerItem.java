package dk.trustworks.bimanager.dto;

/**
 * Created by hans on 08/07/16.
 */
public class AmountPerItem {
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
