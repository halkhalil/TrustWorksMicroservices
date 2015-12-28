package dk.trustworks.usermanager.web;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Created by hans on 20/12/15.
 */
@DesignRoot
public class UserList extends VerticalLayout {
    public UserList() {
        Design.read(this);
    }
}
