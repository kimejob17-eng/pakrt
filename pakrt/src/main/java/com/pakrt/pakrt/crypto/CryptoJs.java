package com.pakrt.pakrt.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;

@Component
public class CryptoJs {
    private static String SECRET_KEY = "L4p0rP4kR7";
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    @Value("${app.security.secret-key:L4p0rP4kR7}")
    public void setSecretKey(String secretKey) {
        CryptoJs.SECRET_KEY = secretKey;
    }

    public static String EncryptorAES(String str) {
        if (str == null) return null;
        try {
            byte[] keyAndIv = generateKeyAndIV(SECRET_KEY, 32, 16);
            byte[] key = Arrays.copyOfRange(keyAndIv, 0, 32);
            byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

            byte[] encrypted = cipher.doFinal(str.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mengenkripsi muatan data CryptoJS", e);
        }
    }

    public static String DecryptorAES(String str) {
        if (str == null) return null;
        try {
            byte[] keyAndIv = generateKeyAndIV(SECRET_KEY, 32, 16);
            byte[] key = Arrays.copyOfRange(keyAndIv, 0, 32);
            byte[] iv = Arrays.copyOfRange(keyAndIv, 32, 48);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decoded = Base64.getDecoder().decode(str);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Gagal mendekripsi muatan data CryptoJS", e);
        }
    }

    /**
     * Menghasilkan Key (32 bytes / 256 bits) dan IV (16 bytes / 128 bits)
     * Menggunakan SHA-256 sebagai pengganti MD5 untuk peningkatan standar keamanan.
     */
    private static byte[] generateKeyAndIV(String password, int keyLength, int ivLength) throws Exception {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] result = new byte[keyLength + ivLength];
        byte[] currentHash = new byte[0];
        int totalBytesWritten = 0;

        while (totalBytesWritten < keyLength + ivLength) {
            sha256.reset();
            sha256.update(currentHash);
            sha256.update(passwordBytes);
            currentHash = sha256.digest();

            int bytesToCopy = Math.min(currentHash.length, (keyLength + ivLength) - totalBytesWritten);
            System.arraycopy(currentHash, 0, result, totalBytesWritten, bytesToCopy);
            totalBytesWritten += bytesToCopy;
        }
        return result;
    }
}


