package softparkmulti;

public class Vehicle{
	
	int id;
	String name;
	double price;
	double tax;

	public Vehicle(int id, String name, double price, double tax) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.tax = tax;
	}
	
	public int getId()
	{
		return id;
	}

	public String getName() {
		return name;
	}

	public double getPrice() {
		return price;
	}
	
	public double getTax() {
		return tax;
	}

	public String toString()
	{
		return name;
	}

}
