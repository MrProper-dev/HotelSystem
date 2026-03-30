package controllers;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.SuperService;

@WebServlet("/super/login")
public class SuperLogintServlet extends HttpServlet{

    private SuperService superService;

    @Override
    public void init() throws ServletException {
        superService = SuperService.getSuperService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/view/super/log_in.html").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String email = req.getParameter("login");
        String password = req.getParameter("password");

        Integer superId = superService.chekPassword(email, password);

        if(superId != null){
            superService.updateSuperLasttLogIn(superId);
            HttpSession session = req.getSession();
            session.setAttribute("superId", superId);
            resp.sendRedirect("/hotelsystem/super/rooms");
        }else{
            resp.sendError(401);
            return;
        }
    }

}
