package storeSqlPackage;

public class CartProductAlreadyExistException extends OnlineStoreGeneralException{
	public CartProductAlreadyExistException(int id) {
		super("Product with the ID: "+id+" is already in cart");
	}

}
