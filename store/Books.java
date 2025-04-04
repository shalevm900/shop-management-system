package storeSqlPackage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Books extends Product{
	
	private final String author;
	private final int pages;
	
	public Books(int id,String name, int quantity, String author, int pages,double price) {
		super(id,name, quantity, price);
		this.author = author;
		this.pages = pages;
		this.maxAmount = 3;
	}

	public Books(Books product) {
		super(product);
		author = product.author;
		pages = product.pages;
	}

	public String getAuthor() {
		return author;
	}

	public int getPages() {
		return pages;
	}
	
	
	
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."))
		.append(String.format("%-15s", getClass().getSimpleName()))
		.append(String.format("%-40s", name))
		.append(String.format("%-10s", available))
		.append(String.format("%-8s",  reserveAmount))
		.append(String.format("%-15s", quantity))
		.append(String.format("%-20s", author))
		.append(String.format("%-8s", pages))
		.append(String.format("%-8s", price))
		;

		return sb.toString();
	}
	
	@Override
	protected String forCart() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."))
		.append(String.format("%-15s", getClass().getSimpleName()))
		.append(String.format("%-40s", name))
		.append(String.format("%-20s", author))
		.append(String.format("%-8s", pages))
		.append(String.format("%-10s", quantity))
		.append(String.format("%-8s", price))
		.append(String.format("%-8s", sdf.format(new Date(lastUpdate))));

		return sb.toString();
	}

	
	

}
