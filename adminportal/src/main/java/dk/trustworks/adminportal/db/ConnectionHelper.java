package dk.trustworks.adminportal.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by hans on 15/12/2016.
 */
public class ConnectionHelper {

    private static ConnectionHelper instance;

    public final HikariDataSource dataSource;

    private ConnectionHelper() {
        //jdbc:mysql://trustworksdb.cm3iylt6ulsl.eu-west-1.rds.amazonaws.com:3306/
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/usermanager");
        config.setUsername("financeuser");
        config.setPassword("Nostromo2014");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);
    }

    public final static ConnectionHelper getInstance() {
        if(instance == null) instance = new ConnectionHelper();
        return instance;
    }
}
