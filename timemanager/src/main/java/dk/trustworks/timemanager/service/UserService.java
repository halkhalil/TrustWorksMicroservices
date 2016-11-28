package dk.trustworks.timemanager.service;


import dk.trustworks.framework.security.JwtModule;
import dk.trustworks.timemanager.client.commands.GetUsersCommand;
import dk.trustworks.timemanager.client.dto.User;

import java.util.List;

/**
 * Created by hans on 24/04/15.
 */
public class UserService {

    public UserService() {
    }

    public List<User> findAll() {
        return new GetUsersCommand(JwtModule.JWTTOKEN.get()).execute();
    }
}
