package softparkmulti;

import java.sql.Timestamp;

public class TransactionsIn {

	private int id;
	private Timestamp entranceDateTime; 
	private String plate;
	private Integer ticketNumber;

	public TransactionsIn(int id, Timestamp entranceDateTime,  String plate, Integer ticketNumber) {
		this.id = id;
		this.plate = plate;
		this.ticketNumber = ticketNumber;
		this.entranceDateTime = entranceDateTime;
	}
	
	public Integer getId() {
		return id;
	}
	
	public Timestamp getEntranceDateTime() {
		return entranceDateTime;
	}
	
	public String getPlate() {
		return plate;
	}
	
	public Integer getTicketNumber() {
		return ticketNumber;
	}

}