package webon;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Valuator implements Comparable<Valuator> {
    public final String name;

    private Field field;
    private Method method;

    public Valuator(String name, Field f) {
        this.name = name;
        this.field = f;
    }

    public Valuator(String name, Method m) {
        this.name = name;
        this.method = m;
    }

    public Object of(Object o) {
        try {
            return (field == null) ? method.invoke(o) : field.get(o);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> type() {
        return (field == null) ? method.getReturnType() : field.getType();
    }

    @Override
    public int compareTo(Valuator that) {
        return this.name.compareTo(that.name);
    }
}