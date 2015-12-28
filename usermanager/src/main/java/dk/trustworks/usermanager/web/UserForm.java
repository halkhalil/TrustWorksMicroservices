package dk.trustworks.usermanager.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.usermanager.dto.User;

import java.sql.SQLException;

/**
 * Created by hans on 06/09/15.
 */
public class UserForm extends FormLayout {

    Button save = new Button("Save", this::save);
    Button cancel = new Button("Cancel", this::cancel);
    TextField firstname = new TextField("First name");
    TextField lastname = new TextField("Last name");
    TextField username = new TextField("Username");
    TextField password = new TextField("Password");
    TextField email = new TextField("Email");

    User user;

    // Easily bind forms to beans and manage validation and buffering
    BeanFieldGroup<User> formFieldBindings;

    public UserForm() {
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setSpacing(true);

        addComponents(actions, firstname, lastname, username, password, email);
    }

    public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            // Save DAO to backend with direct synchronous service API
            getUI().userRepository.create(new ObjectMapper().valueToTree(user));

            String msg = String.format("Saved '%s %s'.",
                    user.getFirstname(),
                    user.getLastname());
            Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);
            getUI().refreshContacts();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Notification.Type.TRAY_NOTIFICATION);
        getUI().contactList.select(null);
        getUI().userForm.setVisible(false);
    }

    void edit(User user) {
        this.user = user;
        if (user != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(user, this);
            firstname.focus();
        }
        setVisible(user != null);
    }

    @Override
    public UserListUI getUI() {
        return (UserListUI) super.getUI();
    }
}
