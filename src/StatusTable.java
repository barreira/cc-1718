import java.net.InetAddress;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class StatusTable {

    private Map<InetAddress, ServerStatus> servers;

    private static long ALPHA = (long) 0.125;
    private static long BETA = (long) 0.25;

    public StatusTable() {
        servers = new HashMap<>();
    }

    public InetAddress chooseServer() {
        return null;
    }

    public synchronized void update(long sentTimestamp, long receivedTimestamp, InetAddress ip, int port, byte cpuUsage,
                                    int freeRAM) {

        long sampleRTT = receivedTimestamp - sentTimestamp;

        ServerStatus status = servers.get(ip);

        if (status == null) { // Ainda não havia nenhum registo do servidor
            long estimatedRTT = sampleRTT;
            long devRTT = sampleRTT / 2;

            status = new ServerStatus(ip, port, cpuUsage, freeRAM, estimatedRTT, devRTT,0);
            servers.put(ip, status);
        }
        else {
            long estimatedRTT = (1 - ALPHA) * status.getRTT() + ALPHA * sampleRTT;
            long devRTT = (1 - BETA) * status.getDevRTT() + BETA * Math.abs(sampleRTT - estimatedRTT);

            status = new ServerStatus(ip, port, cpuUsage, freeRAM, estimatedRTT, devRTT, 0);
            servers.put(ip, status);
        }
    }

    private static class ServerComparator implements Comparator<ServerStatus> {

        @Override
        public int compare(ServerStatus s1, ServerStatus s2) {
            return 0;
        }
    }
}