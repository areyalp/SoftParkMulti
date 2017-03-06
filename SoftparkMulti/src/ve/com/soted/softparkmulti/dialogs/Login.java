package ve.com.soted.softparkmulti.dialogs;

import java.sql.ResultSet;

import ve.com.soted.softparkmulti.db.Db;
import ve.com.soted.softparkmulti.utils.PasswordEncryptor;

public class Login {

	protected static boolean authenticate(String username, String plainPassword){
		boolean isPasswordOk = false;
		boolean authenticated = false;
		ResultSet rows;
		try{
			Db db = new Db();
			rows = db.select("SELECT Id, Password FROM Users WHERE Login='"+ username +"';");
			if(rows.next()){
				String encryptedPassword = rows.getString("Password");
				isPasswordOk = PasswordEncryptor.checkPassword(plainPassword, encryptedPassword);
				if(isPasswordOk){
					authenticated = true;
				}
			}
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		
		return authenticated;
	}
	
}
