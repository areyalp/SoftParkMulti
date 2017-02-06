package ve.com.soted.softparkmulti.objects;

public class User {
	
	private int id;
	private int userTypeId;
	private String name;
	private String login;
	private String userType;
	private boolean logToProgram = false;
	private boolean canCheckOut = false;
	private boolean canPrintReportZ = false;
	private boolean canPrintReportX = false;

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

	public boolean canLogToProgram() {
		return logToProgram;
	}

	public boolean canCheckOut() {
		return canCheckOut;
	}

	public boolean canPrintReportZ() {
		return canPrintReportZ;
	}

	public boolean canPrintReportX() {
		return canPrintReportX;
	}

	public String toString() {
		return name;
	}

}
