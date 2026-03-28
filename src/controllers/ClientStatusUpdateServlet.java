package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.ClientService;

@WebServlet("/admin/client/status/update/*")
public class ClientStatusUpdateServlet extends HttpServlet{

    private ClientService clientService;

    @Override
    public void init() throws ServletException {
        clientService = ClientService.getClientService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String postfixPath = req.getPathInfo();
        Integer clientId = 0;
        if(postfixPath != null){
            String[] pathItems = postfixPath.split("/");
            if(pathItems.length == 2 && !pathItems[1].isEmpty() ){
                try{
                    clientId = Integer.parseInt(pathItems[1]);
                }catch (NumberFormatException e){
                    resp.sendError(404);
                    return;
                }
            }else{
                resp.sendError(404);
                return;
            }
        }else{
            resp.sendError(404);
            return;
        }

        clientService.switchStatus(clientId);        
    }

}
