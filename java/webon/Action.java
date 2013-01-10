package webon;

/**
 * Interface for defining actions performed on nodes.
 * 
 */
public interface Action {
    /**
     * Perform action on a {@link Leaf}.
     */
    void on(Leaf f);

    /**
     * Perform action on a {@link Branch}.
     */
    void on(Branch b);
}
