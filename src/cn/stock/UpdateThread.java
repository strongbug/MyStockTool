package cn.stock;

import java.util.Calendar;
import java.util.Date;

public class UpdateThread implements Runnable {

	private Object lock = null;
	
	public UpdateThread(Object lock) {
		this.lock = lock;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		MyLog.Trace("UpdateThread:run:start.");
		while(true) {
			try {
				Thread.sleep(Config.update_time * 60 * 1000);
				//检查当前时间是否在交易时间内
				//交易时间段：9：30--11：30，1：00--3：00
				//排除周末
				Date time = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(time);
				if(Calendar.SATURDAY == c.get(Calendar.DAY_OF_WEEK) || 
						Calendar.SUNDAY == c.get(Calendar.DAY_OF_WEEK) ) {
					continue;
				}
				int hour = c.get(Calendar.HOUR_OF_DAY);
				int minute = c.get(Calendar.MINUTE);
				if( (hour >= 9 && hour <= 11)) {
					if((hour == 9 && minute < 30) || (hour == 11 && minute > 30)) {
						continue;
					}
				}else if(hour < 13 || hour > 15) {
					continue;
				}
					
				MyLog.Trace("UpdateThread:run:start update.");
				StockCache.update(StockCache.UPDATE_TYPE_HQ);
				
				//提示更新
				synchronized (lock) {
					MyLog.Trace("UpdateThread:run:notify lock.");
					lock.notify();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				MyLog.Error("UpdateThread:run:Exception " + e.toString());
			}
			
		}
	}

}
