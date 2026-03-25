package controllers;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ClientService;

@WebServlet("/login/api/v1")
public class ClinetCheckPasswordServlet extends HttpServlet{

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Integer clientId = clientService.chekPassword(email, password);
        Boolean wrongPassword;

        if(clientId != null) {
            wrongPassword = false;
        }else{
            wrongPassword = true;
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String json = "{\"wrongPassword\" : \"%b\"}".formatted(wrongPassword);
        PrintWriter printWriter = resp.getWriter();
        printWriter.print(json);
        printWriter.flush();
    }

}
