package dk.trustworks.userservice;

import org.jooby.Jooby;
import org.jooby.jdbc.Jdbc;

import javax.sql.DataSource;

/**
 * Created by hans on 20/01/16.
 */
public class App extends Jooby { // 1

    {
        // 2
        use(new Jdbc());
        get("/", req -> {
            DataSource db = req.require(DataSource.class);
            return "Hello World!";
        });
    }

    public static void main(final String[] args) throws Exception {
        new App().start(args); // 3. start the application.
    }

}
