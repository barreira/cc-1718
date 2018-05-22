import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class StatusTable {

    private Map<InetAddress, ServerStatus> servers;

    public StatusTable() {
        servers = new HashMap<>();
    }

    public synchronized ServerStatus chooseServer() {
        if (servers.isEmpty()) {
            return null;
        }
        else {
            ServerStatus serverStatus = null;
            double score = Double.MAX_VALUE;

            for (ServerStatus ss : servers.values()) {
                double newScore = ss.getCPU() * 100 + ss.getRAM() * 10 + ss.getRTT() * 2;

                if (newScore < score) {
                    score = newScore;
                    serverStatus = ss;
                }
            }

            return serverStatus;
        }
    }

    public synchronized boolean update(long receivedTimestamp, long sentTimestamp, InetAddress ip, int port,
                                       byte cpuUsage, int freeRAM) {
        boolean newer = true;
        long sampleRTT = receivedTimestamp - sentTimestamp;

        ServerStatus status = servers.get(ip);

        if (status == null) { // Ainda não havia nenhum registo do servidor
            status = new ServerStatus(ip, port, sentTimestamp, freeRAM, cpuUsage, sampleRTT, sampleRTT, 1);
        }
        else {
            int newNumReceived = status.getNumReceived() + 1;
            long newTotalRTT = status.getTotalRTT() + sampleRTT;
            long newRTT = newTotalRTT / (long) newNumReceived;

            if (sentTimestamp > status.getTimestamp()) { // caso seja informação mais recente acerca do servidor
                status = new ServerStatus(ip, port, sentTimestamp, freeRAM, cpuUsage, newRTT, newTotalRTT, newNumReceived);
            }
            else { // informação recebida é mais antiga do que a que já existia
                newer = false;

                long oldSentTimestamp = status.getTimestamp();
                int oldFreeRAM = status.getRAM();
                int oldCpuUsage = status.getCPU();

                status = new ServerStatus(ip, port, oldSentTimestamp, oldFreeRAM, oldCpuUsage, newRTT, newTotalRTT,
                                          newNumReceived);
            }
        }

        servers.put(ip, status);

        return newer;
    }

    public synchronized String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("StatusTable(" );
        sb.append("\n");

        for (ServerStatus ss : servers.values()) {
            sb.append("\t");
            sb.append(ss.toString());
            sb.append(";");
            sb.append("\n");
        }

        sb.append(")");

        return sb.toString();
    }
}