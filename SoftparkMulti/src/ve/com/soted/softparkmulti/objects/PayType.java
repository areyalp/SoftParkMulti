package ve.com.soted.softparkmulti.objects;

public class PayType {
	
	int id;
	String description;

	public PayType(int id, String description) {
		this.id = id;
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

}
