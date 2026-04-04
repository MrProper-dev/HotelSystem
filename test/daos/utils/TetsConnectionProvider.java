package daos.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TetsConnectionProvider implements ConnectionProvider{

    private static ConnectionProvider instance;
    
    private TetsConnectionProvider(){}

    public static ConnectionProvider getTetsConnectionProvider(){
        if(instance == null){
            synchronized(TetsConnectionProvider.class){
                if(instance == null){
                    instance = new TetsConnectionProvider();
                }
            }
        } 
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelsystemtest", "postgres", "root");
        return connection;
    }

}
