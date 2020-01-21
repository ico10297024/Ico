package ico.ico.security;

import android.util.Base64;

public class BASE64Util {

    /**
     * 使用Base64进行加密
     * <p>
     * 从现在加密算法的复杂性来看Base64这种都不好意思说自己是加密，不过对于完全不懂计算机的人来说也够用了。采用Base64编码具有不可读性，即所编码的数据不会被人用肉眼所直接看到。
     * Base64编码一般用于url的处理，或者说任何你不想让普通人一眼就知道是啥的东西都可以用Base64编码处理后再发布在网络上。F
     * <p>
     * Base64算法基于64个基本字符，加密后的string中只包含这64个字符
     *
     * @param src
     * @return
     */
    public static String encode(String src) {
        byte[] encodeBytes = Base64.encode(src.getBytes(), Base64.DEFAULT);
        return new String(encodeBytes);
    }

    public static String decode(String src) {
        byte[] decodeBytes = Base64.decode(src, Base64.DEFAULT);
        return new String(decodeBytes);
    }
}
