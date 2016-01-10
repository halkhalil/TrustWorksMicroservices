package dk.trustworks.bimanager.web.view;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

/**
 * Created by hans on 03/01/16.
 */
@DesignRoot
public class MenuDesign extends VerticalLayout {
    public MenuDesign() {
        Design.read(this);
    }
}
