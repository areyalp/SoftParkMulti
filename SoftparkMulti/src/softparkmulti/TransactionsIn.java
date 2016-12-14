package softparkmulti;

import java.sql.Timestamp;

public class TransactionsIn {

	private Timestamp entranceDateTime; 
	private String plate;
	private Integer transactionId;
	private Integer stationId;

	public TransactionsIn(Timestamp entranceDateTime,  String plate, Integer transactionId) {

		this.plate = plate;
		this.transactionId = transactionId;
		this.entranceDateTime = entranceDateTime;
	}
	
	public TransactionsIn(String plate, Integer transactionId) {

		this.plate = plate;
		this.transactionId = transactionId;
	}
		
	public TransactionsIn(Integer transactionId, Integer stationId, Timestamp entranceDateTime ) {

		this.transactionId = transactionId;
		this.stationId = stationId;
		this.entranceDateTime = entranceDateTime;
	}
	
	public Timestamp getEntranceDateTime() {
		return entranceDateTime;
	}
	
	public String getPlate() {
		return plate;
	}
	
	public Integer getTransactionId() {
		return transactionId;
	}
	
	public Integer getStationId() {
		return transactionId;
	}

}