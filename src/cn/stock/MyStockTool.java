package cn.stock;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;

public class MyStockTool {
	private static MainDialog main = null;

	// ����ȫ�ֵĸ���������������º���ʾ���������
	public static Object update_lock = null;

	public static void OpenMain() {
		if (main == null) {
			MyLog.Trace("MyStockTool:OpenMain:open MainDialog.");
			main = new MainDialog(null, update_lock);
		}
		main.setVisible(true);
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// ��������
		Config.getConfig();
		MyLog.Init(Config.log_level);
		
		MyLog.Trace("MyStockTool:main:start.");

		StockCache.init();

		// ��������ͼƬ
		//System.out.println(MyStockTool.class.getClassLoader().getResource(""));
		// ImageIcon img = new
		// ImageIcon(MyStockTool.class.getClassLoader().getResource("logo.gif"));
		// ImageIcon img = new
		// ImageIcon(MyStockTool.class.getClassLoader().getResource("divi.jpg"));
//		MyStockTool tool = new MyStockTool();
//		URL icon_url = tool.getClass().getResource(Global.icon);
//		ImageIcon img = new ImageIcon(icon_url);
		MyLog.Trace("MyStockTool:main:" + Global.icon);
		ImageIcon img = new ImageIcon(Global.icon);

		MyLog.Trace("MyStockTool:main:Add tray icon.");
		// �������̵����˵�
		PopupMenu pm = new PopupMenu();
		// ���嵯���˵���
		MenuItem openmenu = new MenuItem("��");
		openmenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				OpenMain();
			}
		});

		MenuItem closemenu = new MenuItem("�˳�");
		closemenu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
		// ��ӵ����˵�������˵�
		pm.add(openmenu);
		pm.add(closemenu);

		SystemTray systemtray = SystemTray.getSystemTray();
		TrayIcon trayicon = new TrayIcon(img.getImage(), "��Ϣ����", pm);
		trayicon.setImageAutoSize(true);
		trayicon.addMouseListener(new MouseAdapter() {

			public void mouseClicked(MouseEvent e) {

				// ���������,���ô���״̬��������ʾ
				if (e.getButton() == MouseEvent.BUTTON1) {
					int clickCount = e.getClickCount();
					if (clickCount == 2) {
						OpenMain();
					}
				}
			}
		});
		try {
			systemtray.add(trayicon);
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			MyLog.Error("MyStockTool:main:Exception " + e.toString());
		}

		update_lock = new Object();

		if (!Config.min_on_start) {
			OpenMain();
		}

//		// ��������߳�
//		Runnable reminder_run = new ReminderThread(60);
//		Thread reminder_thread = new Thread(reminder_run);
//		reminder_thread.start();
		// �������������߳�
		MyLog.Trace("MyStockTool:main:start UpdateThread.");
		Runnable update_run = new UpdateThread(update_lock);
		Thread update_thread = new Thread(update_run);
		update_thread.start();
	}

}
