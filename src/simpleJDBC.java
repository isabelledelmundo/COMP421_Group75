import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

class simpleJDBC {

	public static void main(String[] args) throws SQLException {

		// Unique table names. Either the user supplies a unique identifier as a command
		// line argument, or the program makes one up.
		String tableName = "";
		int sqlCode = 0; // Variable to hold SQLCODE
		String sqlState = "00000"; // Variable to hold SQLSTATE
		Scanner reader = new Scanner(System.in);

		// Register the driver. You must register the driver before you can use it.
		try {
			DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
		} catch (Exception cnfe) {
			System.out.println("Class not found");
		}

		String url = "jdbc:db2://comp421.cs.mcgill.ca:50000/cs421";
		Connection con = DriverManager.getConnection(url, "cs421g75", "Fooddeliver3");
		Statement statement = con.createStatement();

		int run = 0;

		while (run == 0) {
			
			System.out.println("\nSelect an option: ");
			System.out.println("1) Restock items below minimum quantity");
			System.out.println("2) Show items ordered from low price to high price");
			System.out.println("3) Track order by order number");
			System.out.println("4) Add items to sell");
			System.out.println("5) Remove all items past expiry date");
			System.out.println("6) Quit");
			
			System.out.print("\nEnter your option here: ");
			int option = reader.nextInt();
			reader.nextLine();
			
			
			if(option == 1) {
				// Option 1: "show items that must be restocked, and decide which items to restock."
				try {
					boolean restock = true;
					String querySQL = "SELECT name, quantityInStock, minimumQuantity FROM item WHERE (quantityInStock < minimumQuantity) "
							+ "ORDER BY quantityInStock ASC";
					System.out.println(querySQL);
					java.sql.ResultSet rs = statement.executeQuery(querySQL);
					while (rs.next()) {
						int quantityInStock = rs.getInt(2);
						String name = rs.getString(1);
						int minimumQuantity = rs.getInt(3);
						System.out.println("name  " + name);
						System.out.println("quantityInStock:  " + quantityInStock);
						System.out.println("minimumQuantity:  " + minimumQuantity);
					}

					System.out.println(querySQL);
					java.sql.ResultSet ri = statement.executeQuery(querySQL);
					while (ri.next()) {
						int quantityInStock = ri.getInt(2);
						String name = ri.getString(1);
						int minimumQuantity = ri.getInt(3);

						while (restock) {
							System.out.println("select item to restock: ");
							String itemRestock = reader.nextLine();
							System.out.println(itemRestock);
							int newQuantity = quantityInStock + (2 * minimumQuantity);
							String sqlUpdateItem = "UPDATE item SET quantityInStock = " + newQuantity
									+ " WHERE ( name  = \'" + itemRestock + "\' )";
							System.out.println(sqlUpdateItem);
							statement.executeUpdate(sqlUpdateItem);
							System.out.println("Restock another item? (y/n)");
							String cont = reader.nextLine();

							if (cont.equals("n") || cont.equals("N")) {
								restock = false;
							} else if (cont.equals("y") || cont.equals("Y")) {
								restock = true;
							} else {
								System.out.println("invalid input");
							}
						}
					}
					System.out.println("DONE");
				} catch (SQLException e) {
					sqlCode = e.getErrorCode(); // Get SQLCODE
					sqlState = e.getSQLState(); // Get SQLSTATE

					
					System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
				} 

			}
			
			else if(option == 2) {
				// Option 2: "show items price low -> high"
				try {
					String querySQL = "SELECT name, price FROM item ORDER BY price ASC";
					System.out.println(querySQL);
					java.sql.ResultSet rs = statement.executeQuery(querySQL);
					while (rs.next()) {
						int price = rs.getInt(2);
						String name = rs.getString(1);
						System.out.println("name:  " + name);
						System.out.println("price:  " + price);
					}
					System.out.println("DONE");
				} catch (SQLException e) {
					sqlCode = e.getErrorCode(); // Get SQLCODE
					sqlState = e.getSQLState(); // Get SQLSTATE

					
					System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
				} 
				
			}
			
			else if(option == 3) {
				// Option 3: "show order status 
				try {
					System.out.println("Enter order number: ");
					String orderid = reader.nextLine();
					String querySQL = "SELECT status, date_ordered, courier_name FROM order WHERE order_id = \'" + orderid + "\' ORDER BY status, date_ordered";
					System.out.println(querySQL);
					java.sql.ResultSet rs = statement.executeQuery(querySQL);
					while (rs.next()) {
						String status = rs.getString(1);
						String date = rs.getString(2);
						String courrier = rs.getString(3);
						System.out.println("status:  " + status);
						System.out.println("date ordered:  " + date);
						System.out.println("courrier:  " + courrier);
					}
					System.out.println("DONE");
				} catch (SQLException e) {
					sqlCode = e.getErrorCode(); // Get SQLCODE
					sqlState = e.getSQLState(); // Get SQLSTATE

					
					System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
				} 
				
			}
			else if(option == 4) {
				// Option 4: adding items to the table
				try {
					tableName = "item";

					int val = 0;

					while (val == 0) {
						int i = 0;
						int stock = 200;
						System.out.println("Enter item name");
						String itemName = reader.nextLine();
						System.out.println("Set Expiration Date (FORM YYYY-MM-DD)");
						String date = reader.nextLine();
						System.out.println("Set Price");
						String price = reader.nextLine();
						System.out.println("Set min quantity");
						int minQuant = reader.nextInt();
						reader.nextLine();

						String insertSQL = "INSERT INTO " + tableName + " VALUES( \'351\', \'" + price + "\', " + minQuant
								+ ", " + stock + ", \'" + itemName + "\', \'" + date + "\')";
						System.out.println(insertSQL);
						statement.executeUpdate(insertSQL);
						System.out.println("DONE");

						System.out.println("Add another value? y/n");
						String ans = reader.nextLine();

						if (ans.equals("Y") || ans.equals("y")) {
							continue;
						} else {
							val = 1;
						}

					}
				} catch (SQLException e) {
					sqlCode = e.getErrorCode(); // Get SQLCODE
					sqlState = e.getSQLState(); // Get SQLSTATE

					System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
				} 
				
				
			}
			else if(option == 5) {
				// Option 5: Remove all items past expiry date
				try {
					tableName = "item";

					String pattern = "yyyy-MM-dd";
					SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

					Date d1 = new Date();
					String date = simpleDateFormat.format(d1);
					System.out.println(date);

					String deleteSQL = "DELETE FROM " + tableName + " WHERE EXPIRATIONDATE < \'" + date + "\'";
					
					System.out.println(deleteSQL);
					statement.executeUpdate(deleteSQL);
					System.out.println("DONE");

				} catch (SQLException e) {
					sqlCode = e.getErrorCode(); // Get SQLCODE
					sqlState = e.getSQLState(); // Get SQLSTATE

					System.out.println("Code: " + sqlCode + "  sqlState: " + sqlState);
				} 
			}
			
			else if(option == 6) {
				
				statement.close();
				con.close();
				reader.close();
				System.out.println("CLOSED");
				run = 1;
				
			}
			else {
				System.out.println("invalid input");
				
			}
			
			

		}

	}
}


