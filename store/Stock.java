package storeSqlPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;

import storeSqlPackage.Clothing.Gender;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Stock{

	protected Product[] productsInStock;
	protected int numOfProducts;
	
	public Stock() throws IOException, ClassNotFoundException {
		try {
			Connection conn = DatabaseConfig.connection;
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as size FROM products");
			int count = 0;
			if (rs.next()) {
				count = rs.getInt("size");
			}
			productsInStock = new Product[0];
			numOfProducts = 0;
			productsInStock = Arrays.copyOf(productsInStock, count);
			Product p;
			int id;
			rs = stmt.executeQuery("SELECT * FROM BookProducts");
			while (rs.next()) { 
				id = rs.getInt("product_id");
				p = new Books(id,rs.getString("name"),rs.getInt("available"),rs.getString("author"), rs.getInt("pages"), rs.getDouble("price"));
				productsInStock[id-1] = p;
				numOfProducts++;
			}
			
			rs = stmt.executeQuery("SELECT * FROM ElectronicsProducts");
			while (rs.next()) { 
				id = rs.getInt("product_id");
				p = new Electronics(id,rs.getString("name"),rs.getInt("available"),rs.getString("brand"), rs.getString("model"), rs.getDouble("price"));
				productsInStock[id-1] = p;
				numOfProducts++;
			}
			
			rs = stmt.executeQuery("SELECT * FROM ClothingProducts");
			while (rs.next()) { 
				id = rs.getInt("product_id");
				p = new Clothing(id,rs.getString("name"),rs.getInt("available"),rs.getString("size"), rs.getString("color"),rs.getString("gender"), rs.getDouble("price"));
				productsInStock[id-1] = p;
				numOfProducts++;
			}
			
		}
		catch(SQLException ex) {
			ex.printStackTrace();
		}
	}

	protected void addProduct(Product product) {
		productsInStock = Arrays.copyOf(productsInStock, numOfProducts+1);
		productsInStock[numOfProducts] = product;
		numOfProducts++;
	}


	protected void returnItem(int id, int quantity) throws OnlineStoreGeneralException {
		productsInStock[id-1].addQuantity(quantity);
	}
	
	




}
