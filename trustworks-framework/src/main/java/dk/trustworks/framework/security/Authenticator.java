package dk.trustworks.framework.security;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.jooby.Err;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.json.zip.JSONzip.end;

/**
 * Created by hans on 28/11/2016.
 */
public class Authenticator implements MethodInterceptor {

    private Object o;

    public Authenticator(Object o) {
        this.o = o;
    }

    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        Object result;
        try {
            System.out.println("before method ");
            long start = System.nanoTime();

            Annotation[] annotations = method.getDeclaredAnnotations();
            for(Annotation annotation : annotations){
                if(annotation instanceof RoleRight){
                    RoleRight roleRight = (RoleRight) annotation;
                    System.out.println("value: " + roleRight.value());
                    if(JwtModule.SECUREMODE) if(!(JwtModule.USERROLES.get()).hasRole(roleRight.value())) throw new Err(403);
                }
                if(annotation instanceof RoleRights){
                    for (RoleRight roleRight : ((RoleRights) annotation).value()) {
                        System.out.println("value: " + roleRight.value());
                        if(JwtModule.SECUREMODE) if(!(JwtModule.USERROLES.get()).hasRole(roleRight.value())) throw new Err(403);
                    }
                }
            }

            result = proxy.invoke(o, args);
            System.out.println(String.format("%s took %d ns", method.getName(), (end-start)) );
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        } catch (Exception e) {
            throw new RuntimeException("unexpected invocation exception: " + e.getMessage());
        } finally {
            System.out.println("after method " + method.getName());
        }
        return result;
    }
}
