import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

public class ProbeRequest {

    private final long timestamp;
    private final String hmac;

    public ProbeRequest(long timestamp, String hmac) {
        this.timestamp = timestamp;
        this.hmac = hmac;
        
    }

    public ProbeRequest(byte[] response) {
        try {
            ByteBuffer buffer = ByteBuffer.wrap(response);

            timestamp = buffer.getLong();

            byte[] aux = new byte[buffer.remaining()];
            buffer.get(aux);
            hmac = new String(aux, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getHMAC() {
        return hmac;
    }

    public byte[] getData() {
        ByteBuffer buffer = ByteBuffer.allocate(8);

        buffer.putLong(timestamp); // 8

        return buffer.array();
    }

    public byte[] toByteArray() {
        ByteBuffer buffer = ByteBuffer.allocate(40);

        buffer.putLong(timestamp); // 8
        buffer.put(hmac.getBytes()); // 32

        return buffer.array();
    }
}
