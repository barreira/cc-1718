import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ReverseProxy {

    private static final int RP_PORT = 80;
    private static final int BUFFER_SIZE = 4096;

    private static class BackendHandler extends Thread {
        private final Socket clientSocket;
        private final Socket backendSocket;

        private BackendHandler(Socket clientSocket, Socket backendSocket) {
            this.clientSocket = clientSocket;
            this.backendSocket = backendSocket;
        }

        public void run() {
            try {
                int nRead;
                final byte[] buffer = new byte[BUFFER_SIZE];

                InputStream fromBackend = backendSocket.getInputStream();
                OutputStream toClient = clientSocket.getOutputStream();

                while ((nRead = fromBackend.read(buffer)) != -1) {
                    toClient.write(buffer, 0, nRead);
                }
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class ClientHandler extends Thread {

        private Socket clientSocket;
        private ServerStatus backendServer;

        private ClientHandler(Socket clientSocket, ServerStatus backendServer) {
            this.clientSocket = clientSocket;
            this.backendServer = backendServer;
        }

        public void run() {
            try {
                if (backendServer == null) {
                    PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());

                    pw.write("HTTP/1.1 404 Not Found\r\n\r\n");
                    pw.flush();

                    clientSocket.close();
                }
                else {
                    Socket backendSocket = new Socket(backendServer.getIP(), RP_PORT);

                    System.out.println("Client connected to server with address " + backendServer.getIP().getHostName());

                    InputStream fromClient = clientSocket.getInputStream();
                    OutputStream toBackend = backendSocket.getOutputStream();

                    int nRead;
                    byte[] buffer = new byte[BUFFER_SIZE];

                    BackendHandler backendHandler = new BackendHandler(clientSocket, backendSocket);
                    backendHandler.start();

                    while ((nRead = fromClient.read(buffer)) != -1) {
                        toBackend.write(buffer, 0, nRead);
                    }
                    backendHandler.join();

                    clientSocket.close();
                    backendSocket.close();
                }
            }
            catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws IOException {
        StatusTable table = new StatusTable();

        UDPMonitor monitor = new UDPMonitor(table);
        monitor.start();

        ServerSocket serverSocket = new ServerSocket(RP_PORT);
        Socket clientSocket = null;

        while ((clientSocket = serverSocket.accept()) != null) {
            ServerStatus backendServer = table.chooseServer();

            ClientHandler clientHandler = new ClientHandler(clientSocket, backendServer);
            clientHandler.start();
        }
    }
}
