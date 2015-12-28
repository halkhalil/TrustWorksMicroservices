package dk.trustworks.invoicemanager.handlers;

import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.invoicemanager.services.InvoiceService;
import io.undertow.server.HttpServerExchange;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by hans on 16/03/15.
 */
public class InvoiceHandler extends DefaultHandler {

    private static final Logger log = LogManager.getLogger(InvoiceHandler.class);
    private final InvoiceService invoiceService;

    public InvoiceHandler() {
        super("invoice");
        this.invoiceService = new InvoiceService();
        addCommand("create");
    }

    public void create(HttpServerExchange exchange, String[] params) {
        log.debug("TaskWorkerConstraintBudgetHandler.create");
        log.debug("exchange = [" + exchange + "], params = [" + params + "]");
        String projectUUID = exchange.getQueryParameters().get("projectuuid").getFirst();
        int month = Integer.parseInt(exchange.getQueryParameters().get("month").getFirst());
        int year = Integer.parseInt(exchange.getQueryParameters().get("year").getFirst());

    }

    @Override
    protected DefaultLocalService getService() {
        return invoiceService;
    }
}
