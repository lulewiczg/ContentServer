package com.github.lulewiczg.contentserver.utils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.security.sasl.AuthenticationException;
import javax.servlet.ServletContext;

/**
 * Class for common methods
 * 
 * @author lulewiczg
 */
public final class CommonUtil {

    private CommonUtil() {
        // Do Nothing
    }

    /**
     * Normalizes path.
     *
     * @param path
     *            path
     * @return normalized path
     */
    public static String normalizePath(String path) {
        return path.replaceAll("\\\\+", Constants.SEP).replaceAll(String.format("\\%s+", Constants.SEP), Constants.SEP);
    }

    /**
     * Obtains context path.
     *
     * @param context
     *            context
     * @return
     */
    public static String getContextPath(ServletContext servletContext) {
        String path = servletContext.getRealPath(Constants.SEP);
        if (!path.endsWith(Constants.SEP)) {
            path += Constants.SEP;
        }
        return normalizePath(path);
    }

    /**
     * Generates SHA for given string.
     *
     * @param text
     *            text to hash
     * @return hashed string
     * @throws AuthenticationException
     *             the AuthenticationException
     */
    public static String sha1(String text) throws AuthenticationException {
        byte[] textBytes;
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
            textBytes = text.getBytes(StandardCharsets.UTF_8);
        } catch (NoSuchAlgorithmException e) {
            throw new AuthenticationException("Hash algoritm not found", e);
        }

        md.update(textBytes, 0, textBytes.length);
        byte[] sha1hash = md.digest();
        return new BigInteger(1, sha1hash).toString(16).toUpperCase();
    }
}
