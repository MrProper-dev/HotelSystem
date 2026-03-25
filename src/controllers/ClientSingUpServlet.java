package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ClientService;

@WebServlet("/singup")
public class ClientSingUpServlet extends HttpServlet{

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/client/sing_up.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        if(clientService.exist(email)){
           resp.sendError(401);
           return; 
        }

        Integer clientId = clientService.createClient(name, phone, email, password);

        HttpSession session = req.getSession();
        session.setAttribute("clientId", clientId);

        resp.sendRedirect("/hotelsystem/rooms");
    }    

}
