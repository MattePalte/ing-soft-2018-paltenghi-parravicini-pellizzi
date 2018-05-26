package project.ing.soft;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenCalculator {
    public static String computeDigest(String toCompute){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(toCompute.getBytes());
            byte[] digest = md.digest();
            return encodeHex(digest);
        } catch (NoSuchAlgorithmException e) {
            System.out.println("A problem occurred trying to compute hash function: ");
            e.printStackTrace();
        }
        return null;
    }

    private static String encodeHex(byte[] byteArray){
        StringBuilder res = new StringBuilder();
        for(byte b : byteArray){
            String tmp = Integer.toHexString(b & 0xFF);
            if(tmp.length() == 1)
                res.append(0);
            res.append(tmp);
        }
        return new String(res);
    }
}
