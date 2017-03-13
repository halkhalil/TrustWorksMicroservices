package dk.trustworks.salesportal;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import dk.trustworks.salesportal.server.VaadinBootstrapListener;
import dk.trustworks.salesportal.view.SalesHeatMap;
import dk.trustworks.salesportal.view.SalesView;
import org.joda.time.LocalDate;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by hans on 19/12/2016.
 */

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class SalesPortalUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        LocalDate localDateStart = LocalDate.now().plusMonths(0).withDayOfMonth(1);
        LocalDate localDateEnd = localDateStart.plusYears(1);

        SalesView salesView = new SalesView();
        SalesHeatMap salesHeatMap = new SalesHeatMap(localDateStart, localDateEnd);
        salesView.addComponent(salesHeatMap.getChart());
        salesView.addComponent(salesHeatMap.getAvailabilityChart());
        salesView.addComponent(salesHeatMap.getBudgetGrid());
        setContent(salesView);
    }

    @WebServlet(urlPatterns = "/*", name = "SalesPortalServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = SalesPortalUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
        @Override
        public void init(ServletConfig servletConfig) throws ServletException {
            super.init(servletConfig);
        }

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            getService().addSessionInitListener((SessionInitListener) sessionInitEvent -> sessionInitEvent.getSession().addBootstrapListener(new VaadinBootstrapListener()));
        }
    }
}
