package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BuildingService;

@WebServlet("/super/building/update/*")
public class UpdateBuildingServlet extends HttpServlet{

    private BuildingService buildingService;

    @Override
    public void init() throws ServletException {
        buildingService = BuildingService.getBuildingService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer buildingId = null;

        if (postfixPath != null) {
            String[] pathItems = postfixPath.split("/");
            if (pathItems.length == 2 && !pathItems[1].isEmpty()) {
                try {
                    buildingId = Integer.parseInt(pathItems[1]);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, 
                        "Invalid booking id format");
                    return;
                }
            } else {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

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

        buildingService.updateBuilding(buildingId, name, address, floors);
    }

}
