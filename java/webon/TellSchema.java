package webon;

import org.json.JSONException;
import org.json.JSONWriter;

/**
 * Write the schema of a {@link Node} in JSON.
 */
public class TellSchema implements Action {
    protected JSONWriter json;

    public TellSchema(JSONWriter writer) {
        this.json = writer;
    }

    @Override
    public void on(Leaf l) {
        try {
            json.array();
            for (Valuator v : l.schema)
                json.value(v.name);
            json.value(l.type);
            json.endArray();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void on(Branch b) {
        try {
            json.array();
            json.value("branches");
            json.value("leaves");
            json.value(Branch.class.getName());
            json.endArray();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
