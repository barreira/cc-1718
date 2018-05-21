import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public abstract class HMAC {

    public static byte[] generateHMAC(String sharedKey, byte[] data) {
        try {
            Mac hasher = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(sharedKey.getBytes("UTF-8"), "HmacSHA256");
            hasher.init(key);

            return hasher.doFinal(data);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAuthenticated(String sharedKey, byte[] hmac, byte[] data) {
        try {
            Mac hasher = Mac.getInstance("HmacSHA256");
            SecretKeySpec key = new SecretKeySpec(sharedKey.getBytes("UTF-8"), "HmacSHA256");
            hasher.init(key);

            byte[] result = hasher.doFinal(data);

            return Arrays.equals(hmac, result);
        }
        catch (NoSuchAlgorithmException | UnsupportedEncodingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String toBase64String(byte[] hmac) {
        Base64.Encoder encoder = Base64.getEncoder();
        return encoder.encodeToString(hmac);
    }
}
