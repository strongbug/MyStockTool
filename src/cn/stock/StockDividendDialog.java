package cn.stock;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class StockDividendDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String POPUP_MENU_MODIFY = "修改";
	private final String POPUP_MENU_DELETE = "删除";

	private Stock m_stock;

	private Db db;
	private List<StockDividend> stock_dividend_list;

	private JFrame m_parent;

	public StockDividendDialog(JFrame parent, Stock stock) {
		super(parent);
		m_parent = parent;
		m_stock = stock;
		this.setResizable(false);
		this.setSize(300, 300);
		this.setTitle("股票信息");
		this.setIconImage(new ImageIcon(Global.icon).getImage());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setLocationRelativeTo(parent);
		// 设置父窗口不可激活
		this.setModal(true);
		initUI(stock);
	}

	private int state = 0; // 返回值

	public int getReturnStatus() {
		return state;
	}

	private int text_field_column = 8;

	private JTextField stock_code_field; // 股票代码框
	private JTextField stock_name_field; // 股票名称框

	private Object[][] table_data;
	private JTable stock_dividend_table;
	private AbstractTableModel stock_dividend_table_model;

	public void addStockDividend() {
		DividendDialog dlg = new DividendDialog(m_parent, m_stock);
		// add.exist_stocks = stocks;
		int state = dlg.getReturnStatus();
		if (state == 1) {
			// 刷新列表
			StockCache.update(StockCache.UPDATE_TYPE_DIVIDEND, m_stock);
			updateStockDividendList();
		}
	}

	public void modStockDividend() {
		StockDividend dividend = getSelectedStockDividend();

		DividendDialog dlg = new DividendDialog(m_parent, m_stock, dividend);
		// add.exist_stocks = stocks;
		int state = dlg.getReturnStatus();
		if (state == 1) {
			// 刷新列表
			StockCache.update(StockCache.UPDATE_TYPE_DIVIDEND, m_stock);
			updateStockDividendList();
		}
	}

	public void delStockDividend() {
		StockDividend dividend = getSelectedStockDividend();

		int isDelete = JOptionPane.showConfirmDialog(null, "是否删除\"" + DateUtil.getDateString(dividend.date) + "\"的分红记录？", "提示",
				JOptionPane.YES_NO_OPTION);
		if (isDelete != JOptionPane.YES_OPTION) {
			return;
		}

		if (db == null) {
			db = new Sqlite();
		}

		try {
			db.delStockDividend(dividend);

			// 刷新列表
			StockCache.update(StockCache.UPDATE_TYPE_DIVIDEND, m_stock);
			updateStockDividendList();
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockDividendDialog:delStockDividend:Exception " + e.toString());
		}
	}

	private void getStockDividendData() {

		if (db == null) {
			db = new Sqlite();
		}

		try {
			stock_dividend_list = db.getStockDividends(m_stock);
			MyLog.Trace("StockDividendDialog:getStockDividendData:stock dividend num " + 
					stock_dividend_list.size());
			for (StockDividend dividend : stock_dividend_list) {
				MyLog.Trace("StockDividendDialog:getStockDividendData:stock dividend " + 
						dividend.toString());
			}
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockDividendDialog:getStockDividendData:Exception " + e.toString());
		}

		table_data = new Object[stock_dividend_list.size()][4];
		for (int i = 0; i < table_data.length; i++) {
			StockDividend dividend = stock_dividend_list.get(i);
			table_data[i][0] = DateUtil.getDateString(dividend.date);
			table_data[i][1] = dividend.num;
			table_data[i][2] = dividend.songgu;
			table_data[i][3] = dividend.peigu;
		}
	}

	public void updateStockDividendList() {
		MyLog.Trace("StockDividendDialog:updateStockDividendList:update.");
		getStockDividendData();
		stock_dividend_table.validate();
		stock_dividend_table.updateUI();
	}

	public void setStockReminder() {
		ReminderDialog dlg = new ReminderDialog(m_parent, m_stock);
		
		if(dlg.getReturnStatus() == 1) {
			StockCache.update(StockCache.UPDATE_TYPE_REMINDER, m_stock);
		}
	}

	// 获取当前分红列表选中行信息
	public StockDividend getSelectedStockDividend() {
		int row = stock_dividend_table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(null, "未找到选中行！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}

		String dividend_date = stock_dividend_table.getValueAt(row, 0).toString();
		MyLog.Trace("StockDividendDialog:getSelectedStockDividend:select " + dividend_date);
		int index = -1;
		for (int i = 0; i < stock_dividend_list.size(); i++) {
			StockDividend stock_dividend = stock_dividend_list.get(i);
			if (dividend_date.equalsIgnoreCase(DateUtil.getDateString(stock_dividend.date))) {
				index = i;
				break;
			}
		}
		if (index < 0) {
			return null;
		}
		StockDividend dividend = stock_dividend_list.get(index);
		MyLog.Trace("StockDividendDialog:getSelectedStockDividend:select dividend " + dividend.toString());
		return dividend;
	}

	private void initUI(Stock stock) {
		JLabel stock_code_label = new JLabel("代码");
		stock_code_field = new JTextField(text_field_column);
		stock_code_field.setText(stock.code);
		stock_code_field.setEnabled(false);
		stock_code_label.setBounds(20, 20, 30, 25);
		stock_code_field.setBounds(60, 20, 80, 25);

		JLabel stock_name_label = new JLabel("名称");
		stock_name_field = new JTextField(text_field_column);
		stock_name_field.setText(stock.name);
		stock_name_field.setEnabled(false);
		stock_name_label.setBounds(150, 20, 30, 25);
		stock_name_field.setBounds(190, 20, 80, 25);

		JButton more = new JButton("录入分红");
		more.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addStockDividend();
			}
		});
		more.setBounds(100, 60, 90, 25);

		JScrollPane dividendPanel = new JScrollPane();
		getStockDividendData();
		stock_dividend_table_model = getStockDividendTableModel();
		stock_dividend_table = new JTable(stock_dividend_table_model);
		// stock_panel.add(stock_list);
		// dividendPanel.add(stock_dividend_table.getTableHeader(), BorderLayout.NORTH);
		stock_dividend_table.setBounds(10, 90, 280, 160);
		stock_dividend_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0)) {
					//int row = stock_dividend_table.getSelectedRow(); // 获得当前选中的行号
					//String date = stock_dividend_table.getValueAt(row, 0).toString();
					// 弹出右键菜单
					JPopupMenu popup_menu = new JPopupMenu();
					JMenuItem modify_menu = new JMenuItem(POPUP_MENU_MODIFY);
					JMenuItem delete_menu = new JMenuItem(POPUP_MENU_DELETE);

					StockDividendMenuActionListener menu_listener = new StockDividendMenuActionListener();
					modify_menu.addActionListener(menu_listener);
					delete_menu.addActionListener(menu_listener);

					popup_menu.add(modify_menu);
					popup_menu.add(delete_menu);
					popup_menu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
				}
			}
		});
		dividendPanel.setViewportView(stock_dividend_table);
		dividendPanel.setBounds(10, 90, 280, 160);

		JPanel main_panel = new JPanel(null);
		main_panel.add(stock_code_label);
		main_panel.add(stock_code_field);
		main_panel.add(stock_name_label);
		main_panel.add(stock_name_field);
		main_panel.add(more);
		//main_panel.add(set);
		main_panel.add(dividendPanel);

		setContentPane(main_panel);

		this.setVisible(true);
	}

	// 分红列表右键菜单处理
	class StockDividendMenuActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equalsIgnoreCase(POPUP_MENU_MODIFY)) {
				modStockDividend();
			} else if (command.equalsIgnoreCase(POPUP_MENU_DELETE)) {
				delStockDividend();
			} else {
				MyLog.Error("StockDividendDialog:actionPerformed:unsuppoted command " + command);
			}
		}
	}

	public AbstractTableModel getStockDividendTableModel() {
		return new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			private Object[] columnNames = { "分红日期", "股息", "送股数", "配股数" };

			@Override
			public int getColumnCount() {
				return columnNames.length;
			}

			@Override
			public String getColumnName(int column) {
				return columnNames[column].toString();
			}

			@Override
			public int getRowCount() {
				return table_data.length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				// TODO Auto-generated method stub
				return table_data[rowIndex][columnIndex];
			}
		};
	}

}
