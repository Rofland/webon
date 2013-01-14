package webon;

import java.io.File;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;


/**
 * Convenient class to start a Jetty-based pure webon service.
 */
public class Jetty {
    private static Server server;
    private static WebAppContext webContext = new WebAppContext("web", "/");
    private static Context dataContext = new Context();

    public static void initWeb(String path, File root) {
        webContext.setContextPath(path);
        webContext.setResourceBase(root.getPath());
    }

    public static void initServlet(String path, Branch root) {
        ServletHolder holder = new ServletHolder(new Servlet(root));
        dataContext.setAllowNullPathInfo(true);
        dataContext.setContextPath(path);
        dataContext.addServlet(holder, "/");
    }

    public static synchronized void start(int port) throws Exception {
        if (server != null)
            return;
        server = new Server(port);
        server.addHandler(dataContext);
        server.addHandler(webContext);
        server.start();
    }

    public static synchronized void stop() throws Exception {
        if (server == null)
            return;
        server.stop();
    }
}
