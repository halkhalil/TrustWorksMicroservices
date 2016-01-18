package dk.trustworks.adminportal.domain;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import dk.trustworks.adminportal.domain.AmountPerItem;
import dk.trustworks.adminportal.domain.User;
import dk.trustworks.framework.network.Locator;
import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataAccess implements Serializable {

    public DataAccess() {
    }

    public Double[] getRevenuePerDay() {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperday")
                    .header("accept", "application/json")
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenueperday");
            ArrayList<Double> list = new ArrayList<Double>();
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    list.add(jsonArray.getDouble(i) / 1000.0);
                }
            }
            return list.toArray(new Double[30]);
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public Long[] getRevenuePerMonth(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenuepermonth")
                    .queryString("year", year)
                    .header("accept", "application/json")
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

    public Long[] getBudgetPerMonth(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/budgetpermonth")
                    .queryString("year", year)
                    .header("accept", "application/json")
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

    public List<AmountPerItem> getBillableHoursPerUser(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/billablehoursperuser")
                    .queryString("year", year)
                    .header("accept", "application/json")
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
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("revenuepermonthbycapacity");
            ArrayList<Long> list = new ArrayList<Long>();
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

    public int[] getCapacityPerMonthByYear(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("userservice") + "/api/users/capacitypermonth")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("capacitypermonth");
            ArrayList<Integer> list = new ArrayList<Integer>();
            int[] result = new int[12];
            if (jsonArray != null) {
                int len = jsonArray.length();
                for (int i = 0; i < len; i++) {
                    result[i] = jsonArray.getInt(i);
                }
            }
            return result;
        } catch (UnirestException e) {
            System.err.println(e);
        }
        return null;
    }

    public Long[] getBudgetPerMonthByUser(int year, String userUUID) {
        try {
            HttpResponse<JsonNode> jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/budgetpermonthperuser")
                    .queryString("year", year)
                    .queryString("useruuid", userUUID)
                    .header("accept", "application/json")
                    .asJson();
            JSONArray jsonArray = jsonResponse.getBody().getObject().getJSONArray("budgetpermonth");
            ArrayList<Long> list = new ArrayList<Long>();
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

    public List<AmountPerItem> getProjectRevenue(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperproject")
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<AmountPerItem>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke loade: AmountPerItem ", e);
        }
    }

    public List<AmountPerItem> getUserRevenue(int year) {
        try {
            HttpResponse<JsonNode> jsonResponse;
            jsonResponse = Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/statistics/revenueperuser")
                    .queryString("year", year)
                    .header("accept", "application/json")
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
                    .asJson();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonResponse.getRawBody(), new TypeReference<List<User>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}