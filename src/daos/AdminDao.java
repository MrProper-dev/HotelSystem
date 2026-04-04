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
import daos.utils.LoginExistException;
import daos.utils.PreparedStatementCreator;
import dtos.AdminDto;

public class AdminDao {


    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final AdminDao instance = new AdminDao();
    protected AdminDao(){}
    public static AdminDao getAdminDao(){
        return instance;
    }

    public void deleteAdministrator(Integer adminId){
        if (adminId == null) {
            throw new RuntimeException("Admin id cannot be null");
        }
        final String root = "DELETE FROM administrators";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(adminId);
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            if(statement.executeUpdate() != 1){
                throw new RuntimeException("Administrator not found");
            }
        }catch (SQLException e) {
            throw new RuntimeException("Deleting administrator failed", e);
        }
    }

    public void updateAdministrator(AdminDto admin) {
        if (admin == null || admin.getId() == null) {
            throw new RuntimeException("Admin and admin id cannot be null");
        }
        final StringBuilder sql = new StringBuilder("UPDATE administrators SET login = ?, full_name = ?, phone = ?");
        if(admin.getPassword() != null && !admin.getPassword().isEmpty()){
            sql.append( ", password = ?");
        }
        PreparedStatementCreator creator = new PreparedStatementCreator(sql.toString());
        creator.addParam(admin.getLogin());
        creator.addParam(admin.getFullName());
        creator.addParam(admin.getPhone());
        if(admin.getPassword() != null && !admin.getPassword().isEmpty()){
            creator.addParam(admin.getPassword());
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(admin.getId());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Administrator with id " + admin.getId() + " not found");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new LoginExistException("Administrator with login '" + admin.getLogin() + "' already exists", e);
            }
            throw new RuntimeException("Updating administrator failed", e);
        }
    }

    public void addAdministrator(AdminDto admin) {
        if (admin == null) {
            throw new RuntimeException("Admin cannot be null");
        }
        if (admin.getLogin() == null || admin.getLogin().isEmpty()) {
            throw new RuntimeException("Admin login cannot be null or empty");
        }
        if (admin.getPassword() == null || admin.getPassword().isEmpty()) {
            throw new RuntimeException("Admin password cannot be null or empty");
        }
        if (admin.getFullName() == null || admin.getFullName().isEmpty()) {
            throw new RuntimeException("Admin full name cannot be null or empty");
        }
        if (admin.getPhone() == null || admin.getPhone().isEmpty()) {
            throw new RuntimeException("Admin phone cannot be null or empty");
        }
        final String sql = "INSERT INTO administrators (login, password, full_name, phone, creation_at, last_log_in) VALUES";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addValue("(?, ?, ?, ?, DEFAULT, NULL)");
        creator.addParam(admin.getLogin());
        creator.addParam(admin.getPassword());
        creator.addParam(admin.getFullName());
        creator.addParam(admin.getPhone());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                throw new LoginExistException("Administrator with login '" + admin.getLogin() + "' already exists", e);
            }
            throw new RuntimeException("Adding administrator failed", e);
        }
    }

    public Integer getAdminsCountWithFilters(String loginFilter, String fullNameFilter, String phoneFilter) {
        final String sql = "SELECT COUNT(*) FROM administrators";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (loginFilter != null && !loginFilter.isEmpty()) {
            creator.addWhereAndCondition("login LIKE ?");
            creator.addParam("%" + loginFilter + "%");
        }
        if (fullNameFilter != null && !fullNameFilter.isEmpty()) {
            creator.addWhereAndCondition("full_name LIKE ?");
            creator.addParam("%" + fullNameFilter + "%");
        }
        if (phoneFilter != null && !phoneFilter.isEmpty()) {
            creator.addWhereAndCondition("phone LIKE ?");
            creator.addParam("%" + phoneFilter + "%");
        }
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {   
            if (result.next()) {
                return result.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Getting admins count with filters failed", e);
        }
    }

    public List<AdminDto> getAdminsWithFilters(String loginFilter, String fullNameFilter, String phoneFilter, Integer pageNumber, Integer pageSize) {
        final String sql = "SELECT id, login, full_name, phone FROM administrators";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (loginFilter != null && !loginFilter.isEmpty()) {
            creator.addWhereAndCondition("login LIKE ?");
            creator.addParam("%" + loginFilter + "%");
        }
        if (fullNameFilter != null && !fullNameFilter.isEmpty()) {
            creator.addWhereAndCondition("full_name LIKE ?");
            creator.addParam("%" + fullNameFilter + "%");
        }
        if (phoneFilter != null && !phoneFilter.isEmpty()) {
            creator.addWhereAndCondition("phone LIKE ?");
            creator.addParam("%" + phoneFilter + "%");
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
        List<AdminDto> admins = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                AdminDto admin = new AdminDto(
                    result.getInt("id"),
                    result.getString("login"),
                    result.getString("full_name"),
                    result.getString("phone"));
                admins.add(admin);
            }
            return admins;
        } catch (SQLException e) {
            throw new RuntimeException("Getting admins with filters failed", e);
        }
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
