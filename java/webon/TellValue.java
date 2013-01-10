package webon;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Write the value of a {@link Node} in JSON.
 */
public class TellValue implements Action {
    protected JSONWriter json;

    public TellValue(JSONWriter writer) {
        this.json = writer;
    }

    @Override
    public void on(Leaf l) {
        try {
            json.array();
            for (Valuator v : l.schema)
                json.value(v.of(l.obj));
            json.endArray();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void on(Branch branch) {
        try {
            json.array();
            
            json.array();   // for attribute 'branches'
            for (Map.Entry<String, Node> entry : branch.children.entrySet()) {
                String name = entry.getKey();
                Node child = entry.getValue();
                if (child instanceof Branch)
                    json.value(name);
            }
            json.endArray();
            
            json.array();   // for attribute 'leafs'
            for (Map.Entry<String, Node> entry : branch.children.entrySet()) {
                String name = entry.getKey();
                Node child = entry.getValue();
                if (!(child instanceof Branch))
                    json.value(name);
            }
            json.endArray();
            
            json.endArray();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
