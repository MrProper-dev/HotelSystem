package controllers;

import java.io.IOException;

import daos.utils.LoginExistException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminService;

@WebServlet("/super/admin/add")
public class SuperAddAdmin extends HttpServlet{

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = AdminService.getAdminService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");
        String fullname = req.getParameter("fullname");
        String phone = req.getParameter("phone");

        try{
            adminService.createAdmin(login, password, fullname, phone);
        }catch (LoginExistException e){
            resp.sendError(409);
        }
    }

}
