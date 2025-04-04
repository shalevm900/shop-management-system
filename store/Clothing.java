package storeSqlPackage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Clothing extends Product{
	

	protected enum Gender{
		MALE,
		FEMALE,
		NO_GENDER
	}
	private final String size;
	private final String color;
	private final Gender gender;
	
	public Clothing(int id, String name, int quantity, String size, String color, String gender, double price) {
		super(id,name,quantity, price);
		this.size = size;
		this.color = color;
		if (gender.equals("Female")) {
			this.gender = Gender.FEMALE;
		}
		else if (gender.equals("Male")){
			this.gender = Gender.MALE;
		}
		else {
			this.gender = Gender.NO_GENDER;
		}
		this.maxAmount = 5;
		
	}

	public Clothing(Clothing product) {
		super(product);
		size = product.size;
		color = product.color;
		gender = product.gender;
	}

	public String getSize() {
		return size;
	}

	public String getColor() {
		return color;
	}

	public Gender getGender() {
		return gender;
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
		.append(String.format("%-15s", size))
		.append(String.format("%-20s", color))
		.append(String.format("%-10s", gender))
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
		.append(String.format("%-15s", size))
		.append(String.format("%-20s", color))
		.append(String.format("%-10s", gender))
		.append(String.format("%-10s", quantity))
		.append(String.format("%-8s", price))
		.append(String.format("%-8s", sdf.format(new Date(lastUpdate))));

		return sb.toString();
	}


}
