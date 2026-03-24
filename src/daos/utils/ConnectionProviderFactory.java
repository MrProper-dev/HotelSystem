package daos.utils;

public class ConnectionProviderFactory {

    public static ConnectionProvider getConnectionProvider(){
        return TomcatConnectionProvider.getTomcatConnectionProvider();
    }

}
