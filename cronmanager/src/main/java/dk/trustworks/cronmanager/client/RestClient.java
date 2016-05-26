package dk.trustworks.cronmanager.client;

import com.mashape.unirest.http.Unirest;

/**
 * Created by hans on 18/05/15.
 */
public class RestClient {


    public void updateBudgetByMonthAndYear(int month, int year) {
        try {
            Unirest.get(Locator.getInstance().resolveURL("biservice") + "/api/projectbudgets/budgetcleanup")
                    .queryString("month", month)
                    .queryString("year", year)
                    .header("accept", "application/json")
                    .asJson();
        } catch (Exception e) {
            throw new RuntimeException("Kunne ikke kalde: budgetcleanup", e);
        }
    }
}
