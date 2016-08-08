package dk.trustworks.employeemanager.security;

import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.http.credentials.UsernamePasswordCredentials;
import org.pac4j.http.credentials.authenticator.UsernamePasswordAuthenticator;

/**
 * Created by hans on 14/07/16.
 */
public class Authenticator implements UsernamePasswordAuthenticator {
    @Override
    public void validate(UsernamePasswordCredentials usernamePasswordCredentials) {
        System.out.println("usernamePasswordCredentials.getUsername() = " + usernamePasswordCredentials.getUsername());
        System.out.println("usernamePasswordCredentials.getPassword() = " + usernamePasswordCredentials.getPassword());
        System.out.println("usernamePasswordCredentials = " + usernamePasswordCredentials);
        throw new BadCredentialsException("Wrong username or password");
    }
}
