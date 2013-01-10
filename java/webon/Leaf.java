package webon;

public class Leaf extends Node {
    public final String type;
    public final Valuator[] schema;
    public final Object obj; // TODO make it a WeakReference

    public Leaf(String type, Valuator[] schema, Object obj) {
        this.type = type;
        this.schema = schema;
        this.obj = obj;
    }

    @Override
    public final void perform(Action act) {
        act.on(this);
    }
}
