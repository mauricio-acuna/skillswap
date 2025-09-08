package com.skillswap.backend.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for DataEncryptionService
 * Tests encryption, decryption, and security validation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Data Encryption Service Tests")
class DataEncryptionServiceTest {

    private DataEncryptionService encryptionService;

    @BeforeEach
    void setUp() {
        encryptionService = new DataEncryptionService();
        // Set a test encryption key
        ReflectionTestUtils.setField(encryptionService, "encryptionKey", 
            "dGVzdC1lbmNyeXB0aW9uLWtleS0yNTYtYml0cy1mb3ItdGVzdGluZw==");
    }

    @Test
    @DisplayName("Should encrypt and decrypt text successfully")
    void shouldEncryptAndDecryptSuccessfully() throws Exception {
        // Given
        String originalText = "This is sensitive data that needs to be encrypted";

        // When
        String encrypted = encryptionService.encrypt(originalText);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(originalText);
        assertThat(decrypted).isEqualTo(originalText);
    }

    @Test
    @DisplayName("Should handle null and empty strings")
    void shouldHandleNullAndEmptyStrings() throws Exception {
        // Test null input
        assertThat(encryptionService.encrypt(null)).isNull();
        assertThat(encryptionService.decrypt(null)).isNull();

        // Test empty string
        assertThat(encryptionService.encrypt("")).isEmpty();
        assertThat(encryptionService.decrypt("")).isEmpty();
    }

    @Test
    @DisplayName("Should encrypt PII data with validation")
    void shouldEncryptPiiDataWithValidation() throws Exception {
        // Given
        String piiData = "john.doe@example.com";

        // When
        String encrypted = encryptionService.encryptPII(piiData);
        String decrypted = encryptionService.decryptPII(encrypted);

        // Then
        assertThat(encrypted).isNotNull();
        assertThat(encrypted).isNotEqualTo(piiData);
        assertThat(decrypted).isEqualTo(piiData);
    }

    @Test
    @DisplayName("Should reject PII data that is too long")
    void shouldRejectPiiDataTooLong() {
        // Given
        String longPiiData = "a".repeat(1001);

        // When & Then
        assertThatThrownBy(() -> encryptionService.encryptPII(longPiiData))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("PII data too long for encryption");
    }

    @Test
    @DisplayName("Should handle whitespace in PII data")
    void shouldHandleWhitespaceInPiiData() throws Exception {
        // Given
        String piiDataWithSpaces = "  john.doe@example.com  ";

        // When
        String encrypted = encryptionService.encryptPII(piiDataWithSpaces);
        String decrypted = encryptionService.decryptPII(encrypted);

        // Then
        assertThat(decrypted).isEqualTo("john.doe@example.com"); // Trimmed
    }

    @Test
    @DisplayName("Should create consistent hashes")
    void shouldCreateConsistentHashes() throws Exception {
        // Given
        String data = "password123";

        // When
        String hash1 = encryptionService.hashSensitiveData(data);
        String hash2 = encryptionService.hashSensitiveData(data);

        // Then
        assertThat(hash1).isNotNull();
        assertThat(hash1).isEqualTo(hash2);
        assertThat(hash1).isNotEqualTo(data);
    }

    @Test
    @DisplayName("Should verify hash correctly")
    void shouldVerifyHashCorrectly() throws Exception {
        // Given
        String data = "password123";
        String hash = encryptionService.hashSensitiveData(data);

        // When & Then
        assertThat(encryptionService.verifyHash(data, hash)).isTrue();
        assertThat(encryptionService.verifyHash("wrongpassword", hash)).isFalse();
    }

    @Test
    @DisplayName("Should generate different hashes for different data")
    void shouldGenerateDifferentHashesForDifferentData() throws Exception {
        // Given
        String data1 = "password123";
        String data2 = "password456";

        // When
        String hash1 = encryptionService.hashSensitiveData(data1);
        String hash2 = encryptionService.hashSensitiveData(data2);

        // Then
        assertThat(hash1).isNotEqualTo(hash2);
    }

    @Test
    @DisplayName("Should generate valid encryption key")
    void shouldGenerateValidEncryptionKey() throws Exception {
        // When
        String key = encryptionService.generateNewKey();

        // Then
        assertThat(key).isNotNull();
        assertThat(key).isNotEmpty();
        // Base64 encoded 256-bit key should be 44 characters
        assertThat(key.length()).isEqualTo(44);
    }

    @Test
    @DisplayName("Should mask sensitive data for logging")
    void shouldMaskSensitiveDataForLogging() {
        // Test normal string
        String masked = encryptionService.maskForLogging("sensitivedata123");
        assertThat(masked).isEqualTo("se****23");

        // Test short string
        String maskedShort = encryptionService.maskForLogging("abc");
        assertThat(maskedShort).isEqualTo("****");

        // Test null
        String maskedNull = encryptionService.maskForLogging(null);
        assertThat(maskedNull).isEqualTo("****");
    }

    @Test
    @DisplayName("Should encrypt different plaintexts to different ciphertexts")
    void shouldEncryptDifferentPlaintextsToDifferentCiphertexts() throws Exception {
        // Given
        String text1 = "First sensitive message";
        String text2 = "Second sensitive message";

        // When
        String encrypted1 = encryptionService.encrypt(text1);
        String encrypted2 = encryptionService.encrypt(text2);

        // Then
        assertThat(encrypted1).isNotEqualTo(encrypted2);
    }

    @Test
    @DisplayName("Should encrypt same plaintext to different ciphertexts due to IV")
    void shouldEncryptSamePlaintextToDifferentCiphertexts() throws Exception {
        // Given
        String text = "Same message";

        // When
        String encrypted1 = encryptionService.encrypt(text);
        String encrypted2 = encryptionService.encrypt(text);

        // Then
        assertThat(encrypted1).isNotEqualTo(encrypted2); // Different due to random IV
        
        // But both should decrypt to the same original text
        assertThat(encryptionService.decrypt(encrypted1)).isEqualTo(text);
        assertThat(encryptionService.decrypt(encrypted2)).isEqualTo(text);
    }

    @Test
    @DisplayName("Should handle hash verification with invalid hash gracefully")
    void shouldHandleHashVerificationWithInvalidHashGracefully() {
        // Given
        String data = "password123";
        String invalidHash = "invalid-hash-value";

        // When & Then
        assertThat(encryptionService.verifyHash(data, invalidHash)).isFalse();
    }

    @Test
    @DisplayName("Should detect if encryption is properly configured")
    void shouldDetectIfEncryptionIsProperlyConfigured() {
        // Given service with test key (should be false as it contains test defaults)
        DataEncryptionService testService = new DataEncryptionService();
        
        // Test with no key
        assertThat(testService.isEncryptionConfigured()).isFalse();
        
        // Test with default-like key
        ReflectionTestUtils.setField(testService, "encryptionKey", "default-key-change-me");
        assertThat(testService.isEncryptionConfigured()).isFalse();
        
        // Test with proper key
        ReflectionTestUtils.setField(testService, "encryptionKey", 
            "cHJvcGVyLWVuY3J5cHRpb24ta2V5LXdpdGhvdXQtZGVmYXVsdHM=");
        assertThat(testService.isEncryptionConfigured()).isTrue();
    }

    @Test
    @DisplayName("Should handle unicode text correctly")
    void shouldHandleUnicodeTextCorrectly() throws Exception {
        // Given
        String unicodeText = "Héllo Wörld! 你好世界 🌍";

        // When
        String encrypted = encryptionService.encrypt(unicodeText);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertThat(decrypted).isEqualTo(unicodeText);
    }

    @Test
    @DisplayName("Should handle large text data")
    void shouldHandleLargeTextData() throws Exception {
        // Given
        String largeText = "Large data: " + "x".repeat(1000);

        // When
        String encrypted = encryptionService.encrypt(largeText);
        String decrypted = encryptionService.decrypt(encrypted);

        // Then
        assertThat(decrypted).isEqualTo(largeText);
    }
}
