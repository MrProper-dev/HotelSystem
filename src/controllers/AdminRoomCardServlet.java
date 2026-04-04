package controllers;

import java.io.IOException;
import java.util.List;

import dtos.BookingDto;
import dtos.BuildingDto;
import dtos.RoomDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BookingService;
import services.BuildingService;
import services.RoomService;

@WebServlet("/admin/rooms/*")
public class AdminRoomCardServlet extends HttpServlet{

    private RoomService roomService;
    private BuildingService buildingService;
    private BookingService bookingService;

    @Override
    public void init() throws ServletException {
        roomService = RoomService.getRoomService();
        buildingService = BuildingService.getBuildingService();
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

        RoomDto room = roomService.getRoomByIdWithStatus(roomId);
        List<BookingDto> bookings = bookingService.getBookingsByRoomId(roomId);
        List<BuildingDto> buildings = buildingService.getBuildingsWithoutAddress();

        req.setAttribute("room", room);
        req.setAttribute("bookings", bookings);
        req.setAttribute("buildings", buildings);
        req.getRequestDispatcher("/WEB-INF/view/admin/number_card.jsp").forward(req, resp);
    }

}
