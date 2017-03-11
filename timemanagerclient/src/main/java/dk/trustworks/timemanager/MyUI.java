package dk.trustworks.timemanager;


import com.jarektoro.responsivelayout.ResponsiveColumn;
import com.jarektoro.responsivelayout.ResponsiveLayout;
import com.jarektoro.responsivelayout.ResponsiveRow;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.*;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.timemanager.server.VaadinBootstrapListener;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@Theme("valo")
@Widgetset("dk.trustworks.timemanager.MyAppWidgetset")
public class MyUI extends UI {

    @Override
    protected void init(VaadinRequest vaadinRequest) {
        setSizeFull(); // set the size of the UI to fill the screen


        ResponsiveLayout responsiveLayout = new ResponsiveLayout();
        responsiveLayout.setSizeFull();
        setContent(responsiveLayout);

        ResponsiveRow rootRow = responsiveLayout.addRow();
        rootRow.setHeight("100%");

        ResponsiveColumn sideMenuCol = new ResponsiveColumn(12, 12, 2, 2);
        rootRow.addColumn(sideMenuCol);

        // Fluent API
        ResponsiveColumn mainContentCol = rootRow.addColumn().withDisplayRules(12,12,10,10);

        ResponsiveRow menu = new ResponsiveRow();

        Resource res = new ThemeResource("images/logo.svg");
        Embedded object = new Embedded("", res);
        object.setMimeType("image/svg+xml"); // Unnecessary
        object.setWidth("100px");
        menu.addColumn().withComponent(object).withOffset(ResponsiveLayout.DisplaySize.XS, 5).withDisplayRules(7, 12, 12, 12);

        Button burgerMenuButton = new Button(FontAwesome.BARS);
        burgerMenuButton.addStyleName(ValoTheme.BUTTON_ICON_ONLY);
        burgerMenuButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        menu.addColumn().withComponent(burgerMenuButton, ResponsiveColumn.ColumnComponentAlignment.CENTER).withDisplayRules(12, 3, 12, 12).withVisibilityRules(true, false, false, false);

        Button customersButton = new Button("Customers");
        customersButton.setIcon(FontAwesome.USERS);
        customersButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        customersButton.setSizeFull();
        menu.addColumn().withComponent(customersButton).withDisplayRules(12, 3, 12, 12).setVisibilityRules(false, true, true, true);

        Button projectsButton = new Button("Projects");
        projectsButton.setIcon(FontAwesome.USERS);
        projectsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        projectsButton.setSizeFull();
        menu.addColumn().withComponent(projectsButton).withDisplayRules(12, 3, 12, 12).setVisibilityRules(false, true, true, true);

        Button tasksButton = new Button("Tasks");
        tasksButton.setIcon(FontAwesome.USERS);
        tasksButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        tasksButton.setSizeFull();
        menu.addColumn().withComponent(tasksButton).withDisplayRules(12, 3, 12, 12).setVisibilityRules(false, true, true, true);

        Button budgetsButton = new Button("Budgets");
        budgetsButton.setIcon(FontAwesome.USERS);
        budgetsButton.addStyleName(ValoTheme.BUTTON_BORDERLESS);
        budgetsButton.setSizeFull();
        ResponsiveColumn reportCol = menu.addColumn().withComponent(budgetsButton).withDisplayRules(12,3,12,12).withVisibilityRules(false, true, true, true);

        sideMenuCol.setComponent(menu);

        ResponsiveLayout mainContentLayout = new ResponsiveLayout();

        mainContentCol.setComponent(mainContentLayout);


        // simple row with one column that takes 3/12 spaces
        // and then the row centers that column to the middle
        ResponsiveRow titleRow = new ResponsiveRow();
        titleRow.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

        Label title = new Label("Test Subjects");
        titleRow.setMargin(true);


        ResponsiveColumn titleCol = new ResponsiveColumn(3);
        titleCol.setComponent(title);
        titleRow.addColumn(titleCol);

        mainContentLayout.addRow(titleRow);


        // Here we have a new Row just for the test subjects

        ResponsiveRow testSubjectsRow = new ResponsiveRow();

        for (int x = 0; x < 10; x++) {

            // We want each column to take
            // 12/12 on mobile
            // 6/12 on tablet
            // 4/12 on computer screens
            // 3/12 on wide computer screens

            ResponsiveColumn testerCol = new ResponsiveColumn(12, 6, 4, 3);
            testerCol.setComponent(new Panel(/* set size full */));
            testSubjectsRow.addColumn(testerCol);

        }

        // sets spacing between the columns and margin around the whole row

        testSubjectsRow.setHorizontalSpacing(true);
        testSubjectsRow.setVerticalSpacing(true);
        testSubjectsRow.setMargin(true);

        mainContentLayout.addRow(testSubjectsRow);


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
