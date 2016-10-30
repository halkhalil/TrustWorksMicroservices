package dk.trustworks.usermanager.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by hans on 25/10/2016.
 */
public class UserRoles {

    private final Set<String> roles;

    public UserRoles() {
        roles = new TreeSet<>();
    }

    public UserRoles(List<String> roles) {
        this();
        this.roles.addAll(roles);
    }

    public void addRoles(List<String> roles) {
        this.roles.addAll(roles);
    }

    public boolean hasRole(String role) {
        System.out.println("role = " + role);
        System.out.println("roles = " + roles.size());
        return roles.contains(role);
    }

    public Set<String> getRoles() {
        return roles;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UserRoles{");
        sb.append("roles=");
        for (String role : roles) {
            sb.append(role+", ");
        }

        sb.append('}');
        return sb.toString();
    }
}
