package security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter(urlPatterns = {""})
public class AdminAuthFilter extends HttpFilter{

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpSession session = req.getSession();
        Object adminId = session.getAttribute("adminId");
        if(adminId == null){
            res.sendRedirect("/hotelsystem/admin/login");
            return;
        }else{
            chain.doFilter(req, res);
        }
    }

}
