package dk.trustworks.adminportal.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.vaadin.annotations.DesignRoot;
import com.vaadin.annotations.Theme;
import com.vaadin.data.Item;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import dk.trustworks.adminportal.domain.User;
import dk.trustworks.adminportal.domain.UserStatusEnum;
import dk.trustworks.framework.network.Locator;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by hans on 03/01/16.
 */
@DesignRoot
@Theme("usermanagement")
public class UserDesign extends VerticalLayout {

    protected Grid userGrid;

    public UserDesign() {
        Design.read(this);
        userGrid.setSizeFull();
        setSizeFull();
        ArrayList<User> users = (ArrayList<User>) getUsers();
        System.out.println("users.size() = " + users.size());

        BeanItemContainer<User> container = new BeanItemContainer<>(User.class, users);
        System.out.println("container.size() = " + container.size());


        userGrid.setContainerDataSource(container);
        userGrid.setEditorEnabled(true);
        //userGrid.setEditorBuffered(false);
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
    }

    public List<User> getUsers() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
