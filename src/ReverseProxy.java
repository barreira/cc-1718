import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReverseProxy {

    private static final int RP_PORT = 80;

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(RP_PORT);

        StatusTable table = new StatusTable();

        UDPMonitor monitor = new UDPMonitor(table);
        monitor.start();

        while (true) {
            Socket clientSocket = ss.accept();

            ServerStatus server = table.chooseServer();

            if (server == null) {
                // informar cliente que não existem servidores e terminar conexão

                System.out.println("ERROR: No available servers");
                break;
            }

            Socket serverSocket = new Socket(server.getIP(), server.getPort());

            ConnectionThread ct = new ConnectionThread(clientSocket, serverSocket);
            ct.start();
        }
    }
}
