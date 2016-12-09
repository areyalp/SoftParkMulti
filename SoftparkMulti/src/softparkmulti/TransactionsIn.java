package softparkmulti;

import java.sql.Timestamp;

public class TransactionsIn {

	private int id;
	private Timestamp entranceDateTime; 
	private String plate;
	private String ticketNumber;

	public TransactionsIn(int id, Timestamp entranceDateTime,  String plate, String ticketNumber) {
		this.id = id;
		this.plate = plate;
		this.ticketNumber = ticketNumber;
		this.entranceDateTime = entranceDateTime;
	}
	
	public int getId() {
		return id;
	}
	
	public Timestamp getEntranceDateTime() {
		return entranceDateTime;
	}
	
	public String getPlate() {
		return plate;
	}
	
	public String getTicketNumber() {
		return ticketNumber;
	}

}