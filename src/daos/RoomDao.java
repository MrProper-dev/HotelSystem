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
import dtos.RoomStatus;

public class RoomDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final RoomDao instance = new RoomDao();
    private RoomDao(){}
    public static RoomDao getRoomDao(){
        return instance;
    }

    public void updateRoom(RoomDto room) {
        if (room == null || room.getId() == null || room.getBuilding().getId() == null) {
            throw new RuntimeException("Room, room id and building id cannot be null");
        }
        final StringBuilder sql = new StringBuilder("UPDATE rooms SET building_id = ?, number = ?, floor = ?, sleeping_places = ?, price = ?, description = ?");
        if(room.getPicture() != null){
            sql.append(", picture = ?");
        }
        PreparedStatementCreator creator = new PreparedStatementCreator(sql.toString());
        creator.addParam(room.getBuilding().getId());
        creator.addParam(room.getNumber() != null ? room.getNumber() : 0);
        creator.addParam(room.getFloor() != null ? room.getFloor() : 0);
        creator.addParam(room.getSleepingPlaces() != null ? room.getSleepingPlaces() : 0);
        creator.addParam(room.getPrice() != null ? room.getPrice() : 0.0f);
        creator.addParam(room.getDescription() != null ? room.getDescription() : "");
        if(room.getPicture() != null){
            creator.addParam(room.getPicture());
        }
        creator.addWhereAndCondition("id = ?");
        creator.addParam(room.getId());
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Room with id " + room.getId() + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Updating room failed", e);
        }
    }

    public RoomDto getRoomByIdWithStatus(Integer roomId) {
        if (roomId == null) {
            throw new RuntimeException("Room id cannot be null");
        }
        final String sql = """
                SELECT r.id, r.number, r.floor, r.sleeping_places, r.price, r.picture, r.description,
                    b.id as building_id, b.name as building_name, b.floors as building_floors,
                    CASE WHEN EXISTS (
                        SELECT 1 FROM bookings b2 
                        WHERE b2.room_id = r.id 
                        AND b2.status = 'ACTIVE'
                        AND CURRENT_DATE BETWEEN b2.check_in_date AND b2.check_out_date
                    ) THEN 'BUSY' ELSE 'FREE' END as current_status
                FROM rooms r 
                JOIN buildings b ON r.building_id = b.id
                """;
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        creator.addWhereAndCondition("r.id = ?");
        creator.addParam(roomId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            if (result.next()) {
                BuildingDto building = new BuildingDto(
                    result.getInt("building_id"),
                    result.getString("building_name"),
                    null,
                    result.getInt("building_floors")
                );
                RoomDto room = new RoomDto(
                    result.getInt("id"),
                    building,
                    result.getInt("number"),
                    result.getInt("floor"),
                    result.getInt("sleeping_places"),
                    result.getFloat("price"),
                    result.getString("picture"),
                    result.getString("description")
                );
                String status = result.getString("current_status");
                room.setStatus(RoomStatus.valueOf(status));
                return room;
            } else {
                throw new RuntimeException("Room with id " + roomId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Getting room by id with status failed", e);
        }
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
        if(pageNumber != null && pageNumber >= 0){
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

    public List<RoomDto> getPageForAdmin(Integer pageNumber, Integer pageSize, 
            LocalDate checkin, LocalDate checkout, Integer guests, Integer floor,
            Integer buildingId, Float minPrice, Float maxPrice, RoomStatus statusFilter) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
                SELECT r.id, r.number, r.floor, r.sleeping_places, r.price, b.id as building_id, b.name as building_name
                FROM rooms r 
                JOIN buildings b ON r.building_id = b.id
                """);
        List<Object> params = new ArrayList<>();
        boolean needBookingJoin = checkin != null && checkout != null;
        if (needBookingJoin) {
            sqlBuilder.append("LEFT JOIN bookings b2 ON r.id = b2.room_id AND b2.status = 'ACTIVE'");
            if (checkin != null && checkout != null) {
                sqlBuilder.append(" AND b2.check_out_date > ? AND b2.check_in_date < ?");
                params.add(Date.valueOf(checkin));
                params.add(Date.valueOf(checkout));
            }
        }
        sqlBuilder.append(" WHERE 1=1");
        if (needBookingJoin && statusFilter != null) {
            if (statusFilter == RoomStatus.FREE) {
                sqlBuilder.append(" AND b2.id IS NULL");
            } else if (statusFilter == RoomStatus.BUSY) {
                sqlBuilder.append(" AND b2.id IS NOT NULL");
            }
        }
        if (guests != null) {
            sqlBuilder.append(" AND r.sleeping_places = ?");
            params.add(guests);
        }
        if (floor != null) {
            sqlBuilder.append(" AND r.floor = ?");
            params.add(floor);
        }
        if (buildingId != null) {
            sqlBuilder.append(" AND r.building_id = ?");
            params.add(buildingId);
        }
        if (minPrice != null) {
            sqlBuilder.append(" AND r.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sqlBuilder.append(" AND r.price <= ?");
            params.add(maxPrice);
        }
        sqlBuilder.append(" GROUP BY r.id, b.id, b.name");
        if (pageSize != null && pageSize >= 1) {
            sqlBuilder.append(" LIMIT ?");
            params.add(pageSize);
        } else {
            throw new RuntimeException("Page size can't be null and less than 1");
        }
        if (pageNumber != null && pageNumber >= 0) {
            sqlBuilder.append(" OFFSET ?");
            params.add(pageNumber * pageSize);
        } else {
            throw new RuntimeException("Page number can't be null and less than 0");
        }
        PreparedStatementCreator creator = new PreparedStatementCreator(sqlBuilder.toString());
        for (Object param : params) {
            creator.addParam(param);
        }
        try (Connection connection = connectionProvider.getConnection()) {
            List<RoomDto> rooms = new ArrayList<>();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                RoomDto room = mapForAdmin(result);
                rooms.add(room);
            }
            return rooms;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Getting rooms for admin failed", e);
        }
    }

    public Integer getCountForAdmin(LocalDate checkin, LocalDate checkout, Integer guests, Integer floor,
            Integer buildingId, Float minPrice, Float maxPrice, RoomStatus statusFilter) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("""
                SELECT COUNT(*)
                FROM rooms r 
                JOIN buildings b ON r.building_id = b.id
                """);
        List<Object> params = new ArrayList<>();
        boolean needBookingJoin = checkin != null && checkout != null;
        if (needBookingJoin) {
            sqlBuilder.append("LEFT JOIN bookings b2 ON r.id = b2.room_id AND b2.status = 'ACTIVE'");
            if (checkin != null && checkout != null) {
                sqlBuilder.append(" AND b2.check_out_date > ? AND b2.check_in_date < ?");
                params.add(Date.valueOf(checkin));
                params.add(Date.valueOf(checkout));
            }
        }
        sqlBuilder.append(" WHERE 1=1");
        if (needBookingJoin && statusFilter != null) {
            if (statusFilter == RoomStatus.FREE) {
                sqlBuilder.append(" AND b2.id IS NULL");
            } else if (statusFilter == RoomStatus.BUSY) {
                sqlBuilder.append(" AND b2.id IS NOT NULL");
            }
        }
        if (guests != null) {
            sqlBuilder.append(" AND r.sleeping_places = ?");
            params.add(guests);
        }
        if (floor != null) {
            sqlBuilder.append(" AND r.floor = ?");
            params.add(floor);
        }
        if (buildingId != null) {
            sqlBuilder.append(" AND r.building_id = ?");
            params.add(buildingId);
        }
        if (minPrice != null) {
            sqlBuilder.append(" AND r.price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null) {
            sqlBuilder.append(" AND r.price <= ?");
            params.add(maxPrice);
        }
        sqlBuilder.append(" GROUP BY r.id, b.id, b.name");
        PreparedStatementCreator creator = new PreparedStatementCreator(sqlBuilder.toString());
        for (Object param : params) {
            creator.addParam(param);
        }
        try (Connection connection = connectionProvider.getConnection()) {
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            Integer count = 0;
            while(result.next()){
                count++;
            }
            return count;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Getting rooms count for admin failed", e);
        }
    }

    private RoomDto mapForAdmin(ResultSet result) throws SQLException {
        BuildingDto building = new BuildingDto(
            result.getInt("building_id"),
            result.getString("building_name"));
        RoomDto room = new RoomDto(
            result.getInt("id"),
            building,
            result.getInt("number"),
            result.getInt("floor"),
            result.getInt("sleeping_places"),
            result.getFloat("price")
        );
        try{
            room.setStatus(RoomStatus.valueOf(result.getString("room_status")));
        }catch (SQLException e){}
        return room;
    }
}
