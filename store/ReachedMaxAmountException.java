package storeSqlPackage;

public class ReachedMaxAmountException extends OnlineStoreGeneralException{
	public ReachedMaxAmountException(int numOfProducts) {
		super("You have reached the maximum amount of products in your cart - "+numOfProducts+" products, please remove a product if you want to order this one.");
	}

}
