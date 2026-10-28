package com.pakrt.pakrt.utils;

import com.pakrt.pakrt.crypto.CryptoJs;
import org.springframework.stereotype.Component;

@Component
public class EncryptionUtil {

    public String encrypt(String rawData) {
        return CryptoJs.EncryptorAES(rawData);
    }

    public String decrypt(String encryptedData) {
        return CryptoJs.DecryptorAES(encryptedData);
    }

    public static String encryptStatic(String rawData) {
        return CryptoJs.EncryptorAES(rawData);
    }

    public static String decryptStatic(String encryptedData) {
        return CryptoJs.DecryptorAES(encryptedData);
    }
}

