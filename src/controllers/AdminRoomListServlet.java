package controllers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import dtos.BuildingDto;
import dtos.RoomDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BuildingService;
import services.RoomService;

@WebServlet("/admin/rooms")
public class AdminRoomListServlet extends HttpServlet{

    private RoomService roomService;
    private BuildingService buildingService;

    @Override
    public void init() throws ServletException {
        roomService = RoomService.getRoomService();
        buildingService = BuildingService.getBuildingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> paramsMap = req.getParameterMap();

        List<RoomDto> rooms = roomService.getRoomsPageForAdmin(paramsMap);
        Integer roomsCount = roomService.getRoomsCountForAdmin(paramsMap);
        Integer pageCount = roomService.getPageCount(roomsCount);
        Integer maxFloor = buildingService.getMaxFloor();
        Integer maxGuests = roomService.getMaxGuests();
        List<BuildingDto> buildings = buildingService.getBuildings();
        Integer currentPage = 0;

        String strCurrentPage = req.getParameter("page");
        if(strCurrentPage != null && !strCurrentPage.isEmpty()) {
            currentPage = Integer.parseInt(strCurrentPage);
        }        

        req.setAttribute("rooms", rooms);
        req.setAttribute("roomsCount", roomsCount);
        req.setAttribute("pageCount", pageCount);
        req.setAttribute("maxFloor", maxFloor);
        req.setAttribute("maxGuests", maxGuests);
        req.setAttribute("buildings", buildings);
        req.setAttribute("currentPage", currentPage);
        req.getRequestDispatcher("/WEB-INF/view/admin/list_of_rooms.jsp").forward(req, resp);
    }

}
