package com.mycompany.AccountManager;

import java.util.Arrays;

/**
 *
 * @author amirhossein
 */
public class KeyManager {

    private static byte[] currentEncryptionKey;
    private static byte[] newEncryptionKey;

    public static void setCurrentKey(byte[] key) {
        currentEncryptionKey = key;
    }

    public static byte[] getCurrentKey() {
        return currentEncryptionKey;
    }

    public static void clearCurrentKey() {
        if (currentEncryptionKey != null) {
            Arrays.fill(currentEncryptionKey, (byte) 0);
            currentEncryptionKey = null;
        }
    }

}
