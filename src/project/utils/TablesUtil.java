package project.utils;

public final class TablesUtil {

	// Table names
	public static final String itemsTableName = "items";
	public static final String customersTableName = "customers";
	public static final String ordersTableName = "orders";

	// Table colums
	public static final String itemsColumns = "item_id, name, category, unitprice";
	public static final String customersColumns = "customer_id, fname, lname, address, phone, gender";

	// Queries
	/* Select */
	public static final String selectFromOrders = "select  order_id, customers.customer_id, fname, lname, orders.item_id, unitprice, quantity, category, ispaid from orders join items on items.item_id = orders.item_id join customers on customers.customer_id = orders.customer_id;";

	/* Insert */
	public static final String insertItems = "insert into items values(null,?,?,?);";
	public static final String insertCustomers = "insert into customers values(null,?,?,?,?,?);";
	public static final String insertOrders = "insert into orders values(null,?,?,?,?);";

	/* Delete */
	public static final String deleteItems = "delete from items where item_id=?";
	public static final String deleteCustomers = "delete from customers where customer_id=?";
	public static final String deleteOrders = "delete from orders where order_id=?";

	/* Update */
	public static final String updateItems = "update items set name = ?, unitprice = ?, category = ? where item_id = ?";
	public static final String updateCustomers = "update customers set fName = ?, lName = ?, phone = ?, gender = ?, address = ? where customer_id = ?";
	public static final String updateOrders = "update orders set item_id=?, customer_id=?, quantity=?, ispaid=? where order_id=?";

	/* Refresh comboBox */
	public static final String refreshItemsCombo = "select item_id, name, category, unitprice from items";
	public static final String refreshCustomersCombo = "select customer_id, lname, fname from customers";

	/* Double search */
	public static final String doubleSearchOrders = "select order_id,  customers.customer_id, fname, lname, items.item_id, unitprice, quantity, category, ispaid from orders join items on items.item_id = orders.item_id join customers on customers.customer_id = orders.customer_id where lname = ? and category = ?;";
	public static final String searchOrders = "select order_id,  customers.customer_id, fname, lname, items.item_id, unitprice, quantity, category, ispaid from orders join items on items.item_id = orders.item_id join customers on customers.customer_id = orders.customer_id where quantity = ?;";

	// Util methods
	public static final String mainSelect(String columns, String tableName) {
		return "select " + columns + " from " + tableName;
	}

	public static final String searchInTable(String tableName) {
		String condition = "";
		String columns = "";
		switch (tableName) {
		case itemsTableName:
			columns = itemsColumns;
			condition = "category";
			break;
		case customersTableName:
			columns = customersColumns;
			condition = "lname";
			break;
		case ordersTableName:
			return searchOrders;
		}
		return "select " + columns + " from " + tableName + " where " + condition + " = ?";
	}

}
