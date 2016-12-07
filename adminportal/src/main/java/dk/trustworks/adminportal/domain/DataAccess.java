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
import dk.trustworks.framework.model.Revenue;
import dk.trustworks.framework.model.TaskWorkerConstraintBudget;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.network.Locator;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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

    public Double[] getRevenuePerDay() {
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
        System.out.println("DataAccess.getRevenuePerMonth");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
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

            System.out.println("*********************");

            long[] result = new long[12];
            for (int i = 0; i < 12; i++) {
                result[i] = Math.round(revenueList.get(i).revenue);
            }

            for (int i = 0; i < 12; i++) {
                System.out.println("revenueList = " + revenueList.get(i).revenue);
                System.out.println("result = " + result[i]);
                System.out.println("---");
            }
            System.out.println("*********************");

            return result;
/*
            ArrayList<Long> list = new ArrayList<>();
            for (Revenue revenue : revenueList) {
                list.add(Math.round(revenue.revenue / 1000));
            }
            return list.toArray(new Long[12]);
            */
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    public long[] getBudgetPerMonth(LocalDate periodStart, LocalDate periodEnd, int ahead) {
        System.out.println("DataAccess.getBudgetPerMonth");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "], ahead = [" + ahead + "]");
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
            System.out.println("next = " + next);

            List<TaskWorkerConstraintBudget> taskWorkerConstraintBudgetList = mapper.readValue(next, new TypeReference<List<TaskWorkerConstraintBudget>>() {});

            System.out.println("taskWorkerConstraintBudgetList.size() = " + taskWorkerConstraintBudgetList.size());

            long[] budgetPerMonth = new long[12];

            for (TaskWorkerConstraintBudget taskWorkerConstraintBudget : taskWorkerConstraintBudgetList) {
                LocalDate localDate = new LocalDate(taskWorkerConstraintBudget.year, taskWorkerConstraintBudget.month+1, 1);
                System.out.println("localDate = " + localDate);
                System.out.println("periodStart = " + periodStart);
                System.out.println("taskWorkerConstraintBudget = " + taskWorkerConstraintBudget);
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

    public Long[] getRevenuePerMonthByCapacity(int year) {
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

    public long[] getExpensesByPeriod(LocalDate periodStart, LocalDate periodEnd, ExpenseType expenseType) {
        System.out.println("DataAccess.getExpensesByPeriod");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
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

            long[] expensesPerMonth = new long[12];

            for (Expense expense : expenses) {
                if(expenseType != null && expenseType != expense.getType()) continue;
                LocalDate localDate = new LocalDate(expense.getYear(), expense.getMonth()+1, 1);
                expensesPerMonth[new Period(periodStart, localDate, PeriodType.months()).getMonths()] += expense.getExpense();
            }

            return expensesPerMonth;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new long[12];
    }

    public List<Capacity> getCapacityPerMonth(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/capacities")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .queryString("periodStart", periodStart)
                    .queryString("periodEnd", periodEnd)
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
            JodaModule module = new JodaModule();
            mapper.configure(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS , false);
            mapper.registerModule(module);
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<Capacity>>() {});
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
        System.out.println("DataAccess.getUserRevenue");
        System.out.println("periodStart = [" + periodStart + "], periodEnd = [" + periodEnd + "]");
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
            System.out.println("revenues.size() = " + revenues.size());

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
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuerate")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            return jsonResponse.getBody().getObject().getDouble("revenuerate");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
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
            /*
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("income");
            double[] result = new double[12];
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    result[i] = jsonArray.getDouble(i);
                }
            }
            */
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