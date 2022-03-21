import java.io.*;
import java.util.*;

/**
 * This class Implements IHandler interface
 * There is an execute of tasks - (one to four) - the results pass to client
 * and get message from the client
 */
public class MatrixIHandler implements IHandler {
    private Matrix matrix;

    public HashSet<HashSet<Index>> listTask1(int[][] source)
    {

        System.out.println("Server: Got 2d array from client");
        HashSet<HashSet<Index>> listOFHashsets;
        List<Index> listOne;
        Matrix sourceM = new Matrix(source);
        sourceM.printMatrix();
        TraversableMatrix myTraversableM = new TraversableMatrix(sourceM);
        listOne = sourceM.getOne();
        myTraversableM.setStartIndex(myTraversableM.getStartIndex());
        ThreadLocalDfsVisit<Index> algo = new ThreadLocalDfsVisit<>();
        listOFHashsets = algo.parallelDFS(myTraversableM, listOne);
        return listOFHashsets;

    }
    @Override
    public void handle(InputStream fromClient, OutputStream toClient) throws IOException, ClassNotFoundException {
        // ObjectInputStream is a wrapper (decorator) - wraps an InputStream and add functionality
        // treats data as primitives/objects
        ObjectInputStream objectInputStream = new ObjectInputStream(fromClient);
        // ObjectInputStream is a wrapper (decorator) - wraps an OutputStream and add functionality
        // treats data as primitives/objects
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(toClient);

        boolean doWork = true;
        while (doWork) {

            switch (objectInputStream.readObject().toString()) {
                case "Task1": {
                    int[][] tempArray = (int[][]) objectInputStream.readObject();
                    HashSet<HashSet<Index>> listOFHashsets;
                    listOFHashsets=listTask1(tempArray);
                    objectOutputStream.writeObject(listOFHashsets);
                    System.out.println("Task1 finish");
                    break;
                }
                case "Task2": {
                    int[][] tempArray2 = (int[][]) objectInputStream.readObject();
                    System.out.println("Server: Got 2d array from client");
                    Index startIndex, EndIndex;
                    startIndex = (Index) objectInputStream.readObject();
                    System.out.println("From client: start index is: " + startIndex);
                    EndIndex = (Index) objectInputStream.readObject();
                    System.out.println("From client: End index is: " + EndIndex);
                    Matrix mat2 = new Matrix(tempArray2);
                    TraversableMatrix myTraversableM2 = new TraversableMatrix(mat2);
                    myTraversableM2.setStartIndex(startIndex);
                    myTraversableM2.setDestinationIndex(EndIndex);
                    BFSvisit algo2 = new BFSvisit();
                    System.out.println("From Bfs-Task2:");
                    System.out.println("start" + myTraversableM2.getOrigin() + "end " + myTraversableM2.getEnd());
                    List<List<Node<Index>>> arrayLists = algo2.findpaths(myTraversableM2, myTraversableM2.getOrigin(), myTraversableM2.getEnd());
                    System.out.println(arrayLists);
                    objectOutputStream.writeObject(arrayLists);
                    System.out.println("Task2 finish");
                    break;
                }

                case "Task3": {
                    int[][] tempArray = (int[][]) objectInputStream.readObject();
                    HashSet<HashSet<Index>> listOFHashsets;
                    listOFHashsets=listTask1(tempArray);
                    ThreadLocalDfsVisit<Index> algo2 = new ThreadLocalDfsVisit<>();
                    int size = algo2.submarine(listOFHashsets, tempArray);
                    objectOutputStream.writeObject(size);
                    System.out.println("Task3 finish");
                    break;
                }


                case "Task4": {

                    int[][] tempArray = (int[][]) objectInputStream.readObject();
                    Index startIndex, EndIndex;
                    System.out.println("Server: Got 2d array from client");
                    this.matrix = new Matrix(tempArray);
                    this.matrix.printMatrix();
                    startIndex = (Index) objectInputStream.readObject();
                    System.out.println("From client: start index is: " + startIndex);
                    EndIndex = (Index) objectInputStream.readObject();
                    System.out.println("From client: End index is: " + EndIndex);
                    TraversableMatrix myTraversableM4 = new TraversableMatrix(this.matrix);
                    myTraversableM4.setStartIndex(startIndex);
                    myTraversableM4.setDestinationIndex(EndIndex);
                    ThreadLocalBellmanFord B = new ThreadLocalBellmanFord();
                    LinkedList<List<Index>> minWeightList;
                    minWeightList = B.findpathsBellmanFord(myTraversableM4, myTraversableM4.getOrigin(), myTraversableM4.getEnd());
                    System.out.println(minWeightList);
                    objectOutputStream.writeObject(minWeightList);
                    System.out.println("Task4 finish");
                    break;
                }
                case "stop": {
                    doWork = false;
                    break;
                }

            }
        }
    }
}

