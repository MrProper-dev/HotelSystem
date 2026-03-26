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
    private GuestDao() {}
    public static GuestDao getGuestDao() {
        return instance;
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