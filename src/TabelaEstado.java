import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class TabelaEstado {

    private Map<InetAddress, ServerStatus> servidores;

    public TabelaEstado() {
        servidores = new HashMap<>();
    }

    public InetAddress escolherServidor() {

    }
}