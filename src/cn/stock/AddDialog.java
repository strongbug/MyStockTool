package cn.stock;


import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AddDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int text_field_column = 8;
	
	private JTextField stock_code_field;	//股票代码框
	private JTextField stock_name_field;	//股票名称框
	private JTextField stock_price_field;	//股票价格框
	//private JLabel add_info_label;	//提示信息
	private JButton stock_add;		//添加按钮
	
	private Hq hq;	//
	private Db db;
	
	public List<Stock> exist_stocks;	//已添加的股票信息
	private Stock new_stock;			//新添加的股票
	
	private int state = 0;			//返回值
	
	public int getReturnStatus() {
		return state;
	}
	
	public AddDialog(JFrame parent, List<Stock> stocks) {
        super(parent);

        exist_stocks = stocks;
        
        this.setResizable(false);
        this.setSize(200, 200);
        this.setTitle("添加股票");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        //设置父窗口不可激活
        this.setModal(true);
        //this.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
        initUI(parent);
    }
	
	public void checkStockCode(String code) {
		if(code.length() == 6) {
			//get stock info.
			MyLog.Trace("AddDialog:checkStockCode:code " + code);
			
			if(new_stock == null) {
				new_stock = new Stock();
			}
			
			if(hq == null) {
				hq = new Sina();
			}
			try {
				new_stock.code = code;
				new_stock.type = Stock.getStockType(new_stock.code);
				hq.update(new_stock);
				MyLog.Trace("AddDialog:checkStockCode:new_stock " + new_stock.toString());
			} catch (MyException e) {
				// TODO Auto-generated catch block
				MyLog.Error("AddDialog:checkStockCode:Exception " + e.toString());
			}
			
			stock_name_field.setText(new_stock.name);
			stock_price_field.setText(Double.toString(new_stock.price));
			stock_add.setEnabled(true);
		}
		else
		{
			stock_name_field.setText("");
			stock_price_field.setText("");
			stock_add.setEnabled(false);
		}
	}
	
	private void addStock() {
		//检查是否重复
		String stock_code = stock_code_field.getText();
		
		for(Stock stock : exist_stocks) {
			if(stock.code.equalsIgnoreCase(stock_code)) {
				MyLog.Info("AddDialog:addStock:check " + stock_code + " is already exist!");
				JOptionPane.showMessageDialog(null, "不能重复添加！", "错误", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		
		if(db == null) {
			db = new Sqlite();
		}
		try {
			//设置新股票索引
			new_stock.index = exist_stocks.size();
			
			db.addStock(new_stock);
			
			//添加成功后设置返回值，列表需要刷新
			state = 1;
			dispose();
		}catch(MyException e) {
			MyLog.Error("AddDialog:addStock:Exception " + e.toString());
			JOptionPane.showMessageDialog(null, "添加失败！", "错误", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}

	private void initUI(JFrame parent) {
        JLabel stock_code_label = new JLabel("股票代码");
        stock_code_field = new JTextField(text_field_column);
        stock_code_field.setDocument(new NumberLenghtLimitedDmt(6));
        stock_code_field.getDocument().addDocumentListener(new DocumentListener() {

        	protected void changeFilter(DocumentEvent event) {
        		javax.swing.text.Document document = event.getDocument();
        		try {
        			String text=document.getText(0, document.getLength());   
        			checkStockCode(text);
         
        		} catch (Exception ex) {
        			MyLog.Error("AddDialog:initUI:Exception " + ex.toString());
        		}
        	}
			@Override
			public void changedUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				changeFilter(e);
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				changeFilter(e);
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				// TODO Auto-generated method stub
				changeFilter(e);
			}

        });
        
        JLabel stock_name_label = new JLabel("股票名称");
        stock_name_field = new JTextField(text_field_column);
        stock_name_field.setEnabled(false);
        
        JLabel stock_price_label = new JLabel("当前价格");
        stock_price_field = new JTextField(text_field_column);
        stock_price_field.setEnabled(false);
        
        //add_info_label = new JLabel();
        
        stock_add = new JButton("添加");
        stock_add.setEnabled(false);
        stock_add.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addStock();
            }
        });
        
        JPanel code_panel = new JPanel(new FlowLayout());
        JPanel name_panel = new JPanel(new FlowLayout());
        JPanel price_panel = new JPanel(new FlowLayout());
        JPanel add_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        //JPanel info_panel = new JPanel(new FlowLayout());
        JPanel main_panel = new JPanel();
        
        code_panel.add(stock_code_label);
        code_panel.add(stock_code_field);
        
        name_panel.add(stock_name_label);
        name_panel.add(stock_name_field);
        
        price_panel.add(stock_price_label);
        price_panel.add(stock_price_field);
        
        //info_panel.add(add_info_label);
        
		add_panel.add(stock_add);
		
		main_panel.add(code_panel);
		main_panel.add(name_panel);
		main_panel.add(price_panel);
		//main_panel.add(info_panel);
		main_panel.add(add_panel);
		
		setContentPane(main_panel);
        
		this.setVisible(true);
	}
}
