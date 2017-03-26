package ve.com.soted.softparkmulti.objects;

import java.sql.Timestamp;

import org.joda.time.DateTime;

import ve.com.soted.softparkmulti.utils.StringTools;

public class Ticket {
	private int id;
	private int entranceStationId;
	private int cardId;
	private int exitStationId;
	private int summaryId;
	private String plate;
	private String picture;
	private double totalAmount;
	private Timestamp entranceDateTime;
	private Timestamp payDateTime;
	private Timestamp exitDateTime;
	private int printed;
	private int exonerated;
	private int exited;
	private int lost;
	
	public Ticket() {
		super();
	}
	
	public Ticket(int id, int entranceStationId, int cardId, int exitStationId, int summaryId, String plate,
			String picture, double totalAmount, Timestamp entranceDateTime, Timestamp payDateTime,
			Timestamp exitDateTime, int printed, int exonerated, int exited, int lost) {
		super();
		this.id = id;
		this.entranceStationId = entranceStationId;
		this.cardId = cardId;
		this.exitStationId = exitStationId;
		this.summaryId = summaryId;
		this.plate = plate;
		this.picture = picture;
		this.totalAmount = totalAmount;
		this.entranceDateTime = entranceDateTime;
		this.payDateTime = payDateTime;
		this.exitDateTime = exitDateTime;
		this.printed = printed;
		this.exonerated = exonerated;
		this.exited = exited;
		this.lost = lost;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getEntranceStationId() {
		return entranceStationId;
	}

	public void setEntranceStationId(int entranceStationId) {
		this.entranceStationId = entranceStationId;
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public int getExitStationId() {
		return exitStationId;
	}

	public void setExitStationId(int exitStationId) {
		this.exitStationId = exitStationId;
	}

	public int getSummaryId() {
		return summaryId;
	}

	public void setSummaryId(int summaryId) {
		this.summaryId = summaryId;
	}

	public String getPlate() {
		return plate;
	}

	public void setPlate(String plate) {
		this.plate = plate;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Timestamp getEntranceDateTime() {
		return entranceDateTime;
	}

	public void setEntranceDateTime(Timestamp entranceDateTime) {
		this.entranceDateTime = entranceDateTime;
	}

	public Timestamp getPayDateTime() {
		return payDateTime;
	}

	public void setPayDateTime(Timestamp payDateTime) {
		this.payDateTime = payDateTime;
	}

	public Timestamp getExitDateTime() {
		return exitDateTime;
	}

	public void setExitDateTime(Timestamp exitDateTime) {
		this.exitDateTime = exitDateTime;
	}

	public int getPrinted() {
		return printed;
	}

	public void setPrinted(int printed) {
		this.printed = printed;
	}

	public int getExonerated() {
		return exonerated;
	}

	public void setExonerated(int exonerated) {
		this.exonerated = exonerated;
	}

	public int getExited() {
		return exited;
	}

	public void setExited(int exited) {
		this.exited = exited;
	}

	public int getLost() {
		return lost;
	}

	public void setLost(int lost) {
		this.lost = lost;
	}

	@Override
	public String toString() {
		DateTime dt = new DateTime(this.getEntranceDateTime());
		String day = StringTools.fillWithZeros(dt.getDayOfMonth(), 2);
		String month = StringTools.fillWithZeros(dt.getMonthOfYear(), 2);
		String year = String.valueOf(dt.getYear());
		String hour = StringTools.fillWithZeros(dt.getHourOfDay(), 2);
		String minute = StringTools.fillWithZeros(dt.getMinuteOfHour(), 2);
		String second = StringTools.fillWithZeros(dt.getSecondOfMinute(), 2);
		String stationId = StringTools.fillWithZeros(this.getEntranceStationId(), 3);
		String ticketId = StringTools.fillWithZeros(this.getId(), 11);
		
		return day + month + year + hour + minute + second + stationId + ticketId;
	}
	
	

}
