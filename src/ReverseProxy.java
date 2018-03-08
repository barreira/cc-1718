import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ReverseProxy {

    private static final int RP_PORT = 80;

    public static void main(String[] args) throws IOException {

        ServerSocket ss = new ServerSocket(RP_PORT);

        TabelaEstado tabela = new TabelaEstado();

        MonitorUDP monitor = new MonitorUDP(tabela);
        monitor.start();

        while (true) {
            Socket cs = ss.accept();

            tabela.escolherServidor();

            ConnectionThread ct = new ConnectionThread(cs);

            ct.start();
        }
    }
}
