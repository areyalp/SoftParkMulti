package softparkmulti;

public class User {
	
	int id;
	int userTypeId;
	String name;
	String login;
	String userType;
	boolean logToProgram = false;
	boolean canCheckOut = false;
	boolean canPrintReportZ = false;
	boolean canPrintReportX = false;

	public User(int id, int userTypeId, String name, String login, String userType, boolean logToProgram,
			boolean canCheckOut, boolean canPrintReportZ, boolean canPrintReportX) {
		this.id = id;
		this.userTypeId = userTypeId;
		this.name = name;
		this.login = login;
		this.userType = userType;
		this.logToProgram = logToProgram;
		this.canCheckOut = canCheckOut;
		this.canPrintReportZ = canPrintReportZ;
		this.canPrintReportX = canPrintReportX;
	}
	
	public int getId() {
		return id;
	}

	public int getUserTypeId() {
		return userTypeId;
	}

	public String getName() {
		return name;
	}

	public String getLogin() {
		return login;
	}

	public String getUserType() {
		return userType;
	}

	public boolean isLogToProgram() {
		return logToProgram;
	}

	public boolean isCanCheckOut() {
		return canCheckOut;
	}

	public boolean isCanPrintReportZ() {
		return canPrintReportZ;
	}

	public boolean isCanPrintReportX() {
		return canPrintReportX;
	}

	public String toString() {
		return name;
	}

}
