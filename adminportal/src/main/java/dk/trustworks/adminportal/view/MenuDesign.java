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
    protected NativeButton menuButton5;
    protected Panel scroll_panel;

    public MenuDesign() {
        Design.read(this);
        DashboardDesign dashboardDesign = new DashboardDesign();
        UserPerformanceDesign userPerformanceDesign = new UserPerformanceDesign();
        // TODO: make this work
        //UserDesign userDesign = new UserDesign();
        ExpenseView expenseView = new ExpenseView();
        MicroServicesDesign microServicesDesign = new MicroServicesDesign();

        menuButton1.addClickListener((Button.ClickListener) event -> {
            menuButton1.addStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton4.removeStyleName("selected");
            menuButton5.removeStyleName("selected");

            scroll_panel.setContent(dashboardDesign);
        });

        menuButton2.addClickListener((Button.ClickListener) event -> {
            menuButton2.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton4.removeStyleName("selected");
            menuButton5.removeStyleName("selected");

            scroll_panel.setContent(userPerformanceDesign);
        });

        menuButton3.addClickListener((Button.ClickListener) event -> {
            menuButton3.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton4.removeStyleName("selected");
            menuButton5.removeStyleName("selected");
            // TODO: make this work
            //scroll_panel.setContent(userDesign);
        });

        menuButton4.addClickListener((Button.ClickListener) event -> {
            menuButton4.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton5.removeStyleName("selected");

            scroll_panel.setContent(expenseView);
        });

        menuButton5.addClickListener((Button.ClickListener) event -> {
            menuButton5.addStyleName("selected");
            menuButton1.removeStyleName("selected");
            menuButton2.removeStyleName("selected");
            menuButton3.removeStyleName("selected");
            menuButton4.removeStyleName("selected");

            scroll_panel.setContent(microServicesDesign);
        });

        scroll_panel.setContent(dashboardDesign);
    }
}
