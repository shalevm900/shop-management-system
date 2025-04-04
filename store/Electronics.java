package storeSqlPackage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Electronics extends Product{
	
	private final String brand;
	private final String model;
	
	
	public Electronics(int id,String name, int quantity, String brand, String model, double price) {
		super(id ,name, quantity, price);
		this.brand = brand;
		this.model = model;
		this.maxAmount = 3;
	}


	public Electronics(Electronics product) {
		super(product);
		brand = product.brand;
		model = product.model;
	}


	public String getBrand() {
		return brand;
	}

	public String getModel() {
		return model;
	}
	
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."))
		.append(String.format("%-15s", getClass().getSimpleName()))
		.append(String.format("%-40s", name))
		.append(String.format("%-10s", available))
		.append(String.format("%-8s", reserveAmount))
		.append(String.format("%-15s", quantity))
		.append(String.format("%-10s", brand))
		.append(String.format("%-30s", model))
		.append(String.format("%-8s", price));

		return sb.toString();
	}

	
	@Override
	protected String forCart() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."))
		.append(String.format("%-15s", getClass().getSimpleName()))
		.append(String.format("%-40s", name))
		.append(String.format("%-10s", brand))
		.append(String.format("%-30s", model))
		.append(String.format("%-10s", quantity))
		.append(String.format("%-8s", price))
		.append(String.format("%-8s", sdf.format(new Date(lastUpdate))));

		return sb.toString();
	}


}
