package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BookingService;

@WebServlet("/checkbooking/api/v1/*")
public class BookingCheckServlet extends HttpServlet{

    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        bookingService = BookingService.getBookingService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        
        String checkInSrt = req.getParameter("check_in");
        String checkOutSrt = req.getParameter("check_out");

        Boolean availability = bookingService.checkRoomAvailability(roomId, checkInSrt, checkOutSrt);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = "{\"availability\" : \"%b\"}".formatted(availability);
        PrintWriter out = resp.getWriter();
        out.print(json);
        out.flush();
    }

}
