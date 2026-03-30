package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BuildingService;

@WebServlet("/super/building/add")
public class AddBuildingServlet extends HttpServlet{

    private BuildingService buildingService;

    @Override
    public void init() throws ServletException {
        buildingService = BuildingService.getBuildingService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String address = req.getParameter("address");
        String floorsStr = req.getParameter("floors");

        Integer floors = null;

        if(floorsStr != null && !floorsStr.isEmpty()){
            try{
                floors = Integer.parseInt(floorsStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }

        buildingService.createBuilding(name, address, floors);
    }

}
