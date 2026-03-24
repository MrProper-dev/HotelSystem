package services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import daos.RoomDao;
import dtos.RoomDto;

public class RoomService {

    private final Integer PAGE_SIZE = 12;

    private static final RoomService instance = new RoomService();
    private RoomService(){}
    public static RoomService getRoomService(){
        return instance;
    }
    
    private final RoomDao roomDao = RoomDao.getRoomDao();

    public RoomDto getRoomById(Integer roomId){
        return roomDao.getRoomById(roomId);
    }

    public Integer getMaxGuests(){
        return roomDao.getMaxSleepingPlaces();
    }

    public Integer getPageCount(){
        Integer roomsCount = roomDao.getCount();
        Integer pageCount = roomsCount / PAGE_SIZE;
        if (roomsCount%PAGE_SIZE !=0) {
            pageCount++;
        }
        return pageCount;
    }

    public Integer getCount(Map<String, String[]> paramsMap){
        Integer guests = null, floor = null, buildingId = null;
        LocalDate checkin = null, checkout = null;
        Float minPrice = null, maxPrice = null;
        try{
            String[] buff;
            if ((buff = paramsMap.get("guests")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                guests = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("floor")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                floor = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("building")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                buildingId = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("checkin")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                checkin = LocalDate.parse(buff[0]);
            }
            if ((buff = paramsMap.get("checkout")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                checkout = LocalDate.parse(buff[0]);
            }
            if ((buff = paramsMap.get("price_min")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                minPrice = Float.parseFloat(buff[0]);
            }
            if ((buff = paramsMap.get("price_max")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                maxPrice = Float.parseFloat(buff[0]);
            }
        }catch (NumberFormatException e){
            throw new RuntimeException("One of the parameters is incorrect", e);
        }
        return roomDao.getCout(checkin, checkout, guests, floor, buildingId, minPrice, maxPrice);
    }

    public List<RoomDto> getPage(Map<String, String[]> paramsMap){
        Integer pageNumber = null, guests = null, floor = null, buildingId = null;
        LocalDate checkin = null, checkout = null;
        Float minPrice = null, maxPrice = null;
        try{
            String[] buff;
            if ((buff = paramsMap.get("page")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                pageNumber = Integer.parseInt(buff[0])-1;
            }else{
                pageNumber = 0;
            }
            if ((buff = paramsMap.get("guests")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                guests = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("floor")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                floor = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("building")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                buildingId = Integer.parseInt(buff[0]);
            }
            if ((buff = paramsMap.get("checkin")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                checkin = LocalDate.parse(buff[0]);
            }
            if ((buff = paramsMap.get("checkout")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                checkout = LocalDate.parse(buff[0]);
            }
            if ((buff = paramsMap.get("price_min")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                minPrice = Float.parseFloat(buff[0]);
            }
            if ((buff = paramsMap.get("price_max")) != null && buff.length > 0 && !buff[0].isEmpty()) {
                maxPrice = Float.parseFloat(buff[0]);
            }
        }catch (NumberFormatException e){
            throw new RuntimeException("One of the parameters is incorrect", e);
        }
        return roomDao.getPage(pageNumber, 
            PAGE_SIZE, 
            checkin, 
            checkout, 
            guests, 
            floor, 
            buildingId, 
            minPrice, 
            maxPrice);
    }

}
