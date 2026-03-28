package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.AdminDto;

public class AdminDao {


    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final AdminDao instance = new AdminDao();
    private AdminDao(){}
    public static AdminDao getAdminDao(){
        return instance;
    }

    public AdminDto getAdminByLoginAndPassword(String login, String password) {
        final String sql = "SELECT id FROM administrators";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (login == null || password == null){
            throw new RuntimeException("Login and password cannot be null");
        }
        creator.addWhereAndCondition("login = ?");
        creator.addParam(login);
        creator.addWhereAndCondition("password = ?");
        creator.addParam(password);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                AdminDto admin = new AdminDto(
                    result.getInt("id"),
                    login,
                    password);
                return admin;
            } else {
                return null;
            }
            
        } catch (SQLException e) {
            throw new RuntimeException("Getting admin by login and password failed", e);
        }
    }

    public void updateAdminLastLogin(Integer adminId) {
        final String sql = "UPDATE administrators SET last_log_in = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (adminId == null) {
            throw new RuntimeException("Admin id cannot be null");
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(Timestamp.valueOf(LocalDateTime.now()));
        creator.addParam(adminId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Admin with id " + adminId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating admin last login failed", e);
        }
    }

    

}
