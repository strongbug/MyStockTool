package cn.stock;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class DividendDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int text_field_column = 8;
	
	private JTextField stock_code_field;	//��Ʊ�����
	private JTextField stock_name_field;	//��Ʊ���ƿ�
	private JTextField stock_date_field;	//�ֺ����ڿ�
	private JTextField stock_num_field;	//��Ʊ�۸��
	private JTextField stock_peigu_field;
	private JTextField stock_songgu_field;
	
	private Stock m_stock = null;
	private StockDividend m_dividend = null;
	private Db db = null;
	
	public DividendDialog(JFrame parent, Stock stock) {
        super(parent);
        m_stock = stock;
        m_dividend = null;
        this.setResizable(false);
        //this.setSize(220, 300);
        this.setSize(280, 300);
        this.setTitle("¼��ֺ�");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        //���ø����ڲ��ɼ���
        this.setModal(true);
        initUI(stock);
    }
	
	public DividendDialog(JFrame parent, Stock stock, StockDividend dividend) {
        super(parent);
        m_stock = stock;
        m_dividend = dividend;
        this.setResizable(false);
        this.setSize(220, 300);
        this.setTitle("¼��ֺ�");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        //���ø����ڲ��ɼ���
        this.setModal(true);
        initUI(stock);
    }
	
	private int state = 0;			//����ֵ
	
	public int getReturnStatus() {
		return state;
	}	
	
	public void addStockDividend() {
		if(db == null) {
			db = new Sqlite();
		}
		try {
			//������ڸ�ʽ
			String date = stock_date_field.getText();
			Date dividend_date  = DateUtil.getDate(date);
			if (dividend_date == null) {
				JOptionPane.showMessageDialog(null, "��ѡ��ֺ����ڣ�", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			double dnum = 0.0;
			double dpeigu = 0.0;
			double dsonggu = 0.0;
			try {
				String num = stock_num_field.getText();
				dnum = Double.valueOf(num);
				String peigu = stock_peigu_field.getText();
				dpeigu = Double.valueOf(peigu);
				String songgu = stock_songgu_field.getText();
				dsonggu = Double.valueOf(songgu);
			}catch(NumberFormatException e) {
				JOptionPane.showMessageDialog(null, "������ֺ���Ϣ��", "��ʾ", JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			StockDividend dividend = new StockDividend();
			dividend.code = m_stock.code;
			dividend.date = dividend_date;
			dividend.num = dnum;
			dividend.peigu = dpeigu;
			dividend.songgu = dsonggu;
			
			if(m_dividend == null) {
				db.addStockDividend(dividend);
			}else {
				dividend.id = m_dividend.id;
				db.modStockDividend(dividend);
			}
			
			
			//��ӳɹ������÷���ֵ���б���Ҫˢ��
			state = 1;
			dispose();
		}catch(MyException e) {
			JOptionPane.showMessageDialog(null, "���ʧ�ܣ�", "����", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	
	
	
	private void initUI(Stock stock) {
        JLabel stock_code_label = new JLabel("��Ʊ����");
        stock_code_field = new JTextField(text_field_column);
        stock_code_field.setText(stock.code);
        stock_code_field.setEnabled(false);
        stock_code_label.setBounds(20, 20, 60, 25);
        stock_code_field.setBounds(90, 20, 100, 25);
        
        JLabel stock_name_label = new JLabel("��Ʊ����");
        stock_name_field = new JTextField(text_field_column);
        stock_name_field.setText(stock.name);
        stock_name_field.setEnabled(false);
        stock_name_label.setBounds(20, 50, 60, 25);
        stock_name_field.setBounds(90, 50, 100, 25);
        
        DateChooser dateChooser1 = null;
        if(m_dividend != null) {
        	//���÷ֺ�ʱ��
        	dateChooser1 = DateChooser.getInstance(m_dividend.date, "yyyy-MM-dd");
        }else {
        	dateChooser1 = DateChooser.getInstance("yyyy-MM-dd");
        }
        JLabel stock_date_label = new JLabel("�ֺ�����");
        stock_date_field  = new JTextField("����ѡ������");
        if(m_dividend != null) {
        	//���÷ֺ�ʱ��
        	String dividend_date = DateUtil.getDateString(m_dividend.date);
        	stock_date_field.setText(dividend_date);
        }
        stock_date_label.setBounds(20, 80, 60, 25);
        stock_date_field.setBounds(90, 80, 100, 25);
        dateChooser1.register(stock_date_field);
        
        //dateChooser1.setBounds(90, 80, 100, 25);
//        JButton select = new JButton("ѡ��");
//        select.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//            	addStockDividend();
//            	//����ʱ�䴰�ڣ�ѡ��ֺ�ʱ��
//            	
//            }
//        });
//        //select.setBounds(80, 210, 60, 25);
//        dateChooser1.register(select);
//        dateChooser1.setBounds(200, 80, 25, 25);
//        
        
        JLabel stock_num_label = new JLabel("�ֺ�");
        stock_num_field = new JTextField(text_field_column);
        if(m_dividend != null) {
        	stock_num_field.setText(Double.toString(m_dividend.num));
        }
        stock_num_label.setBounds(20, 110, 60, 25);
        stock_num_field.setBounds(90, 110, 100, 25);
        
        JLabel stock_peigu_label = new JLabel("�����");
        stock_peigu_field = new JTextField(text_field_column);
        if(m_dividend != null) {
        	stock_peigu_field.setText(Double.toString(m_dividend.peigu));
        }else {
        	stock_peigu_field.setText("0");		//Ĭ��Ϊ0
        }
        stock_peigu_label.setBounds(20, 140, 60, 25);
        stock_peigu_field.setBounds(90, 140, 100, 25);
        
        
        JLabel stock_songgu_label = new JLabel("�͹���");
        stock_songgu_field = new JTextField(text_field_column);
        if(m_dividend != null) {
        	stock_songgu_field.setText(Double.toString(m_dividend.songgu));
        }else {
        	stock_songgu_field.setText("0");		//Ĭ��Ϊ0
        }
        stock_songgu_label.setBounds(20, 170, 60, 25);
        stock_songgu_field.setBounds(90, 170, 100, 25);
        
        JLabel tips_label = new JLabel("��ʾ������ֵ��Ϊ1�ɵķֺ�����.");
        tips_label.setForeground(Color.RED);
        tips_label.setBounds(20, 200, 200, 25);
        
        JButton more = new JButton("����");
		more.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	addStockDividend();
            }
        });
		more.setBounds(80, 235, 60, 25);
		
		JPanel main_panel = new JPanel(null); 
		main_panel.add(stock_code_label);
		main_panel.add(stock_code_field);
		main_panel.add(stock_name_label);
		main_panel.add(stock_name_field);
		main_panel.add(stock_date_label);
		//main_panel.add(dateChooser1);
		main_panel.add(stock_date_field);
		main_panel.add(stock_num_label);
		main_panel.add(stock_num_field);
		main_panel.add(stock_peigu_label);
		main_panel.add(stock_peigu_field);
		main_panel.add(stock_songgu_label);
		main_panel.add(stock_songgu_field);
		main_panel.add(tips_label);
		main_panel.add(more);
		
		setContentPane(main_panel);
		
		
        
		this.setVisible(true);
	}

}
