package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.service.commands.GetUsersCommand;
import dk.trustworks.framework.model.User;
import dk.trustworks.framework.security.Authenticator;
import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.framework.security.RoleRight;
import net.sf.cglib.proxy.Enhancer;

import javax.sql.DataSource;
import java.util.List;

/**
 * Created by hans on 24/04/15.
 */
public class UserService {

    public UserService() {
    }

    public static UserService getInstance() {
        UserService service = new UserService();
        return (UserService) Enhancer.create(service.getClass(), new Authenticator(service));
    }

    @RoleRight("tm.user")
    public List<User> findAll() {
        System.out.println("JwtModule.JWTTOKEN.get() = " + JwtModule.JWTTOKEN.get());
        return new GetUsersCommand(JwtModule.JWTTOKEN.get()).execute();
    }
}
