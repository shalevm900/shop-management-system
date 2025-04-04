package storeSqlPackage;

import java.io.Serializable;
import java.util.Arrays;

public class Cart implements Serializable{

	private static final long serialVersionUID = -3342365457628798878L;

	protected Product[] products;
	protected int numOfProducts;
	protected final int numOfCatagories = 3;
	protected final int maximumProducts = 3;
	protected int orderId;
	protected int customerId;

	public Cart(int cid) {
		products = new Product[maximumProducts];
		numOfProducts = 0;
		this.orderId = 0;
		this.customerId = cid;
	}
	
	
	protected Product[][] sortByCategory(Product[] products){
		Product[][] responseList = new Product [numOfCatagories][0];
		for (int i = 0; i < products.length; i++) {
			if(products[i] instanceof Books) {
				responseList[0] = Arrays.copyOf(responseList[0], responseList[0].length+1);
				responseList[0][responseList[0].length-1] = products[i];
			}
			else if(products[i] instanceof Clothing) {
				responseList[1] = Arrays.copyOf(responseList[1], responseList[1].length+1);
				responseList[1][responseList[1].length-1] = products[i];
			}
			else if(products[i] instanceof Electronics) {
				responseList[2] = Arrays.copyOf(responseList[2], responseList[2].length+1);
				responseList[2][responseList[2].length-1] = products[i];
			}
		}
		return responseList;
	}

	public String showCart() {
		if (numOfProducts == 0) {
			return "No Products In cart \n";
		}
		Product [][] cartProducts = sortByCategory(products);
		StringBuilder sb = new StringBuilder();
		
		
		for (int i = 0; i < cartProducts[0].length; i++) {
			if (i == 0) {
				sb.append(String.format("%-3s", "ID"))
				.append(String.format("%-15s", "Category"))
				.append(String.format("%-40s", "Name"))
				.append(String.format("%-20s", "Author"))
				.append(String.format("%-8s", "Pages"))
				.append(String.format("%-10s", "Quantity"))
				.append(String.format("%-8s", "Price"))
				.append(String.format("%-8s", "Last Update"))
				.append("\n");
			}
			sb.append(cartProducts[0][i].forCart()).append("\n");
		}
		
		for (int i = 0; i < cartProducts[1].length; i++) {
			if (i == 0) {
				sb.append("\n").append(String.format("%-3s", "ID"))
				.append(String.format("%-15s", "Category"))
				.append(String.format("%-40s", "Name"))
				.append(String.format("%-15s", "Size"))
				.append(String.format("%-20s", "Color"))
				.append(String.format("%-10s", "Gender"))
				.append(String.format("%-10s", "Quantity"))
				.append(String.format("%-8s", "Price"))
				.append(String.format("%-8s", "Last Update"))
				.append("\n");
			}
			sb.append(cartProducts[1][i].forCart()).append("\n");
		}
		
		
		for (int i = 0; i < cartProducts[2].length; i++) {
			if (i == 0) {
				sb.append("\n").append(String.format("%-3s", "ID"))
				.append(String.format("%-15s", "Category"))
				.append(String.format("%-40s", "Name"))
				.append(String.format("%-10s", "Brand"))
				.append(String.format("%-30s", "Model"))
				.append(String.format("%-10s", "Quantity"))
				.append(String.format("%-8s", "Price"))
				.append(String.format("%-8s", "Last Update"))
				.append("\n");
			}
			sb.append(cartProducts[2][i].forCart()).append("\n");
		}
		
		
		
		return sb.toString();
	}

	public int getProductQuantity(int id) {
		for (int i = 0; i < products.length; i++) {
			if(products[i].getId() == id) {
				return products[i].getQuantity();
			}
		}
		return 0;
	}
	
	public long getProductLastUpdate(int id) {
		for (int i = 0; i < products.length; i++) {
			if(products[i].getId() == id) {
				return products[i].getLastUpdate();
			}
		}
		return 0;
	}
	
	

}
