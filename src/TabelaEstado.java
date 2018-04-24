import java.net.InetAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class TabelaEstado {

    private Map<InetAddress, ServerStatus> servidores;

    public TabelaEstado() {
        servidores = new HashMap<>();
    }

    public InetAddress escolherServidor() {

    }

    private static class ServerComparator implements Comparator<ServerStatus> {

        @Override
        public int compare(ServerStatus s1, ServerStatus s2) {
            return 0;
        }
    }
}