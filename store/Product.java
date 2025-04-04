package storeSqlPackage;


import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.SQLException;


public abstract class Product implements Reserveable{

	protected int id;
	protected String name;
	protected int available;
	protected int quantity;
	protected int reserveAmount;
	protected double price;
	protected long lastUpdate;
	protected int maxAmount;


	public Product(int id,String name, int quantity, double price) {
		setName(name);
		setQuantity(quantity);
		this.price = price;
		available = quantity;
		this.id = id;
	}


	public Product(Product other) {
		setName(other.name);
		setQuantity(other.quantity);
		available = other.available;
		reserveAmount = other.reserveAmount;
		this.id = other.id;
		this.price = other.price;
		lastUpdate = System.currentTimeMillis();
	}


	private void setName(String name) {
		if (name.isEmpty() || name == null) {
			this.name = "Unknown";
		}
		else{
			this.name = name;
		}

	}



	protected void setQuantity(int quantity) {
		if (quantity < 0) {
			this.quantity = 0;
		}
		else {
			this.quantity = quantity;
			this.reserveAmount = 0;
			lastUpdate = System.currentTimeMillis();
		}

	}
	
	protected void setAvailable(int available) {
		if (available < 0) {
			this.available = 0;
		}
		else {
			this.available = available;
			lastUpdate = System.currentTimeMillis();
		}
	}
	
	public void reserve(int quantity) throws OnlineStoreGeneralException {
		if (quantity < 0) {
			throw new OnlineStoreGeneralException("Negative Quantity");
		}
		if (quantity > maxAmount) {
			throw new ProductQuantityNotAvailableException(this.getClass().getSimpleName(), this.maxAmount);
		}
		reserveAmount = quantity;
		available = this.quantity - quantity;
		
	}

	protected void addQuantity(int quantity) throws OnlineStoreGeneralException {
		if (quantity < 0) {
			throw new OnlineStoreGeneralException("Negative Quantity");
		}
		else {
			available += quantity;
			reserveAmount -= quantity;
		}

	}

	public int getQuantity() {
		return quantity;
	}


	public boolean inStock() {
		if(quantity > 0) {
			return true;
		}
		return false;
	}

	protected int getId() {
		return id;
	}


	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."));
		sb.append(String.format("%-40s", name));
		sb.append(String.format("%-10s", available));
		sb.append(String.format("%-8s", reserveAmount));
		sb.append(String.format("%-15s", quantity));

		return sb.toString();
	}

	protected String forCart() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."));
		sb.append(String.format("%-40s", name));
		sb.append(String.format("%-10s", quantity));

		return sb.toString();
	}
	
	public boolean equals(Object obj) {
		
		if (this == obj) {
			return true;
		}
		if(obj instanceof Product) {
			Product p = (Product) obj;
			if (this.id == p.id) {
				return true;
			}
		}
		
		return false;
	}


	public long getLastUpdate() {
		return lastUpdate;
	}


	public int getAvailable() {
		return available;
	}
	
	public void saveProduct() throws SQLException {
        String sql = "INSERT INTO products (id, name, available, quantity, last_update) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConfig.connection;
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, getId());
            pstmt.setString(2, name);
            pstmt.setInt(3, getAvailable());
            pstmt.setInt(4, getQuantity());
            pstmt.setLong(5, getLastUpdate());
            pstmt.executeUpdate();
        }
    }

    public void updateProduct() throws SQLException {
        String sql = "UPDATE products SET name = ?, available = ?, quantity = ?, last_update = ? WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, getAvailable());
            pstmt.setInt(3, getQuantity());
            pstmt.setLong(4, getLastUpdate());
            pstmt.setInt(5, getId());
            pstmt.executeUpdate();
        }
    }

    public void deleteProduct() throws SQLException {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
	
	
	
}




