package starting;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppConfig implements ServletContextListener{

    public static final Properties appProperties = new Properties();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        String servletRealPath = context.getRealPath("/WEB-INF/config/");
        String fileName = "app.properties";
        try{
            InputStream stream = new FileInputStream(servletRealPath + fileName);
            appProperties.load(stream);
        }catch (IOException e){
            throw new RuntimeException("Reading properties file failed", e);
        }
    }

}
