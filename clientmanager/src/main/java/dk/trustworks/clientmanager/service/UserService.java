package dk.trustworks.clientmanager.service;

import dk.trustworks.clientmanager.model.User;
import dk.trustworks.clientmanager.service.commands.GetUsersCommand;
import dk.trustworks.framework.security.JwtModule;

import java.util.List;

/**
 * Created by hans on 24/04/15.
 */
public class UserService {

    public UserService() {
    }

    public List<User> findAll() {
        System.out.println("JwtModule.JWTTOKEN.get() = " + JwtModule.JWTTOKEN.get());
        return new GetUsersCommand(JwtModule.JWTTOKEN.get()).execute();
    }
}
