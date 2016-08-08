package dk.trustworks.framework.persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sql2o.Sql2o;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Provides utility methods for the benchmark tests.
 */
public final class Helper {

    private static final Logger log = LogManager.getLogger(Helper.class);
    private static Helper instance;

    private final Sql2o sql2o;

    private Helper() {
        DataSource mysql = Helper.newDataSource(
                System.getProperty("db.url"),
                System.getProperty("db.user"),
                System.getProperty("db.password"));
        sql2o = new Sql2o(mysql);
    }

    public static Helper createHelper() {
        return instance == null ? instance = new Helper() : instance;
    }

    public Sql2o getDatabase() {
        return sql2o;
    }

    /**
     * Constructs a new SQL data source with the given parameters.  Connections
     * to this data source are pooled.
     *
     * @param uri      the URI for database connections
     * @param user     the username for the database
     * @param password the password for the database
     * @return a new SQL data source
     */
    private static DataSource newDataSource(String uri,
                                            String user,
                                            String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(uri);
        config.setUsername(user);
        config.setPassword(password);

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("maximumPoolSize", "10");

        return new HikariDataSource(config);
    }
}
