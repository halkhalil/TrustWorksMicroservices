package dk.trustworks.framework.security;

import java.lang.annotation.*;

/**
 * Created by hans on 28/11/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(RoleRights.class)
public @interface RoleRight {
    String value() default "";
}
