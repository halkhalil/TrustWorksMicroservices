package dk.trustworks.financemanager.service;

import com.google.inject.Inject;
import dk.trustworks.financemanager.model.Expense;
import org.joda.time.LocalDate;
import org.jooby.mvc.*;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hans on 21/01/16.
 */
@Path("/api/expenses")
public class ExpensesService {

    private final Sql2o sql2o;

    @Inject
    public ExpensesService(DataSource ds) {
        sql2o = new Sql2o(ds);
    }

    @GET
    public List<Expense> root(LocalDate periodStart, LocalDate periodEnd) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM expenses " +
                    "WHERE  ((year*10000)+((month+1)*100)+1) between :periodstart and :periodend")
                    .addParameter("periodstart", periodStart.toString("yyyyMMdd"))
                    .addParameter("periodend", periodEnd.toString("yyyyMMdd"))
                    .executeAndFetch(Expense.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @GET
    @Path("/{uuid}")
    public Expense findByID(String uuid) {
        System.out.println("ExpensesService.findByID");
        System.out.println("uuid = [" + uuid + "]");
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM expenses WHERE uuid LIKE :uuid")
                    .addParameter("uuid", uuid)
                    .executeAndFetchFirst(Expense.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Expense();
    }

    @GET
    @Path("/search/findByYear/")
    public List<Expense> findByYear(int year) {
        try (Connection con = sql2o.open()) {
            return con.createQuery("SELECT * FROM expenses WHERE year = :year")
                    .addParameter("year", year)
                    .executeAndFetch(Expense.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    //  ((year*10000)+((month+1)*100)+day) between :periodstart and :periodend

    @POST
    @Consumes("application/json")
    public void create(@Body Expense expense) {
        System.out.println("ExpensesService.create");
        System.out.println("expense = [" + expense + "]");
        try (Connection con = sql2o.open()) {
            Integer resultSize = con.createQuery("SELECT count(uuid) c FROM expenses WHERE uuid LIKE :uuid").addParameter("uuid", expense.getUuid()).executeScalar(Integer.class);
            if(resultSize == 0) {
                con.createQuery("INSERT INTO expenses (uuid, description, type, month, year, expense) VALUES (:uuid, :description, :type, :month, :year, :expense)")
                        .addParameter("uuid", expense.getUuid())
                        .addParameter("description", expense.getDescription())
                        .addParameter("type", expense.getType())
                        .addParameter("month", expense.getMonth())
                        .addParameter("year", expense.getYear())
                        .addParameter("expense", expense.getExpense())
                        .executeUpdate();
            } else {
                con.createQuery("UPDATE expenses SET " +
                        "year = :year, " +
                        "month = :month, " +
                        "expense = :expense, " +
                        "type = :type, " +
                        "description = :description " +
                        "WHERE uuid LIKE :uuid;")
                        .addParameter("uuid", expense.getUuid())
                        .addParameter("description", expense.getDescription())
                        .addParameter("type", expense.getType())
                        .addParameter("month", expense.getMonth())
                        .addParameter("year", expense.getYear())
                        .addParameter("expense", expense.getExpense())
                        .executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @POST
    @Consumes("application/json")
    public void update(@Body Expense expense) {
        System.out.println("ExpensesService.update");
        System.out.println("expense = [" + expense + "]");
        try (Connection con = sql2o.open()) {
            con.createQuery("UPDATE expenses SET " +
                    "year = :year, " +
                    "month = :month, " +
                    "expense = :expense, " +
                    "type = :type, " +
                    "description = :description " +
                    "WHERE uuid LIKE :uuid;")
                    .addParameter("uuid", expense.getUuid())
                    .addParameter("description", expense.getDescription())
                    .addParameter("type", expense.getType())
                    .addParameter("month", expense.getMonth())
                    .addParameter("year", expense.getYear())
                    .addParameter("expense", expense.getExpense())
                    .executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
