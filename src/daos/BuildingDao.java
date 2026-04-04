package daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.BuildingDto;

public class BuildingDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final BuildingDao instance = new BuildingDao();
    protected BuildingDao(){}
    public static BuildingDao getBuildingDao(){
        return instance;
    }

    public void addBuilding(BuildingDto building) {
        if (building == null) {
            throw new RuntimeException("Building cannot be null");
        }
        if (building.getName() == null || building.getName().trim().isEmpty()) {
            throw new RuntimeException("Building name cannot be null or empty");
        }
        if (building.getFloors() == null) {
            throw new RuntimeException("Building floors cannot be null");
        }
        if (building.getFloors() <= 0) {
            throw new RuntimeException("Building floors must be greater than 0");
        }
        final String sql = "INSERT INTO buildings (name, address, floors) VALUES";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addValue("(?, ?, ?)");
        creator.addParam(building.getName().trim());
        creator.addParam(building.getAddress() != null ? building.getAddress().trim() : null);
        creator.addParam(building.getFloors());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Adding building failed", e);
        }
    }

    public void deleteBuilding(Integer buildingId) {
        if (buildingId == null) {
            throw new RuntimeException("Building id cannot be null");
        }
        final String sql = "DELETE FROM buildings";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("id = ?");
        creator.addParam(buildingId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int deletedRows = statement.executeUpdate();
            if (deletedRows == 0) {
                throw new RuntimeException("Building with id " + buildingId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Deleting building failed", e);
        }
    }

    public void updateBuilding(BuildingDto building) {
        if (building == null || building.getId() == null) {
            throw new RuntimeException("Building and building id cannot be null");
        }
        final String sql = "UPDATE buildings SET name = ?, address = ?, floors = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addParam(building.getName());
        creator.addParam(building.getAddress());
        creator.addParam(building.getFloors());
        creator.addWhereAndCondition("id = ?");
        creator.addParam(building.getId());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Building with id " + building.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating building failed", e);
        }
    }

    public Integer getBuildingsCount() {
        final String sql = "SELECT COUNT(*) FROM buildings";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            result.next();
            return result.getInt(1);
        } catch (SQLException e) {
            throw new RuntimeException("Getting all buildings with pagination failed", e);
        }
    }

    public List<BuildingDto> getAllBuildingsWithPagination(Integer pageNumber, Integer pageSize) {
        final String sql = "SELECT id, name, address, floors FROM buildings";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
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
        List<BuildingDto> buildings = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                BuildingDto building = new BuildingDto(
                    result.getInt("id"),
                    result.getString("name"),
                    result.getString("address"),
                    result.getInt("floors")
                );
                buildings.add(building);
            }
            return buildings;
        } catch (SQLException e) {
            throw new RuntimeException("Getting all buildings with pagination failed", e);
        }
    }

    public List<BuildingDto> getAllBuildingsWithoutAddress() {
        final String sql = "SELECT id, name, floors FROM buildings";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        List<BuildingDto> buildings = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                BuildingDto building = new BuildingDto(
                    result.getInt("id"),
                    result.getString("name"),
                    null,  // address - не заполняем
                    result.getInt("floors")
                );
                buildings.add(building);
            }
            return buildings;
        } catch (SQLException e) {
            throw new RuntimeException("Getting all buildings failed", e);
        }
    }

    public Integer getMaxFloor(){
        final String query = """
                SELECT MAX(floors) FROM buildings
                """;
        try(Connection connection = connectionProvider.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.next(); 
            return result.getInt(1);
        }catch (SQLException e){
            throw new RuntimeException("Getting max floors failed", e);
        }
    }

    public List<BuildingDto> getAllWithoutAddressAndFloors(){
        final String query = """
                SELECT id, name FROM buildings
                """;
        List<BuildingDto> buildings = new ArrayList<>();
        try(Connection connection = connectionProvider.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            while (result.next()) {
                BuildingDto building = map(
                    result.getInt("id"), 
                    result.getString("name"), 
                    null,
                    null);
                buildings.add(building);
            }
            return buildings;
        }catch(SQLException e){
            throw new RuntimeException("Getting buildings without address and floors failed", e);
        }
    }

    private BuildingDto map(Integer id, String name, String address, Integer floors){
        return new BuildingDto(id, name, address, floors);
    } 

}
