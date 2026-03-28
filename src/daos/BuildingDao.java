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
    private BuildingDao(){}
    public static BuildingDao getBuildingDao(){
        return instance;
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
