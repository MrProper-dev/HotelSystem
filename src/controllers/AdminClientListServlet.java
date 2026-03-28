package controllers;

import java.io.IOException;
import java.util.List;

import dtos.ClientDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ClientService;

@WebServlet("/admin/clients")
public class AdminClientListServlet extends HttpServlet{

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String name = req.getParameter("name");
        String phone = req.getParameter("phone");
        String email = req.getParameter("email");
        String pageStr = req.getParameter("page");
        Integer page = 0;
        if(pageStr != null && !pageStr.isEmpty()){
            try{
                page = Integer.parseInt(pageStr);
            }catch (NumberFormatException e){
                resp.sendError(400);
            }
        }
        
        List<ClientDto> clients = clientService.getClientsWithFilters(name, phone, email, page);
        Integer clientsCount = clientService.getClientsCountWithFilters(name, phone, email);
        Integer pageCount = clientService.getPagesCount(clientsCount);

        req.setAttribute("clients", clients);
        req.setAttribute("clientsCount", clientsCount);
        req.setAttribute("pageCount", pageCount);
        req.setAttribute("currentPage", page);
        req.getRequestDispatcher("/WEB-INF/view/admin/list_of_clients.jsp").forward(req, resp);
    }

}
