package storeSqlPackage;

public class Customer {
	protected int id;
	protected String firstName;
	protected String lastName;
	
	public Customer(int id,String firstName,String lastName) {
		this.id = id;
		setFirstName(firstName);
		setLastName(lastName);
	}
	
	private void setFirstName(String name) {
		if (name.isEmpty() || name == null) {
			this.firstName = "Unknown";
		}
		else{
			this.firstName = name;
		}

	}
	
	private void setLastName(String name) {
		if (name.isEmpty() || name == null) {
			this.lastName = "Unknown";
		}
		else{
			this.lastName = name;
		}

	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-3s", id + "."))
		.append(String.format("%-40s", firstName))
		.append(String.format("%-40s", lastName));
		
		return sb.toString();
	}
	
	

}
