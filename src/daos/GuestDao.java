package daos;

import dtos.GuestDto;
import daos.utils.PreparedStatementCreator;
import daos.utils.ConnectionProvider;
import daos.utils.ConnectionProviderFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GuestDao {
    
    private ConnectionProvider connectionProvider = ConnectionProviderFactory.getConnectionProvider();
    
    private static final GuestDao instance = new GuestDao();
    protected GuestDao() {}
    public static GuestDao getGuestDao() {
        return instance;
    }

    public void updateGuestsByBookingId(Integer bookingId, List<GuestDto> guests) {
        if (bookingId == null) {
            throw new RuntimeException("Booking id cannot be null");
        }
        if (guests == null) {
            throw new RuntimeException("Guests list cannot be null");
        }
        Connection connection = null;
        try {
            connection = connectionProvider.getConnection();
            connection.setAutoCommit(false);
            String deleteSql = "DELETE FROM guests";
            PreparedStatementCreator deleteCreator = new PreparedStatementCreator(deleteSql);
            deleteCreator.addWhereAndCondition("booking_id = ?");
            deleteCreator.addParam(bookingId);
            PreparedStatement deleteStatement = deleteCreator.createPreparedStatement(connection);
            deleteStatement.executeUpdate();
            if (!guests.isEmpty()) {
                String insertSql = "INSERT INTO guests (booking_id, full_name, birth_date, series_and_number) VALUES ";
                PreparedStatementCreator insertCreator = new PreparedStatementCreator(insertSql);
                for (GuestDto guest : guests) {
                    insertCreator.addValue("(?, ?, ?, ?)");
                    insertCreator.addParam(bookingId);
                    insertCreator.addParam(guest.getFullName() != null ? guest.getFullName() : "");
                    insertCreator.addParam(guest.getBirthDate() != null ? 
                        java.sql.Date.valueOf(guest.getBirthDate()) : null);
                    insertCreator.addParam(guest.getSeriesAndNumber() != null ? guest.getSeriesAndNumber() : "");
                }
                PreparedStatement insertStatement = insertCreator.createPreparedStatement(connection);
                insertStatement.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                throw new RuntimeException("Rollback failed", rollbackException);
            }
            throw new RuntimeException("Updating guests for booking failed", e);
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Connection closing failed", e);
            }
        }
    }
    
    public List<GuestDto> getGuestsByBookingId(Integer bookingId) {
        final String sql = "SELECT full_name, birth_date, series_and_number FROM guests";
        PreparedStatementCreator creator = new PreparedStatementCreator(sql);
        if (bookingId == null) {
            throw new RuntimeException("Booking id cannot be null");
        }
        creator.addWhereAndCondition("booking_id = ?");
        creator.addParam(bookingId);
        List<GuestDto> guests = new ArrayList<>();
        try (Connection connection = connectionProvider.getConnection();
             PreparedStatement statement = creator.createPreparedStatement(connection);
             ResultSet result = statement.executeQuery()) {
            while (result.next()) {
                GuestDto guest = new GuestDto(
                    result.getString("full_name"), 
                    result.getDate("birth_date").toLocalDate(), 
                    result.getString("series_and_number"));
                guests.add(guest);
            }
            return guests;
        } catch (SQLException e) {
            throw new RuntimeException("Getting guests by booking id failed", e);
        }
    }

}