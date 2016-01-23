package dk.trustworks.adminportal.component;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import dk.trustworks.adminportal.domain.Expense;
import dk.trustworks.adminportal.domain.ExpenseDescription;
import dk.trustworks.adminportal.domain.ExpenseType;
import dk.trustworks.adminportal.view.ExpenseView;

import static com.vaadin.ui.Notification.*;

/**
 * Created by hans on 22/01/16.
 */
public class ExpenseForm extends FormLayout {

    Button save = new Button("Save", this::save);
    Button cancel = new Button("Cancel", this::cancel);
    TextField uuid = new TextField("uuid");
    ListSelect description = new ListSelect("description");
    ListSelect type = new ListSelect("type");
    TextField expense = new TextField("expense");
    TextField year = new TextField("year");
    TextField month = new TextField("month");

    Expense expenseItem;

    // Easily bind forms to beans and manage validation and buffering
    BeanFieldGroup<Expense> formFieldBindings;
    private ExpenseView expenseView;

    public ExpenseForm(ExpenseView expenseView) {
        this.expenseView = expenseView;
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
        type.addItems(ExpenseType.values());
        type.setRows(2);
        description.addItems(ExpenseDescription.values());
        description.setRows(6);
        /* Highlight primary actions.
         *
         * With Vaadin built-in styles you can highlight the primary save button
         * and give it a keyboard shortcut for a better UX.
         */
        save.setStyleName(ValoTheme.BUTTON_PRIMARY);
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setSpacing(true);

        addComponents(actions, uuid, description, type, expense, year, month);
    }

    /* Use any JVM language.
     *
     * Vaadin supports all languages supported by Java Virtual Machine 1.6+.
     * This allows you to program user interface in Java 8, Scala, Groovy or any other
     * language you choose.
     * The new languages give you very powerful tools for organizing your code
     * as you choose. For example, you can implement the listener methods in your
     * compositions or in separate controller classes and receive
     * to various Vaadin component events, like button clicks. Or keep it simple
     * and compact with Lambda expressions.
     */
    public void save(Button.ClickEvent event) {
        try {
            // Commit the fields from UI to DAO
            formFieldBindings.commit();

            // Save DAO to backend with direct synchronous service API

            //getUI().service.save(contact);
            expenseView.dataAccess.postExpense(expenseItem);

            String msg = String.format("Saved '%s %s'.",
                    expenseItem.getExpense(),
                    expenseItem.getDescription());
            show(msg, Type.TRAY_NOTIFICATION);
            expenseView.refreshContacts();
        } catch (FieldGroup.CommitException e) {
            // Validation exceptions could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        show("Cancelled", Type.TRAY_NOTIFICATION);
        expenseView.expenseList.select(null);
        //getUI().expenseList.select(null);
    }

    public void edit(Expense expense) {
        this.expenseItem = expense;
        if(expense != null) {
            // Bind the properties of the contact POJO to fiels in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(expense, this);
            uuid.focus();
        }
        setVisible(expense != null);
    }
}