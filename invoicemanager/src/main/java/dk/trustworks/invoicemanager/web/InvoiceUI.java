package dk.trustworks.invoicemanager.web;

import com.google.common.collect.Lists;
import com.vaadin.annotations.Theme;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import dk.trustworks.invoicemanager.client.RestClient;
import dk.trustworks.invoicemanager.dto.Invoice;
import dk.trustworks.invoicemanager.dto.ReportDTO;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

/**
 * Created by hans on 23/09/15.
 */
@SuppressWarnings("serial")
@Theme("valo")
public class InvoiceUI extends UI {

    class Dashboard extends CssLayout {
        public Dashboard() {
            setSizeFull();
            Responsive.makeResponsive(this);

            Collection<Invoice> invoices = Lists.newArrayList();

            BeanItemContainer<Invoice> container = new BeanItemContainer<>(Invoice.class, invoices);

            Grid grid = new Grid(container);
            grid.removeColumn("uuid");
            grid.setColumnOrder("invoiceNumber", "created");
            this.addComponent(grid);
        }
    }

    @Override
    protected void init(VaadinRequest vaadinRequest) {

        Button button = new Button("Do not press this button");

        button.addClickListener(new Button.ClickListener() {
            public void buttonClick(Button.ClickEvent event) {
                Notification.show("Do not press this button again");
            }
        });

        VerticalLayout vertical = new VerticalLayout();
        vertical.addComponent(new Dashboard());
        vertical.addComponent(button);
        vertical.addComponent(selectProject());
        setContent(vertical);
    }

    private HorizontalLayout selectProject() {
        HorizontalLayout fittingLayout = new HorizontalLayout();

        RestClient restClient = new RestClient();
        List<ReportDTO> monthlyReport = restClient.getMonthlyReport(Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.YEAR));

        TreeTable ttable = new TreeTable("Monthly Budget");
        ttable.addContainerProperty("clientName", String.class, null);
        ttable.addContainerProperty("projectName", String.class, null);
        ttable.addContainerProperty("taskName", String.class, null);
        ttable.addContainerProperty("taskUUID", String.class, null);
        ttable.addContainerProperty("workerUUID", String.class, null);
        ttable.addContainerProperty("workerName", String.class, null);
        ttable.addContainerProperty("hours", Double.class, null);
        ttable.addContainerProperty("rate", Double.class, null);
        ttable.addContainerProperty("sum", Double.class, null);

        fittingLayout.addComponent(new Button("Medium-sized"));
        fittingLayout.addComponent(new Button("Quite a big component"));
        return fittingLayout;

    }
}
