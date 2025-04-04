package storeSqlPackage;

public class CartProductNotExistException extends OnlineStoreGeneralException{
	
	public CartProductNotExistException(int id) {
		super("Product with the ID - "+id+" is not in cart");
	}

}
