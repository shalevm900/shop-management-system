package storeSqlPackage;

public class ProductQuantityNotAvailableException extends OnlineStoreGeneralException{
	
	public ProductQuantityNotAvailableException(int id) {
		super("Product with the ID: "+id+" does not have available quantity for you at the moment.");
	}
	
	public ProductQuantityNotAvailableException(String category,int quantity) {
		super("You can only order "+quantity+" items of a specific product from the "+category+" category");
	}
}
