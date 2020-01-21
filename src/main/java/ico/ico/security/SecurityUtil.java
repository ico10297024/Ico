package ico.ico.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityUtil {

    /**
     * 对一组字节数组进行算法加密,通常加密后的数据无法解密
     *
     * @param algorithm 指定使用哪一种算法,通常可以选择的有MD2,MD5,SHA-1,SHA-256,SHA-384,SHA-512
     * @param bytes     需要进行加密的字节数组
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] encrypt(String algorithm, byte[] bytes) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
        return messageDigest.digest(bytes);
    }
}
