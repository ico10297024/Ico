package ico.ico.security;


import android.annotation.SuppressLint;
import android.util.Base64;

import com.nostra13.universalimageloader.utils.L;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import ico.ico.helper.StringHelper;
import ico.ico.util.SafeCloseUtil;

/**
 * RSA常用
 *
 * @author andang.ye
 */
@SuppressWarnings("restriction")

public class RSAUtil {

    /**
     * 加解密算法
     */
    private static final String ENCRYPT_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    private static final String SIGN_ALGORITHM = "MD5withRSA";
    private static final String TAG = "RSAUtil";

    /**
     * PKCS8文件初始化为PrivateKey.
     *
     * @param keyPath
     * @return
     * @throws Exception
     */
    @SuppressWarnings("resource")
    public static PrivateKey initPrivateKey(String keyPath) throws Exception {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(keyPath));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '-') {
                    continue;
                }
                stringBuilder.append(line).append("\r");
            }

            return initPrivateKeyByContent(stringBuilder.toString().getBytes());
        } finally {
            SafeCloseUtil.close(bufferedReader);
        }
    }

    /**
     * initPrivateKeyByContent
     *
     * @param keyContent
     * @return
     * @throws Exception
     */
    @SuppressLint("RestrictedApi")
    public static PrivateKey initPrivateKeyByContent(byte[] keyContent) throws Exception {
        byte[] keyByte = Base64.decode(keyContent, Base64.DEFAULT);

        KeyFactory kf = KeyFactory.getInstance(ENCRYPT_ALGORITHM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
        return kf.generatePrivate(keySpec);
    }

    /**
     * 公钥初始化
     *
     * @param keyPath
     * @return
     */
    @SuppressWarnings("resource")
    public static PublicKey initPublicKey(String keyPath) throws Exception {

        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(keyPath));
            StringBuilder stringBuilder = new StringBuilder();

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.length() == 0 || line.charAt(0) == '-') {
                    continue;
                }
                stringBuilder.append(line).append("\r");
            }

            return initPublicKeyByContent(stringBuilder.toString().getBytes());
        } finally {
            SafeCloseUtil.close(bufferedReader);
        }
    }

    /**
     * 公钥初始化
     *
     * @param keyContent 证书内容
     * @return 公钥
     * @throws Exception
     */
    @SuppressLint("RestrictedApi")
    public static PublicKey initPublicKeyByContent(byte[] keyContent) throws Exception {
        byte[] keyByte = Base64.decode(keyContent, Base64.DEFAULT);

        KeyFactory kf = KeyFactory.getInstance(ENCRYPT_ALGORITHM);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        return kf.generatePublic(keySpec);
    }

    /**
     * 签名算法: MD5withRSA
     *
     * @param content
     * @param privateKey
     * @return
     * @throws Exception 签名异常直接抛出
     */
    public static String signWithMD5(String content, String charset, PrivateKey privateKey) throws Exception {
        Signature signature = Signature.getInstance(SIGN_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(content.getBytes(charset));

        byte[] signByte = signature.sign();

        return Base64.encodeToString(signByte, Base64.DEFAULT);
    }

    /**
     * 验签:MD5whtiRSA ,异常吞掉,返回验签失败
     *
     * @param content
     * @param sign
     * @param charset
     * @param publicKey
     * @return
     */
    public static boolean verifyWithMD5(String content, String sign, String charset, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHM);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(charset));

            byte[] keyByte = Base64.decode(sign, Base64.DEFAULT);

            return signature.verify(keyByte);
        } catch (Exception e) {
            L.e("验签出现异常", TAG);
            return false;
        }
    }

    /**
     * 加密
     *
     * @param content 待加密内容
     * @param charset 字符集
     * @param key     密钥
     * @return
     * @throws Exception
     */
    @SuppressLint("RestrictedApi")
    public static String encrypt(String content, String charset, Key key) throws Exception {
        if (StringHelper.isBlank(content)) {
            throw new NullPointerException("待加密内容不能为空");
        }
        if (key instanceof RSAKey) {
            throw new IllegalArgumentException("key类型必须为RSAKey");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] data = content.getBytes(charset);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        int maxEncryptBlock = calcMaxEncryptBlock((RSAKey) key);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxEncryptBlock) {
                cache = cipher.doFinal(data, offSet, maxEncryptBlock);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxEncryptBlock;
        }

        return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT);
    }

    /**
     * 解密
     *
     * @param encryptedContent 待解密内容
     * @param charset          字符集
     * @param key              密钥
     * @return
     * @throws Exception
     */
    @SuppressLint("RestrictedApi")
    public static String decrypt(String encryptedContent, String charset, Key key) throws Exception {
        if (StringHelper.isBlank(encryptedContent)) {
            throw new NullPointerException("待解密内容不能为空");
        }
        if (key instanceof RSAKey) {
            throw new IllegalArgumentException("key类型必须为RSAKey");
        }
        Cipher cipher = Cipher.getInstance(ENCRYPT_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, key);

        byte[] encryptedData = Base64.decode(encryptedContent, Base64.DEFAULT);

        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        int maxDecryptBlock = calcMaxDecryptBlock((RSAKey) key);
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > maxDecryptBlock) {
                cache = cipher.doFinal(encryptedData, offSet, maxDecryptBlock);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * maxDecryptBlock;
        }

        return new String(out.toByteArray(), charset);
    }

    // 计算加密分块最大长度
    private static int calcMaxEncryptBlock(RSAKey key) {
        return key.getModulus().bitLength() / 8 - 11;
    }

    // 计算解密分块最大长度
    private static int calcMaxDecryptBlock(RSAKey key) {
        return key.getModulus().bitLength() / 8;
    }
}
