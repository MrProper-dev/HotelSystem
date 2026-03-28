package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.ClientService;

@WebServlet("/login")
public class ClientLogInServlet extends HttpServlet{
    
    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/client/log_in.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Integer clientId = clientService.chekPassword(email, password);

        if(clientService.isBlocked(clientId)){
            resp.sendError(401);
            return;
        }

        if(clientId != null){
            clientService.updateClientLogIn(clientId);
            HttpSession session = req.getSession();
            session.setAttribute("clientId", clientId);
            resp.sendRedirect("/hotelsystem/rooms");
        }else{
            resp.sendError(401);
            return;
        }
    }

}
