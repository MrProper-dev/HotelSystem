package controllers;

import java.io.IOException;
import java.util.List;

import dtos.BookingDto;
import dtos.ClientDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BookingService;
import services.ClientService;

@WebServlet("/admin/client/details/*")
public class AdminClientProfileServlet extends HttpServlet{

    private ClientService clientService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
        bookingService = BookingService.getBookingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer clientId = 0;
        if(postfixPath != null){
            String[] pathItems = postfixPath.split("/");
            if(pathItems.length == 2 && !pathItems[1].isEmpty() ){
                try{
                    clientId = Integer.parseInt(pathItems[1]);
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

        ClientDto client = clientService.getClientByIdForAdmin(clientId);
        List<BookingDto> bookings = bookingService.getBookingByClientId(clientId);

        req.setAttribute("client", client);
        req.setAttribute("bookings", bookings);
        req.getRequestDispatcher("/WEB-INF/view/admin/client_profile.jsp").forward(req, resp);
    }

}
