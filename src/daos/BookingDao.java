package daos;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;
import daos.utils.PreparedStatementCreator;
import dtos.BookingDto;
import dtos.GuestDto;

public class BookingDao {

    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();

    private static final BookingDao instance = new BookingDao();
    private BookingDao(){}
    public static BookingDao getBookingDao(){
        return instance;
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

}
