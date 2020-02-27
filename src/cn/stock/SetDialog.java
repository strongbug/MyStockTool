package cn.stock;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

public class SetDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SetDialog(JFrame parent) {
        super(parent);
        this.setResizable(false);
        this.setSize(220, 230);
        this.setTitle("设置");
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLocationRelativeTo(parent);
        //设置父窗口不可激活
        this.setModal(true);
        initUI();
    }
	
	private JCheckBox check_min = null;
	
	private JTextField update_time_field = null;
	
	private JComboBox<String> log_level_box = null;
	
	private int text_field_column = 8;
	
	private void saveConfig() {
		MyLog.Trace("SetDialog:saveConfig:start.");

		Config.min_on_start = check_min.isSelected();
		
		String update_time = update_time_field.getText();
		if(! update_time.isEmpty()) {
			//判定是否是数字
			int time = Integer.valueOf(update_time);
			Config.update_time = time;
		}
		
		Config.log_level = log_level_box.getSelectedItem().toString();
		
		Config.saveConfig();
		
		this.dispose();
	}
	
	private void initUI() {
		check_min = new JCheckBox("启动时最小化");
		check_min.setSelected(Config.min_on_start);
		check_min.setBounds(20, 30, 160, 25);
		
        JLabel update_time_label = new JLabel("更新时间(分钟)");
        update_time_field = new JTextField(text_field_column);
        update_time_field.setText(String.valueOf(Config.update_time));
        update_time_label.setBounds(20, 65, 100, 25);
        update_time_field.setBounds(125, 65, 60, 25);
        
        JLabel log_level_label = new JLabel("日志级别");
        log_level_box = new JComboBox<String>();
        List<String> levels = MyLog.getLevels();
        for(String level : levels) {
        	log_level_box.addItem(level);
        }
        //设置日志级别
        log_level_box.setSelectedItem(Config.log_level);
        log_level_label.setBounds(20, 100, 60, 25);
        log_level_box.setBounds(90, 100, 80, 25);
        

        JButton btn_save = new JButton("保存");
        btn_save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	saveConfig();
            }
        });
        btn_save.setBounds(80, 150, 60, 25);
		
		JPanel main_panel = new JPanel(null); 
		main_panel.add(check_min);
		main_panel.add(update_time_label);
		main_panel.add(update_time_field);
		main_panel.add(log_level_label);
		main_panel.add(log_level_box);
		main_panel.add(btn_save);

		setContentPane(main_panel);

		this.setVisible(true);
	}

}
