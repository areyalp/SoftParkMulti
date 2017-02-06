package ve.com.soted.softparkmulti.objects;

import java.sql.Timestamp;
import java.util.Date;

import org.joda.time.DateTime;

public class Summary {
	
	private int id;
	private int stationId;
	private String stationName;
	private int userId;
	private String userName;
	private String userLogin;
	private int supervisorId;
	private double totalAmount;
	private double taxAmount;
	private DateTime dateCreated;
	private DateTime dateClosing;
	private int status;
	private int firstFiscalInvoice;
	private int lastFiscalInvoice;
	private double cashFlow;
	public Summary(int id, int stationId, String stationName, int userId, String userName, String userLogin,
			int supervisorId, double totalAmount, double taxAmount, Timestamp dateCreated, Timestamp dateClosing,
			int status, int firstFiscalInvoice, int lastFiscalInvoice, double cashFlow) {
		super();
		this.id = id;
		this.stationId = stationId;
		this.stationName = stationName;
		this.userId = userId;
		this.userName = userName;
		this.userLogin = userLogin;
		this.supervisorId = supervisorId;
		this.totalAmount = totalAmount;
		this.taxAmount = taxAmount;
		this.dateCreated = new DateTime(new Date(dateCreated.getTime()));
		this.dateClosing = new DateTime(new Date(dateClosing.getTime()));
		this.status = status;
		this.firstFiscalInvoice = firstFiscalInvoice;
		this.lastFiscalInvoice = lastFiscalInvoice;
		this.cashFlow = cashFlow;
	}
	public int getId() {
		return id;
	}
	public int getStationId() {
		return stationId;
	}
	public String getStationName() {
		return stationName;
	}
	public int getUserId() {
		return userId;
	}
	public String getUserName() {
		return userName;
	}
	public String getUserLogin() {
		return userLogin;
	}
	public int getSupervisorId() {
		return supervisorId;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public double getTaxAmount() {
		return taxAmount;
	}
	public DateTime getDateCreated() {
		return dateCreated;
	}
	public DateTime getDateClosing() {
		return dateClosing;
	}
	public int getStatus() {
		return status;
	}
	public int getFirstFiscalInvoice() {
		return firstFiscalInvoice;
	}
	public int getLastFiscalInvoice() {
		return lastFiscalInvoice;
	}
	public double getCashFlow() {
		return cashFlow;
	}
	
}
