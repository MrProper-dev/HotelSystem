package controllers;

import java.io.IOException;
import java.util.List;

import dtos.BuildingDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.BuildingService;

@WebServlet("/super/buildings")
public class SuperBuildingsListServlet extends HttpServlet{

    private BuildingService buildingService;

    @Override
    public void init() throws ServletException {
        buildingService = BuildingService.getBuildingService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pageStr = req.getParameter("page");

        Integer page = 0;

        if(pageStr != null && !pageStr.isEmpty()){
            try{
                page = Integer.parseInt(pageStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
                return;
            }
        }

        List<BuildingDto> buildings = buildingService.getPageBuildings(page);
        Integer buildingsCount = buildingService.getBuildingsCount();
        Integer pageCount = buildingService.getPageCount(buildingsCount);
        

        req.setAttribute("buildings", buildings);
        req.setAttribute("buildingsCount",buildingsCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("pageCount", pageCount);
        req.getRequestDispatcher("/WEB-INF/view/super/menage_buildings.jsp").forward(req, resp);
    }

}
