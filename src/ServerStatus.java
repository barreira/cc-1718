import java.net.InetAddress;

public class ServerStatus {

    private InetAddress ip;
    private int port;
    private long timestamp;
    private int ram;
    private int cpu;
    private long rtt;
    private long totalRTT;
    private int numReceived;
    private float bandwidth;

    public ServerStatus(InetAddress ip, int port, long timestamp, int ram, int cpu, long rtt, long totalRTT,
                        int numReceived, float bandwidth) {
        this.ip = ip;
        this.port = port;
        this.timestamp = timestamp;
        this.ram = ram;
        this.cpu = cpu;
        this.rtt = rtt;
        this.totalRTT = totalRTT;
        this.numReceived = numReceived;
        this.bandwidth = bandwidth;
    }

    public InetAddress getIP() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getRAM() {
        return ram;
    }

    public int getCPU() {
        return cpu;
    }

    public long getRTT() {
        return rtt;
    }

    public long getTotalRTT() {
        return totalRTT;
    }

    public int getNumReceived() {
        return numReceived;
    }

    public float getBandwidth() {
        return bandwidth;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("IP: " + ip.getHostAddress() + ":" + port);
        sb.append(", ");
        sb.append("SentTimestamp: " + timestamp);
        sb.append(", ");
        sb.append("FreeRAM: " + ram + "MB");
        sb.append(", ");
        sb.append("CPU: " + cpu + "%");
        sb.append(", ");
        sb.append("RTT: " + rtt + "ms");
        sb.append(", ");
//        sb.append("TotalRTT: " + totalRTT);
//        sb.append(", ");
//        sb.append("NumReceived: " + numReceived);
//        sb.append(", ");
        sb.append("Bandwidth: " + bandwidth);

        return sb.toString();
    }
}
