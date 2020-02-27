package cn.stock;


import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class StockDividendRateDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Stock m_stock;

	private Db db;
	private List<DividendRate> stock_dividend_rate_list;
	
	private int text_field_column = 8;

	private JTextField stock_code_field; // 股票代码框
	private JTextField stock_name_field; // 股票名称框

	private Object[][] table_data;
	private JTable stock_dividend_rate_table;
	private AbstractTableModel stock_dividend_rate_table_model;

	private JFrame m_parent;

	public StockDividendRateDialog(JFrame parent, Stock stock) {
		super(parent);
		m_parent = parent;
		m_stock = stock;
		this.setResizable(false);
		this.setSize(300, 300);
		this.setTitle("股息信息");
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


	private void getStockDividendRateData() {

		if (db == null) {
			db = new Sqlite();
		}

		try {
			stock_dividend_rate_list = db.getStockDividendRates(m_stock, null, null);
			MyLog.Trace("StockDividendRateDialog:getStockDividendRateData:rate num " + 
					stock_dividend_rate_list.size());
			for (DividendRate rate : stock_dividend_rate_list) {
				MyLog.Trace("StockDividendRateDialog:getStockDividendRateData:rate " + 
						rate.toString());
			}
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockDividendRateDialog:getStockDividendRateData:Exception " + 
					e.toString());
		}

		table_data = new Object[stock_dividend_rate_list.size()][4];
		for (int i = 0; i < table_data.length; i++) {
			DividendRate rate = stock_dividend_rate_list.get(i);
			table_data[i][0] = DateUtil.getDateString(rate.date);
			table_data[i][1] = rate.price;
			table_data[i][2] = rate.rate;
		}
	}

	public void updateStockDividendList() {
		MyLog.Trace("StockDividendRateDialog:updateStockDividendList:start.");
		getStockDividendRateData();
		stock_dividend_rate_table.validate();
		stock_dividend_rate_table.updateUI();
	}

	public void setStockReminder() {
		ReminderDialog dlg = new ReminderDialog(m_parent, m_stock);
		
		if(dlg.getReturnStatus() == 1) {
			StockCache.update(StockCache.UPDATE_TYPE_REMINDER, m_stock);
		}
	}

//	// 获取当前分红列表选中行信息
//	public StockDividend getSelectedStockDividend() {
//		int row = stock_dividend_table.getSelectedRow();
//		if (row == -1) {
//			JOptionPane.showMessageDialog(null, "未找到选中行！", "提示", JOptionPane.INFORMATION_MESSAGE);
//			return null;
//		}
//
//		String dividend_date = stock_dividend_table.getValueAt(row, 0).toString();
//		System.out.println("select " + dividend_date);
//		int index = -1;
//		for (int i = 0; i < stock_dividend_list.size(); i++) {
//			StockDividend stock_dividend = stock_dividend_list.get(i);
//			if (dividend_date.equalsIgnoreCase(formatter.format(stock_dividend.date))) {
//				index = i;
//				break;
//			}
//		}
//		if (index < 0) {
//			return null;
//		}
//		StockDividend dividend = stock_dividend_list.get(index);
//		System.out.println("select dividend : " + dividend.toString());
//		return dividend;
//	}

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

		JScrollPane dividendPanel = new JScrollPane();
		getStockDividendRateData();
		stock_dividend_rate_table_model = getStockDividendRateTableModel();
		stock_dividend_rate_table = new JTable(stock_dividend_rate_table_model);
		// stock_panel.add(stock_list);
		// dividendPanel.add(stock_dividend_table.getTableHeader(), BorderLayout.NORTH);
		stock_dividend_rate_table.setBounds(10, 60, 280, 160);
		dividendPanel.setViewportView(stock_dividend_rate_table);
		dividendPanel.setBounds(10, 60, 280, 160);

		JPanel main_panel = new JPanel(null);
		main_panel.add(stock_code_label);
		main_panel.add(stock_code_field);
		main_panel.add(stock_name_label);
		main_panel.add(stock_name_field);
		//main_panel.add(set);
		main_panel.add(dividendPanel);

		setContentPane(main_panel);

		this.setVisible(true);
	}


	public AbstractTableModel getStockDividendRateTableModel() {
		return new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			private Object[] columnNames = { "日期", "股价", "股息率(%)"};

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
