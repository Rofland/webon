package webon;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * A node that contains sub-nodes.
 */
public final class Branch extends Node {
    Map<String, Node> children = new TreeMap<String, Node>();

    /**
     * Create a sub-branch with given name. Existing node (if any) is evicted.
     */
    public Branch extend(String name) {
        Branch child = new Branch();
        children.put(name, child);
        return child;
    }

    /**
     * Mount a leaf under this branch.
     */
    public void mount(String name, Leaf l) {
        children.put(name, l);
    }

    @Override
    public void perform(Action act) {
        act.on(this);
    }

    /**
     * Return a sub-node by name or null.
     */
    public Node get(String name) {
        return children.get(name);
    }

    /**
     * Evict a sub-node from the branch.
     */
    public Node evict(String name) {
        return children.remove(name);
    }

    /**
     * Return a list of sub-nodes by names. item i in returned node list
     * corresponds to item i in name list. If certain name is not found, its
     * corresponding node is null.
     * 
     * @param names
     *            Not null.
     * @return List of corresponding nodes. Or empty list if name list is empty.
     */
    public List<Node> get(List<String> names) {
        List<Node> nodes = new ArrayList<Node>(names.size());
        for (String name : names)
            nodes.add(children.get(name));
        return nodes;
    }

    /**
     * Find a descendant node down the given path, starting from this Branch.
     * 
     * @return The descendant or null if path not matched.
     */
    public Node find(List<String> path) {
        Node child = this;
        for (String name : path) {
            if (!(child instanceof Branch))
                return null;
            child = ((Branch) child).children.get(name);
            if (child == null)
                return null;
        }
        return child;
    }

    /**
     * Find a sequence of descendant nodes matching the given path, starting
     * from this Branch.
     * 
     * @return Sequence of nodes or null if path not matched.
     */
    public List<Node> trace(List<String> path) {
        List<Node> nodes = new LinkedList<Node>();
        Node child = this;
        for (String name : path) {
            if (!(child instanceof Branch))
                return null;
            child = ((Branch) child).children.get(name);
            if (child == null)
                return null;
            nodes.add(child);
        }
        return nodes;
    }
}