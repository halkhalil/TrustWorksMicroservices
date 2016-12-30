package dk.trustworks.whereareweportal;

import com.vaadin.annotations.VaadinServletConfiguration;
import dk.trustworks.whereareweportal.db.ConnectionHelper;
import dk.trustworks.whereareweportal.images.WorkIconCreator;
import dk.trustworks.whereareweportal.model.UserLocation;
import org.apache.commons.io.IOUtils;
import org.joda.time.LocalDate;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WhereAreWePortalUI {

    @WebServlet(urlPatterns = "/logo", name = "LogoServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = LogoUI.class, productionMode = false)
    public static class ImageServlet extends HttpServlet {

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String company = request.getParameter("company");
            String[] employees = request.getParameterValues("employees");
            String path = getServletContext().getRealPath(File.separator);
            /*
            BufferedImage bi = new BufferedImage(100, 30,
                    BufferedImage.TYPE_3BYTE_BGR);
            bi.getGraphics().drawChars("test".toCharArray(), 0,
                    "test".length(), 10, 20);
                    */
            response.setContentType("image/png");

            ImageIO.write(new WorkIconCreator().createWorkIcon(path, company, employees), "png", response.getOutputStream());
        }
    }

    @WebServlet(urlPatterns = "/map", name = "MapServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = LogoUI.class, productionMode = false)
    public static class MapServlet extends HttpServlet {

        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            LocalDate localDateStart = LocalDate.now().withDayOfMonth(1).plusMonths(1);
            LocalDate localDateEnd = LocalDate.now().withDayOfMonth(1).plusMonths(2);

            Sql2o sql2o = new Sql2o(ConnectionHelper.getInstance().dataSource);
            List<UserLocation> userLocations = new ArrayList<>();
            List<String> usernames = new ArrayList<>();

            String sql = "SELECT u.username username, c.name clientname, c.latitude latitude, c.longitude longitude " +
                    "FROM clientmanager.taskworkerconstraint_latest b " +
                    "INNER JOIN clientmanager.taskworkerconstraint twc ON twc.uuid = b.taskworkerconstraintuuid " +
                    "INNER JOIN clientmanager.task t ON twc.taskuuid = t.uuid " +
                    "INNER JOIN clientmanager.project p ON t.projectuuid = p.uuid " +
                    "INNER JOIN clientmanager.client c ON p.clientuuid = c.uuid " +
                    "INNER JOIN usermanager.user u ON u.uuid = twc.useruuid " +
                    "WHERE ((b.year*10000)+((b.month+1)*100)) between :periodStart and :periodEnd " +
                    "GROUP BY u.uuid, c.uuid " +
                    "ORDER BY u.lastname DESC, u.uuid;";
            try(Connection con = sql2o.open()) {
                userLocations = con.createQuery(sql)
                        .addParameter("periodStart", localDateStart.toString("yyyyMM")+"00")
                        .addParameter("periodEnd", localDateEnd.toString("yyyyMM")+"00")
                        .executeAndFetch(UserLocation.class);

                usernames = con.createQuery("SELECT username FROM usermanager.user WHERE active = 1;")
                        .executeScalarList(String.class);
                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            userLocations.add(new UserLocation("", "trustworks", 55.68319589999999, 12.570468300000016));

            Map<String, List<UserLocation>> userLocationListMap = new HashMap<>();
            for (UserLocation userLocation : userLocations) {
                userLocation.clientname = userLocation.clientname.replaceAll(" ", "").toLowerCase();
                userLocationListMap.putIfAbsent(userLocation.clientname, new ArrayList<>());
                userLocationListMap.get(userLocation.clientname).add(userLocation);
            }


            String path = getServletContext().getRealPath(File.separator);
            response.setContentType("text/html");

            String html = IOUtils.toString(new FileInputStream(path + "/map.html"));

            String locations = "";
            for (String location : userLocationListMap.keySet()) {
                locations += "var "+location+" = ol.proj.fromLonLat(["+userLocationListMap.get(location).get(0).longitude+", "+userLocationListMap.get(location).get(0).latitude+"]);\n";
            }

/*
            String locations = "    var london = ol.proj.fromLonLat([12.589604, 55.707043]);\n" +
                    "    var moscow = ol.proj.fromLonLat([12.5161887,55.7578284]);\n" +
                    "    var istanbul = ol.proj.fromLonLat([12.593281,55.699177]);\n" +
                    "    var rome = ol.proj.fromLonLat([12.5964017,55.676719]);\n" +
                    "    var bern = ol.proj.fromLonLat([12.5779859,55.6642418]);";
                    */
            html = html.replace("remlocationsrem", locations);

            String features = "";
            String featureNames = "";
            String companyNames = "";
            for (String location : userLocationListMap.keySet()) {
                String[] employees = new String[userLocationListMap.get(location).size()];
                for (int i = 0; i < userLocationListMap.get(location).size(); i++) {
                    employees[i] = userLocationListMap.get(location).get(i).username;
                }

                featureNames += location + "Feature, ";
                companyNames += location + ", ";

                UserLocation userLocation = userLocationListMap.get(location).get(0);
                if(location.equals("trustworks")) {
                    features += getFeature(userLocation.longitude,userLocation.latitude, location, usernames.toArray(new String[usernames.size()]));
                } else {
                    features += getFeature(userLocation.longitude, userLocation.latitude, location, employees);
                }
            }
            featureNames = featureNames.substring(0, featureNames.length()-2);
            companyNames = companyNames.substring(0, companyNames.length()-2);
            /*
            features += getFeature(12.589604,55.707043, "appension", "hans.lassen", "nikolaj.birch", "lars.albert");
            features += getFeature(12.5161887,55.7578284, "dong", "thomas.gammelvind", "gisla.faber");
            features += getFeature(12.593281,55.699177, "banedanmark", "tommy.soerensen");
            features += getFeature(12.5964017,55.676719, "kriminalforsorgen", "paula.hoiby");
            features += getFeature(12.5779859,55.6642418, "kombit", "peter.gaarde");
            */

            features += "var vectorSource = new ol.source.Vector({\n" +
                    "        features: ["+featureNames+"]\n" +
                    "    });";

            html = html.replace("remfeaturesrem", features);

            html = html.replace("remflylocationsrem", "var locations = ["+companyNames+"];");

            response.getOutputStream().print(html);
        }
    }

    private static String getFeature(double x, double y, String company, String... employees) {
        String src = "?company="+company;
        for (String employee : employees) {
            src = src + "&employees="+employee;
        }

        String anim = "var "+company+"Feature = new ol.Feature({\n" +
                "        geometry: new ol.geom.Point(ol.proj.fromLonLat(["+x+", "+y+"])),\n" +
                "    });\n" +
                "\n" +
                "    var "+company+"Style = new ol.style.Style({\n" +
                "        image: new ol.style.Icon(/** @type {olx.style.IconOptions} */ ({\n" +
                "            src: '/logo"+src+"',\n" +
                "            scale: 0.5\n" +
                "        }))\n" +
                "    });\n" +
                "\n" +
                "    "+company+"Feature.setStyle("+company+"Style);\n\n";


        return anim;
    }
}
