package webon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract class for generating schemas for classes. The schema of a class is
 * an array {@link Valuator}s. A concrete sub type of Schematizer is referred to
 * as <i>master</i> class, while the class for which schema is generated is
 * called <i>guest</i> class. A pair of master and guest classes uniquely
 * determine a schema.
 */
public abstract class Schematizer {
    protected abstract void make(Class<?> clazz, List<Valuator> valuators);

    private static List<Valuator> valuators = new ArrayList<Valuator>();

    private static Map<String, Valuator[]> schemas = new HashMap<String, Valuator[]>();

    public final synchronized Valuator[] get(Class<?> guest) {
        // Master + guest classes uniquely determine a schema.
        String key = getClass().getName() + ":" + guest.getName();
        Valuator[] schema = schemas.get(key);
        if (schema == null) {
            valuators.clear();
            make(guest, valuators);
            schema = valuators.toArray(new Valuator[valuators.size()]);
            schemas.put(key, schema);
        }
        return schema;
    }

    /**
     * Singleton Schematizer that includes all public fields in schema.
     */
    public static final Schematizer plain = new Schematizer() {
        @Override
        protected void make(Class<?> clazz, List<Valuator> valuators) {
            for (Field f : clazz.getFields()) {
                if (!f.isAccessible())
                    f.setAccessible(true);
                Valuator v = new Valuator(f.getName(), f);
                valuators.add(v);
            }
        }
    };

    /**
     * Singleton Schematizer that only includes {@link Expose}d public members
     * in schema.
     */
    public static final Schematizer exposed = new Schematizer() {
        @Override
        protected void make(Class<?> clazz, List<Valuator> valuators) {
            for (Field f : clazz.getFields()) {
                Expose expose = f.getAnnotation(Expose.class);
                if (expose == null)
                    continue;
                if (!f.isAccessible())
                    f.setAccessible(true);
                valuators.add(new Valuator(f.getName(), f));
            }

            for (Method m : clazz.getMethods()) {
                Expose expose = m.getAnnotation(Expose.class);
                if (expose == null || m.getParameterTypes().length > 0 || m.getReturnType() == void.class
                                || m.getReturnType() == Void.class)
                    continue;
                if (!m.isAccessible())
                    m.setAccessible(true);
                valuators.add(new Valuator(m.getName() + "()", m));
            }
        }
    };

    /**
     * Singleton Schematizer that includes all bean <i>getters</i> in schema.
     * This is typically used to expose existing JavaBeans in your application
     * and third-party libraries.
     */
    public static final Schematizer bean = new Schematizer() {
        @Override
        protected void make(Class<?> clazz, List<Valuator> valuators) {
            for (Method m : clazz.getMethods()) {
                if (m.getParameterTypes().length > 0 || m.getReturnType() == void.class
                                || m.getReturnType() == Void.class)
                    continue;
                String name = m.getName();
                if (!name.startsWith("get") || name.length() <= 3 || name.equals("getClass"))
                    continue;
                if (!m.isAccessible())
                    m.setAccessible(true);
                name = name.substring(3);
                valuators.add(new Valuator(name, m));
            }
        }
    };
}
