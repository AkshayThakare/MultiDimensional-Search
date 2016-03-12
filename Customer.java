import java.util.BitSet;
/**
 * The Customer class is used to create customer object with attributes: id, amount, categories, number of purchases
 * @author  Akshay Thakare
 * @version 1.0, April 2015 
 */
public class Customer{
	long id;
	double amount;
	BitSet bCategories;
	int numberOfPurchases;
	public Customer(long id, BitSet categories, double amount, int numberOfPurchases){
		this.id=id;
		this.amount=amount;
		this.bCategories = categories;
		this.numberOfPurchases = numberOfPurchases;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public double getAmount() {
		return amount;
	}
	public void setAmount(double amount) {
		this.amount = amount;
	}
	public BitSet getbCategories() {
		return bCategories;
	}
	public void setbCategories(BitSet bCategories) {
		this.bCategories = bCategories;
	}
	public int getNumberOfPurchases() {
		return numberOfPurchases;
	}
	public void setNumberOfPurchases(int numberOfPurchases) {
		this.numberOfPurchases = numberOfPurchases;
	}

}
