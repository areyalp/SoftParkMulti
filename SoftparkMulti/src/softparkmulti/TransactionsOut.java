package softparkmulti;

import java.sql.Timestamp;

public class TransactionsOut {
	private int id;
	private double maxAmount;
	private double tax;
	private Timestamp entranceDateTime;
	private Timestamp exitDateTime;
	private String ticketNumber;
	
	public TransactionsOut(int id, double maxAmount, double tax, String ticketNumber, String plate, Timestamp exitDateTime, Timestamp entranceDateTime) {
		this.id = id;
		this.maxAmount = maxAmount;
		this.tax = tax;
		this.exitDateTime = exitDateTime;
		this.ticketNumber = ticketNumber;
		this.entranceDateTime = entranceDateTime;
	}
	
	public int getId() {
		return id;
	}
	
	public double getMaxAmount() {
		return maxAmount;
	}

	public double getTax() {
		return tax;
	}

	public String getTicketNumber() {
		return ticketNumber;
	}
	
	public Timestamp getExitDateTime() {
		return exitDateTime;
	}

	public Timestamp getEntranceDateTime() {
		return entranceDateTime;
	}

	
}
