package dk.trustworks.adminportal.view;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.*;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.Design;
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.adminportal.domain.User;
import dk.trustworks.adminportal.domain.UserStatusEnum;
import dk.trustworks.framework.network.Locator;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by hans on 03/01/16.
 */
@DesignRoot
@Theme("usermanagement")
public class UserDesign extends VerticalLayout {

    private final DataAccess dataAccess = new DataAccess();
    protected TextField filter = new TextField();
    protected Grid userGrid;
    protected CssLayout user_item1;
    protected Button newUser = new Button("New user");
    protected Button newUserStatus = new Button("New user status");

    public UserDesign() {
        Design.read(this);
        setSizeFull();
        ArrayList<User> users = (ArrayList<User>) dataAccess.getUsers();

        BeanItemContainer<User> container = new BeanItemContainer<>(User.class, users);

        userGrid.setContainerDataSource(container);
        userGrid.setEditorEnabled(true);
        userGrid.setEditorBuffered(false);
        userGrid.setImmediate(true);
        userGrid.removeColumn("password");
        userGrid.removeColumn("firstname");
        userGrid.removeColumn("lastname");
        userGrid.removeColumn("useruuid");
        userGrid.removeColumn("active");
        userGrid.removeColumn("created");
        userGrid.setColumnOrder("username", "email", "status", "statusdate", "allocation");

        userGrid.getEditorFieldGroup().addCommitHandler(new FieldGroup.CommitHandler() {
            @Override
            public void preCommit(FieldGroup.CommitEvent commitEvent)
                    throws FieldGroup.CommitException {
                // Do nothing
            }

            @Override
            public void postCommit(FieldGroup.CommitEvent commitEvent)
                    throws FieldGroup.CommitException {

                Item item = commitEvent.getFieldBinder().getItemDataSource();

                User user = new User(item.getItemProperty("uuid").getValue().toString(),
                        item.getItemProperty("username").getValue().toString(),
                        item.getItemProperty("password").getValue().toString(),
                        item.getItemProperty("firstname").getValue().toString(),
                        item.getItemProperty("lastname").getValue().toString(),
                        item.getItemProperty("email").getValue().toString(),
                        (Date) item.getItemProperty("created").getValue(),
                        (boolean) item.getItemProperty("active").getValue(),
                        item.getItemProperty("useruuid").getValue().toString(),
                        (UserStatusEnum) item.getItemProperty("status").getValue(),
                        (Date) item.getItemProperty("statusdate").getValue(),
                        (int) item.getItemProperty("allocation").getValue());
                try {
                    Unirest.post(Locator.getInstance().resolveURL("userservice") + "/api/users/"+item.getItemProperty("uuid").getValue().toString())
                            .header("accept", "application/json")
                            .body(new ObjectMapper().writeValueAsString(user))
                            .asJson();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //createGraphs(2015);
    }

    private void createGraphs(int year) {
        TopGrossingEmployeesChart topGrossingEmployeesChart = new TopGrossingEmployeesChart(year);
        user_item1.removeAllComponents();
        user_item1.addComponent(topGrossingEmployeesChart);
    }

    public class TopGrossingEmployeesChart extends Chart {

        public TopGrossingEmployeesChart(int year) {
            setWidth("100%");
            setHeight("280px");

            setCaption("Top Grossing Employees");
            getConfiguration().setTitle("");
            getConfiguration().getChart().setType(ChartType.COLUMN);
            getConfiguration().getChart().setAnimation(true);
            getConfiguration().getxAxis().getLabels().setEnabled(true);

            getConfiguration().getxAxis().setTickWidth(0);
            getConfiguration().getyAxis().setTitle("");
            getConfiguration().getLegend().setEnabled(false);

            Credits c = new Credits("");
            getConfiguration().setCredits(c);
        }
    }
}
