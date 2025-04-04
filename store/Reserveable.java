package storeSqlPackage;

public interface Reserveable {
	
	void reserve(int quantity) throws OnlineStoreGeneralException;

}
