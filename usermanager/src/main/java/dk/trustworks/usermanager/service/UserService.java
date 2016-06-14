package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.usermanager.dto.User;
import dk.trustworks.usermanager.persistence.UserRepository;
import org.joda.time.DateTime;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class UserService {

    private UserRepository userRepository;

    public UserService(DataSource ds) {
        userRepository = new UserRepository(ds);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUUID(String uuid) {
        return userRepository.findByUUID(uuid);
    }

    public List<User> findByActiveTrue() {
        return userRepository.findByActiveTrue();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findByActiveTrueOrderByFirstnameAsc(Map<String, Deque<String>> queryParameters) {
        return userRepository.findByActiveTrueOrderByFirstnameAsc();
    }

    public User findByUsernameAndPasswordAndActiveTrue(String username, String password) {
        User credentials = userRepository.findByUsernameAndPasswordAndActiveTrue(username, password);
        return credentials;
    }

    public Map<String, Object> capacitypermonth(int year) {
        int capacityPerMonth[] = new int[12];

        for (int i = 0; i < 12; i++) {
            DateTime dateTime = new DateTime(year, i+1, 1, 0, 0);
            int capacityByMonth = userRepository.calculateCapacityByMonth(dateTime);
            capacityPerMonth[i] = capacityByMonth;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("capacitypermonth", capacityPerMonth);
        return result;
    }

    public Map<String, Object> capacitypermonthbyuser(int year, String userUUID) {
        int capacityPerMonth[] = new int[12];

        for (int i = 0; i < 12; i++) {
            DateTime dateTime = new DateTime(year, i+1, 15, 0, 0);
            int capacityByMonth = userRepository.calculateCapacityByMonthByUser(dateTime, userUUID);
            capacityPerMonth[i] = capacityByMonth;
        }

        Map<String, Object> result = new HashMap<>();
        result.put("capacitypermonthbyuser", capacityPerMonth);
        return result;
    }

    public Map<String, int[]> useravailabilitypermonthbyyear(int year) {
        Map<String, int[]> result = new HashMap<>();
        for (int i = 0; i < 12; i++) {
            DateTime dateTime = new DateTime(year, i+1, 1, 0, 0);
            List<String> activeUsers = userRepository.getAvailabilityByMonth(dateTime);
            for (String activeUser : activeUsers) {
                result.putIfAbsent(activeUser, new int[12]);
                result.get(activeUser)[i] = 1;
            }
        }
        return result;
        /*
        try {
            exchange.getResponseSender().send(new ObjectMapper().writeValueAsString(result));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        */
    }

    public void create(User user) throws SQLException {
        userRepository.create(user);
    }

    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        userRepository.update(jsonNode, uuid);
    }
}
