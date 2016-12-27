package dk.trustworks.whereareweportal;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import dk.trustworks.whereareweportal.maps.BasicMap;
import dk.trustworks.whereareweportal.maps.ConstrainedMap;
import dk.trustworks.whereareweportal.maps.IconFeatureMap;
import dk.trustworks.whereareweportal.maps.VectorLayerMap;
import dk.trustworks.whereareweportal.server.VaadinBootstrapListener;
import org.joda.time.LocalDate;
import org.vaadin.addon.vol3.OLView;
import org.vaadin.addon.vol3.OLViewOptions;
import org.vaadin.addon.vol3.client.Projections;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

/**
 * Created by hans on 19/12/2016.
 */

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class WhereAreWePortalUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        LocalDate localDateStart = LocalDate.now();
        LocalDate localDateEnd = LocalDate.now().plusYears(1);

        setContent(new BasicMap());
    }

    @WebServlet(urlPatterns = "/*", name = "SalesPortalServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = WhereAreWePortalUI.class, productionMode = false)
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
