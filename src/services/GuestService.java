package services;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import daos.GuestDao;
import dtos.GuestDto;

public class GuestService {

    private static final GuestService instance = new GuestService();
    private GuestService(){}
    public static GuestService getGuestService(){
        return instance;
    }

    private GuestDao guestDao = GuestDao.getGuestDao();

    public void replaceGuests(Integer bookingId, List<Map<String, String>> guesgtsMap){
        List<GuestDto> guests = new ArrayList<>();
        for (Map<String,String> map : guesgtsMap) {
            String fullName = map.get("last_name") + " " + map.get("first_name");
            if(map.get("surname") != null) fullName +=" " + map.get("surname");
            String sn = map.get("doc_series") + " " + map.get("doc_number");
            GuestDto guest = new GuestDto(
                fullName, 
                LocalDate.parse(map.get("birthdate")), 
                sn);
            guests.add(guest);
        }
        guestDao.updateGuestsByBookingId(bookingId, guests);
    }

}
