package org.sigmah.shared.util;

/**
 *
 * @author Your Name <your.name at your.org>
 */
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.google.inject.Singleton;
import java.io.UnsupportedEncodingException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
//
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

@Singleton
public class DesEncrypterImpl extends RemoteServiceServlet implements org.sigmah.client.ui.view.calendar.DesEncrypter {

    Cipher ecipher;
    Cipher dcipher;

    byte[] salt = {
        (byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
        (byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0x03
    };

    int iterationCount = 3;
    String passPhrase = "e4ZqtzVDhs=x-eT";

    public DesEncrypterImpl() {

        try {

            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, iterationCount);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);

            ecipher = Cipher.getInstance(key.getAlgorithm());
            dcipher = Cipher.getInstance(key.getAlgorithm());

            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

            ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
            dcipher.init(Cipher.DECRYPT_MODE, key, paramSpec);

        } catch (java.security.InvalidAlgorithmParameterException | java.security.spec.InvalidKeySpecException | javax.crypto.NoSuchPaddingException | java.security.NoSuchAlgorithmException | java.security.InvalidKeyException e) {
        }
    }

    @Override
    public String encrypt(String str) {

        try {

            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = ecipher.doFinal(utf8);

            return new sun.misc.BASE64Encoder().encode(enc);

        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        }

        return null;
    }

    public String decrypt(String str) {

        try {

            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);
            byte[] utf8 = dcipher.doFinal(dec);

            return new String(utf8, "UTF8");

        } catch (javax.crypto.BadPaddingException e) {
        } catch (IllegalBlockSizeException e) {
        } catch (UnsupportedEncodingException e) {
        } catch (java.io.IOException e) {
        }

        return null;
    }
    
//   public static void main(String[] args) {
//       String theP = "HelloAlex";
//       DesEncrypterImpl newD = new DesEncrypterImpl();
//       String theU = "ExportCalendar?type=events&id=204";
//       String theR = newD.encrypt(theU);
//       System.out.println("PassWord " + theP);
//       System.out.println("URL " + theU);
//       System.out.println("Encrypted " + theR);
//   }
}
