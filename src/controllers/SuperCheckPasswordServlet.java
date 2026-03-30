package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.SuperService;

@WebServlet("/super/login/api/v1")
public class SuperCheckPasswordServlet extends HttpServlet{

    private SuperService superService;

    @Override
    public void init() throws ServletException {
        superService = SuperService.getSuperService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String login = req.getParameter("login");
        String password = req.getParameter("password");

        Integer adminId = superService.chekPassword(login, password);
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
