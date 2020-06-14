package com.rawa.cloud.helper;

import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAHelper {
    public static  String PUBLIC_KEY_VALUES = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCIV/RvjK870Fip0Dt7jjMlchjT0UBPLRSBRpUitlrdshu+3NeImXaLHl24j7kZOz7yVtXNuow6VUTENyTYMhAMaqHum7AmR+35kpIXtNNNzd5u0HM/KxanJc3/uQMrTXvauox0qrxEyCQ3mNg7RBdWiuqPXHav5OE2BtjMZIR4uwIDAQAB";
    public static  String PRIVATE_KEY_VALUES = "";
    /**
     * RSA密钥长度必须是64的倍数，在512~65536之间。默认是1024
     */
    public static final int KEY_SIZE = 1024;


    public static void getKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(keySize);
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            // 得到私钥
            RSAPrivateKey oraprivateKey = (RSAPrivateKey) keyPair.getPrivate();
            // 得到公钥
            RSAPublicKey orapublicKey = (RSAPublicKey) keyPair.getPublic();

            //公钥
            byte[] publicKeybyte = orapublicKey.getEncoded();
            String publicKeyString = Base64Utils.encodeToString(publicKeybyte);
            PUBLIC_KEY_VALUES = publicKeyString;
            //私钥
            byte[] privateKeybyte = oraprivateKey.getEncoded();
            String privateKeyString = Base64Utils.encodeToString(privateKeybyte);
            PRIVATE_KEY_VALUES = privateKeyString;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取公钥对象
     *
     */
    public static PublicKey getPublicKey(String publicKeyBase64)
            throws Exception {

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicpkcs8KeySpec =
                new X509EncodedKeySpec(Base64Utils.decodeFromString(publicKeyBase64));
        PublicKey publicKey = keyFactory.generatePublic(publicpkcs8KeySpec);
        return publicKey;
    }

    /**
     * 获取私钥对象
     *
     * @param privateKeyBase64
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    public static PrivateKey getPrivateKey(String privateKeyBase64) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privatekcs8KeySpec =
                new PKCS8EncodedKeySpec(Base64Utils.decodeFromString(privateKeyBase64));
        PrivateKey privateKey = keyFactory.generatePrivate(privatekcs8KeySpec);
        return privateKey;
    }

    /**
     * 使用工钥加密
     */
    public static String encipher(String content, String keyBase64, boolean crazyMode) {
        return encipher(content, keyBase64, KEY_SIZE / 8 - 11, crazyMode);
    }

    /**
     * 使用公司钥加密（分段加密）
     *
     */
    public static String encipher(String content, String keyBase64, int segmentSize, boolean crazyMode) {
        try {
            java.security.Key key = crazyMode ? getPrivateKey(keyBase64) : getPublicKey(keyBase64);
            return encipher(content, key, segmentSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分段加密
     *
     * @param ciphertext  密文
     * @param key         加密秘钥
     * @param segmentSize 分段大小，<=0 不分段
     * @return
     */
    public static String encipher(String ciphertext, java.security.Key key, int segmentSize) {
        try {
            // 用公钥加密
            byte[] srcBytes = ciphertext.getBytes();

            // Cipher负责完成加密或解密工作，基于RSA
            Cipher cipher = Cipher.getInstance("RSA");
            // 根据公钥，对Cipher对象进行初始化
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] resultBytes = null;

            if (segmentSize > 0)
                resultBytes = cipherDoFinal(cipher, srcBytes, segmentSize); //分段加密
            else
                resultBytes = cipher.doFinal(srcBytes);
            String base64Str = Base64Utils.encodeToString(resultBytes);
            return base64Str;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分段大小
     *
     * @param cipher
     * @param srcBytes
     * @param segmentSize
     */
    public static byte[] cipherDoFinal(Cipher cipher, byte[] srcBytes, int segmentSize)
            throws Exception {
        if (segmentSize <= 0)
            throw new RuntimeException("分段大小必须大于0");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int inputLen = srcBytes.length;
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > segmentSize) {
                cache = cipher.doFinal(srcBytes, offSet, segmentSize);
            } else {
                cache = cipher.doFinal(srcBytes, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * segmentSize;
        }
        byte[] data = out.toByteArray();
        out.close();
        return data;
    }

    /**
     * 使用私钥解密
     *
     */
    public static String decipher(String contentBase64, String keyBase64,  boolean crazyMode) {
        return decipher(contentBase64, keyBase64, KEY_SIZE / 8, crazyMode);
    }

    /**
     * 使用私钥解密（分段解密）
     */
    public static String decipher(String contentBase64, String keyBase64, int segmentSize, boolean crazyMode) {
        try {
            java.security.Key key = crazyMode ? getPublicKey(keyBase64) : getPrivateKey(keyBase64);
            return decipher(contentBase64, key, segmentSize);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 分段解密
     *
     * @param contentBase64 密文
     * @param key           解密秘钥
     * @param segmentSize   分段大小（小于等于0不分段）
     * @return
     */
    public static String decipher(String contentBase64, java.security.Key key, int segmentSize) {
        try {
            // 用私钥解密
            byte[] srcBytes = Base64Utils.decodeFromString(contentBase64);
            // Cipher负责完成加密或解密工作，基于RSA
            Cipher deCipher = Cipher.getInstance("RSA");
            // 根据公钥，对Cipher对象进行初始化
            deCipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decBytes = null;//deCipher.doFinal(srcBytes);
            if (segmentSize > 0)
                decBytes = cipherDoFinal(deCipher, srcBytes, segmentSize); //分段加密
            else
                decBytes = deCipher.doFinal(srcBytes);

            String decrytStr = new String(decBytes);
            return decrytStr;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String text = "{\n" +
                "\"mac\": \"f4:0f:24:37:5c:1b\",\n" +
                "\"duration\": 0,\n" +
                "\"limitUser\": 10,\n" +
                "\"free\": false\n" +
                "}";

//        getKeyPair(KEY_SIZE);

        System.out.println("Public Key: \n" + PUBLIC_KEY_VALUES);
        System.out.println("Private Key: \n" + PRIVATE_KEY_VALUES);

        try {
            String encodeText = encipher(text, PRIVATE_KEY_VALUES, true);
            System.out.println("Encode Text: \n" + encodeText);

            System.out.println("Decode Text: \n" + decipher(encodeText, PUBLIC_KEY_VALUES, true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
