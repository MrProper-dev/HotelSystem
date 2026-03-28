package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.AdminService;

@WebServlet("/admin/login")
public class AdminLogInServlet extends HttpServlet{
    
    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = AdminService.getAdminService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/admin/log_in.html").forward(req, resp);;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        Integer adminId = adminService.getAdmintId(login, password);

        if(adminId != null){
            adminService.updateAdminLastLogin(adminId);
            HttpSession session = req.getSession();
            session.setAttribute("adminId", adminId);
            resp.sendRedirect("/hotelsystem/admin/rooms");
        }else{
            resp.sendError(401);
            return;
        }
    }

}
