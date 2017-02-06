package ve.com.soted.softparkmulti.components;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PasswordEncryptor {
   private static MessageDigest md;

   public static String encryptPassword(String pass){
    try {
        md = MessageDigest.getInstance("MD5");
        byte[] passBytes = pass.getBytes("UTF-8");
        md.reset();
        byte[] digested = md.digest(passBytes);
        StringBuffer sb = new StringBuffer();
        for(int i=0;i<digested.length;i++){
            sb.append(Integer.toHexString((0xff & digested[i]) | 0x100).substring(1, 3));
        }
        return sb.toString();
    } catch (NoSuchAlgorithmException ex) {
        Logger.getLogger(PasswordEncryptor.class.getName()).log(Level.SEVERE, null, ex);
    } catch (UnsupportedEncodingException ex) {
    	Logger.getLogger(PasswordEncryptor.class.getName()).log(Level.SEVERE, null, ex);
	}
       return null;

   }
   
   public static Boolean checkPassword(String plainPassword, String encryptedPassword){
	   
	   String plainPasswordEncrypted;
	   
	   plainPasswordEncrypted = PasswordEncryptor.encryptPassword(plainPassword);
	   
	   if(plainPasswordEncrypted.equals(encryptedPassword)) return true;
	   
	   return false;
	   
   }
}