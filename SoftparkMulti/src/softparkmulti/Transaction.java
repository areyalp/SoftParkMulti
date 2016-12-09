package softparkmulti;

public class Transaction {
	private int id;
	private String name;
	private double maxAmount;
	private double tax;
	
	
	public Transaction(int id, String name, double maxAmount, double tax) {
		this.id = id;
		this.name = name;
		this.maxAmount = maxAmount;
		this.tax = tax;
	}
	
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public double getMaxAmount() {
		return maxAmount;
	}

	public double getTax() {
		return tax;
	}

	public String toString() {
		return name;
	}
	
}
