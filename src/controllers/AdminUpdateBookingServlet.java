package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.GuestService;

@WebServlet("/admin/booking/guests/update/*")
public class AdminUpdateBookingServlet extends HttpServlet {

    private GuestService guestService;

    @Override
    public void init() throws ServletException {
        guestService = GuestService.getGuestService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer bookingId = 0;
        if(postfixPath != null){
            String[] pathItems = postfixPath.split("/");
            if(pathItems.length == 2 && !pathItems[1].isEmpty() ){
                try{
                    bookingId = Integer.parseInt(pathItems[1]);
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

        List<Map<String, String>> guests = parseGuests(req.getParameterMap());

        guestService.replaceGuests(bookingId, guests);

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
