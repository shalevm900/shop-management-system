package storeSqlPackage;

public class OnlineStoreGeneralException extends Exception {
	public OnlineStoreGeneralException(String errorMessage) {
		super("Error: "+ errorMessage);
	}

}
