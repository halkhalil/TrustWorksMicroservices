package dk.trustworks.usermanager.service;

import com.fasterxml.jackson.databind.JsonNode;
import dk.trustworks.framework.model.Availability;
import dk.trustworks.framework.model.Capacity;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.RoleRight;
import dk.trustworks.usermanager.persistence.RoleRepository;
import dk.trustworks.usermanager.persistence.UserRepository;
import net.sf.cglib.proxy.Enhancer;
import org.joda.time.LocalDate;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hans on 17/03/15.
 */
public class UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;

    public UserService() {
    }

    public UserService(DataSource ds) {
        userRepository = new UserRepository(ds);
        roleRepository = new RoleRepository(ds);
    }

    public static UserService getInstance(DataSource ds) {
        UserService service = new UserService(ds);
        return (UserService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @RoleRight("tm.user")
    public List<User> findAllV2() {
        return userRepository.findAllV2();
    }

    @RoleRight("tm.user")
    public User findByUUID(String uuid) {
        return userRepository.findByUUID(uuid);
    }

    @RoleRight("tm.user")
    public List<User> findByActiveTrue() {
        return userRepository.findByActiveTrue();
    }

    @RoleRight("tm.user")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @RoleRight("tm.user")
    public User findBySlackUsername(String slackusername) {
        return userRepository.findBySlackUsername(slackusername);
    }

    @RoleRight("tm.user")
    public User findByUsernameAndPasswordAndActiveTrue(String username, String password) {
        User credentials = userRepository.findByUsernameAndPasswordAndActiveTrue(username, password);
        System.out.println("credentials = " + credentials);
        return credentials;
    }

    @RoleRight("tm.admin")
    public List<String> getUserRoles(String username, String password) {
        System.out.println("UserService.getUserRoles");
        System.out.println("username = [" + username + "], password = [" + password + "]");
        User user = findByUsernameAndPasswordAndActiveTrue(username, password);
        List<String> roles = roleRepository.findByUserUUID(user.getUuid());
        return roles;
    }

    @RoleRight("tm.user")
    public List<Capacity> capacitypermonth(LocalDate periodStart, LocalDate periodEnd) {
        List<Capacity> capacities = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            Capacity capacity = new Capacity();
            capacity.activeDate = currentDate;
            int capacityByMonth = userRepository.calculateCapacityByMonth(currentDate);
            capacity.capacity = capacityByMonth;
            capacities.add(capacity);
            currentDate = currentDate.plusMonths(1);
        }

        return capacities;
    }

    @RoleRight("tm.user")
    public List<Capacity> capacitypermonthbyuser(String userUUID, LocalDate periodStart, LocalDate periodEnd) {
        List<Capacity> capacities = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            Capacity capacity = new Capacity();
            capacity.activeDate = currentDate;
            int capacityByMonth = userRepository.calculateCapacityByMonthByUser(currentDate, userUUID);
            capacity.capacity = capacityByMonth;
            capacities.add(capacity);
            currentDate = currentDate.plusMonths(1);
        }
        return capacities;
    }

    @RoleRight("tm.user")
    public List<Availability> useravailabilitypermonthbyyear(LocalDate periodStart, LocalDate periodEnd) {
        List<Availability> availabilities = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            List<String> activeUsers = userRepository.getAvailabilityByMonth(currentDate);
            for (String activeUser : activeUsers) {
                Availability availability = new Availability(activeUser, currentDate);
                availabilities.add(availability);
            }
            currentDate = currentDate.plusMonths(1);
        }

        return availabilities;
    }

    @RoleRight("tm.user")
    public List<Availability> useravailabilitypermonthbyyearbyuser(String userUUID, LocalDate periodStart, LocalDate periodEnd) {
        List<Availability> availabilities = new ArrayList<>();

        LocalDate currentDate = periodStart;
        while(currentDate.isBefore(periodEnd)) {
            List<String> activeUsers = userRepository.getAvailabilityByMonthAndUser(userUUID, currentDate);
            for (String activeUser : activeUsers) {
                Availability availability = new Availability(activeUser, currentDate);
                availabilities.add(availability);
            }
            currentDate = currentDate.plusMonths(1);
        }
        return availabilities;
    }

    @RoleRight("tm.admin")
    public void create(User user) throws SQLException {
        userRepository.create(user);
    }

    @RoleRight("tm.admin")
    public void updatePassword(String password, String uuid) throws SQLException {
        userRepository.updatePassword(password, uuid);
    }

    @RoleRight("tm.admin")
    public void update(User user, String uuid) throws SQLException {
        userRepository.update(user, uuid);
    }
}
