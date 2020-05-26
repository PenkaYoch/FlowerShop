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

public class ItemsTablePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JScrollPane scroller = new JScrollPane(MyFrame.itemsTable);

	private JPanel upPanel = new JPanel();
	private JPanel midPanel = new JPanel();
	private JPanel downPanel = new JPanel();

	private JButton addButton = new JButton("Добави");
	private JButton delButton = new JButton("Изтрий");
	private JButton editButton = new JButton("Редактирай");
	private JButton searchButton = new JButton("Търсене по категория");
	private JButton refreshButton = new JButton("Обнови");

	private JLabel itemLabel = new JLabel("Име на продукта:");
	private JLabel priceLabel = new JLabel("Единична цена:");
	private JTextField itemNameTField = new JTextField();
	private JTextField priceTField = new JTextField();
	private JLabel categoryLabel = new JLabel("Категория:");

	private String[] categories = { "Букети", "Кошници", "Саксии", "Градински растения", "Стайни растения" };
	private JComboBox<String> categoryCombo = new JComboBox<>(categories);
	private Connection conn = null;
	private PreparedStatement state = null;
	private ResultSet result = null;
	private MyModel model = null;
	private int id = -1; // selected id

	public ItemsTablePanel() {
		this.add(upPanel);
		this.add(midPanel);
		this.add(downPanel);
		this.setLayout(new GridLayout(3, 1));

		// upPanel
		upPanel.setLayout(new GridLayout(3, 2));
		upPanel.add(itemLabel);
		upPanel.add(itemNameTField);
		upPanel.add(priceLabel);
		upPanel.add(priceTField);
		upPanel.add(categoryLabel);
		upPanel.add(categoryCombo);
		// midPanel
		midPanel.add(addButton);
		midPanel.add(delButton);
		midPanel.add(editButton);
		midPanel.add(searchButton);
		midPanel.add(refreshButton);
		addButton.addActionListener(new AddActionItem());
		delButton.addActionListener(new DeleteActionItem());
		editButton.addActionListener(new UpdateActionItem());
		searchButton.addActionListener(new SearchActionItem());
		refreshButton.addActionListener(new RefreshActionItem());
		// downPanel
		scroller.setPreferredSize(new Dimension(800, 200));
		downPanel.add(scroller);
		MyFrame.itemsTable.addMouseListener(new MouseAction());

	}

	class DeleteActionItem implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnector.getConnection();
			String sql = TablesUtil.deleteItems;
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				MyFrame.getModelOfTable(TablesUtil.itemsTableName);
				id = -1;
				clearFormItems();
				OrdersTablePanel.refreshComboItems();
			} catch (JdbcSQLException e1) {
//				e1.printStackTrace(System.out);
				JOptionPane.showMessageDialog(null,
						"Не можете да изтриете този запис в таблицата, защото участва в таблицата с поръчките! "
								+ e1.getMessage(),
						null, JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	class MouseAction implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = MyFrame.itemsTable.getSelectedRow();
			id = Integer.parseInt(MyFrame.itemsTable.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				itemNameTField.setText(MyFrame.itemsTable.getValueAt(row, 1).toString());
				String category = MyFrame.itemsTable.getValueAt(row, 2).toString();
				priceTField.setText(MyFrame.itemsTable.getValueAt(row, 3).toString());
				int index = 0;
				switch (category) {
				case "Букети":
					index = 0;
					break;
				case "Кошници":
					index = 1;
					break;
				case "Саксии":
					index = 2;
					break;
				case "Градински растения":
					index = 3;
					break;
				default:
					// "Стайни растения"
					index = 4;
					break;
				}
				categoryCombo.setSelectedIndex(index);
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

	class AddActionItem implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String itemName = itemNameTField.getText();
			String price = priceTField.getText();
			String category = categoryCombo.getSelectedItem().toString();

			if (!itemName.isEmpty() && !price.isEmpty()) {
				conn = DBConnector.getConnection();
				String query = TablesUtil.insertItems;

				try {
					state = conn.prepareStatement(query);
					state.setString(1, itemName);
					state.setString(2, category);
					state.setString(3, price);
					state.execute();
					MyFrame.getModelOfTable(TablesUtil.itemsTableName);
					clearFormItems();
					MyFrame.getModelOfTable(TablesUtil.ordersTableName);
					OrdersTablePanel.refreshComboItems();
				} catch (JdbcSQLException e1) {
					JOptionPane.showMessageDialog(null, "Некоректни данни! " + e1.getMessage(), null,
							JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Попълнете всички полета!", null, JOptionPane.ERROR_MESSAGE);
			}
		}// end method

	}// end AddAction

	class UpdateActionItem implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String itemName = itemNameTField.getText();
			double price = Double.parseDouble(priceTField.getText());
			String category = categoryCombo.getSelectedItem().toString();

			conn = DBConnector.getConnection();
			String sql = TablesUtil.updateItems;

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, itemName);
				state.setDouble(2, price);
				state.setString(3, category);
				state.setInt(4, id);
				state.execute();
				clearFormItems();
				id = -1;
				MyFrame.getModelOfTable(TablesUtil.itemsTableName);
				MyFrame.getModelOfTable(TablesUtil.ordersTableName);
				OrdersTablePanel.refreshComboItems();
			} catch (JdbcSQLException e1) {
				JOptionPane.showMessageDialog(null, "Некоректни данни! " + e1.getMessage(), null,
						JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();

			}
		}
	}

	class SearchActionItem implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String category = categoryCombo.getSelectedItem().toString();
			conn = DBConnector.getConnection();
			String sql = TablesUtil.searchInTable(TablesUtil.itemsTableName);
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, category);
				result = state.executeQuery();
				model = new MyModel(result);
				MyFrame.itemsTable.setModel(model);
				clearFormItems();
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
			clearFormItems();
			MyFrame.getModelOfTable(TablesUtil.itemsTableName);
		}
	}

	private void clearFormItems() {
		itemNameTField.setText("");
		priceTField.setText("");
	}

}
