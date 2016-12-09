package softparkmulti;

import java.sql.Timestamp;

public class Ticket {
	private int id;
	private int entryStationId;
	private int StationId;
	private int CardId;
	private int ticketNumber;
	private int summaryId;
	private double totalAmount;
	private Timestamp entryDate;
	private Timestamp payDate;
	
	public Ticket(int id, int entryStationId,
			int StationId,int CardId,
			int ticketNumber, int SummaryId, 
			double totalAmount, Timestamp entryDate,
			Timestamp payDate) {
		// TODO Auto-generated constructor stub
		this.id=id;
		this.entryStationId= entryStationId;
		this.StationId=StationId;
		this.CardId=CardId;
		this.ticketNumber=ticketNumber;
		this.summaryId=SummaryId;
		this.totalAmount=totalAmount;
		this.entryDate=entryDate;
		this.payDate=payDate;
		
	}

	public int getId(){return id;}
	
	public int getEntryStationId(){return entryStationId;}
	
	public int getStationId(){return StationId;}
	
	public int getCardId(){return CardId;}
	
	public int getTicketNumber(){return ticketNumber;}
	
	public int getsummaryId(){return summaryId;}
	
	public double getTotalAmount(){return totalAmount;}
	
	public Timestamp getEntryDate(){return entryDate;}
	
	public Timestamp getPayDate(){return payDate;}
	

}
