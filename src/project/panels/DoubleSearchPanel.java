package project.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import project.connection.DBConnector;
import project.connection.MyModel;
import project.utils.TablesUtil;

public class DoubleSearchPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	JTable doubleTable = new JTable();
	JScrollPane scroller = new JScrollPane(doubleTable);

	JPanel upPanel = new JPanel();
	JPanel midPanel = new JPanel();
	JPanel downPanel = new JPanel();

	JButton searchButton = new JButton("Търсене по фамилно име на клиент и категория на продукт: ");

	JLabel fnameLabel = new JLabel("Фамилно име на клиент:");
	JTextField fnameTField = new JTextField();
	JLabel categoryLabel = new JLabel("Категория на продукт:");
	String[] categories = { "Букети", "Кошници", "Саксии", "Градински растения", "Стайни растения" };
	JComboBox<String> categoryCombo = new JComboBox<>(categories);

	Connection conn = null;
	PreparedStatement state = null;
	ResultSet result = null;
	MyModel model = null;
	int id = -1; // selected id

	public DoubleSearchPanel(Connection conn, PreparedStatement state, ResultSet result, MyModel model) {
		this.conn = conn;
		this.state = state;
		this.result = result;
		this.model = model;
		this.add(upPanel);
		this.add(midPanel);
		this.add(downPanel);
		this.setLayout(new GridLayout(3, 1));

		// upPanel
		upPanel.setLayout(new GridLayout(3, 2));
		upPanel.add(fnameLabel);
		upPanel.add(fnameTField);
		upPanel.add(categoryLabel);
		upPanel.add(categoryCombo);

		// midPanel
		midPanel.add(searchButton);
		searchButton.addActionListener(new DoubleSearchAction());
		// downPanel
		scroller.setPreferredSize(new Dimension(800, 200));
		downPanel.add(scroller);
	}

	class DoubleSearchAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String fname = fnameTField.getText();
			String category = categoryCombo.getSelectedItem().toString();
			conn = DBConnector.getConnection();
			String sql = TablesUtil.doubleSearchOrders;
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, fname);
				state.setString(2, category);
				result = state.executeQuery();
				model = new MyModel(result);
				doubleTable.setModel(model);
				clearDoubleSearchForm();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@SuppressWarnings("finally")
//		private MyModel getFromTableOrders() {
//			conn = DBConnector.getConnection();
//			String sql = "select fname, lname, unitprice, quantity, ispaid from orders join items on items.item_id = orders.item_id join customers on customers.customer_id = orders.customer_id;";
//			try {
//				state = conn.prepareStatement(sql);
//				result = state.executeQuery();
//				model = new MyModel(result);
//			} catch (SQLException e) {
//				e.printStackTrace();
//			} catch (Exception e) {
//				e.printStackTrace();
//			} finally {
//				return model;
//			}
//		}
		
		private void clearDoubleSearchForm() {
			fnameTField.setText("");
			categoryCombo.setSelectedIndex(0);
		}
	}
}
