package dk.trustworks.invoicemanager.dto;

/**
 * Created by hans on 07/10/15.
 */
public class ProductLine {

    public String uuid;
    public String invoiceuuid;
    public String description;
    public double rate;
    public double amount;

    public ProductLine() {
    }

    public ProductLine(String uuid, String invoiceuuid, String description, double rate, double amount) {
        this.uuid = uuid;
        this.invoiceuuid = invoiceuuid;
        this.description = description;
        this.rate = rate;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "ProductLine{" +
                "uuid='" + uuid + '\'' +
                ", invoiceuuid='" + invoiceuuid + '\'' +
                ", description='" + description + '\'' +
                ", rate=" + rate +
                ", amount=" + amount +
                '}';
    }
}
