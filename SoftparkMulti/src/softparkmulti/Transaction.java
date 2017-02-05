package softparkmulti;

import java.util.ArrayList;

public class Transaction {
	private Integer id;
	private String name;
	private Double maxAmount;
	private Double tax;
	
	
	public Transaction(Integer id, String name, Double maxAmount, Double tax) {
		this.id = id;
		this.name = name;
		this.maxAmount = maxAmount;
		this.tax = tax;
	}
	
	public Integer getId() {
		return id;
	}

	public String getName() {
		return name;
	}
	
	public Double getMaxAmount() {
		return maxAmount;
	}

	public Double getTax() {
		return tax;
	}

	public String toString() {
		return name;
	}
	
	public static Double getTotalAmount(ArrayList<Transaction> transactions) {
		Double total = 0.00;
		for(Transaction t:transactions) {
			total += t.getMaxAmount();
		}
		return total;
	}
	
}
