package dmitr.ecn;

import java.util.Random;

public class SaltGenerator {
	
	public static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	private static final Random random = new Random();
	
    public static String getString(int length) {
    	String result = new String();
        for (int i = 0; i < length; i++)
            result += chars.charAt(random.nextInt(chars.length()));
        return result;
    }

}