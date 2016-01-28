package dk.trustworks.adminportal.view;

import com.vaadin.data.sort.Sort;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.data.sort.SortDirection;
import com.vaadin.ui.*;
import com.vaadin.ui.renderers.NumberRenderer;
import dk.trustworks.adminportal.component.ExpenseForm;
import dk.trustworks.adminportal.domain.DataAccess;
import dk.trustworks.adminportal.domain.Expense;

import java.text.ChoiceFormat;

/**
 * Created by hans on 22/01/16.
 */
public class ExpenseView extends HorizontalLayout {

    public DataAccess dataAccess = new DataAccess();
    TextField filter = new TextField();
    public Grid expenseList = new Grid();
    Button newExpense = new Button("New expense");

    // ContactForm is an example of a custom component class
    ExpenseForm expenseForm = new ExpenseForm(this);

    public ExpenseView() {
        this.setSizeFull();
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
         /* Synchronous event handling.
         *
         * Receive user interaction events on the server-side. This allows you
         * to synchronously handle those events. Vaadin automatically sends
         * only the needed changes to the web page without loading a new page.
         */
        newExpense.addClickListener(e -> expenseForm.edit(new Expense()));

        filter.setInputPrompt("Filter expenses...");
        filter.addTextChangeListener(e -> refreshContacts(e.getText()));

        expenseList.setContainerDataSource(new BeanItemContainer<>(Expense.class));
        expenseList.setColumnOrder("description", "type", "year", "month", "expense");
        expenseList.getColumn("year").setRenderer(new NumberRenderer());
        expenseList.sort(Sort.by("year", SortDirection.DESCENDING)
                .then("month", SortDirection.DESCENDING).then("description", SortDirection.ASCENDING));
        expenseList.removeColumn("uuid");
        expenseList.setSelectionMode(Grid.SelectionMode.SINGLE);
        expenseList.addSelectionListener(e -> expenseForm.edit((Expense) expenseList.getSelectedRow()));
        refreshContacts();
    }

    /* Robust layouts.
     *
     * Layouts are components that contain other components.
     * HorizontalLayout contains TextField and Button. It is wrapped
     * with a Grid into VerticalLayout for the left side of the screen.
     * Allow user to resize the components with a SplitPanel.
     *
     * In addition to programmatically building layout in Java,
     * you may also choose to setup layout declaratively
     * with Vaadin Designer, CSS and HTML.
     */
    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newExpense);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, expenseList);
        left.setSizeFull();
        expenseList.setSizeFull();
        left.setExpandRatio(expenseList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, expenseForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        // Split and allow resizing
        addComponent(mainLayout);
    }

    /* Choose the design patterns you like.
     *
     * It is good practice to have separate data access methods that
     * handle the back-end access and/or the user interface updates.
     * You can further split your code into classes to easier maintenance.
     * With Vaadin you can follow MVC, MVP or any other design pattern
     * you choose.
     */
    public void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void refreshContacts(String stringFilter) {
        expenseList.setContainerDataSource(new BeanItemContainer<>(
                Expense.class, dataAccess.getExpenses()));
        expenseForm.setVisible(false);
    }
}
