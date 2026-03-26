package controllers;

import java.io.IOException;
import java.util.List;

import dtos.BookingDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.BookingService;

@WebServlet("/booking/history")
public class BookingHistoryServlet extends HttpServlet{

    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        bookingService = BookingService.getBookingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer clientId = (Integer) session.getAttribute("clientId");
        Integer pageNumber = 0; 
        Integer totalPages = bookingService.getTotalPagesByClientId(clientId);

        String pageNumberStr = req.getParameter("page");
        if(pageNumberStr != null && !pageNumberStr.isEmpty()){
            try{
                pageNumber = Integer.parseInt(pageNumberStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }

        List<BookingDto> bookings = bookingService.getBookingsPageByClientId(clientId, pageNumber);
        
        req.setAttribute("bookings", bookings);
        req.setAttribute("page", pageNumber);
        req.setAttribute("totalPages", totalPages);
        req.getRequestDispatcher("/WEB-INF/view/client/bookings_history.jsp").forward(req, resp);
    }

}
