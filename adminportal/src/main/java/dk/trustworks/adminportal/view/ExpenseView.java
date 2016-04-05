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

    private final DataAccess dataAccess = new DataAccess();
    TextField filter = new TextField();
    public Grid expenseList = new Grid();
    Button newExpense = new Button("New expense");

    ExpenseForm expenseForm = new ExpenseForm(this);

    public ExpenseView() {
        this.setSizeFull();
        configureComponents();
        buildLayout();
    }

    private void configureComponents() {
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

        addComponent(mainLayout);
    }

    public void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void refreshContacts(String stringFilter) {
        expenseList.setContainerDataSource(new BeanItemContainer<>(
                Expense.class, dataAccess.getExpenses()));
        expenseForm.setVisible(false);
    }
}
