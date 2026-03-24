package daos.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class TomcatConnectionProvider implements ConnectionProvider{

    private DataSource dataSource;

    private static ConnectionProvider instance;
    
    private TomcatConnectionProvider(){
        try{
            Context context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/DataSource");
        } catch (NamingException exception){
            throw new RuntimeException("JNDI lookup failed: java:comp/env/jdbc/DataSource", exception);
        }
    }

    public static ConnectionProvider getTomcatConnectionProvider(){
        if(instance == null){
            synchronized(TomcatConnectionProvider.class){
                if(instance == null){
                    instance = new TomcatConnectionProvider();
                }
            }
        } 
        return instance;
    }

    @Override
    public Connection getConnection(){
        try{
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException("Getting connection failed", e);
        }
    }

}
