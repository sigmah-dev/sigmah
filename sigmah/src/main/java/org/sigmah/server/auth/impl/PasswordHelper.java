package org.sigmah.server.auth.impl;


public class PasswordHelper {
	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}
	
    /**
     * Generates a new password.
     * @return A password of 8 characters with 2 caps, 1 number and 1 special character.
     */
    public static String generatePassword() {
        final StringBuilder password = new StringBuilder();

        int[] remainings = new int[] {4, 2, 1, 1};
        int size = 8;

        while(size > 0) {
            int nextChar = -1;
            while(nextChar == -1) {
                int alphabet = (int) (Math.random() * remainings.length);
                if(remainings[alphabet] > 0) {
                    nextChar = alphabets[alphabet][(int) (Math.random() * alphabets[alphabet].length)];
                    remainings[alphabet]--;
                }
            }
            password.append((char)nextChar);

            size--;
        }

        return password.toString();
    }
    private static final char[] letters = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','p','q','r','s','t','u','v','w','x','y','z'};
    private static final char[] caps    = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final char[] numbers = {'1','2','3','4','5','6','7','8','9'};
    private static final char[] symbols = {'$','+','-','=','_','!','%','@'};
    private static final char[][] alphabets = {letters, caps, numbers, symbols};
	
}
