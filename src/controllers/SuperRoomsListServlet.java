package controllers;

import java.io.IOException;
import java.util.List;

import dtos.BuildingDto;
import dtos.RoomDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BuildingService;
import services.RoomService;

@WebServlet("/super/rooms")
public class SuperRoomsListServlet extends HttpServlet{

    private BuildingService buildingService;
    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        buildingService = BuildingService.getBuildingService();
        roomService = RoomService.getRoomService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String floorStr = req.getParameter("floor");
        String buildingIdStr = req.getParameter("building");
        String pageStr = req.getParameter("page");

        Integer floor = null;
        Integer buildingId = null;
        Integer page = 0;

        if(floorStr != null && !floorStr.isEmpty()){
            try{
                floor = Integer.parseInt(floorStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }
        if(buildingIdStr != null && !buildingIdStr.isEmpty()){
            try{
                buildingId = Integer.parseInt(buildingIdStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }
        if(pageStr != null && !pageStr.isEmpty()){
            try{
                page = Integer.parseInt(pageStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }

        List<RoomDto> rooms = roomService.getRoomsForSuperWithFilters(floor, buildingId, page);
        Integer roomsCount = roomService.getRoomsCountForSuperWithFilters(floor, buildingId);
        Integer pageCount = roomService.getPageCountForSuper(roomsCount);
        List<BuildingDto> buildings = buildingService.getBuildingsWithoutAddress();
        Integer maxFloot = buildingService.getMaxFloor();

        req.setAttribute("rooms", rooms);
        req.setAttribute("maxFloot", maxFloot);
        req.setAttribute("roomsCount", roomsCount);
        req.setAttribute("buildings", buildings);
        req.setAttribute("currentPage", page);
        req.setAttribute("pageCount", pageCount);
        req.getRequestDispatcher("/WEB-INF/view/super/manage_rooms.jsp").forward(req, resp);
    }

}
