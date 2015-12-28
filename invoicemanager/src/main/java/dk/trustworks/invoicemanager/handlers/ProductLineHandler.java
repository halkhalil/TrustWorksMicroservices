package dk.trustworks.invoicemanager.handlers;

import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.invoicemanager.services.ProductLineService;

/**
 * Created by hans on 16/03/15.
 */
public class ProductLineHandler extends DefaultHandler {

    private final ProductLineService productLineService;

    public ProductLineHandler() {
        super("productline");
        this.productLineService = new ProductLineService();
    }

    @Override
    protected DefaultLocalService getService() {
        return productLineService;
    }
}
