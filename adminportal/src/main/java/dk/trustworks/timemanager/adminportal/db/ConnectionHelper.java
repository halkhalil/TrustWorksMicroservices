package dk.trustworks.timemanager.adminportal.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.util.Properties;

/**
 * Created by hans on 15/12/2016.
 */
public class ConnectionHelper {

    private static ConnectionHelper instance;

    public final HikariDataSource dataSource;

    private ConnectionHelper() {
        //jdbc:mysql://trustworksdb.cm3iylt6ulsl.eu-west-1.rds.amazonaws.com:3306/

        Properties props = System.getProperties();
        //props.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        //props.setProperty("dataSource.user", "test");
        //props.setProperty("dataSource.password", "test");
        //props.setProperty("dataSource.databaseName", "mydb");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(props.getProperty("db.url"));
        config.setUsername(props.getProperty("db.user"));
        config.setPassword(props.getProperty("db.password"));
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("maximumPoolSize", 2);

        dataSource = new HikariDataSource(config);
    }

    public final static ConnectionHelper getInstance() {
        if(instance == null) instance = new ConnectionHelper();
        return instance;
    }
}
