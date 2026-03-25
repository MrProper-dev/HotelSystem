package daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.BuildingDto;
import dtos.RoomDto;

public class RoomDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final RoomDao instance = new RoomDao();
    private RoomDao(){}
    public static RoomDao getRoomDao(){
        return instance;
    }

    public Float getPriceById(Integer id){
        final String root = "SELECT price FROM rooms";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(id == null){
            throw new RuntimeException("Room id can`t be null");
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(id);
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getFloat(1);
        } catch (SQLException e) {
            throw new RuntimeException("Getting room price failed", e);
        }
    }

    public RoomDto getRoomById(Integer roomId){
        final String query = "SELECT r.*, b.name FROM rooms r JOIN buildings b ON r.building_id=b.id";
        final PreparedStatementCreator creator = new PreparedStatementCreator(query);
        creator.addWhereAndCondition("r.id = ?");
        creator.addParam(roomId);
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            if(result.next()) {
                return map(result);
            }else{
                return null;
            }
        }catch (SQLException e){
            throw new RuntimeException("Getting room by id(%s) failed".formatted(roomId), e);
        }
    }

    public Integer getMaxSleepingPlaces(){
        final String query = "SELECT MAX(sleeping_places) FROM rooms";
        try (Connection connection = connectionProvider.getConnection()) {
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.next();
            return result.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Getting max sleeping places failed");
        }
    }

    public Integer getCount(){
        final String query = "SELECT COUNT(*) FROM rooms";
        try(Connection connection = connectionProvider.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(query);
            result.next();
            return result.getInt(1);
        } catch (SQLException e){
            throw new RuntimeException("Getting rooms quantity failed", e);
        }
    }

    public Integer getCout(LocalDate checkin, LocalDate checkout, Integer guests, Integer floor,
            Integer buildingId, Float minPrice, Float maxPrice){
        final String  root = "SELECT COUNT(r.*) FROM rooms r";
        final PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(checkin != null && checkout != null && checkin.isBefore(checkout)){
            creator.addWhereAndCondition("r.id NOT IN(SELECT room_id FROM bookings WHERE check_out_date > ? AND check_in_date < ?)");
            creator.addParam(Date.valueOf(checkin));
            creator.addParam(Date.valueOf(checkout));
        }
        if(guests != null){
            creator.addWhereAndCondition("sleeping_places = ?");
            creator.addParam(guests);
        }
        if(floor != null) {
            creator.addWhereAndCondition("floor = ?");
            creator.addParam(floor);
        }
        if(buildingId != null) {
            creator.addWhereAndCondition("building_id = ?");
            creator.addParam(buildingId);
        }
        if(minPrice != null) {
            creator.addWhereAndCondition("price > ?");
            creator.addParam(minPrice);
        }
        if(maxPrice != null) {
            creator.addWhereAndCondition("price < ?");
            creator.addParam(maxPrice);
        }
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1);
        } catch (SQLException e){
            throw new RuntimeException("Getting rooms quantity with params failed", e);
        }
    }

    public List<RoomDto> getPage(Integer pageNumber, Integer pageSize, 
            LocalDate checkin, LocalDate checkout, Integer guests, Integer floor,
            Integer buildingId, Float minPrice, Float maxPrice){
        final String  root = "SELECT r.*, b.name FROM rooms r JOIN buildings b ON r.building_id=b.id";
        final PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(checkin != null && checkout != null && checkin.isBefore(checkout)){
            creator.addWhereAndCondition("r.id NOT IN(SELECT room_id FROM bookings WHERE check_out_date > ? AND check_in_date < ?)");
            creator.addParam(Date.valueOf(checkin));
            creator.addParam(Date.valueOf(checkout));
        }
        if(guests != null){
            creator.addWhereAndCondition("sleeping_places = ?");
            creator.addParam(guests);
        }
        if(floor != null) {
            creator.addWhereAndCondition("floor = ?");
            creator.addParam(floor);
        }
        if(buildingId != null) {
            creator.addWhereAndCondition("building_id = ?");
            creator.addParam(buildingId);
        }
        if(minPrice != null) {
            creator.addWhereAndCondition("price > ?");
            creator.addParam(minPrice);
        }
        if(maxPrice != null) {
            creator.addWhereAndCondition("price < ?");
            creator.addParam(maxPrice);
        }
        if(pageSize != null && pageSize >= 1){
            creator.addLimit();
            creator.addParam(pageSize);
        }else{
            throw new RuntimeException("Page size can`t be null and less 1");
        }
        if(pageNumber != null && pageNumber >=0){
            creator.addOffset();
            creator.addParam(pageNumber * pageSize);
        }else{
            throw new RuntimeException("Page number can`t be null and less 0");
        }

        try(Connection connection = connectionProvider.getConnection()){
            List<RoomDto> rooms = new ArrayList<>();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            while(result.next()){
                RoomDto room = map(result);
                rooms.add(room);
            }
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Getting rooms failed", e);
        }
    }

    private RoomDto map(ResultSet result) throws SQLException{
        return new RoomDto(
            result.getInt("id"),
            new BuildingDto(
                result.getInt("building_id"), 
                result.getString("name"), 
                null, 
                null),
            result.getInt("number"), 
            result.getInt("floor"), 
            result.getInt("sleeping_places"), 
            result.getBigDecimal("price").floatValue(), 
            result.getString("picture"), 
            result.getString("description"));
    }

}
