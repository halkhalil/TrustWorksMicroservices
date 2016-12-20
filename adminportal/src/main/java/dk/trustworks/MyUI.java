package dk.trustworks;

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
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.adminportal.domain.JwtToken;
import dk.trustworks.adminportal.server.VaadinBootstrapListener;
import dk.trustworks.adminportal.view.MenuDesign;
import org.joda.time.DateTime;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Theme("usermanagement")
@Widgetset("dk.trustworks.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        String jwtTokenString = (String)VaadinSession.getCurrent().getAttribute("jwtToken");
        try {
            if(jwtTokenString!=null &&
                    new Date((int)((Map<String, Object>) new ObjectMapper().readValue(Base64.getDecoder().decode(jwtTokenString.split("\\.")[1]), new TypeReference<Map<String, Object>>(){})).get("exp")).after(new Date())) {
                setContent(new MenuDesign());
            } else {
                DefaultVerticalLoginForm loginForm = new DefaultVerticalLoginForm();
                loginForm.addLoginListener((LoginForm.LoginListener) event -> {
                    try {
                        if(!event.getUserName().equals("admin") && !event.getPassword().equals("volenti")) throw new Exception("Could not log in");
                        /*
                        JwtToken jwtToken = new DataAccess().getJwtToken(event.getUserName(), event.getPassword());
                        byte[] decode = Base64.getDecoder().decode(jwtToken.jwtToken.split("\\.")[1]);
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> map = mapper.readValue(decode, new TypeReference<Map<String, Object>>(){});
                        if(!map.get("sub").equals(event.getUserName())) throw new Exception("Could not log in");
                        VaadinSession.getCurrent().setAttribute("jwtToken", jwtToken.jwtToken);
                        */
                        MyUI.this.setContent(new MenuDesign());
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.err.println(
                                "Logged in with user name " + event.getUserName() +
                                        " and password of length " + event.getPassword());
                    }
                });
                setContent(loginForm);
            }
        } catch (Exception e) {
            VaadinSession.getCurrent().setAttribute("jwtToken", null);
            e.printStackTrace();
        }
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
