package dk.trustworks.usermanager.web;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.*;
import dk.trustworks.usermanager.dto.User;
import dk.trustworks.usermanager.persistence.UserRepository;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 06/09/15.
 */
@Title("UserList")
@Theme("valo")
public class UserListUI extends UI {

    UserRepository userRepository = new UserRepository();

    TextField filter = new TextField();
    Grid contactList = new Grid();
    Button newContact = new Button("New contact");

    // ContactForm is an example of a custom component class
    UserForm userForm = new UserForm();
    UserList userList = new UserList();

    @Override
    protected void init(VaadinRequest vaadinRequest) {


        setContent(userList);


        //newContact.addClickListener(e -> userForm.edit(new User()));

        //filter.setInputPrompt("Filter contacts...");
        //filter.addTextChangeListener(e -> refreshContacts(e.getText()));
        List<Map<String, Object>> mapList = userRepository.findByActiveTrue();

        ArrayList<User> users = getUsers(mapList);

        BeanItemContainer<User> container =
                new BeanItemContainer<>(User.class, users);

        contactList.setEditorEnabled(true);
        contactList.setContainerDataSource(container);
        contactList.setColumnOrder("firstname", "lastname", "email", "username");
        contactList.removeColumn("UUID");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactList.addSelectionListener(e -> userForm.edit((User) contactList.getSelectedRow()));
        refreshContacts();

        HorizontalLayout actions = new HorizontalLayout(filter, newContact);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, contactList);
        left.setSizeFull();
        contactList.setSizeFull();
        left.setExpandRatio(contactList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, userForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        setContent(mainLayout);
    }

    private ArrayList<User> getUsers(List<Map<String, Object>> mapList) {
        ArrayList<User> users = new ArrayList<>();
        for (Map<String, Object> map : mapList) {
            User user = new User();
            users.add(user);
            try {
                BeanUtils.populate(user, map);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return users;
    }

    void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void refreshContacts(String stringFilter) {
        contactList.setContainerDataSource(new BeanItemContainer<>(
                User.class, getUsers(userRepository.findByActiveTrue())));
        //contactForm.setVisible(false);
    }
}
