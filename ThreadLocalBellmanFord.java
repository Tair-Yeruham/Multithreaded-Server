
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * This class represents BellmanFord method - finds the lightest weight path in some graph
 * This is a generic type
 *
 * @param <T>
 */
public class ThreadLocalBellmanFord<T> implements Serializable {
    /*protected final ThreadLocal<LinkedList<List<Node<T>>>> listThreadLocal =
            ThreadLocal.withInitial(() -> new LinkedList<>());*/

    protected final ThreadLocal<Queue<List<Node<T>>>> queueThreadLocal =
            ThreadLocal.withInitial(() -> new LinkedList<>());

    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());

    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * SumPathWeight: The function summarize a specific path by its nodes
     *
     * @param someGraph type:Traversable<T>
     * @param list type:List<Node<T>>
     * @return int
     */
    public int SumPathWeight(Traversable<T> someGraph, List<Node<T>> list) {
        int sum = 0;
        for (Node<T> node : list) {
            sum = sum + someGraph.getValueN(node.getData());

        }
        return sum;
    }

    /**
     * isNotVisited: The function check if a specific node is exist in path
     * the function returns true if it is exist and false other
     *
     * @param x type:Node<T>
     * @param path type: List<Node<T>>
     * @return boolean
     */
    public boolean isNotVisited(Node<T> x, List<Node<T>> path) {
        for (Node<T> v : path)
            if (path.contains(x))
                return false;

        return true;
    }

    /**
     * findPaths: The function finds paths that start and end like the indexes input(src,dst)
     * by ThreadLocal
     *
     * @param someGraph type: Traversable<T>
     * @param src type: Node<T>
     * @param dst type: Node<T>
     * @return LinkedList<List < Node < T>>>
     */
    public LinkedList<List<Node<T>>> findPaths(Traversable<T> someGraph, Node<T> src, Node<T> dst) {
        List<Node<T>> path = new ArrayList<>();
        LinkedList<List<Node<T>>> listPaths = new LinkedList<>();
        path.add(src);
        queueThreadLocal.get().offer(path);
        while (!queueThreadLocal.get().isEmpty()) {

            path = queueThreadLocal.get().poll();
            Node<T> last = path.get(path.size() - 1);
            // If last vertex is the desired destination
            // then print the path
            if (last.equals(dst)) {
                listPaths.add(path);
            }
            Collection<Node<T>> lastNode = someGraph.getNeighborsNoCross(last);
            for (Node<T> single : lastNode) {
                if (isNotVisited(single, path)) {
                    List<Node<T>> newpath = new ArrayList<>(path);
                    newpath.add(single);
                    queueThreadLocal.get().offer(newpath);
                }

            }
        }
        return listPaths;
    }

    /**
     * findpathsBellmanFord: the function calls to FindPathS method and
     * finds the lightest weight of paths in a parallel way
     * each search wrap in callable
     * There is use in locks
     * There is use in ThreadPool Executor
     *
     * @param someGraph type: Traversable<T>
     * @param src type: Node<T>
     * @param dst type: Node<T>
     * @return LinkedList<List < Node < T>>>
     */
    public LinkedList<List<Node<T>>> findpathsBellmanFord(Traversable<T> someGraph, Node<T> src, Node<T> dst) {
        AtomicInteger sumP = new AtomicInteger();
        AtomicInteger min = new AtomicInteger();
        AtomicInteger minTotal = new AtomicInteger();
        min.set(Integer.MAX_VALUE);
        LinkedList<Future<List<Node<T>>>> futureList = new LinkedList<>();
        LinkedList<List<Node<T>>> listpaths = findPaths(someGraph, src, dst);
        LinkedList<List<Node<T>>> listMinTotalWeight1 = new LinkedList<>();
        LinkedList<List<Node<T>>> listMinTotalWeightfurure = new LinkedList<>();
        for (List<Node<T>> list : listpaths) {
            Callable<List<Node<T>>> callable = () -> {
                readWriteLock.writeLock().lock();
                sumP.set(SumPathWeight(someGraph, list));

                if (sumP.get() <= min.get()) {
                    min.set(sumP.get());
                    minTotal.set(min.get());
                    readWriteLock.writeLock().unlock();
                    if (minTotal.get() < min.get())
                        minTotal.set(min.get());
                    return list;
                } else {
                    sumP.set(0);
                    readWriteLock.writeLock().unlock();
                    return null;

                }

            };
            Future<List<Node<T>>> futurePath = threadPoolExecutor.submit(callable);
            futureList.add(futurePath);
        }

        for (Future<List<Node<T>>> futureP : futureList) {

            try {
                if (futureP.get() != null)
                    listMinTotalWeightfurure.add(futureP.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }


        }

        int sum = 0;
        for (List<Node<T>> listp : listMinTotalWeightfurure) {
            for (Node<T> nodeT : listp) {
                sum = sum + someGraph.getValueN(nodeT.getData());
            }
            if (sum == minTotal.get()) {
                listMinTotalWeight1.add(listp);
            }
            sum = 0;

        }
        this.threadPoolExecutor.shutdown();
        return listMinTotalWeight1;
    }

}





