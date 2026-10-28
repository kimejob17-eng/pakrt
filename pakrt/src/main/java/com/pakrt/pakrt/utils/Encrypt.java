package com.pakrt.pakrt.utils;

import com.pakrt.pakrt.crypto.CryptoJs;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class Encrypt implements AttributeConverter<String, String> {
    @Override
    public String convertToDatabaseColumn(String attribute) {
        // Data disimpan dalam format asli (plaintext) di database PostgreSQL
        return attribute;
    }

    @Override
    public String convertToEntityAttribute(String attribute) {
        // Saat data ditarik dari database untuk dikirim ke List Model / DTO, otomatis dienkripsi dengan AES (SHA-256)
        if (attribute == null) return null;
        return CryptoJs.EncryptorAES(attribute);
    }
}
