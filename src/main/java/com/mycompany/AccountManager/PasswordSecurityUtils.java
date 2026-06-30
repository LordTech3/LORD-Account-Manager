package com.mycompany.AccountManager;

import com.nulabinc.zxcvbn.*;
import de.mkammerer.argon2.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.security.SecureRandom;
import javafx.scene.control.Alert;
import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 *
 * @author amirhossein
 */
public class PasswordSecurityUtils {

    private static final int ITERATIONS = 4;
    private static final int MEMORY = 131072;
    private static final int THREADS = 1;
    private static final Argon2 ARGON2 = Argon2Factory.create();
    private static final Zxcvbn ZXCVBN = new Zxcvbn();
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordSecurityUtils() {

    }

    public static int passStrength(String password) {

        return ZXCVBN.measure(password).getScore();
    }

    public static String createHash(char[] password) {

      
            return ARGON2.hash(ITERATIONS, MEMORY, THREADS, password);
       
    }

   public static boolean verifyHash(String storedHash, byte[] password) {
        try {
            return ARGON2.verify(storedHash, password);

        } finally {
            /*The wipe is dangerous*/
            ARGON2.wipeArray(password);
        }
    }

    public static byte[] generateEncryptionKey(char[] masterPassword, byte[] salt) {
        return Argon2Factory.createAdvanced().pbkdf(
                ITERATIONS, MEMORY, THREADS, masterPassword, StandardCharsets.UTF_8, salt, 32);

    }

    public static String encrypt(String plainText, byte[] nonce, byte[] encryptionKey) {
        byte[] bytePlainText = null;
        try {

            SecretKey keyWrapper = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, keyWrapper, spec);
            bytePlainText = plainText.getBytes(StandardCharsets.UTF_8);
            return base64BytesToString(cipher.doFinal(bytePlainText));
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        } finally {
            ARGON2.wipeArray(bytePlainText);
        }
    }

    public static String encrypt(char[] plainText, byte[] nonce, byte[] encryptionKey) {
        byte[] bytePlainText = null;
        try {

            SecretKey keyWrapper = new SecretKeySpec(encryptionKey, "AES");
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, nonce);
            cipher.init(Cipher.ENCRYPT_MODE, keyWrapper, spec);
            ByteBuffer bb = StandardCharsets.UTF_8.encode(CharBuffer.wrap(plainText));

            bytePlainText = new byte[bb.remaining()];
            bb.get(bytePlainText);

            return base64BytesToString(cipher.doFinal(bytePlainText));
        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        } finally {
            ARGON2.wipeArray(bytePlainText);
        }
    }

    public static char[] decryptToChars(String encryptedText, byte[] nonce, byte[] encryptionKey) {
        try {
            SecretKey keyWrapper = new SecretKeySpec(encryptionKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, nonce);

            cipher.init(Cipher.DECRYPT_MODE, keyWrapper, spec);

            byte[] plaintextBytes = cipher.doFinal(base64StringToBytes(encryptedText));

            // convert bytes → chars (UTF-8 safe approach is NOT trivial, but common for app use)
            char[] result = new String(plaintextBytes, StandardCharsets.UTF_8).toCharArray();

            Arrays.fill(plaintextBytes, (byte) 0); // wipe intermediate bytes

            return result;

        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, "Decryption failed");
            return null;
        }
    }

    public static String decryptToString(String encryptedText, byte[] nonce, byte[] encryptionKey) {
        try {
            SecretKey keyWrapper = new SecretKeySpec(encryptionKey, "AES");

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            GCMParameterSpec spec = new GCMParameterSpec(128, nonce);

            cipher.init(Cipher.DECRYPT_MODE, keyWrapper, spec);

            byte[] plaintextBytes = cipher.doFinal(base64StringToBytes(encryptedText));

            // convert bytes → chars (UTF-8 safe approach is NOT trivial, but common for app use)
            String result = new String(plaintextBytes, StandardCharsets.UTF_8);

            Arrays.fill(plaintextBytes, (byte) 0); // wipe intermediate bytes

            return result;

        } catch (Exception e) {
            Alerts.Alert(Alert.AlertType.ERROR, e.getMessage());
            return null;
        }
    }

    public static byte[] generateSalt() {
        byte[] salt = new byte[32];
        RANDOM.nextBytes(salt);
        return salt;
    }

    public static byte[] generateNonce() {
        byte[] nonce = new byte[12];
        RANDOM.nextBytes(nonce);
        String string;
        return nonce;
    }

    public static String base64BytesToString(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static byte[] base64StringToBytes(String str) {
        return Base64.getDecoder().decode(str);
    }

    public static byte[] UTF8StringToBytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

}
