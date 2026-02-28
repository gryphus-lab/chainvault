package ch.gryphus.chainvault.utils;

import org.apache.commons.codec.binary.Hex;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The type Hash utils.
 */
public class HashUtils {

    private HashUtils() {
    }

    /**
     * Sha 256 string.
     *
     * @param path the path
     * @return the string
     * @throws IOException              the io exception
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static String sha256(Path path) throws IOException, NoSuchAlgorithmException {
        return sha256(Files.readAllBytes(path));
    }

    /**
     * Sha 256 string.
     *
     * @param data the data
     * @return the string
     * @throws NoSuchAlgorithmException the no such algorithm exception
     */
    public static String sha256(byte[] data) throws NoSuchAlgorithmException {
        return Hex.encodeHexString(MessageDigest.getInstance("SHA-256").digest(data));
    }
}
