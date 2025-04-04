package storeSqlPackage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

public class Main {

    private static Scanner s = new Scanner(System.in);

    public static void cartMenu(int cid, Store store) throws OnlineStoreGeneralException {

        store.cart = new Cart(cid);
        store.createOrder();
        int choice;
        do {
            displayCartMenu();
            while (!s.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                s.next();
            }
            choice = s.nextInt();
            s.nextLine();

            switch (choice) {

                case 1: {
                    System.out.println(store.cart.showCart());
                    break;
                }

                case 2: {
                    System.out.println(store.showAvailable());
                    System.out.println("Enter product ID:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next();
                    }
                    int id = s.nextInt();
                    System.out.println("Enter quantity to add:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next();
                    }
                    int quantity = s.nextInt();
                    s.nextLine();
                    try {
                        System.out.println(store.addToCart(id, quantity));
                    } catch (OnlineStoreGeneralException e) {
                        System.out.println(e.getMessage());
                        System.out.println();
                    }
                    break;
                }

                case 3: {
                    System.out.println("Enter product ID:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next(); 
                    }
                    int id = s.nextInt();
                    System.out.println("Enter quantity to update:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next();
                    }
                    int quantity = s.nextInt();
                    s.nextLine();
                    try {
                        System.out.println(store.updateQuantity(id, quantity));
                    } catch (OnlineStoreGeneralException e) {
                        System.out.println(e.getMessage());
                        System.out.println();
                    }
                    break;
                }

                case 4: {
                    System.out.println("Enter product ID:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next();
                    }
                    int id = s.nextInt();
                    s.nextLine();
                    try {
                        System.out.println(store.removeFromCart(id));
                    } catch (OnlineStoreGeneralException e) {
                        System.out.println(e.getMessage());
                        System.out.println();
                    }
                    break;

                }
                case 5: {
                    store.purchaseCart();
                    break;
                }
                case 6: {
                    store.purchaseCancel();
                    break;
                }

                default:
                    System.out.println("Invalid choice. Please select a valid option (1-6).");

            }
        } while (choice != 6 && choice != 5);

    }

    public static void main(String[] args) throws IOException, OnlineStoreGeneralException, ClassNotFoundException, SQLException {

        DatabaseConfig.connection = DatabaseConfig.getConnection();
        Stock stock = new Stock();

        Cart cart = new Cart(0);

        Store store = new Store(stock, cart);
        store.readCustomers();

        int choice;
        do {
        	System.out.println();
            displayMenu();
            while (!s.hasNextInt()) {
                System.out.println("Invalid input. Please enter an integer.");
                s.next();
            }
            choice = s.nextInt();
            s.nextLine();

            switch (choice) {
                case 1: {
                    System.out.println(store.showStock());
                    break;
                }

                case 2: {
                    System.out.println(store.showCustomers());
                    System.out.println("Enter customer id:");
                    while (!s.hasNextInt()) {
                        System.out.println("Invalid input. Please enter an integer.");
                        s.next();
                    }
                    int cid = s.nextInt();
                    s.nextLine();
                    cartMenu(cid, store);
                    break;
                }

                case 3: {
                    Product products[] = new Product[store.stock.numOfProducts];
                    for (int i = 0; i < products.length; i++) {
                        if (store.stock.productsInStock[i] instanceof Books) {
                            products[i] = new Books((Books) store.stock.productsInStock[i]);
                        } else if (store.stock.productsInStock[i] instanceof Clothing) {
                            products[i] = new Clothing((Clothing) store.stock.productsInStock[i]);
                        } else if (store.stock.productsInStock[i] instanceof Electronics) {
                            products[i] = new Electronics((Electronics) store.stock.productsInStock[i]);
                        }
                    }
                    int sort;
                    do {
                        System.out.println("1. By Category");
                        System.out.println("2. By Name");
                        System.out.println("3. By Quantity");
                        while (!s.hasNextInt()) {
                            System.out.println("Invalid input. Please enter an integer.");
                            s.next();
                        }
                        sort = s.nextInt();
                        s.nextLine();
                        if (sort > 3 || sort < 1) {
                            System.out.println("Invalid Input, try again");
                        }
                    } while (sort > 3 || sort < 1);

                    StringBuilder sb = new StringBuilder();
                    sb.append(String.format("%-3s", "ID"))
                            .append(String.format("%-15s", "Category"))
                            .append(String.format("%-40s", "Name"))
                            .append(String.format("%-10s", "Available"))
                            .append(String.format("%-8s", "In Cart"))
                            .append(String.format("%-15s", "Actual"))
                            .append(String.format("%-15s", "Additional Info"));

                    System.out.println(sb.toString());
                    switch (sort) {
                        case 1: {
                            Arrays.sort(products, new Comparator<Product>() {
                                public int compare(Product product1, Product product2) {
                                    return product1.getClass().getSimpleName().compareTo(product2.getClass().getSimpleName());
                                }
                            });
                            for (int i = 0; i < products.length; i++) {
                                System.out.println(products[i]);
                            }
                            break;
                        }
                        case 2: {
                            Arrays.sort(products, new Comparator<Product>() {
                                public int compare(Product product1, Product product2) {
                                    return product1.name.compareToIgnoreCase(product2.name);
                                }
                            });
                            for (int i = 0; i < products.length; i++) {
                                System.out.println(products[i]);
                            }
                            break;
                        }
                        case 3: {
                            Arrays.sort(products, new Comparator<Product>() {
                                public int compare(Product product1, Product product2) {
                                    int p1Quantity = product1.quantity - product1.reserveAmount, p2Quantity = product2.quantity - product2.reserveAmount;
                                    if (p1Quantity == p2Quantity) {
                                        return product1.name.compareTo(product2.name);
                                    }
                                    return p1Quantity - p2Quantity;
                                }
                            });
                            for (int i = 0; i < products.length; i++) {
                                System.out.println(products[i]);
                            }
                            break;
                        }
                    }
                    System.out.println();
                    break;
                }
                case 4: {
                    System.out.println("Enter first name:");
                    String fname = s.nextLine();
                    System.out.println("Enter last name:");
                    String lname = s.nextLine();

                    System.out.println(store.insertCustomer(fname, lname));

                    break;
                }
                case 5:{
                	System.out.println("Enter product name:");
                	String name = s.nextLine();
                	System.out.println("Enter quantity:");
                	int quantity = s.nextInt();
                	System.out.println("Enter price:");
                	double price = s.nextDouble();
                	System.out.println("Choose Type:");
                	System.out.println("1.Book");
                	System.out.println("2.Clothing");
                	System.out.println("3.Electronics");
                	int type = s.nextInt();
                	while (type != 1 && type != 2 && type != 3){
                		System.out.println("Type have to be between 1 to 3, enter again");
                		System.out.println("1.Book");
                    	System.out.println("2.Clothing");
                    	System.out.println("3.Electronics");
                		type = s.nextInt();
                	}
                	System.out.println(store.insertProduct(name,quantity,price,type,s));
                	break;
                }
                
                case 6:{
                	System.out.println("Enter product id:");
                	int id = s.nextInt();
                	System.out.println("Enter quantity:");
                	int quantity = s.nextInt();
                	System.out.println(store.updateProductQuantity(id, quantity));
                	
                	break;
                }

                case -1: {
                    DatabaseConfig.closeConnection();
                    break;
                }

                default:
                    System.out.println("Invalid choice. Please select a valid option (1-6).");

            }
        } while (choice != -1);

        s.close();

    }


	private static void displayMenu() {
        System.out.println("1. Show products in store");
        System.out.println("2. Create new cart");
        System.out.println("3. Show products sorted by");
        System.out.println("4. Add customer");
        System.out.println("5. Add product to stock");
        System.out.println("6. Update product quantity");
        System.out.println("-1. Exit");

    }

    private static void displayCartMenu() {
        System.out.println("1. Show shopping cart");
        System.out.println("2. Add product to the cart");
        System.out.println("3. Update product quantity");
        System.out.println("4. Remove a product from cart");
        System.out.println("5. Purchase cart");
        System.out.println("6. Cancel purchase");

    }

}
