package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ClientService;

@WebServlet("/client/update/api/v1/*")
public class ClinetUpdateServlet extends HttpServlet{

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();

        Integer clientId = (Integer) session.getAttribute("clientId");
        String email = req.getParameter("email");
        String password = req.getParameter("password");
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");

        clientService.updateClient(clientId, email, password, phone, name);
    }

}
