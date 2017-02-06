package softparkmulti;

public class StationType {
	
	private Integer id;
	private String name;
	
	public StationType(Integer id) {
		this.id = id;
		this.name = Db.getStationTypeName(id);
	}
	
	public StationType(Integer id, String name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
