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
    protected NativeButton menuButton4;
    protected Panel scroll_panel;

    public MenuDesign() {
        Design.read(this);
        DashboardDesign dashboardDesign = new DashboardDesign();
        //dashboardDesign.setSizeFull();

        menuButton1.addClickListener((Button.ClickListener) event -> {
            menuButton1.addStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton4.removeStyleName("selected");

            DashboardDesign currentDesign = new DashboardDesign();
            scroll_panel.setContent(currentDesign);
        });

        menuButton2.addClickListener((Button.ClickListener) event -> {
            menuButton2.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton4.removeStyleName("selected");

            UserPerformanceDesign currentDesign = new UserPerformanceDesign();
            scroll_panel.setContent(currentDesign);
        });

        menuButton3.addClickListener((Button.ClickListener) event -> {
            menuButton3.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton4.removeStyleName("selected");

            UserDesign currentDesign = new UserDesign();
            scroll_panel.setContent(currentDesign);
        });

        menuButton4.addClickListener((Button.ClickListener) event -> {
            menuButton4.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton3.removeStyleName("selected");

            ExpenseView currentDesign = new ExpenseView();
            scroll_panel.setContent(currentDesign);
        });

        scroll_panel.setContent(dashboardDesign);
    }
}
