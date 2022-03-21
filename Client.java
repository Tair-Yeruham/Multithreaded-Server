import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class send tasks and messages to the server
 * There is four tasks - arrange with case number
 * and get updates and results from the server
 */

public class Client {

    /**
     * RandMatrix: The function returns RandMatrix (rand value: 1 or 0)
     * there is no input value
     *
     * @return int[][]
     */
    public static int[][] RandMatrix() {
        Random rand = new Random();
        int row = rand.nextInt(6 - 1) + 1;
        int col = rand.nextInt(6 - 1) + 1;
        int[][] mat = new int[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                mat[i][j] = rand.nextInt(2);
        return mat;
    }

    /**
     * RandMatrixWithWeight: The function returns RandMatrix (rand value - int numbers)
     * there is no input value
     *
     * @return int[][]
     */
    public static int[][] RandMatrixWithWeight() {
        Random rand = new Random();
        int row = rand.nextInt(5 - 1) + 1;
        int col = rand.nextInt(5 - 1) + 1;
        int[][] mat = new int[row][col];
        for (int i = 0; i < row; i++)
            for (int j = 0; j < col; j++)
                mat[i][j] = rand.nextInt(800);
        return mat;
    }

    /**
     * RandMatrixWithLength: The function returns RandMatrix (rand value - 1 or 0)
     * by input length value
     *
     * @param length type:int
     * @return  int[][]
     */
    public static int[][] RandMatrixWithLength(int length) {
        Random rand = new Random();
        int[][] mat = new int[length][length];
        for (int i = 0; i < length; i++)
            for (int j = 0; j < length; j++)
                mat[i][j] = rand.nextInt(2);
        return mat;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner scanner = new Scanner(System.in);
        Socket socket = new Socket("127.0.0.1", 8010);
        System.out.println("client: Socket was created");

        ObjectOutputStream toServer = new ObjectOutputStream(socket.getOutputStream());
        ObjectInputStream fromServer = new ObjectInputStream(socket.getInputStream());

        int[][] source1 = {
                {0, 0, 1},
                {1, 0, 1},
                {1, 0, 0}
        };

        int[][] source4 = {
                {100, 500, 100},
                {300, 300, 100},
                {100, 300, 100}
        };

        boolean flag = false;
        while (!flag) {
            System.out.println("Please enter one of the following: Task1,Task2,Task3,Task4, Stop");
            String result = scanner.next();
            switch (result) {
                case "Task1": {
                    System.out.println("from client: Task1 run:");
                    toServer.writeObject("Task1");
                    int[][] source = RandMatrix();
                    toServer.writeObject(source);
                    HashSet<HashSet<Index>> listOFHashsets =
                            new HashSet<>((HashSet<HashSet<Index>>) fromServer.readObject());
                    List<HashSet<Index>> list = listOFHashsets.stream().sorted(Comparator.comparingInt(HashSet::size))
                            .collect(Collectors.toList());

                    System.out.println("from Server - Reachable Indices are:  " + list);
                    System.out.println("from client: Task1 finish");
                    scanner.nextLine();
                }
                break;

                case "Task2": {
                    System.out.println("from client: Task2 run:");
                    toServer.writeObject("Task2");
                    System.out.println("Please Enter Length Of Matrix: ");
                    int lengthOfMatrix = scanner.nextInt();
                    if (lengthOfMatrix > 50 || lengthOfMatrix < 1) {
                        System.out.println("Length of matrix is illegal");
                        break;
                    }

                    int[][] source = RandMatrixWithLength(lengthOfMatrix);
                    toServer.writeObject(source);
                    Matrix mat2 = new Matrix(source);
                    mat2.printMatrixwithLength(source, lengthOfMatrix);
                    System.out.println("Enter Please Start Index - row and col");
                    int rowM = scanner.nextInt();
                    int colM = scanner.nextInt();
                    if ((rowM > lengthOfMatrix) || (colM > lengthOfMatrix)) {
                        System.out.println("Index is illegal- out of bound");
                        break;
                    }
                    Index startIndex = new Index(rowM, colM);
                    toServer.writeObject(startIndex);
                    System.out.println("Enter Please Destination Index - row and col");
                    int rowD = scanner.nextInt();
                    int colD = scanner.nextInt();
                    if ((rowD > lengthOfMatrix) || (colD > lengthOfMatrix)) {
                        System.out.println("Index is illegal- out of bound");
                        break;
                    }
                    Index DestinationIndex = new Index(rowD, colD);
                    toServer.writeObject(DestinationIndex);
                    List<List<Node<Index>>> paths = new ArrayList<>((List<List<Node<Index>>>) fromServer.readObject());
                    System.out.println("The shortest path are: " + paths);
                    System.out.println("from client: Task2 finish");
                    break;
                }


                case "Task3": {
                    System.out.println("from client: Task3 run:");
                    toServer.writeObject("Task3");
                    int[][] source = RandMatrix();
                    toServer.writeObject(source);
                    int sizeS = (int) fromServer.readObject();
                    System.out.println("from Server - Number of valid submarines is:  " + sizeS);
                    System.out.println("from client: Task3 finish");
                    break;
                }


                case "Task4": {
                    System.out.println("from client: Task4 run:");
                    toServer.writeObject("Task4");
                    int[][] source = RandMatrixWithWeight();
                    toServer.writeObject(source);
                    System.out.println("Enter Please Start Index - row and col");
                    int rowM = scanner.nextInt();
                    int colM = scanner.nextInt();
                    Index startIndex = new Index(rowM, colM);
                    toServer.writeObject(startIndex);
                    System.out.println("Enter Please Destination Index - row and col");
                    rowM = scanner.nextInt();
                    colM = scanner.nextInt();
                    Index DestinationIndex = new Index(rowM, colM);
                    toServer.writeObject(DestinationIndex);
                    LinkedList<List<Index>> minWeightList = new LinkedList<>((LinkedList<List<Index>>) fromServer.readObject());
                    System.out.println("from Server - The easiest routes are: " + minWeightList);
                    toServer.writeObject(minWeightList);
                    System.out.println("from client: Task4 finish");
                    break;
                }


                case "Stop": {
                    flag = true;
                    toServer.writeObject("stop");
                    fromServer.close();
                    toServer.close();
                    socket.close();
                    System.out.println("client: Closed operational socket");
                    break;
                }


            }
        }

    }
}

