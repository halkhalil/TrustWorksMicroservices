package dk.trustworks.adminportal.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.vaadin.server.VaadinSession;
import dk.trustworks.adminportal.db.ConnectionHelper;
import dk.trustworks.framework.model.Revenue;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.network.Locator;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONArray;
import org.json.JSONObject;
import org.sql2o.Connection;
import org.sql2o.Sql2o;
import org.sql2o.data.Row;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class DataAccess implements Serializable {

    public DataAccess() {
    }

    public JwtToken getJwtToken(String username, String password) throws Exception {
        HttpResponse<JsonNode> jsonResponse;
        jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/jwttoken")
                .queryString("username", username)
                .queryString("password", password)
                .header("accept", "application/json")
                .asJson();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<JwtToken>() {});
    }

    /*
    HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/api/works/search/findByPeriodAndUserUUID")
                    .header("accept", "application/json")
                    .header("jwt-token", jwtToken)
                    .queryString("periodStart", periodStart.toString("yyyy-MM-dd"))
                    .queryString("periodEnd", periodEnd.toString("yyyy-MM-dd"))
                    .queryString("useruuid", userUUID)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Work>>() {
     */

    /*
    SELECT w.year, w.month, w.day, SUM(w.workduration * twc.price)  FROM timemanager.work_latest w
          INNER JOIN usermanager.user u ON w.useruuid = u.uuid
          INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid
          WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between 20160700 and 20170631
          GROUP BY w.month, w.day;
     */

    public Double[] getRevenuePerDay() {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT concat((w.year),'-',((w.month+1)),'-',w.day) date, " +
                "SUM(w.workduration * twc.price) revenue FROM timemanager.work_latest w " +
                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :fromDate and :toDate " +
                "GROUP BY w.year, w.month, w.day;";
        List<Map<String,Object>> revenueList;
        try(Connection con = sql2o.open()) {
            revenueList = con.createQuery(sql)
                    .addParameter("fromDate", LocalDate.now().minusMonths(1).toString("yyyyMMdd"))
                    .addParameter("toDate", LocalDate.now().toString("yyyyMMdd"))
                    .executeAndFetchTable().asList();
            con.close();
            Double[] revenue = new Double[30];
            for (int i = 0; i < 30; i++) {
                revenue[i] = 0.0;
                for (Map<String, Object> objectMap : revenueList) {
                    if(new LocalDate(objectMap.get("date")).isEqual(LocalDate.now().minusDays(i))) {
                        revenue[i] = (double)objectMap.get("revenue");
                    }
                }
            }
            return revenue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Double[0];
    }

    @Deprecated
    public Double[] getRevenuePerDayRest() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/revenues/days")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", LocalDate.now().minusMonths(1).toString("yyyy-MM-dd"))
                    .queryString("periodEnd", LocalDate.now().toString("yyyy-MM-dd"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            List<Revenue> revenueList = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Revenue>>() {});
            ArrayList<Double> list = new ArrayList<>();
            for (Revenue revenue : revenueList) {
                list.add(revenue.revenue);
            }
            return list.toArray(new Double[31]);
        } catch (Exception e) {
            System.err.println(e);
        }
        return new Double[0];
    }

    public long[] getRevenuePerMonth(LocalDate periodStart, LocalDate periodEnd) {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT SUM(w.workduration * twc.price) revenue  FROM timemanager.work_latest w " +
                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                "GROUP BY w.year, w.month ORDER BY w.year, w.month;";
        List<Double> revenueList;
        try(Connection con = sql2o.open()) {
            revenueList = con.createQuery(sql)
                    .addParameter("periodStart", periodStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", periodEnd.toString("yyyyMMdd"))
                    .executeScalarList(Double.class);
            con.close();
            return Arrays.copyOf(revenueList.stream().mapToLong(i -> Math.round(i)).toArray(), new Period(periodStart, periodEnd, PeriodType.months()).getMonths());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    @Deprecated
    public long[] getRevenuePerMonthRest(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/revenues/months")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<Revenue> revenueList = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Revenue>>() {});

            long[] result = new long[12];
            for (int i = 0; i < 12; i++) {
                result[i] = Math.round(revenueList.get(i).revenue);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    public long[] getBudgetPerMonth(LocalDate periodStart, LocalDate periodEnd, int ahead) {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT SUM(t.budget) FROM clientmanager.taskworkerconstraint_latest t " +
                "WHERE ((t.year*10000)+((t.month+1)*100)) between :periodStart and :periodEnd " +
                "GROUP BY t.year, t.month " +
                "ORDER BY t.year, t.month;";
        List<Double> budgetList;
        try(Connection con = sql2o.open()) {
            budgetList = con.createQuery(sql)
                    .addParameter("periodStart", periodStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", periodEnd.toString("yyyyMMdd"))
                    .executeScalarList(Double.class);
            con.close();
            return Arrays.copyOf(budgetList.stream().mapToLong(i -> Math.round(i)).toArray(), new Period(periodStart, periodEnd, PeriodType.months()).getMonths());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    @Deprecated
    public long[] getBudgetPerMonthRest(LocalDate periodStart, LocalDate periodEnd, int ahead) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/budget/search/findByPeriod")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .queryString("ahead", ahead)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();

            String next = new Scanner(jsonResponse.getRawBody(), "utf-8").useDelimiter("\\Z").next();

            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudgetList = mapper.readValue(next, new TypeReference<List<TaskWorkerConstraintBudget>>() {});

            long[] budgetPerMonth = new long[12];

            for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : taskWorkerConstraintBudgetList) {
                LocalDate localDate = new LocalDate(taskWorkerConstraintBudget.year, taskWorkerConstraintBudget.month+1, 1);
                budgetPerMonth[new Period(periodStart, localDate, PeriodType.months()).getMonths()] += taskWorkerConstraintBudget.budget;
            }

/*
            for (int i = 0; i < 12; i++) {
                budgetPerMonth[i] += Math.round(taskWorkerConstraintBudgetList.get(i).budget);
            }
*/
            return budgetPerMonth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    public List<AmountPerItem> getBillableHoursPerUser(int year, boolean fiscal) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/billablehoursperuser")
                    .queryString("year", year)
                    .queryString("fiscal", fiscal)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<AmountPerItem> getBillableHoursPercentagePerUser(int year, boolean fiscal) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("financeservice") + "/api/statistics/billablehourspercentageperuser")
                    .queryString("year", year)
                    .queryString("fiscal", fiscal)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<AmountPerItem> getWorkPerUserPerTaskByProject(String projectUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuepertaskperpersonbyproject")
                    .queryString("projectuuid", projectUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public long[] getRevenuePerMonthPerUser(LocalDate periodStart, LocalDate periodEnd, String useruuid) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/"+useruuid+"/revenues/months")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .queryString("useruuid", useruuid)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<Revenue> revenues = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Revenue>>() {});

            long[] result = new long[12];
            for (int i = 0; i < result.length; i++) {
                result[i] = Math.round(revenues.get(1).revenue);
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    public Double[] getSickDaysPerMonthPerUser(int year, String useruuid) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/sickdayspermonthperuser")
                    .queryString("year", year)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("sickdayspermonth");
            ArrayList<Double> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.getDouble(i));
                }
            }
            return list.toArray(new Double[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Double[12];
    }

    public Double[] getFreeDaysPerMonthPerUser(int year, String useruuid) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/freedayspermonthperuser")
                    .queryString("year", year)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("freedayspermonth");
            ArrayList<Double> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.getDouble(i));
                }
            }
            return list.toArray(new Double[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Double[12];
    }

    public long[] getRevenuePerMonthByCapacity(LocalDate periodStart, LocalDate periodEnd) {
        long[] revenuepermonth = getRevenuePerMonth(periodStart, periodEnd);
        long[] capacitypermonth = getCapacityPerMonth(periodStart, periodEnd);

        for (int i = 0; i < revenuepermonth.length; i++) {
            if(revenuepermonth[i] == 0) continue;
            if(capacitypermonth[i] == 0) continue;
            revenuepermonth[i] = Math.round((revenuepermonth[i] / capacitypermonth[i] * 37.0f));
        }
        return revenuepermonth;

    }

    @Deprecated
    public Long[] getRevenuePerMonthByCapacityRest(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuepermonthbycapacity")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenuepermonthbycapacity");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i)));
                }
            }
            return list.toArray(new Long[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Long[12];
    }

    public Long[] getExpensesByCapacityByYear(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/expensepermonthbycapacity")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("expensepermonthbycapacity");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i)));
                }
            }
            return list.toArray(new Long[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Long[12];
    }

    public Long[] getExpensesByCapacityByYearExceptSalary(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/expensepermonthbycapacityexceptsalary")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("expensepermonthbycapacity");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i)));
                }
            }
            return list.toArray(new Long[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Long[12];
    }

    public List<Expense> getExpensesByPeriod(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("financeservice") + "/api/expenses")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();

            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<Expense> expenses = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Expense>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public long[] getCapacityPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT * FROM (";
        LocalDate localDate = periodStart;
        do {
            sql += "SELECT SUM(allocation) alle FROM user u RIGHT JOIN ( " +
                    "select t.useruuid, t.status, t.statusdate, t.allocation " +
                    "from userstatus t " +
                    "inner join ( " +
                    "select useruuid, status, max(statusdate) as MaxDate " +
                    "from userstatus " +
                    "WHERE statusdate <= '"+localDate.toString("yyyy-MM-dd")+"' " +
                    "group by useruuid " +
                    ") tm " +
                    "on t.useruuid = tm.useruuid and t.statusdate = tm.MaxDate " +
                    ") usi " +
                    "ON u.uuid = usi.useruuid";
            localDate = localDate.plusMonths(1);
            if(!localDate.isEqual(periodEnd)) sql += " UNION ALL ";
        } while (!localDate.isEqual(periodEnd));
        sql += ") m2;";

        long[] result = new long[12];
        List<Long> longs;
        try(Connection con = sql2o.open()) {
            longs = con.createQuery(sql).executeScalarList(Long.class);
            con.close();

            result = longs.stream().mapToLong(i -> i).toArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Deprecated
    public List<Capacity> getCapacityPerMonthRest(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/capacities")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            JodaModule module = new JodaModule();
            mapper.registerModule(module);
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<Availability> getUserAvailabilityPerMonthByYear(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/availabilities")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JodaModule module = new JodaModule();
            mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
            mapper.registerModule(module);
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Availability>>() {});
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }



    public List<Salary> getUserSalaryPerMonthByYear(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/salaries")
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JodaModule module = new JodaModule();
            mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
            mapper.registerModule(module);

            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Salary>>() {});
        } catch (UnirestException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public Long[] getBudgetPerMonthByUser(int year, String userUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/budgetpermonthperuser")
                    .queryString("year", year)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("budgetpermonth");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i)));
                }
            }
            return list.toArray(new Long[12]);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Long[12];
    }

    public List<AmountPerItem> getProjectRevenue(LocalDate periodStart, LocalDate periodEnd) {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT p.name description, p.uuid uuid, SUM(w.workduration * twc.price) amount  " +
                "FROM timemanager.work_latest w " +
                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                "INNER JOIN clientmanager.task t ON t.uuid = twc.taskuuid " +
                "INNER JOIN clientmanager.project p ON p.uuid = t.projectuuid " +
                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                "GROUP BY p.uuid ORDER BY amount;";
        List<AmountPerItem> projectRevenue;
        try(Connection con = sql2o.open()) {
            projectRevenue = con.createQuery(sql)
                    .addParameter("periodStart", periodStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", periodEnd.toString("yyyyMMdd"))
                    .executeAndFetch(AmountPerItem.class);
            con.close();
            return projectRevenue;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Deprecated
    public List<AmountPerItem> getProjectRevenueRest(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/revenues/projects")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<Revenue> revenues = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Revenue>>() {});

            List<AmountPerItem> result = new ArrayList<>();
            for (Revenue revenue : revenues) {
                result.add(new AmountPerItem(revenue.parentUUID, revenue.description, revenue.revenue));
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public List<AmountPerItem> getUserRevenue(LocalDate periodStart, LocalDate periodEnd) {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT concat(u.firstname, ' ', u.lastname) description, u.uuid uuid, SUM(w.workduration * twc.price) amount  " +
                "FROM timemanager.work_latest w " +
                "INNER JOIN usermanager.user u ON w.useruuid = u.uuid " +
                "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.taskuuid = w.taskuuid AND twc.useruuid = u.uuid " +
                "WHERE ((w.year*10000)+((w.month+1)*100)+w.day) between :periodStart and :periodEnd " +
                "GROUP BY w.useruuid ORDER BY amount;";
        List<AmountPerItem> revenueList;
        try(Connection con = sql2o.open()) {
            revenueList = con.createQuery(sql)
                    .addParameter("periodStart", periodStart.toString("yyyyMMdd"))
                    .addParameter("periodEnd", periodEnd.toString("yyyyMMdd"))
                    .executeAndFetch(AmountPerItem.class);
            con.close();
            return revenueList;
            //return Arrays.copyOf(revenueList.stream().mapToLong(i -> Math.round(i)).toArray(), new Period(periodStart, periodEnd, PeriodType.months()).getMonths());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Deprecated
    public List<AmountPerItem> getUserRevenueRest(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/api/revenues/users")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart.toString("yyyy-MM-dd"))
                    .queryString("periodEnd", periodEnd.toString("yyyy-MM-dd"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JodaModule());
            List<Revenue> revenues = mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Revenue>>() {});

            List<AmountPerItem> result = new ArrayList<>();
            for (Revenue revenue : revenues) {
                result.add(new AmountPerItem(revenue.parentUUID, revenue.description, revenue.revenue));
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public List<AmountPerItem> getWorkRegistrationDelay(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/workregisterdelay")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public double[] getBillableHoursPerUserPerDay(int year, String userUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/billablehoursperuserperday")
                    .queryString("year", year)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("billablehoursperday");
            double[] result = new double[7];
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    result[i] = jsonArray.getDouble(i);
                }
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public List<User> getUsers() {
        Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);

        String sql = "SELECT uuid, active, created, email, firstname, lastname, username FROM usermanager.user;";
        List<User> users;
        try(Connection con = sql2o.open()) {
            users = con.createQuery(sql)
                    .executeAndFetch(User.class);
            con.close();
            return users;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    @Deprecated
    public List<User> getUsersWS() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Expense> getExpenses() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("financeservice") + "/api/expenses")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Expense>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void postExpense(Expense expense) {
        try {
            Unirest.post(Locator.getInstance().resolveURL("financeservice") + "/api/expenses")
                    .header("Content-Type", "application/json")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .body(new ObjectMapper().writeValueAsString(expense))
                    .asJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double getRevenueRate() {
        long[] thisMonthRevenue = getRevenuePerMonthByCapacity(LocalDate.now().minusMonths(1), LocalDate.now());
        System.out.println("thisMonthRevenue = " + thisMonthRevenue.length);
        System.out.println("thisMonthRevenue[0] = " + thisMonthRevenue[0]);
        long[] lastYearMonthRevenue = getRevenuePerMonthByCapacity(LocalDate.now().minusYears(1).minusMonths(1), LocalDate.now().minusYears(1));
        System.out.println("lastYearMonthRevenue = " + lastYearMonthRevenue.length);
        System.out.println("lastYearMonthRevenue[0] = " + lastYearMonthRevenue[0]);
        return (((float)thisMonthRevenue[0])/((float)lastYearMonthRevenue[0]));
    }

    public double[] getFiscalYearIncome(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/fiscalyearincome")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("year", year)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return ((Map<String, double[]>)mapper.readValue(jsonResponse.getRawBody(), new TypeReference<Map<String, double[]>>() {})).get("income");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new double[12];
    }

    public JSONObject getBiServiceMetrics() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/servlets/metrics")
                    .header("accept", "application/json")
                    .asJson();
            return jsonResponse.getBody().getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getUserServiceMetrics() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/servlets/metrics")
                    .header("accept", "application/json")
                    .asJson();
            return jsonResponse.getBody().getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getClientServiceMetrics() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("clientservice") + "/servlets/metrics")
                    .header("accept", "application/json")
                    .asJson();
            return jsonResponse.getBody().getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getMotherServiceMetrics() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("motherservice") + "/sys/metrics")
                    .header("accept", "application/json")
                    .asJson();
            return jsonResponse.getBody().getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public JSONObject getTimeServiceMetrics() {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("timeservice") + "/servlets/metrics")
                    .header("accept", "application/json")
                    .asJson();
            return jsonResponse.getBody().getObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}