package actor;

import akka.actor.UntypedActor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import util.Configuration;

import javax.sql.DataSource;
import java.io.File;
import java.net.URISyntaxException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseReaderActor extends UntypedActor {

    private final String databaseName;

    private static final String DRIVER = "org.sqlite.JDBC";

    private JdbcTemplate template;


    public DatabaseReaderActor(String databaseName) {
        this.databaseName = databaseName;
    }

    @Override
    public void onReceive(Object message) throws Exception {
        //TODO: IMPLEMENTS JDBC QUERIES
    }

    @Override
    public void preStart() {
        String path = Configuration.additionalFiles;
        try {
            File directory = new File(getClass().getClassLoader().getResource(path).toURI());
            Class.forName(DRIVER);
            String url = String.format("jdbc:sqlite://%s/%s", directory.getAbsolutePath(), databaseName);
            Driver driver = DriverManager.getDriver(url);
            DataSource dataSource = new SimpleDriverDataSource(driver, url);
            template = new JdbcTemplate(dataSource);

            if (!directory.isDirectory()) {
                throw new IllegalStateException(String.format("%s must exists", path));
            }

        } catch (URISyntaxException e) {
            throw new IllegalStateException(String.format("%s must exists", path));
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

    }


}
