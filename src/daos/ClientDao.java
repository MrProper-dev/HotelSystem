package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.ClientDto;

public class ClientDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final ClientDao instance = new ClientDao();
    private ClientDao(){}
    public static ClientDao getClientDao(){
        return instance;
    }

    public ClientDto getByEmail(String email){
        final String root = "SELECT id, password FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(email == null || email.isEmpty()){
            throw new RuntimeException("Email can`t be null");
        }
        creator.addWhereAndCondition("email = ?");
        creator.addParam(email);
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return new ClientDto(result.getInt("id"), email, result.getString("password"));
            }else{
                return null;
            }
        }catch (SQLException e){
            throw new RuntimeException("Checking client email failed", e);
        }
    }
    
    public Boolean existByEmail(String email){
        final String root = "SELECT true FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(email == null || email.isEmpty()){
            throw new RuntimeException("Email can`t be null");
        }
        creator.addWhereAndCondition("email = ?");
        creator.addParam(email);
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return result.getBoolean(1);
            }else{
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Checking client email failed", e);
        }
    }

    public ClientDto createClient(ClientDto client){
        final String root = "INSERT INTO clients VALUES";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        creator.addValue("(DEFAULT, ?, ?, ?, ?, DEFAULT, DEFAULT, DEFAULT)");
        if (client.getEmail() == null || client.getPassword() == null || client.getPhone() == null || client.getName() == null) {
            throw new RuntimeException("One of the parameters is null");
        }
        creator.addParam(client.getEmail());
        creator.addParam(client.getPassword());
        creator.addParam(client.getPhone());
        creator.addParam(client.getName());
        creator.addReturning("id");
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            result.next();
            client.setId(result.getInt(1));
            return client;
        }catch (SQLException e){
            throw new RuntimeException("Inserting client failed", e);
        }
    }

}
