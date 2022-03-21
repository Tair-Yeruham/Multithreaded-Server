
import java.util.*;
import java.util.Queue;

/**
 * This class represents BFS method - finds shortest path in some graph
 * This is a generic type
 *
 * @param <T>
 */
public class BFSvisit<T> {
    /**
     * Utility function to check if current vertex is already present in path
     *
     * @param x type: Node<T>
     * @param path type: List<Node<T>>
     * @return boolean
     */
    // Utility function to check if current
// vertex is already present in path
    public boolean isNotVisited(Node<T> x, List<Node<T>> path) {
        for (Node<T> v : path)
            if (path.contains(x))
                return false;
        return true;
    }

    /**
     * Utility function for finding paths in graph from source to destination
     *
     * @param someGraph type:Traversable<T>
     * @param src type:Node<T>
     * @param dst type:Node<T>
     * @return List<ArrayList < Node < T>>>
     */
    // Utility function for finding paths in graph
    // from source to destination
    public List<List<Node<T>>> findpaths(Traversable<T> someGraph, Node<T> src, Node<T> dst) {
        int countMin = Integer.MAX_VALUE;
        Queue<List<Node<T>>> queue = new LinkedList<>();
        List<List<Node<T>>> minPaths = new ArrayList<>();
        // Path vector to store the current path
        ArrayList<Node<T>> path = new ArrayList<>();
        path.add(src);
        queue.offer(path);

        while (!queue.isEmpty()) {
            path = (ArrayList<Node<T>>) queue.poll();
            Node<T> last = path.get(path.size() - 1);
            // If last vertex is the desired destination
            if (last.equals(dst)) {
                if (countMin < path.size()) {
                    break;
                } else {
                    minPaths.add(path);
                    countMin = path.size();
                }
            }

            // Traverse to all the nodes connected to
            // current vertex and push new path to queue
            Collection<Node<T>> lastNode = someGraph.getReachableNodes(last);
            for (Node<T> single : lastNode) {
                if (isNotVisited(single, path)) {
                    ArrayList<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(single);
                    queue.offer(newpath);
                }
            }
        }
        return minPaths;
    }
}

