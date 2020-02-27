package cn.stock;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class MainDialog extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Db db;
	private List<Stock> stocks;
	private Object[][] table_data;
	// private StockTableModel stock_table_model;
	private AbstractTableModel stock_table_model;

	private JTable stock_table;
	
	//private Object lock = null;

	public MainDialog(String title, Object lock) {
		super(title);
		//this.lock = lock;
		getStockData();
		initUI();
		Runnable update_run = new Update(this, lock);
		Thread update_thread = new Thread(update_run);
		update_thread.start();
	}

	public void addStock() {
		AddDialog add = new AddDialog(this, stocks);
		// add.exist_stocks = stocks;
		int state = add.getReturnStatus();
		MyLog.Trace("MainDialog:addStock:AddDialog return " + state);
		if (state == 1) {
			// 刷新列表
			StockCache.update(StockCache.UPDATE_TYPE_STOCK);
			updateStockList();
		}
	}

	public void updateStockList() {
		MyLog.Trace("MainDialog:updateStockList:start.");
		getStockData();
		stock_table.validate();
		stock_table.updateUI();
	}

	private void getStockData() {

		stocks = StockCache.getStockList();

		table_data = new Object[stocks.size()][3];
		for (int i = 0; i < table_data.length; i++) {
			Stock stock = stocks.get(i);
			table_data[i][0] = stock.name;
			table_data[i][1] = stock.price;
			if (stock.price > 0.0 && stock.dividend > 0.0) {
//				// 计算股息率
//				double dividend_rate = stock.dividend / stock.price * 100;
//				String dividendrate = String.format("%.3f", dividend_rate);
//				table_data[i][2] = dividendrate;
				table_data[i][2] = stock.dividend_rate;
			} else {
				table_data[i][2] = "-";
			}

		}
	}

	public AbstractTableModel getTableModel() {
		return new AbstractTableModel() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			private Object[] columnNames = { "股票名称", "最新价", "股息率(%)" };

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

	// 获取当前分红列表选中行信息
	public Stock getSelectedStock() {
		int row = stock_table.getSelectedRow();
		if (row == -1) {
			JOptionPane.showMessageDialog(null, "未找到选中行！", "提示", JOptionPane.INFORMATION_MESSAGE);
			return null;
		}

		String stock_name = stock_table.getValueAt(row, 0).toString();
		int index = -1;
		for (int i = 0; i < stocks.size(); i++) {
			Stock stock = stocks.get(i);
			if (stock.name.equalsIgnoreCase(stock_name)) {
				index = i;
				break;
			}
		}
		if (index < 0) {
			return null;
		}
		Stock stock = stocks.get(index);
		MyLog.Trace("select stock : " + stock.toString());
		return stock;
	}

	public void showStockInfo() {
		Stock stock = getSelectedStock();
		if (stock == null) {
			return;
		}

		StockDividendDialog dlg = new StockDividendDialog(this, stock);
//		// add.exist_stocks = stocks;
//		int state = dlg.getReturnStatus();
//		if (state == 1) {
//			// 刷新列表
//			updateStockList();
//		}
		updateStockList();
	}

	public void delStock() {
		Stock stock = getSelectedStock();
		if (stock == null) {
			return;
		}

		int isDelete = JOptionPane.showConfirmDialog(null, "是否删除\"" + stock.name + "\"？", "提示",
				JOptionPane.YES_NO_OPTION);
		if (isDelete != JOptionPane.YES_OPTION) {
			return;
		}

		if (db == null) {
			db = new Sqlite();
		}

		try {
			db.init();

			db.delStock(stock);

			// update list
			StockCache.update(StockCache.UPDATE_TYPE_STOCK);
			updateStockList();
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("MainDialog:delStock:Exception " + e.toString());
		}

	}
	
	public void showDividendInfo() {
		Stock stock = getSelectedStock();
		if (stock == null) {
			return;
		}

		StockDividendRateDialog dlg = new StockDividendRateDialog(this, stock);
	}
	
	public void showStockAlert() {
		Stock stock = getSelectedStock();
		if (stock == null) {
			return;
		}

		ReminderDialog dlg = new ReminderDialog(this, stock);
		
		if(dlg.getReturnStatus() == 1) {
			StockCache.update(StockCache.UPDATE_TYPE_REMINDER, stock);
		}
		updateStockList();
	}
	
	public void showSetting() {
		SetDialog dlg = new SetDialog(this);
	}

	private final String POPUP_MENU_DIVIDEND = "分红";
	private final String POPUP_MENU_DIVIDENDRATE = "股息";
	private final String POPUP_MENU_ALERT = "提醒";
	private final String POPUP_MENU_DELETE = "删除";
	
	JButton dividend = null;
	JButton dividendrate = null;

	private void initUI() {
		BorderLayout mainLayout = new BorderLayout();
		getContentPane().setLayout(mainLayout);

		//JPanel tool_panel = new JPanel();
		JButton add = new JButton("添加");
		add.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println("添加按钮被点击");
				// AddDialog add = new AddDialog(null);
				// add.setVisible(true);
				addStock();
			}
		});
		add.setBounds(10, 20, 60, 25);

		dividend = new JButton("分红");
		dividend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println("添加按钮被点击");
				// AddDialog add = new AddDialog(null);
				// add.setVisible(true);
				showStockInfo();
			}
		});
		dividend.setEnabled(false);
		dividend.setBounds(75, 20, 60, 25);
		
		dividendrate = new JButton("股息");
		dividendrate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// System.out.println("添加按钮被点击");
				// AddDialog add = new AddDialog(null);
				// add.setVisible(true);
				showDividendInfo();
			}
		});
		dividendrate.setEnabled(false);
		dividendrate.setBounds(140, 20, 60, 25);

		JButton set = new JButton("设置");
		set.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showSetting();
			}
		});
		set.setBounds(205, 20, 60, 25);
		
//		tool_panel.add(add);
//		tool_panel.add(dividend);
//		tool_panel.add(dividendrate);
//		tool_panel.add(set);

		JScrollPane stock_panel = new JScrollPane();
		//JPanel centerPanel = new JPanel();
		// JScrollPane stock_panel = new JScrollPane();
		// stock_panel.setViewportView(centerPanel);
		// JList stock_list = new JList();
		// getStockData();
		// stock_table_model = new StockTableModel(table_data);
		stock_table_model = getTableModel();
		stock_table = new JTable(stock_table_model);
		stock_table.setBounds(10, 60, 260, 300);
		//设置仅能选中一行
		ListSelectionModel select_model = stock_table.getSelectionModel();
		select_model.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		stock_table.addMouseListener(new MouseAdapter() {
			public void mouseClicked(final MouseEvent arg0) {
				if (SwingUtilities.isRightMouseButton(arg0)) {
					int row = stock_table.getSelectedRow(); // 获得当前选中的行号
					String stock_name = stock_table.getValueAt(row, 0).toString();
					// 弹出右键菜单
					JPopupMenu popup_menu = new JPopupMenu();
					JMenuItem dividend_menu = new JMenuItem(POPUP_MENU_DIVIDEND);
					JMenuItem dividendrate_menu = new JMenuItem(POPUP_MENU_DIVIDENDRATE);
					JMenuItem alert_menu = new JMenuItem(POPUP_MENU_ALERT);
					JMenuItem delete_menu = new JMenuItem(POPUP_MENU_DELETE);

					StockMenuActionListener menu_listener = new StockMenuActionListener();
					dividend_menu.addActionListener(menu_listener);
					dividendrate_menu.addActionListener(menu_listener);
					alert_menu.addActionListener(menu_listener);
					delete_menu.addActionListener(menu_listener);

					popup_menu.add(dividend_menu);
					popup_menu.add(dividendrate_menu);
					popup_menu.add(alert_menu);
					popup_menu.add(delete_menu);
					popup_menu.show(arg0.getComponent(), arg0.getX(), arg0.getY());
				} else if(SwingUtilities.isLeftMouseButton(arg0)) {
					int select_num = stock_table.getSelectedRowCount();
					if(select_num > 0) {
						dividend.setEnabled(true);
						dividendrate.setEnabled(true);
					}else {
						dividend.setEnabled(false);
						dividendrate.setEnabled(false);
					}
				}
			}
		});
		stock_panel.setViewportView(stock_table);
		stock_panel.setBounds(10, 60, 260, 300);
//		// stock_panel.add(stock_list);
//		centerPanel.add(stock_table.getTableHeader(), BorderLayout.NORTH);
//		centerPanel.add(stock_table);

//		getContentPane().add(tool_panel, BorderLayout.NORTH);
//		getContentPane().add(centerPanel, BorderLayout.CENTER);
		
		JPanel main_panel = new JPanel(null);
		main_panel.add(add);
		main_panel.add(dividend);
		main_panel.add(dividendrate);
		main_panel.add(set);
		main_panel.add(stock_panel);
		setContentPane(main_panel);

		this.setSize(280, 400);
		this.setTitle("股息工具");
//		URL icon_url = this.getClass().getResource(Global.icon);
//		ImageIcon img = new ImageIcon(icon_url);
		ImageIcon img = new ImageIcon(Global.icon);
		this.setIconImage(img.getImage());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // 点击关闭按钮时退出
		this.setLocationRelativeTo(null); // 设置窗口在屏幕的中央
		// 设置大小不可调
		this.setResizable(false);

	}

	// 股票列表右键菜单处理
	class StockMenuActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			String command = e.getActionCommand();
			if (command.equalsIgnoreCase(POPUP_MENU_DIVIDEND)) {
				showStockInfo();
			} else if (command.equalsIgnoreCase(POPUP_MENU_DIVIDENDRATE)) {
				showDividendInfo();

			} else if (command.equalsIgnoreCase(POPUP_MENU_ALERT)) {
				showStockAlert();

			} else if (command.equalsIgnoreCase(POPUP_MENU_DELETE)) {
				delStock();

			} else {
				MyLog.Error("MainDialog:actionPerformed:unsuppoted command " + command);
			}
		}
	}


	public class Update implements Runnable {

		private MainDialog dialog = null;
		private Object lock = null;
		
		public Update(MainDialog dialog, Object lock) {
			this.dialog = dialog;
			this.lock = lock;
		}
		
	    public void run() {
	        while (true) {
	            try {
	            	synchronized(lock) {
	            		MyLog.Trace("MainDialog UpdateThread wait.");
	            		lock.wait();
	            		MyLog.Trace("MainDialog UpdateThread update.");
	            	}
	            } catch (InterruptedException e) {
	                e.printStackTrace();
	                MyLog.Error("MainDialog:Update:run:Exception " + e.toString());
	            }

	            dialog.updateStockList();
	        }
	    }

	}
}
