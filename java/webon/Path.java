package webon;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Any list of String can be considered as a hierarchical path. This is
 * enclosing class of path-related utilities.
 */
public class Path {
    /**
     * Prevent from creating Path instance.
     */
    private Path() {
    }

    /**
     * A comparator that compares two paths by their lexical order.
     */
    public static final Comparator<List<String>> lexical = new Comparator<List<String>>() {

        @Override
        public int compare(List<String> path1, List<String> path2) {
            Iterator<String> itr1 = path1.iterator();
            Iterator<String> itr2 = path2.iterator();
            while (itr1.hasNext() && itr2.hasNext()) {
                String name1 = itr1.next();
                String name2 = itr2.next();
                int result = name1.compareTo(name2);
                if (result != 0)
                    return result;
            }
            if (itr1.hasNext())
                return 1;
            else if (itr2.hasNext())
                return -1;
            return 0;
        }
    };

    /**
     * Get the ending name of the path.
     */
    public static String name(List<String> path) {
        if (path.isEmpty())
            return "";
        if (path instanceof LinkedList)
            return ((LinkedList<String>) path).getLast();
        return path.get(path.size() - 1);
    }

    /**
     * Retaining all but the ending name (a.k.a., name(path))
     */
    public static void stem(List<String> path) {
        if (path.isEmpty())
            return;
        if (path instanceof LinkedList)
            ((LinkedList<String>) path).removeLast();
        else
            path.remove(path.size() - 1);
    }

    /**
     * Abstract class for conversion between path and raw String.
     */
    public static abstract class Converter {
        public abstract List<String> parse(String s);

        public abstract String format(List<String> path);

    }

    /**
     * Singleton object for parsing and formatting Unix path.
     */
    public static final Converter unix = new Converter() {
        @Override
        public List<String> parse(String s) {
            List<String> path = new LinkedList<String>();

            int head = 0;
            while (head < s.length() && s.charAt(head) == '/')
                head++; // Move head to the beginning of first name
            if (head == s.length())
                return path;

            int tail = head + 1;
            while (tail < s.length()) {
                if (s.charAt(tail) != '/') {
                    tail++;
                    continue;
                }

                /*
                 * Reach the end segment name. Should position head at the
                 * beginning of the next segment name.
                 */
                path.add(s.substring(head, tail));
                head = tail;
                while (head < s.length() && s.charAt(head) == '/')
                    head++;
                if (head == s.length())
                    return path;
                tail = head + 1;
            }
            path.add(s.substring(head, tail));
            return path;
        }

        @Override
        public String format(List<String> path) {
            if (path.isEmpty())
                return "/";
            StringBuilder sb = new StringBuilder();
            for (String segment : path)
                sb.append('/').append(segment);
            return sb.toString();
        }
    };
}
