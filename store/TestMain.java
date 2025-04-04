package storeSqlPackage;
//package id_208040154;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestInstance;
//import org.junit.jupiter.api.TestInstance.Lifecycle;
//
//import id_208040154.Cart.StatusCode;
//
//import org.junit.jupiter.api.BeforeAll;
//
//@TestInstance(Lifecycle.PER_CLASS)
//class TestMain {
//
//	private Cart c;
//	private Stock stock;
//
//
//
//	@BeforeAll
//	public void initialize() throws FileNotFoundException {
//
//		File file = new File("products_list.csv");
//		Scanner s = new Scanner(file);
//		stock = new Stock(s);
//
//		s.close();
//		c = new Cart();
//
//	}
//
//	@Test
//	void testAddToCart() {
//		c.addToCart(1,3);
//		long lastUpdate = System.currentTimeMillis();
//		assertEquals(3,c.getProductQuantity(1));
//		assertEquals(Stock.actualQuantity[0] - 3,Stock.productsInStock[0].getQuantity());
//		assertEquals(lastUpdate, c.getProductLastUpdate(1));
//	}
//
//	@Test
//	public void testAddToCartInvalidProduct() {
//		assertEquals(StatusCode.IdNotInRange,c.addToCart(100, 2)); 
//	}
//
//	@Test
//	public void testAddToCartNotEnoughStock() {
//		assertEquals(StatusCode.NotInStock,c.addToCart(2, 51));
//	}
//
//	@Test
//	public void testUpdateQuantity() {
//		long lastUpdate = System.currentTimeMillis();
//		c.addToCart(3, 2); 
//		c.updateQuantity(3, 5);
//		assertEquals(5, c.getProductQuantity(3));
//		assertEquals(lastUpdate, c.getProductLastUpdate(3));
//	}
//
//	@Test
//	public void testRemoveFromCart() {
//
//		c.addToCart(4, 2); 
//		c.removeFromCart(4);
//		assertEquals(0, c.getProductQuantity(4));
//	}
//
//	@Test
//	public void testShowAvailable() {
//		String[] show = c.showAvailable().split("\n");
//		assertEquals(34, show.length);
//	}
//
//	@Test
//	public void testShowCart() {
//		String[] show = c.showCart().split("\n");
//		assertEquals(3, show.length);
//	}
//
//
//}
