package dk.trustworks.adminportal.view;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;

/**
 * Created by hans on 03/01/16.
 */
@DesignRoot
@Theme("usermanagement")
public class MenuDesign extends VerticalLayout {
    protected CssLayout header_bar;
    protected NativeButton user_menu;
    protected Label user_name_label;
    protected TextField search_field;
    protected HorizontalLayout main_area;
    protected CssLayout side_bar;
    protected NativeButton menuButton1;
    protected NativeButton menuButton2;
    protected NativeButton menuButton3;
    protected Panel scroll_panel;

    public MenuDesign() {
        Design.read(this);
        DashboardDesign dashboardDesign = new DashboardDesign();
        dashboardDesign.setSizeFull();

        menuButton2.addClickListener((Button.ClickListener) event -> {
            menuButton2.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton3.removeStyleName("selected");

            UserDesign userDesign = new UserDesign();
            userDesign.setSizeFull();
            scroll_panel.setContent(userDesign);
        });

        scroll_panel.setContent(dashboardDesign);
    }
}
