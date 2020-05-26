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

public class CustomersTablePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scroller = new JScrollPane(MyFrame.customersTable);

	private JPanel upPanel = new JPanel();
	private JPanel midPanel = new JPanel();
	private JPanel downPanel = new JPanel();

	private JButton addButton = new JButton("Добави");
	private JButton delButton = new JButton("Изтрий");
	private JButton editButton = new JButton("Редактирай");
	private JButton searchButton = new JButton("Търсене по фамилия");
	private JButton refreshButton = new JButton("Обнови");

	private JLabel fNameLabel = new JLabel("Първо име:");
	private JLabel lNameLabel = new JLabel("Фамилия:");
	private JLabel addressLabel = new JLabel("Адрес:");
	private JLabel phoneLabel = new JLabel("Телефон:");
	private JLabel genderLabel = new JLabel("Пол:");

	private JTextField fNameTField = new JTextField();
	private JTextField lNameTField = new JTextField();
	private JTextField addressTField = new JTextField();
	private JTextField phoneTField = new JTextField();
	private String[] genders = { "Female", "Male" };
	private JComboBox<String> genderCombo = new JComboBox<>(genders);
	private Connection conn = null;
	private PreparedStatement state = null;
	private ResultSet result = null;
	private MyModel model = null;
	private int id = -1; // selected id

	public CustomersTablePanel(Connection conn, PreparedStatement state, ResultSet result, MyModel model) {
		this.conn = conn;
		this.state = state;
		this.result = result;
		this.model = model;
		this.add(upPanel);
		this.add(midPanel);
		this.add(downPanel);

		this.setLayout(new GridLayout(3, 1));

		// upPanel
		upPanel.setLayout(new GridLayout(6, 2));
		upPanel.add(fNameLabel);
		upPanel.add(fNameTField);
		upPanel.add(lNameLabel);
		upPanel.add(lNameTField);
		upPanel.add(addressLabel);
		upPanel.add(addressTField);
		upPanel.add(phoneLabel);
		upPanel.add(phoneTField);
		upPanel.add(genderLabel);
		upPanel.add(genderCombo);
		// midPanel
		midPanel.add(addButton);
		midPanel.add(delButton);
		midPanel.add(editButton);
		midPanel.add(searchButton);
		midPanel.add(refreshButton);
		addButton.addActionListener(new AddActionCustomer());
		delButton.addActionListener(new DeleteActionCustomer());
		editButton.addActionListener(new UpdateActionCustomer());
		searchButton.addActionListener(new SearchActionCustomer());
		refreshButton.addActionListener(new RefreshActionCustomer());
		// downPanel
		scroller.setPreferredSize(new Dimension(800, 200));
		downPanel.add(scroller);
		MyFrame.customersTable.addMouseListener(new MouseActionCustomer());
	}

	class DeleteActionCustomer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {

			conn = DBConnector.getConnection();
			String sql = TablesUtil.deleteCustomers;
			try {
				state = conn.prepareStatement(sql);
				state.setInt(1, id);
				state.execute();
				MyFrame.getModelOfTable(TablesUtil.customersTableName);
				id = -1;
				OrdersTablePanel.refreshComboCustomers();
			} catch (JdbcSQLException e1) {
//				e1.printStackTrace(System.out);
				JOptionPane.showMessageDialog(null,
						"Не можете да изтриете този запис в таблицата, защото участва в таблицата с поръчките! " + e1.getMessage(), null,
						JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}

	}

	class MouseActionCustomer implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
			int row = MyFrame.customersTable.getSelectedRow();
			id = Integer.parseInt(MyFrame.customersTable.getValueAt(row, 0).toString());
			if (e.getClickCount() > 1) {
				fNameTField.setText(MyFrame.customersTable.getValueAt(row, 1).toString());
				lNameTField.setText(MyFrame.customersTable.getValueAt(row, 2).toString());
				addressTField.setText(MyFrame.customersTable.getValueAt(row, 3).toString());
				phoneTField.setText(MyFrame.customersTable.getValueAt(row, 4).toString());
				if (MyFrame.customersTable.getValueAt(row, 5).equals("f")) {
					genderCombo.setSelectedIndex(0);
				} else {
					genderCombo.setSelectedIndex(1);
				}
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

	class AddActionCustomer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String fName = fNameTField.getText();
			String lName = lNameTField.getText();
			String address = addressTField.getText();
			String phone = phoneTField.getText();
			String gender = genderCombo.getSelectedIndex() == 0 ? "f" : "m";

			if (!fName.isEmpty() && !lName.isEmpty() && !address.isEmpty() && !phone.isEmpty()) {

				conn = DBConnector.getConnection();
				String query = TablesUtil.insertCustomers;

				try {
					state = conn.prepareStatement(query);
					state.setString(1, fName);
					state.setString(2, lName);
					state.setString(3, phone);
					state.setString(4, gender);
					state.setString(5, address);
					state.execute();
					MyFrame.getModelOfTable(TablesUtil.customersTableName);
					clearFormCustomers();
					MyFrame.getModelOfTable(TablesUtil.ordersTableName);
					OrdersTablePanel.refreshComboCustomers();
				} catch(JdbcSQLException e1) {
					JOptionPane.showMessageDialog(null, "Некоректни данни! " + e1.getMessage(), null, JOptionPane.ERROR_MESSAGE);
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			} else {
				JOptionPane.showMessageDialog(null, "Попълнете всички полета!", null, JOptionPane.ERROR_MESSAGE);
			}

		}// end method

	}// end AddAction

	class UpdateActionCustomer implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			String fName = fNameTField.getText();
			String lName = lNameTField.getText();
			String address = addressTField.getText();
			String phone = phoneTField.getText();
			String gender = genderCombo.getSelectedIndex() == 0 ? "f" : "m";
			
			conn = DBConnector.getConnection();
			String sql = TablesUtil.updateCustomers;

			try {
				state = conn.prepareStatement(sql);
				state.setString(1, fName);
				state.setString(2, lName);
				state.setString(3, phone);
				state.setString(4, gender);
				state.setString(5, address);
				state.setInt(6, id);

				state.execute();
				clearFormCustomers();
				id = -1;
				MyFrame.getModelOfTable(TablesUtil.customersTableName);
				MyFrame.getModelOfTable(TablesUtil.ordersTableName);
				OrdersTablePanel.refreshComboCustomers();
			} catch(JdbcSQLException e1) {
				JOptionPane.showMessageDialog(null, "Некоректни данни! " + e1.getMessage(), null, JOptionPane.ERROR_MESSAGE);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	class SearchActionCustomer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String lname = lNameTField.getText();
			conn = DBConnector.getConnection();
			String sql = TablesUtil.searchInTable(TablesUtil.customersTableName);
			try {
				state = conn.prepareStatement(sql);
				state.setString(1, lname);
				result = state.executeQuery();
				model = new MyModel(result);
				MyFrame.customersTable.setModel(model);
				clearFormCustomers();
			} catch (SQLException e1) {
				e1.printStackTrace();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}

	class RefreshActionCustomer implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			clearFormCustomers();
			MyFrame.getModelOfTable(TablesUtil.customersTableName);
		}
	}

	private void clearFormCustomers() {
		fNameTField.setText("");
		lNameTField.setText("");
		addressTField.setText("");
		phoneTField.setText("");
		genderCombo.setSelectedIndex(0);
	}
}
