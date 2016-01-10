package dk.trustworks.bimanager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import dk.trustworks.bimanager.web.view.MenuDesign;

import javax.servlet.annotation.WebServlet;

/**
 *
 */
@Title("AdminUI")
@Theme("valo")
public class AdminUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setContent(new MenuDesign());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = AdminUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
