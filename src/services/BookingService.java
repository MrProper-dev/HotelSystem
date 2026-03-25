package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import daos.BookingDao;
import daos.RoomDao;
import dtos.BookingDto;
import dtos.BookingStatus;
import dtos.ClientDto;
import dtos.GuestDto;
import dtos.RoomDto;

public class BookingService {

    private static final BookingService instance = new BookingService();
    private BookingService(){}
    public static BookingService getBookingService(){
        return instance;
    }

    private BookingDao bookingDao = BookingDao.getBookingDao();
    private RoomDao roomDao = RoomDao.getRoomDao();

    public Boolean checkRoomAvailability(Integer roomId, String checkIn, String checkOut){
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);
        return bookingDao.checkRoomAvailabilityByRoomIdAndDates(roomId, checkInDate, checkOutDate);
    }

    public void crateBooking(List<Map<String, String>> guestsParams, Integer roomId, Integer clientId, LocalDate checkIn, LocalDate checkOut){
        Float roomPrice = roomDao.getPriceById(roomId);
        Integer nights = checkOut.getDayOfYear() - checkIn.getDayOfYear();
        if(nights <= 0){
            throw new RuntimeException("Nights count can`t be 0 or less than 0");
        }
        Float totalPrice = roomPrice * nights;

        BookingDto booking = new BookingDto(
            null, 
            new ClientDto(clientId), 
            new RoomDto(roomId), 
            checkIn, 
            checkOut, 
            totalPrice,
            BookingStatus.CREATED);
        
        List<GuestDto> guests = new ArrayList<>();
        for (Map<String,String> params : guestsParams) {
            GuestDto guest = new GuestDto();
            if(params.get("last_name") != null && params.get("first_name") != null && params.get("patronymic") != null){
                StringBuilder sb = new StringBuilder(params.get("last_name"));
                sb.append(" ");
                sb.append(params.get("first_name"));
                sb.append(" ");
                sb.append(params.get("patronymic"));
                guest.setFullName(sb.toString());
            }
            if(params.get("birthdate") != null){
                guest.setBirthDate(LocalDate.parse(params.get("birthdate")));
            }
            if(params.get("doc_series") != null && params.get("doc_number") != null){
                StringBuilder sb = new StringBuilder(params.get("doc_series"));
                sb.append(" ");
                sb.append(params.get("doc_number"));
                guest.setSeriesAndNumber(sb.toString());
            }
            guests.add(guest);
        }
    
        bookingDao.createBooking(booking, guests);
    }

}
