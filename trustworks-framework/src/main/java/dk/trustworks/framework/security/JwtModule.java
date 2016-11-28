package dk.trustworks.framework.security;

import com.google.inject.Binder;
import com.typesafe.config.Config;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.jooby.*;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 25/10/2016.
 */
public class JwtModule implements Jooby.Module {

    public static final String KEY = "2b393761-fd50-4c54-8d41-61bcb17cf173";

    public static ThreadLocal<String> JWTTOKEN = new ThreadLocal<>();

    public static Boolean SECUREMODE;

    public static ThreadLocal<UserRoles> USERROLES = new ThreadLocal<>();

    public JwtModule() {
    }

    public JwtModule(boolean secureMode) {
        SECUREMODE = secureMode;
    }

    @Override
    public void configure(Env env, Config config, Binder binder) {
        Routes routes = env.routes();
        routes.before((req, rsp) -> {
            //req.set("secureMode", secureMode);
            if(SECUREMODE) {
                Optional<String> jwtToken = req.header("jwt-token").toOptional();
                if(!jwtToken.isPresent()) {
                    USERROLES.set(new UserRoles());
                    return;
                }
                JWTTOKEN.set(jwtToken.get());

                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(KEY)
                        .parseClaimsJws(jwtToken.get());
                List<String> roles = claims.getBody().get("roles", List.class);

                UserRoles userRoles = new UserRoles(roles);
                USERROLES.set(userRoles);
            }
        });

    }

    public static void authorize(Request req) {
        if(req.get("secureMode")) if(!((UserRoles)req.get("roles")).hasRole(req.route().attr("role"))) throw new Err(403);
    }

}
