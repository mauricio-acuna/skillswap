package com.skillswap.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Service for encrypting and decrypting sensitive data at rest
 * Uses AES-256-GCM for authenticated encryption
 */
@Service
public class DataEncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;
    
    @Value("${skillswap.encryption.key:#{null}}")
    private String encryptionKey;
    
    private final SecureRandom secureRandom = new SecureRandom();
    
    /**
     * Encrypt sensitive data
     */
    public String encrypt(String plainText) throws Exception {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        
        SecretKey secretKey = getOrGenerateKey();
        
        // Generate random IV
        byte[] iv = new byte[GCM_IV_LENGTH];
        secureRandom.nextBytes(iv);
        
        // Initialize cipher
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);
        
        // Encrypt the data
        byte[] encryptedData = cipher.doFinal(plainText.getBytes("UTF-8"));
        
        // Combine IV and encrypted data
        byte[] encryptedWithIv = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, encryptedWithIv, 0, iv.length);
        System.arraycopy(encryptedData, 0, encryptedWithIv, iv.length, encryptedData.length);
        
        // Return Base64 encoded result
        return Base64.getEncoder().encodeToString(encryptedWithIv);
    }
    
    /**
     * Decrypt sensitive data
     */
    public String decrypt(String encryptedText) throws Exception {
        if (encryptedText == null || encryptedText.isEmpty()) {
            return encryptedText;
        }
        
        SecretKey secretKey = getOrGenerateKey();
        
        // Decode from Base64
        byte[] encryptedWithIv = Base64.getDecoder().decode(encryptedText);
        
        // Extract IV and encrypted data
        byte[] iv = new byte[GCM_IV_LENGTH];
        byte[] encryptedData = new byte[encryptedWithIv.length - GCM_IV_LENGTH];
        System.arraycopy(encryptedWithIv, 0, iv, 0, iv.length);
        System.arraycopy(encryptedWithIv, iv.length, encryptedData, 0, encryptedData.length);
        
        // Initialize cipher for decryption
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, parameterSpec);
        
        // Decrypt the data
        byte[] plainTextData = cipher.doFinal(encryptedData);
        
        return new String(plainTextData, "UTF-8");
    }
    
    /**
     * Encrypt personal identifiable information (PII)
     */
    public String encryptPII(String piiData) throws Exception {
        if (piiData == null || piiData.trim().isEmpty()) {
            return piiData;
        }
        
        // Additional validation for PII
        if (piiData.length() > 1000) {
            throw new IllegalArgumentException("PII data too long for encryption");
        }
        
        return encrypt(piiData.trim());
    }
    
    /**
     * Decrypt personal identifiable information (PII)
     */
    public String decryptPII(String encryptedPII) throws Exception {
        return decrypt(encryptedPII);
    }
    
    /**
     * Hash sensitive data for comparison purposes (one-way)
     */
    public String hashSensitiveData(String data) throws Exception {
        if (data == null || data.isEmpty()) {
            return data;
        }
        
        // Use encryption key as salt for consistency
        SecretKey key = getOrGenerateKey();
        byte[] salt = key.getEncoded();
        
        // Use PBKDF2 for key derivation
        java.security.spec.KeySpec spec = new javax.crypto.spec.PBEKeySpec(
            data.toCharArray(), salt, 100000, 256);
        javax.crypto.SecretKeyFactory factory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        
        return Base64.getEncoder().encodeToString(hash);
    }
    
    /**
     * Check if data matches hash
     */
    public boolean verifyHash(String data, String hash) {
        try {
            String computedHash = hashSensitiveData(data);
            return computedHash.equals(hash);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Generate a new encryption key
     */
    public String generateNewKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        return Base64.getEncoder().encodeToString(secretKey.getEncoded());
    }
    
    /**
     * Get or generate encryption key
     */
    private SecretKey getOrGenerateKey() throws Exception {
        if (encryptionKey != null && !encryptionKey.isEmpty()) {
            byte[] decodedKey = Base64.getDecoder().decode(encryptionKey);
            return new SecretKeySpec(decodedKey, ALGORITHM);
        } else {
            // Generate a default key (NOT recommended for production)
            String defaultKey = "skillswap-default-key-not-for-production-use-change-me";
            byte[] keyBytes = java.security.MessageDigest.getInstance("SHA-256")
                .digest(defaultKey.getBytes("UTF-8"));
            return new SecretKeySpec(keyBytes, ALGORITHM);
        }
    }
    
    /**
     * Validate encryption configuration
     */
    public boolean isEncryptionConfigured() {
        return encryptionKey != null && !encryptionKey.isEmpty() 
               && !encryptionKey.contains("default") && !encryptionKey.contains("change-me");
    }
    
    /**
     * Mask sensitive data for logging
     */
    public String maskForLogging(String sensitiveData) {
        if (sensitiveData == null || sensitiveData.length() <= 4) {
            return "****";
        }
        return sensitiveData.substring(0, 2) + "****" + sensitiveData.substring(sensitiveData.length() - 2);
    }
    
    /**
     * Secure data wipe (overwrite memory)
     */
    public void secureWipe(byte[] data) {
        if (data != null) {
            java.util.Arrays.fill(data, (byte) 0);
        }
    }
    
    /**
     * Secure string wipe (best effort)
     */
    public void secureWipe(StringBuilder data) {
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                data.setCharAt(i, '\0');
            }
            data.setLength(0);
        }
    }
}
