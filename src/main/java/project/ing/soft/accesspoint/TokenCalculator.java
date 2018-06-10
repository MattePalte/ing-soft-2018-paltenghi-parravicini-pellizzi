package project.ing.soft.accesspoint;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a utility class, whose objective is to compute a digest that will be the token which associates
 * a client with the game
 */
public class TokenCalculator {
    private TokenCalculator(){

    }

    /**
     * Method which returns a MD5 digest of the given string
     * @param toCompute the string of which a digest is required
     * @return the string digest computed with MD5 algorithm
     */
    public static String computeDigest(String toCompute){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(toCompute.getBytes());
            byte[] digest = md.digest();
            return encodeHex(digest);
        } catch (NoSuchAlgorithmException e) {
            Logger.getAnonymousLogger().log( Level.SEVERE,"A problem occurred trying to compute hash function: ",e );

        }
        return null;
    }

    /**
     * Method used to encode a byte array in a hexadecimal string. This is used to have digest of a fixed
     * length of 32 characters
     * @param byteArray the array of bytes to convert in hexadecimal string
     * @return a hexadecimal string of 32 characters
     */
    private static String encodeHex(byte[] byteArray){
        StringBuilder res = new StringBuilder();
        for(byte b : byteArray){
            String tmp = Integer.toHexString(b & 0xff);
            if(tmp.length() == 1)
                res.append(0);
            res.append(tmp);
        }
        return new String(res);
    }
}
