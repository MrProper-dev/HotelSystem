package security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebFilter("/admin/*")
public class AdminAuthFilter extends HttpFilter{

    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/admin/login", "/admin/login/api/v1"
    );

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String path = req.getServletPath();
        if (isPublicPath(path)) {
            chain.doFilter(req, res);
            return;
        }
        HttpSession session = req.getSession();
        Object adminId = session.getAttribute("adminId");
        if(adminId == null){
            res.sendRedirect("/hotelsystem/admin/login");
            return;
        }else{
            chain.doFilter(req, res);
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(excl -> 
            path.equals(excl) || (excl.endsWith("/*") && path.startsWith(excl.substring(0, excl.length()-2)))
        );
    }

}
