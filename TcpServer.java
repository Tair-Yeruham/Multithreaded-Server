import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class TcpServer {

    private final int port;
    private volatile boolean stopServer;
    private ThreadPoolExecutor threadPool;
    private IHandler requestHandler;
    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public TcpServer(int port) {
        this.port = port;
        // initialize data members (although they are initialized by default)
        stopServer = false;
        threadPool = null;
        requestHandler = null;
    }

    // listen to incoming connections, accept if possible and handle clients
    public void supportClients(IHandler concreteHandler) {
        this.requestHandler = concreteHandler;

        new Thread(() -> {
            // lazy loading
            threadPool = new ThreadPoolExecutor(3, 5, 10,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>());
            try {
                /*
                 if no port is specified - one will be automatically allocated by OS
                 backlog parameter- number of maximum pending requests
                 ServerSocket constructor - socket creation + bind to a specific port
                 Server Socket API:
                 1. create socket
                 2. bind to a specific port number
                 3. listen for incoming connections (a client initiates a tcp connection with server)
                 4. try to accept (if 3-way handshake is successful)
                 5. return operational socket (2 way pipeline)
                 */
                ServerSocket serverSocket = new ServerSocket(port);
                while (!stopServer) {
                    Socket serverToSpecificClient = serverSocket.accept(); // 2 operations: listen()+accept()
                /*
                 server will handle each client in a separate thread
                 define every client as a Runnable task to execute
                 */
                    Runnable clientHandling = () -> {
                        try {
                            requestHandler.handle(serverToSpecificClient.getInputStream(),
                                    serverToSpecificClient.getOutputStream());
                            // finished handling client. now close all streams
                            serverToSpecificClient.getInputStream().close();
                            serverToSpecificClient.getOutputStream().close();
                            serverToSpecificClient.close();
                        } catch (IOException ioException) {
                            System.err.println(ioException.getMessage());
                        } catch (ClassNotFoundException ce) {
                            System.err.println(ce.getMessage());
                        }
                    };

                    threadPool.execute(clientHandling);
                }
                serverSocket.close();

            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }).start();
    }

    /**
     * stop()- the function Stop the server action.
     * using in double-check locking.
     */
    public void stop() {
        if (!stopServer) {
            try {
                readWriteLock.writeLock().lock();
                if (!stopServer) {
                    if (threadPool != null)
                        threadPool.shutdown();
                }
            } catch (SecurityException se) {
                se.printStackTrace();
            } finally {
                stopServer = true;
                readWriteLock.writeLock().unlock();
            }
        }
    }


    public void jvmInfo() {
        System.out.println(ProcessHandle.current().pid());
        System.out.println(Runtime.getRuntime().maxMemory());
    }

    public static void main(String[] args) {
        TcpServer matrixServer = new TcpServer(8010);
        matrixServer.supportClients(new MatrixIHandler());
        try {
            Thread.sleep(100000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Stopping the server");
        matrixServer.stop();
    }
}