package dk.trustworks;

import com.ejt.vaadin.loginform.DefaultVerticalLoginForm;
import com.ejt.vaadin.loginform.LoginForm;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.ui.UI;
import dk.trustworks.adminportal.server.VaadinBootstrapListener;
import dk.trustworks.adminportal.view.MenuDesign;
import dk.trustworks.framework.persistence.Helper;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.UriSpec;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        String username = (String)VaadinSession.getCurrent().getAttribute("username");
        if(username!=null && username.equals("admin")) {
            setContent(new MenuDesign());
        } else {
            DefaultVerticalLoginForm loginForm = new DefaultVerticalLoginForm();
            loginForm.addLoginListener((LoginForm.LoginListener) event -> {
                if(event.getUserName().equals("admin") && event.getPassword().equals("volenti")) {
                    setContent(new MenuDesign());
                }
                System.err.println(
                        "Logged in with user name " + event.getUserName() +
                                " and password of length " + event.getPassword());
                VaadinSession.getCurrent().setAttribute("username", "admin");
            });
            setContent(loginForm);
        }
        //setContent(new MenuDesign());
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
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
