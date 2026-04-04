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
import dtos.SuperDto;

public class SuperDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final SuperDao instance = new SuperDao();
    protected SuperDao(){}
    public static SuperDao getSuperDao(){
        return instance;
    }

    public SuperDto getSuperUserByLoginAndPassword(String login, String password) {
        if (login == null || password == null) {
            throw new RuntimeException("Login and password cannot be null");
        }
        final String sql = "SELECT id FROM superusers";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("login = ?");
        creator.addParam(login);
        creator.addWhereAndCondition("password = ?");
        creator.addParam(password);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                SuperDto superUser = new SuperDto(
                    result.getInt("id"),
                    login,
                    password
                );
                return superUser;
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Getting superuser by login and password failed", e);
        }
    }

    public void updateSuperUserLastLogin(Integer id) {
        if (id == null) {
            throw new RuntimeException("Superuser id cannot be null");
        }
        final String sql = "UPDATE superusers SET last_log_in = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addParam(Timestamp.valueOf(LocalDateTime.now()));
        creator.addWhereAndCondition("id = ?");
        creator.addParam(id);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Superuser with id " + id + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating superuser last login failed", e);
        }
    }

}
