package cn.stock;

import java.util.List;

public class ReminderThread implements Runnable {

	public final int default_check_time = 120;
	
	private int check_time = 120; // ��λ��

	public ReminderThread(int time) {
		if(time <= 0) {
			check_time = default_check_time;
		}
		else {
			check_time = time;
		}
	}
	
	public void checkStockReminder() {
		// ��Ʊ��Ϣ���º󣬼���Ƿ���Ҫ����
		List<Stock> stock_list = StockCache.getStockList();
		
		String info = "";
		for (Stock stock : stock_list) {
			for (Reminder reminder : stock.reminders) {
				switch (reminder.type) {
				case Reminder.REMINDER_TYPE_DIVIDEND_MAX:
					if(stock.dividend_rate > reminder.value) {
						MyLog.Trace(stock.name + " catch Dividend MAX.");
						info += stock.name + "��ǰ��Ϣ��[" + stock.dividend_rate + 
								"]��������ֵ[" + reminder.value + "]\n";
					}
					break;
				case Reminder.REMINDER_TYPE_DIVIDEND_MIN:
					if(stock.dividend_rate < reminder.value) {
						MyLog.Trace(stock.name + " catch Dividend MIN.");
						info += stock.name + "��ǰ��Ϣ��[" + stock.dividend_rate + 
								"]С������ֵ[" + reminder.value + "]\n";
					}
					break;
				case Reminder.REMINDER_TYPE_PRICE_MAX:
					if(stock.price > reminder.value) {
						MyLog.Trace(stock.name + " catch Price MAX.");
						info += stock.name + "��ǰ�۸�[" + stock.price + 
								"]��������ֵ[" + reminder.value + "]\n";
					}
					break;
				case Reminder.REMINDER_TYPE_PRICE_MIN:
					if(stock.price < reminder.value) {
						MyLog.Trace(stock.name + " catch Price MIN.");
						info += stock.name + "��ǰ�۸�[" + stock.price + 
								"]С������ֵ[" + reminder.value + "]\n";
					}
					break;
				default:
					MyLog.Error(stock.name + " unsupported reminder type " + reminder.type);
					break;
				}
			}
		}
		MyLog.Trace(info);
		
		if(info.length() > 0) {
			//�������Ѵ���
			InfoUtil tool = new InfoUtil(); 
			tool.show("����", info);
		}
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		MyLog.Info("ReminderThread start.");
		while(true) {
			try {
				Thread.sleep(check_time * 1000);
				
				MyLog.Trace("ReminderThread check.");
				checkStockReminder();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				MyLog.Error("ReminderThread:run:Exception " + e.toString());
			}
			
		}
	}

}
