package dk.trustworks.usermanager.handlers;

import dk.trustworks.framework.server.DefaultHandler;
import dk.trustworks.framework.service.DefaultLocalService;
import dk.trustworks.usermanager.service.UserService;

/**
 * Created by hans on 16/03/15.
 */
public class UserHandler extends DefaultHandler {

    private final UserService userService;

    public UserHandler() {
        super("user");
        this.userService = new UserService();
    }

    @Override
    protected DefaultLocalService getService() {
        return userService;
    }
}
