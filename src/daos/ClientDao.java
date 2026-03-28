package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public Integer getClientsCountWithFilters(String nameFilter, String phoneFilter, String emailFilter) {
        final String sql = "SELECT COUNT(*) FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (nameFilter != null && !nameFilter.isEmpty()) {
            creator.addWhereAndCondition("name LIKE ?");
            creator.addParam("%" + nameFilter + "%");
        }
        if (phoneFilter != null && !phoneFilter.isEmpty()) {
            creator.addWhereAndCondition("phone LIKE ?");
            creator.addParam("%" + phoneFilter + "%");
        }
        if (emailFilter != null && !emailFilter.isEmpty()) {
            creator.addWhereAndCondition("email LIKE ?");
            creator.addParam("%" + emailFilter + "%");
        }
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {   
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Getting clients count with filters failed", e);
        }
    }

    public List<ClientDto> getClientsWithFilters(String nameFilter, String phoneFilter, String emailFilter, Integer pageNumber, Integer pageSize) {
        final String sql = """
                SELECT id, name, email, phone, is_blocked, created_at, last_log_in
                FROM clients
                """;
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (nameFilter != null && !nameFilter.isEmpty()) {
            creator.addWhereAndCondition("name LIKE ?");
            creator.addParam("%" + nameFilter + "%");
        }
        if (phoneFilter != null && !phoneFilter.isEmpty()) {
            creator.addWhereAndCondition("phone LIKE ?");
            creator.addParam("%" + phoneFilter + "%");
        }
        if (emailFilter != null && !emailFilter.isEmpty()) {
            creator.addWhereAndCondition("email LIKE ?");
            creator.addParam("%" + emailFilter + "%");
        }
        creator.addOrderBy("id ASC");
        if (pageSize != null && pageSize >= 1) {
            creator.addLimit();
            creator.addParam(pageSize);
        } else {
            throw new RuntimeException("Page size can't be null and less than 1");
        }
        if (pageNumber != null && pageNumber >= 0) {
            creator.addOffset();
            creator.addParam(pageNumber * pageSize);
        } else {
            throw new RuntimeException("Page number can't be null and less than 0");
        }
        List<ClientDto> clients = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                ClientDto client = new ClientDto(
                    result.getInt("id"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getString("name"),
                    result.getBoolean("is_blocked"));
                clients.add(client);
            }
            return clients;
        } catch (SQLException e) {
            throw new RuntimeException("Getting clients with filters failed", e);
        }
    }

    public Boolean getClientBlockStatus(Integer clientId) {
        if (clientId == null) {
            throw new RuntimeException("Client id cannot be null");
        }
        final String sql = "SELECT is_blocked FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(clientId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                return result.getBoolean("is_blocked");
            } else {
                throw new RuntimeException("Client with id " + clientId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Getting client block status failed", e);
        }
    }

    public void toggleClientBlockStatus(Integer clientId) {
        if (clientId == null) {
            throw new RuntimeException("Client id cannot be null");
        }
        final String sql = "UPDATE clients SET is_blocked = NOT is_blocked";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(clientId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Client with id " + clientId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Toggling client block status failed", e);
        }
    }

    public ClientDto getClientContactInfo(Integer clientId) {
        if (clientId == null) {
            throw new RuntimeException("Client id cannot be null");
        }
        final String sql = "SELECT name, email, phone, is_blocked FROM clients";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(clientId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                ClientDto client = new ClientDto();
                client.setId(clientId);
                client.setName(result.getString("name"));
                client.setEmail(result.getString("email"));
                client.setPhone(result.getString("phone"));
                client.setBlocked(result.getBoolean("is_blocked"));
                return client;
            } else {
                throw new RuntimeException("Client with id " + clientId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Getting client contact info failed", e);
        }
    }

    public void updateClientLastLogin(Integer clientId) {
        final String sql = "UPDATE clients SET last_log_in = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (clientId == null) {
            throw new RuntimeException("Client id cannot be null");
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(Timestamp.valueOf(LocalDateTime.now()));
        creator.addParam(clientId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Client with id " + clientId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating client last login failed", e);
        }
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
