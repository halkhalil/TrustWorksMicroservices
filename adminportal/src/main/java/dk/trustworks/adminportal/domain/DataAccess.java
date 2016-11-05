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
import dk.trustworks.framework.network.Locator;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public Double[] getRevenuePerDay() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperday")
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenueperday");
            ArrayList<Double> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.getDouble(i) / 1000.0);
                }
            }
            return list.toArray(new Double[30]);
        } catch (Exception e) {
            System.err.println(e);
        }
        return new Double[0];
    }

    public Long[] getRevenuePerMonth(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuepermonth")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenuepermonth");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(Math.round(jsonArray.getDouble(i)/1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public Long[] getBudgetPerMonth(int year, int ahead) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/budgetpermonth")
                    .queryString("year", year)
                    .queryString("ahead", ahead)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("budgetpermonth");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i=0;i<len;i++){
                    list.add(Math.round(jsonArray.getDouble(i)/1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public List<AmountPerItem> getBillableHoursPercentagePerUser(int year, boolean fiscal) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/billablehourspercentageperuser")
                    .queryString("year", year)
                    .queryString("fiscal", fiscal)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: billablehourspercentageperuser ", e);
        }
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

    public Long[] getRevenuePerMonthPerUser(int year, String useruuid) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuepermonthperuser")
                    .queryString("year", year)
                    .queryString("useruuid", useruuid)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenuepermonth");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
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
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public Long[] getExpensesByYear(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/expensepermonth")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("expensepermonth");
            ArrayList<Long> list = new ArrayList<>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public List<Capacity> getCapacityPerMonthByYear(LocalDate periodStart, LocalDate periodEnd) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/capacities")
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
        return null;
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
        return null;
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
        return null;
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
                    list.add(Math.round(jsonArray.getDouble(i) / 1000.0));
                }
            }
            return list.toArray(new Long[12]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public List<AmountPerItem> getProjectRevenue(int year, boolean fiscal) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperproject")
                    .queryString("year", year)
                    .queryString("fiscal", fiscal)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public List<AmountPerItem> getUserRevenue(int year, boolean fiscal) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperuser")
                    .queryString("year", year)
                    .queryString("fiscal", fiscal)
                    .header("accept", "application/json")
                    .header("jwt-token", (String) VaadinSession.getCurrent().getAttribute("jwtToken"))
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
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
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
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