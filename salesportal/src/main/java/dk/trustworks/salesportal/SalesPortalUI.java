package dk.trustworks.salesportal;

import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.ejt.vaadin.loginform.LoginForm;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.SessionInitListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;
import dk.trustworks.salesportal.server.VaadinBootstrapListener;
import dk.trustworks.salesportal.view.SalesHeatMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

/**
 * Created by hans on 19/12/2016.
 */

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class SalesPortalUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(new SalesHeatMap().getChart());
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
