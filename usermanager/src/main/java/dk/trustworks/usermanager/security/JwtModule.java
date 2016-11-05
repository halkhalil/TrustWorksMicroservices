package dk.trustworks.usermanager.security;

import com.google.common.base.Throwables;
import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.name.Names;
import com.typesafe.config.Config;
import dk.trustworks.usermanager.UserApplication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import javaslang.control.Try;
import org.jooby.Env;
import org.jooby.Err;
import org.jooby.Jooby;
import org.jooby.Routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Created by hans on 25/10/2016.
 */
public class JwtModule implements Jooby.Module {

    private boolean secureMode;

    public JwtModule() {
    }

    public JwtModule(boolean secureMode) {
        this.secureMode = secureMode;
    }

    @Override
    public void configure(Env env, Config config, Binder binder) {
        Routes routes = env.routes();
        routes.before((req, rsp) -> {
            req.set("secureMode", secureMode);
            if(secureMode) {
                Optional<String> jwtToken = req.header("jwt-token").toOptional();
                if(!jwtToken.isPresent()) throw new Err(401);
                System.out.println("jwtToken = " + jwtToken);

                Jws<Claims> claims = Jwts.parser()
                        .setSigningKey(UserApplication.KEY)
                        .parseClaimsJws(jwtToken.get());
                List<String> roles = claims.getBody().get("roles", List.class);
                for (String role : roles) {
                    System.out.println("role = " + role);
                }

                UserRoles userRoles = new UserRoles(roles);
                req.set("roles", userRoles);
            }
        });

    }

}
