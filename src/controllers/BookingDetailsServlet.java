package controllers;

import dtos.BookingDto;
import services.BookingService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/booking/details/*")
public class BookingDetailsServlet extends HttpServlet {
    
    private BookingService bookingService;
    
    @Override
    public void init() throws ServletException {
        bookingService = BookingService.getBookingService();
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer bookingId = null;
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
        
        BookingDto booking = bookingService.getBookingById(bookingId);
        
        req.setAttribute("booking", booking);
        req.getRequestDispatcher("/WEB-INF/view/client/booking_details.jsp").forward(req, resp);
    }
}