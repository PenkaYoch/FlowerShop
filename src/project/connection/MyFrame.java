package project.connection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import project.panels.CustomersTablePanel;
import project.panels.DoubleSearchPanel;
import project.panels.ItemsTablePanel;
import project.panels.OrdersTablePanel;
import project.utils.TablesUtil;

public class MyFrame extends JFrame{
	
	/**
	 * 
	 * @author Penka Yochkova
	 * @author Dida Negasa
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static Connection conn = null;
	static PreparedStatement state = null;
	static ResultSet result = null;
	static MyModel model = null;
	int id = -1; //selected id
	JTabbedPane tab = new JTabbedPane();
	public static JTable itemsTable = new JTable();
	public static JTable customersTable = new JTable();
	public static JTable ordersTable = new JTable();

	ItemsTablePanel panel1 = new ItemsTablePanel(conn, state, result, model);
	CustomersTablePanel panel2 = new CustomersTablePanel(conn, state, result, model);
	OrdersTablePanel panel3 = new OrdersTablePanel(conn, state, result, model);
	DoubleSearchPanel panel4 = new DoubleSearchPanel(conn, state, result, model);
	
	public MyFrame() {
		this.setVisible(true);
		this.setSize(900, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tab.add("Продукти", panel1);
		tab.add("Клиенти", panel2);
		tab.add("Поръчки", panel3);
		tab.add("Справка", panel4);
		this.add(tab);
		MyFrame.getModelOfTable(TablesUtil.itemsTableName);
		MyFrame.getModelOfTable(TablesUtil.customersTableName);
		MyFrame.ordersTable.setModel(getFromTableOrders());
	}//end constructor
	
	public static void getModelOfTable(String tableName) {
		String columns = null;
		switch (tableName) {
		case TablesUtil.itemsTableName:
			columns = TablesUtil.itemsColumns;
			itemsTable.setModel(getModelOfTable(TablesUtil.itemsTableName, columns));
			break;

		case TablesUtil.customersTableName:
			columns = TablesUtil.customersColumns;
			customersTable.setModel(getModelOfTable(TablesUtil.customersTableName, columns));
			break;
			
		case TablesUtil.ordersTableName:
			ordersTable.setModel(getFromTableOrders());
			break;

		default:
			break;
		}
	}
	
	@SuppressWarnings("finally")
	public static MyModel getModelOfTable(String tableName, String columns) {
		conn = DBConnector.getConnection();
		String sql = TablesUtil.mainSelect(columns, tableName); 
		
		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			model = new MyModel(result);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return model;
		}
	}
	
	@SuppressWarnings("finally")
	public static MyModel getFromTableOrders() {
		conn = DBConnector.getConnection();
		String sql = TablesUtil.selectFromOrders;
		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			model = new MyModel(result);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return model;
		}
	}
	
}//end class MyFrame
