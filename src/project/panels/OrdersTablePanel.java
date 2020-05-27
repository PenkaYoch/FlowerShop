package project.panels;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.h2.jdbc.JdbcSQLException;

import project.connection.DBConnector;
import project.connection.MyFrame;
import project.connection.MyModel;
import project.utils.TablesUtil;

public class OrdersTablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JScrollPane scroller = new JScrollPane(MyFrame.ordersTable);

	private JPanel upPanel = new JPanel();
	private JPanel midPanel = new JPanel();
	private JPanel downPanel = new JPanel();

	private JButton addButton = new JButton("Добави");
	private JButton delButton = new JButton("Изтрий");
	private JButton editButton = new JButton("Редактирай");
	private JButton searchButton = new JButton("Търсене по количество");
	private JButton refreshButton = new JButton("Обнови");

	private JLabel itemsLabel = new JLabel("Моля, изберете артикул: ");
	private static String[] items = { "Item1", "Item2" };
	private static JComboBox<String> itemCombo = new JComboBox<>(items);
	private JLabel quantityLabel = new JLabel("Брой артикули: ");
	private JTextField quantityTField = new JTextField();

	private JLabel customerLabel = new JLabel("Клиент:");
	private static String[] customers = { "Cust1", "Cust2" };
	private static JComboBox<String> customerCombo = new JComboBox<>(customers);

	private JCheckBox isPaidCheckBox = new JCheckBox("Поръчката е ПЛАТЕНА");

	private static Connection conn = null;
	private static PreparedStatement state = null;
	private static ResultSet result = null;
	private static MyModel model = null;
	private int id = -1; // selected id

	public OrdersTablePanel() {
		this.add(upPanel);
		this.add(midPanel);
		this.add(downPanel);

		// upPanel
		upPanel.setLayout(new GridLayout(4, 2));
		upPanel.add(quantityLabel);
		upPanel.add(quantityTField);
		upPanel.add(customerLabel);
		upPanel.add(customerCombo);
		this.refreshComboCustomers();
		upPanel.add(itemsLabel);
		upPanel.add(itemCombo);
		this.refreshComboItems();
		upPanel.add(isPaidCheckBox);
		// midPanel
		midPanel.add(addButton);
		midPanel.add(delButton);
		midPanel.add(editButton);
		midPanel.add(searchButton);
		midPanel.add(refreshButton);
		addButton.addActionListener(new AddAction());
		delButton.addActionListener(new DeleteAction());
		searchButton.addActionListener(new SearchActionOrder());
		refreshButton.addActionListener(new RefreshActionItem());
		editButton.addActionListener(new UpdateActionOrder());
		// downPanel
		scroller.setPreferredSize(new Dimension(800, 200));
		downPanel.add(scroller);
		MyFrame.ordersTable.addMouseListener(new MouseAction());

	}

	class DeleteAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnector.getConnection();
			String sql = TablesUtil.deleteOrders;
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				MyFrame.getModelOfTable(TablesUtil.ordersTableName);
				id = -1;
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = MyFrame.ordersTable.getSelectedRow();
			id = Integer.parseInt(MyFrame.ordersTable.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				findIndexInComboBox(row, true);
				findIndexInComboBox(row, false);
				quantityTField.setText(MyFrame.ordersTable.getValueAt(row, 7).toString());
				boolean isPaid = (boolean) MyFrame.ordersTable.getValueAt(row, 9);
				isPaidCheckBox.setSelected(isPaid);
			}
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseExited(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mousePressed(MouseEvent e) {
			// TODO Auto-generated method stub

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			// TODO Auto-generated method stub

		}

	}

	class AddAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			boolean isPaid = isPaidCheckBox.isSelected() ? true : false;

			int quantity = quantityTField.getText().isEmpty() ? 0 : Integer.parseInt(quantityTField.getText());
			String customer_ID = getCustomerId();
			String item_ID = getItemId();
			conn = DBConnector.getConnection();
			String query = TablesUtil.insertOrders;

			if (quantity > 0) {
				try {
					state = conn.prepareStatement(query);
					state.setInt(1, Integer.parseInt(item_ID));
					state.setInt(2, Integer.parseInt(customer_ID));
					state.setInt(3, quantity);
					state.setBoolean(4, isPaid);
					state.execute();
					MyFrame.getModelOfTable(TablesUtil.ordersTableName);
					clearFormOrders();
				} catch (JdbcSQLException e1) {
					JOptionPane.showMessageDialog(null, "Некоректни данни! " + e1.getMessage(), null,
							JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Въведете количество различно от нула!", null,
						JOptionPane.ERROR_MESSAGE);
			}
		}// end method

	}// end AddAction

	class UpdateActionOrder implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			boolean isPaid = isPaidCheckBox.isSelected() ? true : false;
			int quantity = quantityTField.getText().isEmpty() ? 0 : Integer.parseInt(quantityTField.getText());
			String customer_ID = getCustomerId();
			String item_ID = getItemId();

			conn = DBConnector.getConnection();
			String sql = TablesUtil.updateOrders;

			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, Integer.parseInt(item_ID));
				state.setInt(2, Integer.parseInt(customer_ID));
				state.setInt(3, quantity);
				state.setBoolean(4, isPaid);
				state.setInt(5, id);
				state.execute();
				clearFormOrders();
				MyFrame.getModelOfTable(TablesUtil.ordersTableName);
				id = -1;
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
		}
	}

	class SearchActionOrder implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			int quantity = quantityTField.getText().isEmpty() ? 0 : Integer.parseInt(quantityTField.getText());
			conn = DBConnector.getConnection();
			String sql = TablesUtil.searchInTable(TablesUtil.ordersTableName);
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, quantity);
				result = state.executeQuery();
				model = new MyModel(result);
				MyFrame.ordersTable.setModel(model);
				clearFormOrders();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	class RefreshActionItem implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			clearFormOrders();
			MyFrame.ordersTable.setModel(MyFrame.getFromTableOrders());
		}
	}

	public void clearFormOrders() {
		quantityTField.setText("");
	}

	public static void refreshComboItems() {
		itemCombo.removeAllItems();
		conn = DBConnector.getConnection();
		String sql = TablesUtil.refreshItemsCombo;
		String item = "";
		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			while (result.next()) {
				item = result.getObject(1).toString() + ":" + result.getObject(2).toString() + "-"
						+ result.getObject(3).toString();
				itemCombo.addItem(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void refreshComboCustomers() {
		customerCombo.removeAllItems();
		conn = DBConnector.getConnection();
		String sql = TablesUtil.refreshCustomersCombo;
		String item = "";
		try {
			state = conn.prepareStatement(sql);
			result = state.executeQuery();
			while (result.next()) {
				item = result.getObject(1).toString() + ":" + result.getObject(2).toString() + "-"
						+ result.getObject(3).toString();
				customerCombo.addItem(item);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void findIndexInComboBox(int row, boolean isCust) {
		String recentId = "";
		if (isCust) {
			for (int i = 0; i < this.customerCombo.getItemCount(); i++) {
				recentId = getCustomerId(i);
				if (MyFrame.ordersTable.getValueAt(row, 1).toString().equals(recentId)) {
					customerCombo.setSelectedIndex(i);
				}
			}
		} else {
			for (int i = 0; i < this.itemCombo.getItemCount(); i++) {
				recentId = getItemId(i);
				if (MyFrame.ordersTable.getValueAt(row, 4).toString().equals(recentId)) {
					itemCombo.setSelectedIndex(i);
				}
			}
		}
	}

	private String getCustomerId() {
		return customerCombo.getSelectedItem().toString().substring(0,
				customerCombo.getSelectedItem().toString().indexOf(':'));
	}

	private String getCustomerId(int index) {
		return customerCombo.getItemAt(index).toString().substring(0,
				customerCombo.getItemAt(index).toString().indexOf(':'));
	}

	private String getItemId() {
		return itemCombo.getSelectedItem().toString().substring(0, itemCombo.getSelectedItem().toString().indexOf(':'));
	}

	private String getItemId(int index) {
		return itemCombo.getItemAt(index).toString().substring(0, itemCombo.getItemAt(index).toString().indexOf(':'));
	}

}
