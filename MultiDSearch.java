import java.util.*;
import java.io.*;

/**
 * The MultiDSearch class implements functions insert, find ,delete, topthree, addinterest, removeinterest, addrevenue, range, samesame, numberpurchases
 * @author  Akshay Thakare
 * @version 1.0, April 2015 
 */
public class MultiDSearch {
	//hashmap of Customer with id as key and Customer object as value
	HashMap<Long, Customer> hCustomer = new HashMap<>();
	//hashmap of Customer with category as key and TreeSet of Customer as value
	HashMap<Integer,TreeSet<Customer>> hCategoryAmount = new HashMap<>();
	//TreeSet of Customer with user defined comparator
	TreeSet<Customer> tCustomer=new TreeSet<>(new compAmount());
	static int[] categories;
	static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
	static int DEBUG = 9;
	private int phase = 0;
	private long startTime, endTime, elapsedTime;

	public static void main(String[] args)  throws FileNotFoundException {
		categories = new int[NUM_CATEGORIES];
		Scanner in;
		if(args.length > 0) {
			in = new Scanner(new File(args[0]));
		} else {
			in = new Scanner(System.in);
		}
		MultiDSearch x = new MultiDSearch();
		x.timer();
		long rv = x.driver(in);
		System.out.println(rv);
		x.timer();
	}

	/** Read categories from in until a 0 appears.
	 *  Values are copied into static array categories.  Zero marks end.
	 * @param in : Scanner from which inputs are read
	 * @return : Number of categories scanned
	 */
	public static int readCategories(Scanner in) {
		int cat = in.nextInt();
		int index = 0;
		while(cat != 0) {
			categories[index++] = cat;
			cat = in.nextInt();
		}
		categories[index] = 0;
		return index;
	}

	public long driver(Scanner in) {
		String s;
		long rv = 0, id;
		int cat;
		double purchase;

		while(in.hasNext()) {
			s = in.next();
			if(s.charAt(0) == '#') {
				s = in.nextLine();
				continue;
			}
			if(s.equals("Insert")) {
				id = in.nextLong();
				readCategories(in);
				rv += insert(id, categories);
			} else if(s.equals("Find")) {
				id = in.nextLong();
				rv += find(id);
			} else if(s.equals("Delete")) {
				id = in.nextLong();
				rv += delete(id);
			} else if(s.equals("TopThree")) {
				cat = in.nextInt();
				rv += topthree(cat);
			} else if(s.equals("AddInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += addinterests(id, categories);
			} else if(s.equals("RemoveInterests")) {
				id = in.nextLong();
				readCategories(in);
				rv += removeinterests(id, categories);
			} else if(s.equals("AddRevenue")) {
				id = in.nextLong();
				purchase = in.nextDouble();
				rv += addrevenue(id, purchase);
			} else if(s.equals("Range")) {
				double low = in.nextDouble();
				double high = in.nextDouble();
				rv += range(low, high);
			} else if(s.equals("SameSame")) {
				rv += samesame();
			} else if(s.equals("NumberPurchases")) {
				id = in.nextLong();
				rv += numberpurchases(id);
			} else if(s.equals("End")) {
				return rv % 997;
			} else {
				System.out.println("Houston, we have a problem.\nUnexpected line in input: "+ s);
				System.exit(0);
			}
		}
		// This can be inside the loop, if overflow is a problem
		rv = rv % MOD_NUMBER;

		return rv;
	}

	public void timer()
	{
		if(phase == 0) {
			startTime = System.currentTimeMillis();
			phase = 1;
		} else {
			endTime = System.currentTimeMillis();
			elapsedTime = endTime-startTime;
			System.out.println("Time: " + elapsedTime + " msec.");
			memory();
			phase = 0;
		}
	}

	public void memory() {
		long memAvailable = Runtime.getRuntime().totalMemory();
		long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
		System.out.println("Memory: " + memUsed/1000000 + " MB / " + memAvailable/1000000 + " MB.");
	}

	
	 //user defined comparator class to compare two Customer Objects
	 
	private static class compAmount implements Comparator<Customer> {
		/**
		 * This method compares two customers c1 and c2 on their amount attribute
		 * if amount attribute is same for them, then it compares using the id attribute
		 * @param Customer c1, c2
		 * @return int
		 */
		public int compare(Customer c1, Customer c2) {
			int res=Double.compare(c1.amount, c2.amount);
			if(res==0)
			{
				if(c1.id > c2.id)
				{
					return 1;
				}
				else if(c1.id < c2.id)
				{
					return -1;
				}
				else
				{
					return 0;
				}
			}
			return res;
		}
	}

	/**
	 * Method to insert the customer with given id and categories array into Customer HashMap
	 * @param id : Customer id and categories : Categories array to be inserted for the customer
	 * @return 1 if customer inserted successfully or -1 if customer is already present
	 */

	int insert(long id, int[] categories) { 
		if(!hCustomer.containsKey(id)){
			BitSet bCategories = new BitSet(1000);
			for(int i=0; i<categories.length;i++){
				if(categories[i]!=0){
					bCategories.set(categories[i]);
				}else{
					break;
				}
			}
			//Create a customer with given id, set its Category BitSet and amount and number of purchases are set to 0
			Customer c = new Customer(id,bCategories,0,0);
			//add this customer to Customer HashMap
			hCustomer.put(id,c);
			return 1;
		}
		return -1;
	}

	/**
	 * Method to find a specified customer with his/her id from Hashmap of Customer
	 * @param id : id of customer
	 * @return amount associated with the customer or -1 if no such customer present
	 */

	int find(long id) { 
		if(hCustomer.containsKey(id)){
			Customer c = hCustomer.get(id);
			double amount = c.getAmount();
			return (int)amount;
		}
		return -1;
	}

	/**
	 * Method to delete a specified customer with his/her id from Hashmap of Customer and TreeSet of Customer
	 * @param id : id of customer
	 * @return amount associated with the customer or -1 if no such customer present
	 */
	int delete(long id) { 
		if(hCustomer.containsKey(id)){
			Customer c = hCustomer.get(id);
			double amount = c.getAmount();
			//remove the customer from HashMap of customer
			hCustomer.remove(id);
			tCustomer.remove(c);
			if(amount>0){
				int i = c.getbCategories().nextSetBit(0);
				while(i!=-1){
					TreeSet<Customer> tCust = hCategoryAmount.get(i);
					//remove the customer from TreeSet of Customer
					if(tCust!=null)
					{
						if(tCust.contains(c))
						{
							tCust.remove(c);
						}
					}
					i=c.getbCategories().nextSetBit(i+1);
				}
			}
			return (int)amount;
		}
		return -1;
	}

	/**
	 * Method to get the addition of amount of the top three customers in particular category
	 * @param cat: category
	 * @return total sum of the amounts of top three customers
	 */

	int topthree(int cat) {
		TreeSet<Customer> treeCust = hCategoryAmount.get(cat);
		double amount=0.0;
		int count=0;
		if(treeCust!=null){
			for(Customer c:treeCust.descendingSet()){
				if(count<3){
					amount = amount + c.getAmount();
					count++;
				}else{
					break;
				}
			}
			return (int)amount;
		}
		return 0; 
	}

	/**
	 * Method to add interests for a particular customer
	 * @param id: Customer id and categories: array of Category to be added to the customer with given id 
	 * @return int : number of categories added
	 */

	int addinterests(long id, int[] categories) { 
		if(hCustomer.containsKey(id)){
			Customer c = hCustomer.get(id);
			tCustomer.remove(c);
			TreeSet<Customer> cust;
			int count=0;
			for(int i=0; i<categories.length;i++){
				if(categories[i]!=0){
					if(c.getbCategories().get(categories[i])!=true){// check if the bit is already set
						c.getbCategories().set(categories[i]);
						count+=1;
						//check if the category is already present in the HashMap of Categories
						if(hCategoryAmount.containsKey(i)){
							cust = hCategoryAmount.get(i);
							cust.add(c);
						}else{//if category is not present in the HashMap of Categories
							cust = new TreeSet<Customer>(new compAmount());
							cust.add(c);
							hCategoryAmount.put(i, cust);
						}
					}
				}else{
					break;
				}
			}
			//Add customer into TreeSet
			tCustomer.add(c);
			return count;
		}
		return -1;	
	}

	/**
	 * Method to remove the interests of a particular customer
	 * @param id: Customer id and categories: array of Category to be removed
	 * @return int: remaining number of categories
	 */

	int removeinterests(long id, int[] categories) { 
		if(hCustomer.containsKey(id)){
			Customer c = hCustomer.get(id);
			tCustomer.remove(c);
			for(int i=0; i<categories.length;i++){
				if(categories[i]!=0){//check if the bit is set
					if(c.getbCategories().get(categories[i])){
						if(c.getAmount()>0){
							TreeSet<Customer> cust = hCategoryAmount.get(i);
							if(cust!=null){// remove the Customer from TreeMap
								if(cust.contains(c)){
									cust.remove(c);
								}
							}
						}
					}
				}else{
					break;
				}
			}

			for(int i =0;i<categories.length;i++)
			{
				if(categories[i]!=0)//check if the bit is set
				{
					if(c.getbCategories().get(categories[i]))
					{
						c.getbCategories().set(categories[i],false);//clear the bit if it is set
					}
				}
				else
				{
					break;
				}

			}
			tCustomer.add(c);
			BitSet bCategories = c.getbCategories();
			return bCategories.cardinality(); // return the remaining set bits from BitSet of Categories
		}
		return -1;	
	}

	/**
	 * Method to add purchase amount to the amount attribute of the customer
	 * @param id: Customer id and purchase: new purchase amount
	 * @return amount after adding purchase amount
	 */

	int addrevenue(long id, double purchase) { 
		//delete the specified customer from the HashMap 
		if(hCustomer.containsKey(id))
		{
			Customer c = hCustomer.get(id);
			int i = c.getbCategories().nextSetBit(0);
			while(i!=-1)
			{
				TreeSet<Customer> tCust = hCategoryAmount.get(i);
				if(tCust!=null){
					if(tCust.contains(c)){
						tCust.remove(c);
					}
				}
				i=c.getbCategories().nextSetBit(i+1);
			}
			tCustomer.remove(c);
			//update the customer object
			double amount = c.getAmount();
			amount= amount+purchase;
			c.setAmount(amount);
			int numpurchases = c.getNumberOfPurchases();
			c.setNumberOfPurchases(numpurchases+1);
			//add the updated customer object to the HashMap
			i=c.getbCategories().nextSetBit(0);
			while(i!=-1)
			{
				if(hCategoryAmount.containsKey(i)){
					TreeSet<Customer> cust = hCategoryAmount.get(i);
					cust.add(c);
				}else{
					TreeSet<Customer> cust = new TreeSet<>(new compAmount());
					cust.add(c);
					hCategoryAmount.put(i, cust);
				}
				i=c.getbCategories().nextSetBit(i+1);
			}
			tCustomer.add(c);
			return (int) c.getAmount();
		}
		return -1;	
	}

	/**
	 * Method to get number of customers whose amount is between given two values
	 * @param low,high:  values of amount 
	 * @return int : number of customers
	 */

	int range(double low, double high) { 
		int count = 0;
		high = high + 0.001;
		BitSet tempCatSet = new BitSet();
		Customer cLow = new Customer((long)-1,tempCatSet,low,0); 
		Customer cHigh = new Customer((long)-2,tempCatSet,high,0);
		//subSet returns a view of the portion of this set whose elements falls in given range
		count = tCustomer.subSet(cLow, true, cHigh, true).size();
		return count; 
	}

	/**
	 * Method to find customers who have exactly the same set of 5 or more categories of interest
	 * @param 
	 * @return int
	 */

	int samesame() { 
		HashMap<BitSet,Long> hsamesame=new HashMap<BitSet,Long>();
		Long val=(long)0;
		//iterate over each customer
		for(Long l:hCustomer.keySet())
		{
			//get the categories of each customer
			BitSet bCategories=hCustomer.get(l).getbCategories();
			//hash if and only if the number of interest of customer is greater than or equal to 5 
			if(bCategories.cardinality()>=5)
				//Check if customer category is hashed
				if(hsamesame.containsKey(bCategories))
				{
					////if the category is hashed, increment the value at that key by 1
					val=hsamesame.get(bCategories)+1;
					hsamesame.put(bCategories, val);
				}
				else{
					//else insert value as 1
					hsamesame.put(bCategories, (long)1);
				}
		}
		int samesame=0;
		for(Long samecount:hsamesame.values())
		{
			if(samecount>1)
				samesame=(int)(samesame+samecount);
		}
		return samesame;
	}

	/**
	 * Method to find the number of times customer has purchased products
	 * @param id: Customer id 
	 * @return int: number of times customer purchased or '-1' if such customer id does'nt exist
	 */

	int numberpurchases(long id) { 
		if(hCustomer.containsKey(id)){
			Customer c = hCustomer.get(id);
			return c.getNumberOfPurchases();
		}
		return -1;
	}
}