package storeSqlPackage;


import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.Scanner;

public class Store{
	
	enum StatusCode{
		ProductAdded,
		ProductRemoved, 
		QuantityUpdated,
		PurchaseCompleted,
		PurchaseCancelled,
		CustomerAddedSuccessfully,
		CustomerInsertedSuccessfully,
		QuantityUpdatedFailed,
		ProductAddedFailed,
		CartCreated,
		CartCreationFailed,
		PurchaseCancelledFailed
	}
	
	protected Stock stock;
	protected Cart cart;
	protected Customer[] customers;
	protected int numOfCustomers;
	
	public Store(Stock stock, Cart cart) {
	    this.stock = stock;
	    this.cart = cart;
	    this.customers = new Customer[0];
		this.numOfCustomers = 0;
	}

	
	public String showStock() {
		Product [][] allProducts = cart.sortByCategory(stock.productsInStock);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-20s", "Author"))
		.append(String.format("%-8s", "Pages"))
		.append(String.format("%-8s", "Price"))
		
		.append("\n");
		for (int i = 0; i < allProducts[0].length; i++) {
			sb.append(allProducts[0][i]).append("\n");
		}
		
		sb.append("\n").append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-15s", "Size"))
		.append(String.format("%-20s", "Color"))
		.append(String.format("%-10s", "Gender"))
		.append(String.format("%-8s", "Price"))
		
		
		.append("\n");
		for (int i = 0; i < allProducts[1].length; i++) {
			sb.append(allProducts[1][i]).append("\n");
		}
		
		sb.append("\n").append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-10s", "Brand"))
		.append(String.format("%-30s", "Model"))
		.append(String.format("%-8s", "Price"))
		
		.append("\n");
		for (int i = 0; i < allProducts[2].length; i++) {
			sb.append(allProducts[2][i]).append("\n");
		}
		return sb.toString();
	}

	public String showAvailable() {
		Product [][] allProducts = cart.sortByCategory(stock.productsInStock);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-20s", "Author"))
		.append(String.format("%-8s", "Pages"))
		.append(String.format("%-8s", "Price"))
		.append("\n");
		for (int i = 0; i < allProducts[0].length; i++) {
			if (this.checkAvailability(allProducts[0][i])) {
				sb.append(allProducts[0][i]).append("\n");
			}
		}
		
		sb.append("\n").append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-15s", "Size"))
		.append(String.format("%-20s", "Color"))
		.append(String.format("%-10s", "Gender"))
		.append(String.format("%-8s", "Price"))
		.append("\n");
		for (int i = 0; i < allProducts[1].length; i++) {
			if (this.checkAvailability(allProducts[1][i])) {
				sb.append(allProducts[1][i]).append("\n");
			}
		}
		
		sb.append("\n").append(String.format("%-3s", "ID"))
		.append(String.format("%-15s", "Category"))
		.append(String.format("%-40s", "Name"))
		.append(String.format("%-10s", "Available"))
		.append(String.format("%-8s", "In Cart"))
		.append(String.format("%-15s", "Actual"))
		.append(String.format("%-10s", "Brand"))
		.append(String.format("%-30s", "Model"))
		.append(String.format("%-8s", "Price"))
		.append("\n");
		for (int i = 0; i < allProducts[2].length; i++) {
			if (this.checkAvailability(allProducts[2][i])) {
				sb.append(allProducts[2][i]).append("\n");
			}
		}
		return sb.toString();
	}
	
	private boolean checkAvailability(Product product) {
		if (!product.inStock()) {
			return false;
		}
		for (int i = 0; i < cart.numOfProducts; i++) {
			if (cart.products[i].equals(product)) {
				return false;
			}
		}
		return true;
	}

	public StatusCode addToCart(int id,int quantity) throws OnlineStoreGeneralException {
		if (id < 1 || id > stock.numOfProducts) {
			throw new OnlineStoreGeneralException("ID - "+ id+ " not in range");
		}
		if(cart.numOfProducts+1 > cart.maximumProducts) {
			throw new ReachedMaxAmountException(cart.numOfProducts);
		}
		if (quantity <= 0) {
			throw new OnlineStoreGeneralException("Invalid Quantity");
		}
		for (int i = 0; i < cart.numOfProducts; i++) {
			if(cart.products[i].getId() == id) {
				throw new CartProductAlreadyExistException(id);
			}
		}

		if (stock.productsInStock[id-1].getAvailable()-quantity < 0) {
			throw new ProductQuantityNotAvailableException(id);
		}
		Product productCart;
		if (stock.productsInStock[id-1] instanceof Books) {
			productCart = new Books((Books) stock.productsInStock[id-1]);
		}
		else if (stock.productsInStock[id-1] instanceof Clothing) {
			productCart = new Clothing((Clothing) stock.productsInStock[id-1]);
		}
		else if (stock.productsInStock[id-1] instanceof Electronics) {
			productCart = new Electronics((Electronics) stock.productsInStock[id-1]);
		}
		else {
			throw new OnlineStoreGeneralException("Category Missing");
		}
		
		productCart.setQuantity(quantity);
		stock.productsInStock[id-1].reserve(quantity);
		cart.products[cart.numOfProducts] = productCart;
		cart.numOfProducts++;
		return StatusCode.ProductAdded;
	}

	public StatusCode updateQuantity(int id, int quantity) throws OnlineStoreGeneralException {
		if (id < 1 || id > stock.numOfProducts) {
			throw new OnlineStoreGeneralException("ID - "+ id+ " not in range");
		}
		if (quantity < 0) {
			throw new OnlineStoreGeneralException("Negative Quantity");
		}
		
		if (quantity > stock.productsInStock[id-1].getAvailable()) {
			throw new ProductQuantityNotAvailableException(id);
		}
		if (quantity == 0) {
					this.removeFromCart(id);
		}
		else{
			boolean found = false;
			for (int i = 0; i < cart.numOfProducts; i++) {
				if (cart.products[i].getId() == id) {
					stock.productsInStock[id-1].reserve(quantity);
					cart.products[i].setQuantity(quantity);
					found = true;
				}
			}
			if (!found) {
				throw new CartProductNotExistException(id);
			}
		}
		
		return StatusCode.QuantityUpdated;
	}

	public StatusCode removeFromCart(int id) throws OnlineStoreGeneralException {

		if(id < 1 || id > stock.numOfProducts) {
			throw new OnlineStoreGeneralException("ID - "+ id+ " not in range");
		}
		Product[] newProducts = new Product[cart.numOfProducts];
		int index = 0;
		boolean found = false;

		for (int i = 0; i < cart.numOfProducts; i++) {
			if (cart.products[i].getId() == id) {
				stock.returnItem(id, cart.products[i].getQuantity());
				found = true;
			}
			else {
				newProducts[index] = cart.products[i];
				index++;
			}
		}

		if (!found) {
			throw new CartProductNotExistException(id);
		}

		cart.products = Arrays.copyOf(newProducts, cart.maximumProducts);
		cart.numOfProducts--;
		return StatusCode.ProductRemoved;
	}
	
	public StatusCode createOrder() {
		int orderId=0;
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "INSERT INTO Orders (cid) VALUES (?) RETURNING order_id;";
			PreparedStatement pstmt = conn.prepareStatement(query);
			
			pstmt.setInt(1, this.cart.customerId);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				orderId = rs.getInt("order_id");
			}
			this.cart.orderId = orderId;
			return StatusCode.CartCreated;
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		return StatusCode.CartCreationFailed;
	}
	
	public StatusCode purchaseCart() throws OnlineStoreGeneralException {
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "INSERT INTO OrderProducts (order_id, product_id, quantity, price_per_unit) VALUES (?, ?, ?, ?) RETURNING order_product_id;";
			PreparedStatement pstmt = conn.prepareStatement(query);
			ResultSet rs;
			
			for (int i=0; i < this.cart.numOfProducts; i++) {
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, this.cart.orderId);
				pstmt.setInt(2, this.cart.products[i].id);
				pstmt.setInt(3, this.cart.products[i].quantity);
				pstmt.setDouble(4, this.cart.products[i].price);
				
				pstmt.executeQuery();
			}
			
			query = "SELECT available FROM products WHERE id=?;";
			for (int i=0; i < this.cart.numOfProducts; i++) {
				pstmt = conn.prepareStatement(query);
				pstmt.setInt(1, cart.products[i].id);
				
				rs = pstmt.executeQuery();
				if (rs.next()) {
					this.stock.productsInStock[cart.products[i].id-1].setQuantity(rs.getInt("available"));
				}
			}
			
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		
		return StatusCode.PurchaseCompleted;
	}
	
	public StatusCode purchaseCancel() throws OnlineStoreGeneralException {
		for (int i = 0; i < this.cart.numOfProducts; i++) {
			this.stock.returnItem(this.cart.products[i].id, this.cart.products[i].quantity);
		}
		
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "DELETE FROM Orders WHERE order_id=? RETURNING order_id;";
			PreparedStatement pstmt = conn.prepareStatement(query);
			
			pstmt.setInt(1, this.cart.orderId);
			pstmt.executeQuery();
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		return StatusCode.PurchaseCancelled;
		
	}
	
	public void readCustomers() throws IOException, ClassNotFoundException{
		try {
			Connection conn = DatabaseConfig.connection;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM customers");
			Customer c;
			
			while (rs.next()) {
				c = new Customer(rs.getInt("cid"),rs.getString("first_name"),rs.getString("last_name"));
				addCustomer(c);
			}
			
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public StatusCode insertCustomer(String fname, String lname) {
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "INSERT INTO customers (first_name, last_name) VALUES (?,?) RETURNING cid;";
			PreparedStatement pstmt = conn.prepareStatement(query);
			int generatedId = -1;
			
			pstmt.setString(1,fname);
			pstmt.setString(2, lname);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
                generatedId = rs.getInt("cid");
                Customer customer = new Customer(generatedId,fname, lname);
                System.out.println(addCustomer(customer));
                return StatusCode.CustomerInsertedSuccessfully;
            }
			
			
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		

	    return StatusCode.CustomerInsertedSuccessfully;
	}
	public StatusCode addCustomer(Customer customer) {
	    if (numOfCustomers >= customers.length) {
	        customers = Arrays.copyOf(customers, customers.length +1);
	    }
	    customers[numOfCustomers] = customer;
	    numOfCustomers++;

	    return StatusCode.CustomerAddedSuccessfully;
	}
	
	public String showCustomers() {
		if (numOfCustomers <= 0)
			return "No customers!";
		
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", "ID"))
		.append(String.format("%-40s", "First Name"))
		.append(String.format("%-40s", "Last Name"))
		.append("\n");
		for (int i = 0; i < numOfCustomers; i++) {
			sb.append(customers[i]).append("\n");
		}
		
		return sb.toString();
	}


	public StatusCode insertProduct(String name, int quantity, double price, int type, Scanner s) {
		
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "INSERT INTO products (name, available, quantity, last_update, price, product_type) VALUES (?,?,?,CURRENT_DATE,?,?) RETURNING id;",query2;
			PreparedStatement pstmt = conn.prepareStatement(query),pstmt2;
			int generatedId = -1;
			
			ResultSet rs;
			
			pstmt.setString(1,name);
			pstmt.setInt(2,quantity);
			pstmt.setInt(3,quantity);
			pstmt.setDouble(4, price);
			s.nextLine();
			if (type == 1) {
				pstmt.setString(5, "Book");
				System.out.println("Enter author:");
				String author = s.nextLine();
				System.out.println("Enter number of pages:");
				int pages = s.nextInt();
				query2 = "INSERT INTO Books (product_id, author, pages) VALUES (?, ?, ?) RETURNING product_id;";
				pstmt2 = conn.prepareStatement(query2);
				pstmt2.setString(2, author);
				pstmt2.setInt(3, pages);
				
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
	                generatedId = rs.getInt("id");
	                pstmt2.setInt(1, generatedId);
	                rs = pstmt2.executeQuery();
	                Product p = new Books(generatedId,name,quantity,author,pages,price);
	                stock.addProduct(p);
	            }
			}
			
			else if (type == 2) {
				pstmt.setString(5, "Clothing");
				System.out.println("Enter size:");
				String size = s.nextLine();
				System.out.println("Enter color:");
				String color = s.nextLine();
				System.out.println("Choose gender");
				System.out.println("1.MALE");
				System.out.println("2.FEMALE");
				System.out.println("3.NO GENDER");
				int g = s.nextInt();
				while (g != 1 && g != 2 && g != 3) {
					System.out.println("Type have to be between 1 to 3, enter again");
					System.out.println("1.MALE");
					System.out.println("2.FEMALE");
					System.out.println("3.NO GENDER");
					g = s.nextInt();
				}
				String gender;
				if (g == 1) {
					gender = "Male";
				}
				else if (g == 2) {
					gender = "Female";
				}
				else {
					gender = "No-Gender";
				}
				query2 = "INSERT INTO Clothing (product_id, size, color, gender) VALUES (?, ?, ?, ?) RETURNING product_id;";
				pstmt2 = conn.prepareStatement(query2);
				pstmt2.setString(2,size);
				pstmt2.setString(3, color);
				pstmt2.setObject(4, gender, java.sql.Types.OTHER);
				
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
	                generatedId = rs.getInt("id");
	                pstmt2.setInt(1, generatedId);
	                rs = pstmt2.executeQuery();
	                Product p = new Clothing(generatedId,name,quantity,size,color,gender,price);
	                stock.addProduct(p);
	            }
				
			}
			
			else {
				pstmt.setString(5, "Electronics");
				System.out.println("Enter brand:");
				String brand = s.nextLine();
				System.out.println("Enter Model:");
				String model = s.nextLine();
				query2 = "INSERT INTO Electronics (product_id, brand, model) VALUES (?, ?, ?) RETURNING product_id;";
				pstmt2 = conn.prepareStatement(query2);
				pstmt2.setString(2,brand);
				pstmt2.setString(3, model);
				
				rs = pstmt.executeQuery();
				
				if (rs.next()) {
	                generatedId = rs.getInt("id");
	                pstmt2.setInt(1, generatedId);
	                rs = pstmt2.executeQuery();
	                Product p = new Electronics(generatedId,name,quantity,brand,model,price);
	                stock.addProduct(p);
	            }
			}
			return StatusCode.ProductAdded;
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		
		return StatusCode.ProductAddedFailed;
		
	}
	
	public StatusCode updateProductQuantity(int id, int quantity) throws OnlineStoreGeneralException{
		if (id > this.stock.numOfProducts || id < 1) {
			throw new OnlineStoreGeneralException("ID - "+ id+ " not in range");
		}
		try {
			Connection conn = DatabaseConfig.connection;
			String query = "UPDATE products SET quantity=?,available=? WHERE id=? RETURNING id;";
			PreparedStatement pstmt = conn.prepareStatement(query);
			int generatedId = -1;
			
			pstmt.setInt(1,quantity);
			pstmt.setInt(2, quantity);
			pstmt.setInt(3, id);
			
			ResultSet rs = pstmt.executeQuery();
			
			if (rs.next()) {
                generatedId = rs.getInt("id");
                if (generatedId == id) {
                	this.stock.productsInStock[id-1].setQuantity(quantity);
                	this.stock.productsInStock[id-1].setAvailable(quantity);
                	return StatusCode.QuantityUpdated;
                }
            }
			
			
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
		return StatusCode.QuantityUpdatedFailed;
	}

	


}
