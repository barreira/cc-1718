import java.nio.ByteBuffer;

public class ProbeRequest {

    private final long timestamp;
    private byte[] hmac;

    public ProbeRequest(long timestamp) {
        this.timestamp = timestamp;
        this.hmac = null;
    }

    public ProbeRequest(byte[] request) {
        ByteBuffer buffer = ByteBuffer.wrap(request);

        timestamp = buffer.getLong();
        hmac = new byte[32];
        buffer.get(hmac);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public byte[] getHMAC() {
        return hmac;
    }

    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        buffer.putLong(timestamp); // 8

        return buffer.array();
    }

    public void addHMAC(byte[] hmac) {
        this.hmac = hmac;
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(40);

        buffer.putLong(timestamp); // 8 bytes
        buffer.put(hmac); // 32 bytes = 256 bits (from SHA-256 output)

        return buffer.array();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("ProbeRequest(");
        sb.append("Timestamp: " + timestamp);
        sb.append(", ");
        sb.append("HMAC: " + HMAC.toBase64String(hmac));
        sb.append(")");

        return sb.toString();
    }
}
