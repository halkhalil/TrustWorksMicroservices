package dk.trustworks.usermanager.security;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.AbstractUsernamePasswordAuthenticator;
import org.pac4j.core.exception.BadCredentialsException;
import org.pac4j.core.exception.HttpAction;

/**
 * Created by hans on 14/07/16.
 */
public class Authenticator extends AbstractUsernamePasswordAuthenticator {
    @Override
    public void validate(UsernamePasswordCredentials usernamePasswordCredentials, WebContext webContext) throws HttpAction {
        System.out.println("usernamePasswordCredentials.getUsername() = " + usernamePasswordCredentials.getUsername());
        System.out.println("usernamePasswordCredentials.getPassword() = " + usernamePasswordCredentials.getPassword());
        System.out.println("usernamePasswordCredentials = " + usernamePasswordCredentials);
        throw new BadCredentialsException("Wrong username or password");
    }
}
