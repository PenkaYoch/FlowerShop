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

	private JTable doubleTable = new JTable();
	private JScrollPane scroller = new JScrollPane(doubleTable);

	private JPanel upPanel = new JPanel();
	private JPanel midPanel = new JPanel();
	private JPanel downPanel = new JPanel();

	private JButton searchButton = new JButton("Търсене по фамилно име на клиент и категория на продукт: ");

	private JLabel fnameLabel = new JLabel("Фамилно име на клиент:");
	private JTextField fnameTField = new JTextField();
	private JLabel categoryLabel = new JLabel("Категория на продукт:");
	private String[] categories = { "Букети", "Кошници", "Саксии", "Градински растения", "Стайни растения" };
	private JComboBox<String> categoryCombo = new JComboBox<>(categories);

	private Connection conn = null;
	private PreparedStatement state = null;
	private ResultSet result = null;
	private MyModel model = null;

	public DoubleSearchPanel() {
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

		private void clearDoubleSearchForm() {
			fnameTField.setText("");
			categoryCombo.setSelectedIndex(0);
		}
	}
}
