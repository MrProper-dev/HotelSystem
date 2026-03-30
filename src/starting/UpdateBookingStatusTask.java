package starting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class UpdateBookingStatusTask implements ServletContextListener{

    private final AtomicReference<Connection> referenceToConnection = new AtomicReference<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try{
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/hotelsystem", "postgres", "root");
            referenceToConnection.set(connection);
        }catch (SQLException e){
            throw new RuntimeException("Connection opening failed", e);
        }
        Thread updateBookingTask = new DailyTaskThread(referenceToConnection.get());
        updateBookingTask.start();
    }

    private static class DailyTaskThread extends Thread {

        private final Connection connection;

        public DailyTaskThread(Connection connection){
            this.connection = connection;
        }

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    LocalDateTime now = LocalDateTime.now();
                    LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
                    long millisToMidnight = ChronoUnit.MILLIS.between(now, nextMidnight);
                    Thread.sleep(millisToMidnight);
                    performDailyTask();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
        
        private void performDailyTask() {
            try{
                final String query = "UPDATE bookings SET status = 'COMPLETED' WHERE check_out_date < CURRENT_DATE AND status != 'CANCELED'";
                Statement statement = connection.createStatement();
                statement.execute(query);
            }catch (SQLException e){
                throw new RuntimeException("Updating booking status failed", e);
            }
        }
    }

}
