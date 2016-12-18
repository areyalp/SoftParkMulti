package softparkmulti;
public class Item {
	int id;
	String name;

	Item(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		// very important. this is what shows in combobox
		return name;
	}
}