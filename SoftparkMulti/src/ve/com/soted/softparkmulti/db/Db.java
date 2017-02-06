package ve.com.soted.softparkmulti.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
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

import ve.com.soted.softparkmulti.objects.PayType;
import ve.com.soted.softparkmulti.objects.Station;
import ve.com.soted.softparkmulti.objects.StationType;
import ve.com.soted.softparkmulti.objects.Summary;
import ve.com.soted.softparkmulti.objects.Transaction;
import ve.com.soted.softparkmulti.objects.User;
import ve.com.soted.softparkmulti.objects.Vehicle;

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
	
	public boolean testConnection() {
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
	
	public ResultSet select(String queryString) {
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
	
	public int insert(String queryString) {
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
	
	public boolean update(String queryString) {
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
	
	public int insertTransactionsIn(int entranceStationId, Timestamp entranceDateTime, String plate, int transactionTypeId) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntranceStationId,EntranceDateTime,Plate)"
				+ " VALUES (" + entranceStationId + "," 
				+ entranceDateTime + ","
				+ plate +   ")";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId,TypeId)"
					+ " VALUES (" + insertedId + "," 
					+ transactionTypeId + ")";
			this.insert(sql);
		}
		return insertedId;
	}
	
	public int insertTransactionsOut(int entranceStationId, int summaryId,	
			double totalAmount, double taxAmount, int transactionTypeId, int payTypeId, int printed) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntranceStationId,TicketNumber,SummaryId,TotalAmount,Printed)"
				+ " VALUES (" + entranceStationId + "," 
				+ summaryId + "," 
				+ totalAmount + ","
				+ printed + ")";
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
	
	public int insertExonerated(int entranceStationId, int summaryId,	
			double totalAmount, double taxAmount, int transactionTypeId, int payTypeId, int printed, boolean exonerated) {

		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntranceStationId,SummaryId,TotalAmount,Printed,Exonerated)"
				+ " VALUES (" + entranceStationId + "," 
				+ summaryId + "," 
				+ totalAmount + ","
				+ printed +  ","
				+ (exonerated?1:0) + ")";
		insertedId = this.insert(sql);
		if(insertedId > 0) {
			sql = "INSERT INTO TransactionsDetail (TransactionId,TypeId,TotalAmount,TaxAmount)"
					+ " VALUES (" + insertedId + "," 
					+ transactionTypeId + ","
					+ totalAmount + ","
					+ taxAmount + ")";
			this.insert(sql);			
		}
		return insertedId;
	}
	
	public int preInsertTransaction(int stationId) {
		
		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntryStationId) VALUES(" + stationId + ")";
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
	
	public int preTransactionIn(int entranceStationId, String plate) {
		
		String sql;
		int insertedId = 0;
		sql = "INSERT INTO Transactions (EntranceStationId,Plate) VALUES(" + entranceStationId + ",'" + plate + "')";
		insertedId = this.insert(sql);
		return insertedId;
	}
	
	public int transactionIn (int entranceStationId, String plate){		
		
		String sql;
		int insertedId = 0;
		sql = "UPDATE Transactions SET (EntkranceStationId, Plate) VALUES('" + entranceStationId + "','" + plate  + "')";
		insertedId = this.insert(sql);
		return insertedId;
		
	}
	//
	
	public int insertTransaction(int stationId, int summaryId, int ticketNumber, double totalAmount, double taxAmount,
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
	
	public int insertSummary(int stationId, int userId, int firstInvoiceNumber) {
		String sql;
		int insertedSummaryId = 0;
		
		sql = "INSERT INTO Summary (StationId,UserId,FirstFiscalInvoice)"
				+ " VALUES (" + stationId + ","
				+ userId + ","
				+ firstInvoiceNumber + ")";
		insertedSummaryId = this.insert(sql);
		return insertedSummaryId;
	}
	
	public static boolean closeSummary(int summaryId, int supervisorId) {
		Db db = new Db();
		boolean updatedSummary = db.update("UPDATE Summary"
				+ " SET Status = 1, SupervisorId = " + supervisorId + ", DateClosing = NOW()"
				+ " WHERE Id = " + summaryId);
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return updatedSummary;
	}

	public static ArrayList<Station> getStationsWithSummary() {
		ArrayList<Station> stations;
		Db db = new Db();
		ResultSet rowsStations = db.select("SELECT su.StationId, st.TypeId, st.Name, st.LevelId"
				+ " FROM Summary as su, Stations as st"
				+ " WHERE su.StationId = st.id AND Status = 0 GROUP BY su.StationId");
		stations = new ArrayList<Station>();
		try {
			while(rowsStations.next()) {
				stations.add(new Station(rowsStations.getInt("StationId"),
						new StationType(rowsStations.getInt("TypeId")),
						rowsStations.getString("Name"),
						rowsStations.getInt("LevelId")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return stations;
	}
	
	public static int getSummaryId(int userId, int stationId) {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vaultSummaryId;
	}
	
	public int countSummaryInvoices(int vaultSummaryId) {
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
	
	public static int getUserId(String username) {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return userId;
	}
	
	public User loadUserInfo(int userId) {
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
	
	public static ArrayList<Summary> loadSummaries() {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return summaries;
	}
	
	public static ArrayList<Vehicle> loadVehiclesInfo() {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vehicles;
	}
	
	public static ArrayList<Transaction> loadAllTransactions() {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return transactionTypes;
	}
	
	public static ArrayList<Transaction> loadTransactionsOutTypes() {
		ArrayList<Transaction> transactionsOutTypes = null;
		Db db = new Db();
		ResultSet rowsTransactionsOutTypes = db.select("SELECT "
				+ "t.Name, "
				+ "r.Id, "
				+ "r.MaxAmount, "
				+ "r.Tax "
			+ "FROM TransactionTypes t, Rates r "
			+ "WHERE t.Id = r.TransactionTypeId");
		
		transactionsOutTypes = new ArrayList<Transaction>();
		try {
			while(rowsTransactionsOutTypes.next()) {
				transactionsOutTypes.add(new Transaction(
						rowsTransactionsOutTypes.getInt("Id"),
						rowsTransactionsOutTypes.getString("Name"),
						rowsTransactionsOutTypes.getDouble("MaxAmount"),
						rowsTransactionsOutTypes.getDouble("Tax")
						));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return transactionsOutTypes;
	}
	
	public static String getConfig(String name, String type){
		Db db = new Db();
		String configValue = "";
		String query = "SELECT Value FROM Configs WHERE Name = '" +  name  + "' AND Type = '" + type + "'";
		ResultSet rowsConfigValue = db.select(query);
		try {
			if(rowsConfigValue.next()) {
				configValue = rowsConfigValue.getString("Value");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return configValue;
	}
	
	public int getHourRates(int cantHoras){
		int hourAmount = 0;
		String query = "SELECT Amount FROM rateamounts WHERE RangeTo = '" +  cantHoras + "'";
		ResultSet rowsRatesAmount = this.select(query);
		try {
			if(rowsRatesAmount.next()) {
				hourAmount = rowsRatesAmount.getInt("Amount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hourAmount;
	}
	
	public int getFractionRates(int cantHoras){
		
		Db db = new Db();
		int fractionAmount = 0;
		ResultSet rowsFractionRatesAmount = db.select("SELECT Fraction FROM rateamounts WHERE RangeFrom = '" +  cantHoras + "'");
		try {
			if(rowsFractionRatesAmount.next()) {
				fractionAmount = rowsFractionRatesAmount.getInt("Fraction");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return fractionAmount;
	}
	
	public int getOvernightRates(String overnight){
		
		Db db = new Db();
		int overnightAmount = 0;
		ResultSet rowsOvernightRatesAmount = db.select("SELECT MaxAmount FROM rates WHERE Name = '" +  overnight + "'");
		try {
			if(rowsOvernightRatesAmount.next()) {
				overnightAmount = rowsOvernightRatesAmount.getInt("MaxAmount");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return overnightAmount;
	}
	
	public String getPlate (int ticketNumber){
		
		Db db = new Db();
		String plate = "";
		ResultSet rowsPlate = db.select("SELECT Plate FROM transactions WHERE Id = '" +  ticketNumber + "'");
		try {
			if(rowsPlate.next()) {
				plate = rowsPlate.getString("Plate");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return plate;
		
	}
	
	public static ArrayList<PayType> loadPayTypes() {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return payTypes;
	}

	public static boolean checkTicket(int ticketNumber) {
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
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return isTicketProcessed;
	}

	public static boolean isTicketIn(int ticketNumber) {
		Db db = new Db();
		boolean isTicketIn = false;
		ResultSet rowTicketIn = db.select("SELECT IFNULL(COUNT(*),0) as cnt "
				+ "FROM Transactions "
				+ "WHERE transactions.Id = " + ticketNumber
				+ " AND Exited = 0");		
		try {
			if(rowTicketIn.next()) {
				if(rowTicketIn.getInt("cnt") > 0) {
					isTicketIn = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return isTicketIn;
	}

	public static boolean isTicketOut(int ticketNumber) {
		Db db = new Db();
		boolean isTicketOut = false;
		ResultSet rowTicketOut = db.select("SELECT IFNULL(COUNT(*),0) as cnt "
				+ "FROM Transactions "
				+ "WHERE transactions.TicketNumber = " + ticketNumber);		//TODO check this method to verify when the ticket is out
		try {
			if(rowTicketOut.next()) {
				if(rowTicketOut.getInt("cnt") > 0) {
					isTicketOut = true;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return isTicketOut;
	}

	public static int getAvailablePlaces(int levelId) {
		return (Db.getLevelPlaces(levelId) - Db.getVehiclesIn(levelId));
	}
	
	public static int getLevelPlaces(int levelId) {
		Db db = new Db();
		int places = 0;
		ResultSet rowsPlaces = db.select("SELECT Places FROM levels WHERE id = " +  levelId);
		try {
			if(rowsPlaces.next()) {
				places = rowsPlaces.getInt("Places");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return places;
	}
	
	public static int getVehiclesIn(int levelId){
		Db db = new Db();
		int vehiclesIn = 0;
		ResultSet rowsVehiclesIn = db.select("SELECT COUNT(1) as cnt FROM transactions "
				+ "JOIN stations ON stations.Id = transactions.EntranceStationId "
				+ "WHERE Exited = 0 AND LevelId = " + levelId);
		try {
			if(rowsVehiclesIn.next()) {
				vehiclesIn = rowsVehiclesIn.getInt("cnt");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return vehiclesIn;
	}
	
	public ArrayList<Integer> getParkingPlaces(int boardId) {
		//TODO have to fix this method and create the storeProcedure in the DB
		ArrayList<Integer> ParkingPlaces = new ArrayList<Integer>();
		ResultSet setBoardSwitchMainIds;
		CallableStatement statement;
		
		try {
			statement = this.conn.prepareCall("{call SP_GET_PARKING_PLACES(?)}");
			statement.setInt(1, boardId);
			setBoardSwitchMainIds = statement.executeQuery();
			
			while(setBoardSwitchMainIds.next()) {
				ParkingPlaces.add(setBoardSwitchMainIds.getInt(1));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return ParkingPlaces;
	}

	public static String getStationTypeName(int stationTypeId) {
		Db db = new Db();
		String name = "";
		ResultSet rowName = db.select("SELECT Name FROM stationstype WHERE id = " +  stationTypeId);
		try {
			if(rowName.next()) {
				name = rowName.getString("Name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			db.conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return name;
	}

}
