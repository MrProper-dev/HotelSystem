package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminService;

@WebServlet("/super/admin/delete/*")
public class SuperDeleteAdminServlet extends HttpServlet{

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = AdminService.getAdminService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer adminId = null;

        if (postfixPath != null) {
            String[] pathItems = postfixPath.split("/");
            if (pathItems.length == 2 && !pathItems[1].isEmpty()) {
                try {
                    adminId = Integer.parseInt(pathItems[1]);
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

        adminService.deleteAdmin(adminId);
    }

}
