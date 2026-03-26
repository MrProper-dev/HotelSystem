package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dtos.RoomDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BookingService;
import services.RoomService;

@WebServlet("/book/*")
public class BookingServlet extends HttpServlet {

    private RoomService roomService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        roomService = RoomService.getRoomService();
        bookingService = BookingService.getBookingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer roomId = 0;
        if(postfixPath != null){
            String[] pathItems = postfixPath.split("/");
            if(pathItems.length == 2 && !pathItems[1].isEmpty() ){
                try{
                    roomId = Integer.parseInt(pathItems[1]);
                }catch (NumberFormatException e){
                    resp.sendError(404);
                    return;
                }
            }else{
                resp.sendError(404);
                return;
            }
        }else{
            resp.sendError(404);
            return;
        }
        
        RoomDto room = roomService.getRoomById(roomId);
        
        req.setAttribute("room", room);
        req.getRequestDispatcher("/WEB-INF/view/client/booking_of_number.jsp").forward(req, resp);
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> params = req.getParameterMap();

        List<Map<String, String>> guests = parseGuests(params);
        Integer roomId;
        Integer clientId = (Integer) req.getSession().getAttribute("clientId");
        LocalDate checkin;
        LocalDate checkout;

        String checkinSrt = req.getParameter("checkin");
        String checkoutStr = req.getParameter("checkout");
        if(checkinSrt != null && checkoutStr != null && !checkinSrt.isEmpty() && !checkoutStr.isEmpty()){
            try{
                checkin = LocalDate.parse(checkinSrt);
                checkout = LocalDate.parse(checkoutStr);
            }catch (Exception e){
                resp.sendError(400);
                return;
            }
        }else{
            resp.sendError(400);
            return;
        }

        String roomIdStr = req.getParameter("room_id");
        if(roomIdStr != null && !roomIdStr.isEmpty()){
            try{
                roomId = Integer.parseInt(roomIdStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }else{
            resp.sendError(400);
            return;
        }

        bookingService.crateBooking(guests, roomId, clientId, checkin, checkout);

        resp.sendRedirect("/hotelsystem/booking/history");
    }

    

    private List<Map<String, String>> parseGuests(Map<String, String[]> params){
        List<Map<String, String>> guests = new ArrayList<>();
        Pattern guestPattern = Pattern.compile("^guests\\[(\\d+)\\]\\[(\\w+)\\]$");

        for (String key : params.keySet()) {
            Matcher matcher = guestPattern.matcher(key);
            if (matcher.matches()) {
                int index = Integer.parseInt(matcher.group(1));
                String field = matcher.group(2);
                while (guests.size() <= index) {
                    guests.add(new HashMap<>());
                }
                guests.get(index).put(field, params.get(key)[0]);
            }
        }
        
        return guests;
    }
    
}
