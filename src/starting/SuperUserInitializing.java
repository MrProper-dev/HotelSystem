package starting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class SuperUserInitializing implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String login = AppConfig.appProperties.getProperty("superuser.login");
        String password = AppConfig.appProperties.getProperty("superuser.password");
        try(Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelsystem", "postgres", "root")){
            final String querySelect = "SELECT * FROM superusers";
            Statement statementSelect = connection.createStatement();
            ResultSet result = statementSelect.executeQuery(querySelect);
            if(result.next()) return;
            final String queryInsert = "INSERT INTO superusers (login, password) VALUES ('"+ login +"','"+ password +"')";
            Statement statementInsert = connection.createStatement();
            statementInsert.executeUpdate(queryInsert);
        }catch(SQLException e){
            throw new RuntimeException("Set superuser params faild", e);
        }
    }

}
