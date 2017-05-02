package ve.com.soted.softparkmulti.components;

import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

public class Barcode {
	
	private String strentryStationId;
	private String strstationId;
	private String strticketNumber;
	private StringBuilder date;
	private StringBuilder time;
	private String datetime;
		

	public Barcode(String barcode) {

		strentryStationId = barcode.substring(0,3);
		strstationId = barcode.substring(3,6);
		strticketNumber  =barcode.substring(6,12);
		date = new StringBuilder(); 
			date.append(barcode.substring(12,18));
			date.insert(2, "/");date.insert(5, "/");
		time = new StringBuilder(); 
			time.append(barcode.substring(18,22));
			time.insert(2, ":");
			this.datetime = date.toString() + " " + time.toString();
	}
	
	public int getEntryStationId(){return Integer.parseInt( strentryStationId); }
	
	public int getStationId(){return Integer.parseInt(strstationId);}
	
	public int getTicketNumber(){return Integer.parseInt(strticketNumber);}
	
	public Timestamp getEntryDate(){
		DateTime datetime =DateTime.parse(this.datetime, 
                DateTimeFormat.forPattern("dd/mm/yy hh:mm"));

		return  new Timestamp(datetime.getMillis());
	}
	

}
