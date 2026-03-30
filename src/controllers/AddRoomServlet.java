package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.RoomService;

@WebServlet("/super/room/add")
public class AddRoomServlet extends HttpServlet{

    private RoomService roomService;

    @Override
    public void init() throws ServletException {
        roomService = RoomService.getRoomService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String roomNumberStr = req.getParameter("room_number");
        String floorStr = req.getParameter("floor");
        String buildingIdStr = req.getParameter("building");

        Integer floor = null;
        Integer buildingId = null;
        Integer roomNumber = 0;

        if(roomNumberStr != null && !roomNumberStr.isEmpty()){
            try{
                roomNumber = Integer.parseInt(roomNumberStr);
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
        if(floorStr != null && !floorStr.isEmpty()){
            try{
                floor = Integer.parseInt(floorStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }

        roomService.createRoom(roomNumber, buildingId, floor);
        
    }

}
