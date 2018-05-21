import java.nio.ByteBuffer;

public class ProbeResponse {

    private final byte cpuUsage;
    private final int freeRam;
    private final long timestamp;
    private byte[] hmac;

    public ProbeResponse(byte cpuUsage, int freeRam, long timestamp) {
        this.cpuUsage = cpuUsage;
        this.freeRam = freeRam;
        this.timestamp = timestamp;
        this.hmac = null;
    }

    public ProbeResponse(byte[] response) {
        ByteBuffer buffer = ByteBuffer.wrap(response);

        cpuUsage = buffer.get();
        freeRam = buffer.getInt();
        timestamp = buffer.getLong();
        hmac = new byte[32]; // 32 bytes = 256 bits (from SHA-256 output)
        buffer.get(hmac);
    }

    public byte getCpuUsage() {
        return cpuUsage;
    }

    public int getFreeRam() {
        return freeRam;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getHMAC() {
        return hmac;
    }

    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(13);

        buffer.put(cpuUsage); // 1
        buffer.putInt(freeRam); // 4
        buffer.putLong(timestamp); // 8

        return buffer.array();
    }

    public void addHMAC(byte[] hmac) {
        this.hmac = hmac;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(45);

        buffer.put(cpuUsage); // 1 byte
        buffer.putInt(freeRam); // 4 bytes
        buffer.putLong(timestamp); // 8 bytes
        buffer.put(hmac); // 32 bytes = 256 bits (from SHA-256 output)

        return buffer.array();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ProbeResponse(");
        sb.append("CPU: " + cpuUsage + "%");
        sb.append(", ");
        sb.append("RAM: " + freeRam + " MB");
        sb.append(", ");
        sb.append("Timestamp: " + timestamp);
        sb.append(", ");
        sb.append("HMAC: " + HMAC.toBase64String(hmac));
        sb.append(")");

        return sb.toString();
    }
}
