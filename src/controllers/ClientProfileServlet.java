package controllers;

import java.io.IOException;

import dtos.ClientDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ClientService;

@WebServlet("/profile")
public class ClientProfileServlet extends HttpServlet {

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        Integer clientId = (Integer) session.getAttribute("clientId");
        ClientDto client = clientService.getClientById(clientId);

        req.setAttribute("client", client);
        req.getRequestDispatcher("/WEB-INF/view/client/profile.jsp").forward(req, resp);;
    }

}
