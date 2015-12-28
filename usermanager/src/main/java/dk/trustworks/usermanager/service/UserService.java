package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.persistence.GenericRepository;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.usermanager.persistence.UserRepository;

import java.sql.SQLException;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Created by hans on 17/03/15.
 */
public class UserService extends DefaultLocalService {

    private UserRepository userRepository;

    public UserService() {
        userRepository = new UserRepository();
    }


    public List<Map<String, Object>> findByActiveTrue(Map<String, Deque<String>> queryParameters) {
        return userRepository.findByActiveTrue();
    }

    public List<Map<String, Object>> findByActiveTrueOrderByFirstnameAsc(Map<String, Deque<String>> queryParameters) {
        return userRepository.findByActiveTrueOrderByFirstnameAsc();
    }

    public Map<String, Object> findByEmail(Map<String, Deque<String>> queryParameters) {
        String email = queryParameters.get("email").getFirst();
        return userRepository.findByEmail(email);
    }

    public Map<String, Object> findByUsername(Map<String, Deque<String>> queryParameters) {
        String username = queryParameters.get("username").getFirst();
        return userRepository.findByUsername(username);
    }

    public Map<String, Object> findByUsernameAndPasswordAndActiveTrue(Map<String, Deque<String>> queryParameters) {
        System.out.println("UserService.findByUsernameAndPasswordAndActiveTrue");
        String username = queryParameters.get("username").getFirst();
        String password = queryParameters.get("password").getFirst();
        System.out.println("username = " + username);
        System.out.println("password = " + password);
        Map<String, Object> credentials = userRepository.findByUsernameAndPasswordAndActiveTrue(username, password);
        System.out.println("credentials = " + credentials);
        return credentials;
    }

    @Override
    public void create(JsonNode jsonNode) throws SQLException {
        userRepository.create(jsonNode);
    }

    @Override
    public void update(JsonNode jsonNode, String uuid) throws SQLException {
        userRepository.update(jsonNode, uuid);
    }

    @Override
    public GenericRepository getGenericRepository() {
        return userRepository;
    }

    @Override
    public String getResourcePath() {
        return "users";
    }
}
