import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * This class enables the scan of some object in Thread-Local manner
 * Will we able to divide and conquer the scanned object:
 * Each thread will get a part of the problem and no other thread can change the
 * data
 * <p>
 * TLS - Thread-local Storage
 * Create one single instance of ThreadLocalDfsVisit and it is ensured that each
 * thread will have its own copy of the stack and set
 */

/*
If we have an object to scan and there are several working threads
 */
public class ThreadLocalDfsVisit<T> {
    protected final ThreadLocal<Stack<Node<T>>> stackThreadLocal =
            ThreadLocal.withInitial(() -> new Stack<>());

    protected final ThreadLocal<Set<Node<T>>> setThreadLocal =
            ThreadLocal.withInitial(() -> new HashSet<>());
    public ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(5,
            10, 1000, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    protected ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    /**
     * threadLocalPush: the function push a Node to the Stack
     * there is no return value
     *
     * @param initialNode type:Node<T>
     */
    protected void threadLocalPush(Node<T> initialNode) {
        this.stackThreadLocal.get().push(initialNode);
    }

    /**
     * threadLocalPop: the function returns the stack after pop action
     * there is no input value
     *
     * @return Node<T>
     */
    protected Node<T> threadLocalPop() {
        return this.stackThreadLocal.get().pop();
    }

    /**
     * parallelTraverse: the function calls to traverse in a parallel way and wrap each traverse in callable
     * There is use in locks
     * There is use in ThreadPool Executor
     *
     * @param SomeGraph type:Traversable<T>
     * @param listOfIndex type:List<Index>
     * @return HashSet<HashSet < T>>
     */
    public HashSet<HashSet<T>> parallelDFS(Traversable<T> SomeGraph, List<Index> listOfIndex) {

        HashSet<Future<HashSet<T>>> futureListofScc = new HashSet<>();
        HashSet<HashSet<T>> listIndexScc = new HashSet<>();
        for (int i = 0; i < listOfIndex.size(); i++) {
            int finalI = i;
            Callable<HashSet<T>> Mycallable = () -> {
                readWriteLock.writeLock().lock();
                SomeGraph.SetStartIndex(listOfIndex.get(finalI));
                HashSet<Index> singleSCC = (HashSet<Index>) this.traverse(SomeGraph);
                readWriteLock.writeLock().unlock();
                return (HashSet<T>) singleSCC;
            };
            Future<HashSet<T>> futureSCC = threadPoolExecutor.submit(Mycallable);
            futureListofScc.add(futureSCC);
        }
        for (Future<HashSet<T>> futureScc : futureListofScc) {
            try {
                listIndexScc.add(futureScc.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        this.threadPoolExecutor.shutdown();
        return listIndexScc;
    }

    /**
     * traverse: The function execute DFS method by ThreadLocal
     * returns the shortest path
     *
     * @param someGraph type:Traversable<T>
     * @return HashSet<T>
     */
    public HashSet<T> traverse(Traversable<T> someGraph) {
        setThreadLocal.remove();
        stackThreadLocal.remove();
        threadLocalPush(someGraph.getOrigin());
        while (!stackThreadLocal.get().isEmpty()) {
            Node<T> poppedNode = threadLocalPop();
            setThreadLocal.get().add(poppedNode);
            Collection<Node<T>> reachableNodes = someGraph.getReachableNodes(poppedNode);
            for (Node<T> singleReachableNode : reachableNodes) {
                if (!setThreadLocal.get().contains(singleReachableNode) &&
                        !stackThreadLocal.get().contains(singleReachableNode)) {
                    threadLocalPush(singleReachableNode);
                }
            }
        }
        HashSet<T> blackList = new LinkedHashSet<>();
        for (Node<T> node : setThreadLocal.get()) {
            blackList.add(node.getData());
        }
        return blackList;
    }

    /**
     * submarine: the function count number of valid submarines
     *
     * @param hashSetList type: HashSet<HashSet<Index>>
     * @param primitiveMatrix type: int[][]
     * @return int
     */
    public int submarine(HashSet<HashSet<Index>> hashSetList, int[][] primitiveMatrix) {
        int count = hashSetList.size(), minRow = 10000, minCol = 10000, maxRow = -1, maxCol = -1;
        int flag1 = 0;
        for (HashSet<Index> s : hashSetList) {
            for (Index index : s) {

                if (s.size() == 1) {
                    flag1 = 1;
                }
                if (flag1 == 1)
                    count--;
                flag1 = 0;
                if (index.row <= minRow)
                    minRow = index.row;
                if (index.column <= minCol)
                    minCol = index.column;
                if (index.row > maxCol)
                    maxRow = index.row;
                if (index.column > maxCol)
                    maxCol = index.column;
            }

            for (int i = minRow; i <= maxRow; i++) {
                for (int j = minCol; j <= maxCol; j++) {
                    if (primitiveMatrix[i][j] == 0) {
                        flag1 = 1;
                    }

                }

            }
            if (flag1 == 1)
                count--;
            flag1 = 0;
            minRow = 10000;
            minCol = 10000;
            maxRow = -1;
            maxCol = -1;
        }
        if (count < 0)
            count = 0;
        return count;
    }

}