package softparkmulti;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Db {
	
	Connection conn = null;

	public Db() {
		Properties prop = new Properties();
		InputStream propertiesInput;
		String host = "", database = "", dbuser = "", dbpassword = "";
		String dbuser2 = "", dbpassword2 = "";
		try{
			propertiesInput = getClass().getResourceAsStream("config.properties");
			// load a properties file
			prop.load(propertiesInput);
			host = prop.getProperty("host");
			database = prop.getProperty("database");
			dbuser = prop.getProperty("dbuser");
			dbpassword = prop.getProperty("dbpassword");
			dbuser2 = prop.getProperty("dbuser2");
			dbpassword2 = prop.getProperty("dbpassword2");
			
			}catch(IOException ex){
				JOptionPane.showMessageDialog(null, ex.getMessage());
			}
			
			try{
				conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, dbuser, dbpassword);
			}catch(Exception ex){
				try{
					conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database, dbuser2, dbpassword2);
				}catch(SQLException ex2){
					JOptionPane.showMessageDialog(null, ex2.getMessage());
				}
			}		
	}
	
	protected boolean testConnection() {
		String queryString = "SELECT * FROM Users;";
		ResultSet rows = this.select(queryString);
		try {
			if(rows.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	protected ResultSet select(String queryString) {
		ResultSet queryResult = null;
		Statement sqlState;
		try {
			sqlState = conn.createStatement();
			queryResult = sqlState.executeQuery(queryString);
			
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return queryResult;
	}
	
	protected int insert(String queryString) {
		Statement sqlState;
		int insertedId = 0;
		try {
			sqlState = conn.prepareStatement(queryString);
			sqlState.executeUpdate(queryString, Statement.RETURN_GENERATED_KEYS);
			try (ResultSet generatedKeys = sqlState.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	insertedId = generatedKeys.getInt(1);
	            }
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return insertedId;
	}
	
	protected boolean update(String queryString) {
		PreparedStatement sqlState;
		try{
			sqlState = conn.prepareStatement(queryString);
			int count = sqlState.executeUpdate();
			if(count > 0){
				return true;
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	//Added new method insertTransactionsIn with it's arguments... check this...
	
	protected int insertTransactionsIn(int stationId, Timestamp entranceDate, String plate, int ticketNumber,
			int transactionTypeId) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO TransactionsIn (StationId,EntranceDate,Plate,TicketNumber)"
				+ " VALUES (" + stationId + "," 
				+ entranceDate + ","
				+ plate +  "," 
				+ ticketNumber + ")";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId,TypeId)"
					+ " VALUES (" + insertedId + "," 
					+ transactionTypeId + ")";
			this.insert(sql);
		}
		return insertedId;
	}
	
	protected int insertTransactionsOut(int stationId, int summaryId, int ticketNumber, Timestamp entranceDate, Timestamp exitDate,
			String plate, int transactionTypeId, double totalAmount, double taxAmount, int payTypeId) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO TransactionsOut (StationId,TicketNumber,EntranceDate,ExitDate,Plate,SummaryId,TotalAmount)"
				+ " VALUES (" + stationId + "," 
				+ ticketNumber + "," 
				+ entranceDate + ","
				+ exitDate + ","
				+ plate + ","
				+ summaryId + "," 
				+ totalAmount + ")";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId,TypeId,TotalAmount,TaxAmount)"
					+ " VALUES (" + insertedId + "," 
					+ transactionTypeId + ","
					+ totalAmount + ","
					+ taxAmount + ")";
			this.insert(sql);
			sql = "INSERT INTO TransactionsPay (TransactionId,PayTypeId,Amount)"
					+ " VALUES (" + insertedId + "," 
					+ payTypeId + "," 
					+ totalAmount + ")";
			this.insert(sql);
		}
		return insertedId;
	}
	
	protected int preInsertTransaction(int stationId) {
		
		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntryStationId, TicketNumber) VALUES(" + stationId + ", 0)";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId)"
					+ " VALUES (" + insertedId + ")";
			this.insert(sql);
			sql = "INSERT INTO TransactionsPay (TransactionId)"
					+ " VALUES (" + insertedId + ")";
			this.insert(sql);
		}
		return insertedId;
	}
	
	protected Integer transactionIn (Integer stationId, String plate){		
		
		String sql;
		int insertedId = 0;
		sql = "INSERT INTO TransactionsIn (StationId, Plate, TicketNumber) VALUES(" + stationId + ", '" + plate + "', 0)";
		insertedId = this.insert(sql);
		return insertedId;
		
	}
	//
	
	protected int insertTransaction(int stationId, int summaryId, int ticketNumber, double totalAmount, double taxAmount,
			int transactionTypeId, int payTypeId) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (StationId,TicketNumber,SummaryId,TotalAmount)"
				+ " VALUES (" + stationId + "," 
				+ ticketNumber + "," 
				+ summaryId + "," 
				+ totalAmount + ")";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId,TypeId,TotalAmount,TaxAmount)"
					+ " VALUES (" + insertedId + "," 
					+ transactionTypeId + "," 
					+ totalAmount + ","
					+ taxAmount + ")";
			this.insert(sql);
			sql = "INSERT INTO TransactionsPay (TransactionId,PayTypeId,Amount)"
					+ " VALUES (" + insertedId + "," 
					+ payTypeId + "," 
					+ totalAmount + ")";
			this.insert(sql);
		}
		return insertedId;
	}
	
	protected int insertSummary(int stationId, int userId, int firstInvoiceNumber) {
		String sql;
		int insertedSummaryId = 0;
		
		sql = "INSERT INTO Summary (StationId,UserId,FirstFiscalInvoice)"
				+ " VALUES (" + stationId + ","
				+ userId + ","
				+ firstInvoiceNumber + ")";
		insertedSummaryId = this.insert(sql);
		return insertedSummaryId;
	}
	
	protected static boolean closeSummary(int summaryId, int supervisorId) {
		Db db = new Db();
		boolean updatedSummary = db.update("UPDATE Summary"
				+ " SET Status = 1, SupervisorId = " + supervisorId + ", DateClosing = NOW()"
				+ " WHERE Id = " + summaryId);
		return updatedSummary;
	}

	protected static ArrayList<Station> getStationsWithSummary() {
		ArrayList<Station> stations;
		Db db = new Db();
		ResultSet rowsStations = db.select("SELECT su.StationId, st.TypeId, st.Name"
				+ " FROM Summary as su, Stations as st"
				+ " WHERE su.StationId = st.id AND Status = 0 GROUP BY su.StationId");
		stations = new ArrayList<Station>();
		try {
			while(rowsStations.next()) {
				stations.add(new Station(rowsStations.getInt("StationId"),
						rowsStations.getInt("TypeId"),
						rowsStations.getString("Name")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return stations;
	}
	
	protected static int getSummaryId(int userId, int stationId) {
		Db db = new Db();
		int vaultSummaryId = 0;
		ResultSet rowsVaultSummary = db.select("SELECT Id FROM Summary WHERE Status = 0 AND StationId = " 
		+ stationId + " AND UserId = " + userId);
		try {
			if(rowsVaultSummary.next()) {
				vaultSummaryId = rowsVaultSummary.getInt("Id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return vaultSummaryId;
	}
	
	protected int countSummaryInvoices(int vaultSummaryId) {
		int invoiceCount = 0;
		ResultSet rowsCountSummaryInvoices;
		try{
			rowsCountSummaryInvoices = this.select("SELECT IFNULL(count(*),0) as cnt FROM Transactions WHERE SummaryId = " + vaultSummaryId);
			rowsCountSummaryInvoices.next();
			invoiceCount = rowsCountSummaryInvoices.getInt("cnt");
		} catch(Exception e){
			e.printStackTrace();
		}
		return invoiceCount;
	}
	
	protected static int getUserId(String username) {
		Db db = new Db();
		int userId = 0;
		ResultSet rowUser = db.select("SELECT Id FROM Users WHERE Login = '" + username + "'");
		
		try {
			if(rowUser.next()) {
				userId = rowUser.getInt("Id");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return userId;
	}
	
	protected User loadUserInfo(int userId) {
		User user = null;
		ResultSet rowsUser = this.select(
				"SELECT u.Id, "
					+ "u.UserTypeId, "
					+ "u.FirstName, "
					+ "u.LastName, "
					+ "u.Login, "
					+ "t.Name, "
					+ "p.LogToProgram, "
					+ "p.CanCheckOut, "
					+ "p.CanPrintReportZ, "
					+ "p.CanPrintReportX "
				+ "FROM Users u, UserType t, UserTypePermissions p "
				+ "WHERE u.Id = " + userId + " AND u.UserTypeId = t.Id AND u.UserTypeId = p.UserTypeId");
		try {
			while(rowsUser.next()){
				user = new User(rowsUser.getInt("Id"),
						rowsUser.getInt("UserTypeId"),
						rowsUser.getString("FirstName") + " " + rowsUser.getString("LastName"),
						rowsUser.getString("Login"),
						rowsUser.getString("Name"),
						rowsUser.getBoolean("LogToProgram"),
						rowsUser.getBoolean("CanCheckOut"),
						rowsUser.getBoolean("CanPrintReportZ"),
						rowsUser.getBoolean("CanPrintReportX"));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return user;
	}
	
	protected static ArrayList<Summary> loadSummaries() {
		ArrayList<Summary> summaries = null;
		Db db = new Db();
		ResultSet rowsSummaries = db.select("SELECT su.*, st.Name, u.FirstName, u.LastName, u.Login "
				+ "FROM Summary as su, Stations as st, Users as u "
				+ "WHERE su.StationId = st.Id AND su.UserId = u.Id AND su.Status = 0");
		
		summaries = new ArrayList<Summary>();
		try {
			while(rowsSummaries.next()) {
				summaries.add(new Summary(
						rowsSummaries.getInt("Id"),
						rowsSummaries.getInt("StationId"),
						rowsSummaries.getString("Name"),
						rowsSummaries.getInt("UserId"),
						rowsSummaries.getString("FirstName") + " " + rowsSummaries.getString("LastName"),
						rowsSummaries.getString("Login"),
						rowsSummaries.getInt("SupervisorId"),
						rowsSummaries.getDouble("TotalAmount"),
						rowsSummaries.getDouble("TaxAmount"),
						rowsSummaries.getTimestamp("DateCreated"),
						rowsSummaries.getTimestamp("DateClosing"),
						rowsSummaries.getInt("Status"),
						rowsSummaries.getInt("FirstFiscalInvoice"),
						rowsSummaries.getInt("LastFiscalInvoice"),
						rowsSummaries.getDouble("cashFlow")
						));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return summaries;
	}
	
	protected static ArrayList<Vehicle> loadVehiclesInfo() {
		ArrayList<Vehicle> vehicles = null;
		Db db = new Db();
		ResultSet rowsVehicles = db.select("SELECT "
					+ "t.Name, "
					+ "r.Id, "
					+ "r.Amount, "
					+ "r.Tax "
				+ "FROM TransactionTypes t, Rates r "
				+ "WHERE t.Id = r.TransactionTypeId");
		
		vehicles = new ArrayList<Vehicle>();
		try {
			while(rowsVehicles.next()) {
				vehicles.add(new Vehicle(
						rowsVehicles.getInt("Id"),
						rowsVehicles.getString("Name"),
						rowsVehicles.getDouble("Amount"),
						rowsVehicles.getDouble("Tax")
						));
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		return vehicles;
	}
	
	protected static ArrayList<Transaction> loadTransactionTypes() {
		ArrayList<Transaction> transactionTypes = null;
		Db db = new Db();
		ResultSet rowsTransactionTypes = db.select("SELECT "
				+ "t.Name, "
				+ "r.Id, "
				+ "r.MaxAmount, "
				+ "r.Tax "
			+ "FROM TransactionTypes t, Rates r "
			+ "WHERE t.Id = r.TransactionTypeId");
		
		transactionTypes = new ArrayList<Transaction>();
		try {
			while(rowsTransactionTypes.next()) {
				transactionTypes.add(new Transaction(
						rowsTransactionTypes.getInt("Id"),
						rowsTransactionTypes.getString("Name"),
						rowsTransactionTypes.getDouble("MaxAmount"),
						rowsTransactionTypes.getDouble("Tax")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return transactionTypes;
	}
	
	protected static ArrayList<PayType> loadPayTypes() {
		ArrayList<PayType> payTypes = null;
		Db db = new Db();
		ResultSet rowsPayTypes = db.select("SELECT * FROM PayTypes");
		
		payTypes = new ArrayList<PayType>();
		
		try{
			while(rowsPayTypes.next()) {
				payTypes.add(new PayType(rowsPayTypes.getInt("Id"),
						rowsPayTypes.getString("Description")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return payTypes;
	}

	protected static boolean checkTicket(int ticketNumber) {
		Db db = new Db();
		boolean isTicketProcessed = false;
		ResultSet rowTicketProcessed = db.select("SELECT IFNULL(COUNT(*),0) as cnt "
				+ "FROM Transactions "
				+ "WHERE TicketNumber = " + ticketNumber);
		try {
			if(rowTicketProcessed.next()) {
				if(rowTicketProcessed.getInt("cnt") > 0) {
					isTicketProcessed = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isTicketProcessed;
	}


}
