package cn.stock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class ReminderDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Stock m_stock = null;
	private List<Reminder> m_reminders = null;
	
	private Db db = null;
	
	private int text_field_column = 8;
	
	private JTextField stock_name_field;	//股票名称框
	private JTextField stock_dividend_max_field;	//股息上限
	private JTextField stock_dividend_min_field;	//股息下限
	private JTextField stock_price_max_field;	//股价上限
	private JTextField stock_price_min_field;	//股价下限
	
	private double stock_dividend_max = 0.0;
	private double stock_dividend_min = 0.0;
	private double stock_price_max = 0.0;
	private double stock_price_min = 0.0;
	
	private int state = 0; // 返回值

	public int getReturnStatus() {
		return state;
	}
	
	public ReminderDialog(JFrame parent, Stock stock) {
        super(parent);
        m_stock = stock;
        this.setResizable(false);
        this.setSize(220, 250);
        this.setTitle("设置提醒");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        //设置父窗口不可激活
        this.setModal(true);
        //先查询历史设置
        getStockReminders();
        initUI();
    }
	
	private double getStockReminder(int type) {
		double value = 0.0;
		
		for(Reminder reminder : m_reminders) {
			if(reminder.type == type) {
				value = reminder.value;
				break;
			}
		}
		
		return value;
	}
	
	private void getStockReminders() {
		if(db == null) {
			db = new Sqlite();
		}
		
		try {
			m_reminders = db.getStockReminders(m_stock);
			if(m_reminders == null || m_reminders.size() <= 0) {
				MyLog.Trace("ReminderDialog:getStockReminders:" + m_stock.code + " no reminder.");
			}
			
			stock_dividend_max = getStockReminder(Reminder.REMINDER_TYPE_DIVIDEND_MAX);
			stock_dividend_min = getStockReminder(Reminder.REMINDER_TYPE_DIVIDEND_MIN);
			stock_price_max = getStockReminder(Reminder.REMINDER_TYPE_PRICE_MAX);
			stock_price_min = getStockReminder(Reminder.REMINDER_TYPE_PRICE_MIN);
		} catch (MyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public Reminder getStockReminder(JTextField field, double old_value, int type) {
		Reminder reminder = new Reminder();
		reminder.code = m_stock.code;
		reminder.type = type;
		
		String s_new_value = field.getText();
		if(s_new_value != null && s_new_value.length() > 0) {
			double d_new_value = Double.valueOf(s_new_value);
			if(d_new_value != old_value) {
				//设置为新值
				reminder.value = d_new_value;
			}
		}else if(stock_dividend_max != 0.0) {
			//新值为空，原值不为空时，将设置置空
			reminder.value = 0.0;
		}
		
		return reminder;
	}
	
	public void saveStockReminder() {
		List<Reminder> save_list = new ArrayList<Reminder>();
		
		Reminder dividend_max_reminder = getStockReminder(stock_dividend_max_field, stock_dividend_max, 
				Reminder.REMINDER_TYPE_DIVIDEND_MAX);
		save_list.add(dividend_max_reminder);
		Reminder dividend_min_reminder = getStockReminder(stock_dividend_min_field, stock_dividend_min, 
				Reminder.REMINDER_TYPE_DIVIDEND_MIN);
		save_list.add(dividend_min_reminder);
		Reminder price_max_reminder = getStockReminder(stock_price_max_field, stock_price_max, 
				Reminder.REMINDER_TYPE_PRICE_MAX);
		save_list.add(price_max_reminder);
		Reminder price_min_reminder = getStockReminder(stock_price_min_field, stock_price_min, 
				Reminder.REMINDER_TYPE_PRICE_MIN);
		save_list.add(price_min_reminder);
		
		if(save_list.size() > 0) {
			if(db == null) {
				db = new Sqlite();
			}
			
			try {
				for(Reminder reminder : save_list) {
					db.modReminder(reminder);
				}
			} catch (MyException e) {
				// TODO Auto-generated catch block
				MyLog.Trace("ReminderDialog:saveStockReminder:Exception " + e.toString());
			}
		}
		
		state = 1;
		this.dispose();
	}
	
	private void initUI() {
        JLabel stock_name_label = new JLabel("股票名称");
        stock_name_field = new JTextField(text_field_column);
        stock_name_field.setText(m_stock.name);
        stock_name_field.setEnabled(false);
        stock_name_label.setBounds(10, 20, 70, 25);
        stock_name_field.setBounds(90, 20, 100, 25);
        
        JLabel stock_dividend_max_label = new JLabel("股息上限(%)");
        stock_dividend_max_field = new JTextField(text_field_column);
        if(stock_dividend_max > 0.0) {
        	stock_dividend_max_field.setText(Double.toString(stock_dividend_max));
        }
        stock_dividend_max_label.setBounds(10, 50, 70, 25);
        stock_dividend_max_field.setBounds(90, 50, 100, 25);
        
        JLabel stock_dividend_min_label = new JLabel("股息下限(%)");
        stock_dividend_min_field = new JTextField(text_field_column);
        if(stock_dividend_min > 0.0) {
        	stock_dividend_min_field.setText(Double.toString(stock_dividend_min));
        }
        stock_dividend_min_label.setBounds(10, 80, 70, 25);
        stock_dividend_min_field.setBounds(90, 80, 100, 25);
        
        
        JLabel stock_price_max_label = new JLabel("股价上限");
        stock_price_max_field = new JTextField(text_field_column);
        if(stock_price_max > 0.0) {
        	stock_price_max_field.setText(Double.toString(stock_price_max));
        }
        stock_price_max_label.setBounds(10, 110, 70, 25);
        stock_price_max_field.setBounds(90, 110, 100, 25);
        
        JLabel stock_price_min_label = new JLabel("股价下限");
        stock_price_min_field = new JTextField(text_field_column);
        if(stock_price_min > 0.0) {
        	stock_price_min_field.setText(Double.toString(stock_price_min));
        }
        stock_price_min_label.setBounds(10, 140, 70, 25);
        stock_price_min_field.setBounds(90, 140, 100, 25);
        
        JButton save = new JButton("保存");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	saveStockReminder();
            }
        });
        save.setBounds(80, 180, 60, 25);
		
		JPanel main_panel = new JPanel(null); 
		main_panel.add(stock_name_label);
		main_panel.add(stock_name_field);
		main_panel.add(stock_dividend_max_label);
		main_panel.add(stock_dividend_max_field);
		main_panel.add(stock_dividend_min_label);
		main_panel.add(stock_dividend_min_field);
		main_panel.add(stock_price_max_label);
		main_panel.add(stock_price_max_field);
		main_panel.add(stock_price_min_label);
		main_panel.add(stock_price_min_field);
		main_panel.add(save);
		
		setContentPane(main_panel);

		this.setVisible(true);
	}

}
