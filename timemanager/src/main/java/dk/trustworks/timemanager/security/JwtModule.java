package dk.trustworks.timemanager.security;

import com.google.inject.Binder;
import com.typesafe.config.Config;
import dk.trustworks.timemanager.TimeApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.jooby.Env;
import org.jooby.Jooby;
import org.jooby.Routes;

import java.util.List;
import java.util.Optional;

/**
 * Created by hans on 25/10/2016.
 */
public class JwtModule implements Jooby.Module {

    public JwtModule() {
    }

    @Override
    public void configure(Env env, Config config, Binder binder) {
        Routes routes = env.routes();
        routes.before((req, rsp) -> {
            Optional<String> jwtToken = req.header("jwt-token").toOptional();
            System.out.println("jwtToken = " + jwtToken);

            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(TimeApplication.KEY)
                    .parseClaimsJws(jwtToken.get());
            List<String> roles = claims.getBody().get("roles", List.class);
            UserRoles userRoles = new UserRoles(roles);
            req.set("roles", userRoles);
        });

    }

}
