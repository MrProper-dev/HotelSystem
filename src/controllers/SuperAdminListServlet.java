package controllers;

import java.io.IOException;
import java.util.List;

import dtos.AdminDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminService;

@WebServlet("/super/admins")
public class SuperAdminListServlet extends HttpServlet{

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = AdminService.getAdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String fullName = req.getParameter("fullname");
        String phone = req.getParameter("phone");
        Integer page = 0;

        String pageStr = req.getParameter("page");
        if(pageStr != null && !pageStr.isEmpty()){
            try{
                page = Integer.parseInt(pageStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
            }
        }

        List<AdminDto> admins = adminService.getAdminsWithFilters(login, fullName, phone, page);
        Integer adminsCount = adminService.getAdminsCountWithFilters(login, fullName, phone);
        Integer pageCount = adminService.getPageCount(adminsCount);

        req.setAttribute("admins", admins);
        req.setAttribute("adminsCount", adminsCount);
        req.setAttribute("currentPage", page);
        req.setAttribute("pageCount", pageCount);
        req.getRequestDispatcher("/WEB-INF/view/super/manage_admins.jsp").forward(req, resp);
    }

}
