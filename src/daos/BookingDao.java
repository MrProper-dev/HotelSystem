package daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.BookingDto;
import dtos.BookingStatus;
import dtos.BuildingDto;
import dtos.ClientDto;
import dtos.GuestDto;
import dtos.RoomDto;

public class BookingDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final BookingDao instance = new BookingDao();
    private BookingDao(){}
    public static BookingDao getBookingDao(){
        return instance;
    }

    public BookingDto getBookingById(Integer bookingId) {
        final String root = """
                SELECT b.id, b.status, b.check_in_date, b.check_out_date, b.total_price, r.number, r.floor, r.picture, r.price, r.sleeping_places, bu.name as building_name, g.full_name, g.birth_date, g.series_and_number
                FROM bookings b JOIN rooms r ON r.id = b.room_id JOIN buildings bu ON bu.id = r.building_id JOIN guests g ON g.booking_id = b.id
                """;
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if (bookingId == null) {
            throw new RuntimeException("Booking id cannot be null");
        }
        creator.addWhereAndCondition("b.id = ?");
        creator.addParam(bookingId);
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery()) {
            BookingDto booking = null;
            while (result.next()) {
                if (booking == null) {
                    ClientDto client = new ClientDto();
                    BuildingDto building = new BuildingDto(result.getString("building_name"));
                    RoomDto room = new RoomDto(
                        building,
                        result.getInt("number"),
                        result.getInt("floor"),
                        result.getInt("sleeping_places"),
                        result.getFloat("price"),
                        result.getString("picture")
                    );
                    booking = new BookingDto(
                        result.getInt("id"),
                        client,
                        room,
                        result.getDate("check_in_date").toLocalDate(),
                        result.getDate("check_out_date").toLocalDate(),
                        result.getFloat("total_price"),
                        mapToStatus(result.getString("status"))
                    );
                }
            }
            if (booking == null) {
                throw new RuntimeException("Booking with id " + bookingId + " not found");
            }
            return booking;
        } catch (SQLException e) {
            throw new RuntimeException("Getting booking details failed", e);
        }
    }

    public void cancelBooking(Integer bookingId) {
        final String sql = "UPDATE bookings SET status = ?";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        
        if (bookingId == null) {
            throw new RuntimeException("Booking id cannot be null");
        }
        
        creator.addWhereAndCondition("id = ?");
        creator.addParam(BookingStatus.CANCELED.name());
        creator.addParam(bookingId);
        
        try (Connection connection = connectionProvider.getConnection();
            PreparedStatement statement = creator.createPreparedStatement(connection)) {
            
            int updatedRows = statement.executeUpdate();
            if (updatedRows == 0) {
                throw new RuntimeException("Booking with id " + bookingId + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Canceling booking failed", e);
        }
    }

    public Integer getBookingsCount(Integer clientId){
        final String root = "SELECT COUNT(*) FROM bookings";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(clientId == null){
            throw new RuntimeException("Client id can`t be null");
        }
        creator.addWhereAndCondition("client_id = ?");
        creator.addParam(clientId);
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            result.next();
            return result.getInt(1);
        }catch (SQLException e){
            throw new RuntimeException("Getting count of bookings failed", e);
        }

    }

    public List<BookingDto> getBookingsPageByClientId(Integer clientId, Integer pageNumber, Integer pageSize){
        final String root = """
                SELECT b.id, b.status, b.check_in_date, b.check_out_date, b.total_price, r.number, r.floor, r.picture, bu.name, COUNT(g.id) AS g_count FROM bookings b 
                JOIN rooms r ON r.id = b.room_id JOIN buildings bu ON bu.id = r.building_id JOIN guests g ON g.booking_id = b.id
                """;
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(clientId == null || pageNumber == null || pageSize == null){
            throw new RuntimeException("One of the params is null");
        }
        creator.addWhereAndCondition("b.client_id = ?");
        creator.addParam(clientId);
        creator.addGroupBy("b.id, b.status, b.check_in_date, b.check_out_date, b.total_price, r.number, r.floor, r.picture, bu.name");
        creator.addOrderBy("b.id ASC");
        creator.addLimit();
        creator.addParam(pageSize);
        creator.addOffset();
        creator.addParam(pageSize * pageNumber);
        List<BookingDto> bookings = new ArrayList<>();
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet resul = statement.executeQuery();
            while(resul.next()){
                List<GuestDto> guests = new ArrayList<>();
                for(int i = 0; i<resul.getInt("g_count"); i++){
                    guests.add(new GuestDto());
                }
                BookingDto booking = new BookingDto(
                    resul.getInt("id"), 
                    new ClientDto(clientId), 
                    new RoomDto( 
                        new BuildingDto(
                            resul.getString("name")
                        ), 
                        resul.getInt("number"), 
                        resul.getInt("floor"), 
                        resul.getString("picture")), 
                    resul.getDate("check_in_date").toLocalDate(), 
                    resul.getDate("check_out_date").toLocalDate(), 
                    resul.getFloat("total_price"), 
                    mapToStatus(resul.getString("status")));
                booking.setGuests(guests);
                bookings.add(booking);
            }
            return bookings;
        }catch (SQLException e){
            throw new RuntimeException("Getting booking, client, group, room data failed", e);
        }
    }

    public Boolean checkRoomAvailabilityByRoomIdAndDates(Integer roomId, LocalDate checkIn, LocalDate checkOut){
        final String root = "SELECT true FROM bookings";
        PreparedStatementCreator creator = new PreparedStatementCreator(root);
        if(roomId != null && checkIn != null && checkOut != null){
            creator.addWhereAndCondition("room_id = ?");
            creator.addParam(roomId);
            creator.addWhereAndCondition("NOT (check_out_date > ? AND check_in_date < ?)");
            creator.addParam(Date.valueOf(checkIn));
            creator.addParam(Date.valueOf(checkOut));
        }else{
            throw new RuntimeException("Room id, chek in date, chek out date can`t be null");
        }
        try(Connection connection = connectionProvider.getConnection()){
            PreparedStatement statement = creator.createPreparedStatement(connection);
            ResultSet result = statement.executeQuery();
            if(result.next()){
                return result.getBoolean(1);
            }else{
                return false;
            }
        }catch (SQLException e){
            throw new RuntimeException("Check room availability failed", e);
        }
    }

    public void createBooking(BookingDto booking, List<GuestDto> guests){
        final String bookingsRoot = "INSERT INTO bookings VALUES";
        final String guestsRoot = "INSERT INTO guests VALUES";
        PreparedStatementCreator bookingsCreator = new PreparedStatementCreator(bookingsRoot);
        if(booking.getClient() == null || booking.getClient().getId() == null || 
            booking.getRoom() == null || booking.getRoom().getId() == null || booking.getCheckInDate() == null || 
            booking.getCheckOutDate() == null || booking.getTotalPrice() == null || booking.getStatus() == null){
            throw new RuntimeException("One of the parameters is null");
        }
        bookingsCreator.addValue("(DEFAULT, ?, ?, ?, ?, ?, ?)");
        bookingsCreator.addReturning("id");
        bookingsCreator.addParam(booking.getClient().getId());
        bookingsCreator.addParam(booking.getRoom().getId());
        bookingsCreator.addParam(Date.valueOf(booking.getCheckInDate()));
        bookingsCreator.addParam(Date.valueOf(booking.getCheckOutDate()));
        bookingsCreator.addParam(booking.getTotalPrice());
        bookingsCreator.addParam(booking.getStatus().name());
        Connection connection = null;
        try{
            connection = connectionProvider.getConnection();
            connection.setAutoCommit(false);
            PreparedStatement bookingStatement = bookingsCreator.createPreparedStatement(connection);
            ResultSet biikingResult = bookingStatement.executeQuery();
            biikingResult.next();
            Integer bookingId = biikingResult.getInt(1);

            PreparedStatementCreator guestsCreator = new PreparedStatementCreator(guestsRoot);
            for (GuestDto guest : guests) {
                guestsCreator.addValue("(DEFAULT, ?, ?, ?, ?)");
                guestsCreator.addParam(bookingId);
                guestsCreator.addParam(guest.getFullName());
                guestsCreator.addParam(Date.valueOf(guest.getBirthDate()));
                guestsCreator.addParam(guest.getSeriesAndNumber());
            }
            PreparedStatement guestStatement = guestsCreator.createPreparedStatement(connection);
            guestStatement.executeUpdate();
            connection.commit();
            connection.setAutoCommit(true);
        }catch (SQLException e){
            try {
                connection.rollback();
            } catch (SQLException e1) {
                throw new RuntimeException("Rollback failed");
            }
            throw new RuntimeException("Inserting booking and his guests failed", e);
        }finally{
            try {
                connection.close();
            } catch (SQLException e) {
                throw new RuntimeException("Connection closing failed", e);
            }
        }
    }

    private BookingStatus mapToStatus(String status){
        if(BookingStatus.ACTIVE.name().equals(status)){
            return BookingStatus.ACTIVE;
        }else if(BookingStatus.CANCELED.name().equals(status)){
            return BookingStatus.CANCELED;
        }else{
            return BookingStatus.COMPLETED;
        }
    }

}
