package softparkmulti;

import java.sql.Timestamp;

public class TransactionsOut {
	private int id;
	private double maxAmount;
	private double tax;
	private String ticketNumber;
	private String name;
	
	public TransactionsOut(int id, String name, double maxAmount, double tax, String ticketNumber) {
		this.id = id;
		this.maxAmount = maxAmount;
		this.tax = tax;
		this.name= name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
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
	
	
}
