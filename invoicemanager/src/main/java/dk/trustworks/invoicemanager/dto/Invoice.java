package dk.trustworks.invoicemanager.dto;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Invoice {

    private String uuid;

    private String invoiceNumber;

    private String description;

    private int month;

    private int year;

    private String projectUUID;

    private Date created;

    private InvoiceStatus status;

    public Invoice() {
        uuid = UUID.randomUUID().toString();
        created = Calendar.getInstance().getTime();
    }

    public Invoice(String uuid, String invoiceNumber, String description, int month, int year, String projectUUID, InvoiceStatus status) {
        this.uuid = uuid;
        this.invoiceNumber = invoiceNumber;
        this.description = description;
        this.month = month;
        this.year = year;
        this.projectUUID = projectUUID;
        created = Calendar.getInstance().getTime();
        this.status = status;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getProjectUUID() {
        return projectUUID;
    }

    public void setProjectUUID(String projectUUID) {
        this.projectUUID = projectUUID;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public InvoiceStatus getStatus() {
        return status;
    }

    public void setStatus(InvoiceStatus status) {
        this.status = status;
    }
}
