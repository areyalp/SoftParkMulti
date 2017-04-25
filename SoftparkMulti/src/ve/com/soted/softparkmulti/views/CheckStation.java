package ve.com.soted.softparkmulti.views;
import java.sql.ResultSet;
import javax.swing.JOptionPane;

import ve.com.soted.softparkmulti.comm.GetNetworkAddress;
import ve.com.soted.softparkmulti.db.Db;

public class CheckStation {

	int stationId = 0;
	public static void main(String[] args) {

		new CheckStation();

	} // END OF main

	public CheckStation() {
		try{
			Db db = new Db();
			String macAddress = GetNetworkAddress.GetAddress("mac");
			
			if(!(macAddress == null)){
				//if(macAddress.equalsIgnoreCase("00-19-21-20-01-4e")) { //Mac Address antigua
				if(macAddress.equalsIgnoreCase("00-e0-4d-76-ba-c1") || macAddress.equalsIgnoreCase("08-00-27-73-9c-b9")) { //MAC Pollera
				//if(true) {
					ResultSet rowsMac = db.select("SELECT Id FROM Stations WHERE"
							+ " MacAddress = '" + macAddress + "'");
					if(rowsMac.next()){
						stationId = rowsMac.getInt("Id");
						new SoftParkMultiView(stationId);
					}else{
						new SelectStationView();
					}
				}else{
					JOptionPane.showMessageDialog(null, "Instalacion incorrecta");
					System.exit(0);
				}
			}else{
				JOptionPane.showMessageDialog(null, "No esta conectado a la red", "Conectese a la red", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}
		} //END OF try
		catch(Exception ex){
			JOptionPane.showMessageDialog(null, ex.getCause());
		} //END OF catch
		
	} // END OF CheckStation
	

	


}//END of class