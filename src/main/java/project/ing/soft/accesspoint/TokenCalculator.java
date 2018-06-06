package project.ing.soft.accesspoint;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TokenCalculator {
    private TokenCalculator(){

    }

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
