package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.AdminService;

// TODO: добавить задачу, которая будет обновлять статус у броней
// TODO: доделать старницу (отправка запроса на изменение и список последних бронирований)

@WebServlet("/admin/login/api/v1")
public class AdminCheckPasswodServlet extends HttpServlet{

    private AdminService adminService;

    @Override
    public void init() throws ServletException {
        adminService = AdminService.getAdminService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        Integer adminId = adminService.getAdmintId(login, password);
        Boolean isValid;

        if(adminId != null){
            isValid = true;
        }else{
            isValid = false;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = "{\"isValid\" : \"%b\"}".formatted(isValid);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);
        printWriter.flush();
    }

}
