package ve.com.soted.softparkmulti.objects;

import ve.com.soted.softparkmulti.db.Db;

public class StationType {
	
	private int id;
	private String name;
	
	public StationType(int id) {
		this.id = id;
		this.name = Db.getStationTypeName(id);
	}
	
	public StationType(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
