package webon;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONWriter;

/**
 * Serve HTTP queries with webon data. Both POST and GET method are accepted.
 * <ul>
 * <li>Query schemas: [URL]?aspect=schema&key=path.0&key=path.1
 * <li>Query states: [URL]?aspect=state&&key=path.0&key=path.1
 * </ul>
 */
public class Servlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final Branch root;

    public Servlet(Branch root) {
        this.root = root;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] paramPaths = req.getParameterValues("key");
        if (paramPaths == null)
            return;
        Node[] nodes = new Node[paramPaths.length];
        for (int i = 0; i < nodes.length; i++) {
            List<String> path = Path.unix.parse(paramPaths[i]);
            nodes[i] = root.find(path);
        }

        String paramAspect = req.getParameter("aspect");
        JSONWriter json = new JSONWriter(resp.getWriter());
        Action action = null;
        if ("schema".equals(paramAspect))
            action = new TellSchema(json);
        if (action == null)
            action = new TellValue(json);

        resp.setContentType("text/json;charset=utf-8");
        try {
            json.array();
            for (Node node : nodes) {
                if (node == null)
                    json.value(null);
                else
                    node.perform(action);
            }
            json.endArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }
}
