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

    public void updateClientWithoutPassword(ClientDto client) {
        if (client == null || client.getId() == null) {
            throw new RuntimeException("Client and client id cannot be null");
        }
        final String sql = "UPDATE clients SET email = ?, phone = ?, name = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(client.getEmail());
        creator.addParam(client.getPhone());
        creator.addParam(client.getName());
        creator.addParam(client.getId());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Client with id " + client.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating client without password failed", e);
        }
    }

    public void updateClient(ClientDto client) {
        if (client == null || client.getId() == null) {
            throw new RuntimeException("Client and client id cannot be null");
        }
        final String sql = "UPDATE clients SET email = ?, password = ?, phone = ?, name = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(client.getEmail());
        creator.addParam(client.getPassword());
        creator.addParam(client.getPhone());
        creator.addParam(client.getName());
        creator.addParam(client.getId());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Client with id " + client.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating client failed", e);
        }
    }

    public ClientDto getClientById(Integer clientId) {
        final String sql = "SELECT email, phone, name FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (clientId == null) {
            throw new RuntimeException("Client id cannot be null");
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(clientId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                ClientDto client = new ClientDto(
                    clientId,
                    result.getString("email"), 
                    result.getString("phone"), 
                    result.getString("name"));
                return client;
            } else {
                throw new RuntimeException("Client with id " + clientId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Getting client by id failed", e);
        }
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
