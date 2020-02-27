package cn.stock;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class StockCache {
	// 全局数据缓存类
	static private List<Stock> stock_list = null;
	static private Db db = new Sqlite();
	static private Hq hq = new Sina();

	public static void init() {
		// 初始化，加载股票列表、提醒设置、分红信息等
		if (db == null) {
			db = new Sqlite();
		}
		if (hq == null) {
			hq = new Sina();
		}

		try {
			db.init();

			updateStock();
			updateDividend();
			updateReminder();
			updateHq();
			updateDividendRate();
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockCache:init:Exception " + e.toString());
		}

		for (Stock stock : stock_list) {
			MyLog.Trace("StockCache:init:stock " + stock.toString());
		}
	}

	public static void updateStock() {
		try {
			stock_list = db.getStocks();
			MyLog.Trace("StockCache:updateStock:stock num " + stock_list.size());
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockCache:updateStock:Exception " + e.toString());
		}
	}

	public static void updateHq() {
		try {
			hq.update(stock_list);
		} catch (MyException e) {
			// TODO Auto-generated catch block
			MyLog.Error("StockCache:updateHq:Exception " + e.toString());
		}
	}
	
	public static void updateReminder(Stock stock) {
		// 获取股票股息，计算股息率
		try {
			for (Stock cache_stock : stock_list) {
				if(cache_stock.code.equals(stock.code)) {
					List<Reminder> reminders = db.getStockReminders(stock);
					cache_stock.reminders.clear();
					// 去除值为0的
					for (Reminder reminder : reminders) {
						if (reminder.value > 0.0) {
							//
							cache_stock.reminders.add(reminder);
						}
					}
				}
			}
		} catch (MyException e) {
			MyLog.Error("StockCache:updateReminder:Exception " + e.toString());
		}

	}

	public static void updateReminder() {
		// 获取股票股息，计算股息率
		for (Stock stock : stock_list) {
			updateReminder(stock);
		}
	}
	
	public static void updateDividendRate(Stock stock) {
			if (stock.price > 0.0 && stock.dividend > 0.0) {
				// 计算股息率
				double dividend_rate = stock.dividend / stock.price * 100;
				// 取3位小数精度
				BigDecimal bg = new BigDecimal(dividend_rate);
				stock.dividend_rate = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
				// stock.dividend_rate_str = String.format("%.3f", stock.dividend_rate);
				
				//判断是否当前的数据。
				
				DividendRate rate = new DividendRate();
				rate.date = new Date();
				rate.code = stock.code;
				rate.price = stock.price;
				rate.rate = stock.dividend_rate;
				
				if(!DateUtil.checkDay()) {
					MyLog.Trace("StockCache:updateDividendRate:stock rate don't update.");
					return;
				}
				if(db == null) {
					db = new Sqlite();
				}
				try {
					db.modDividendHistory(rate);
				} catch (MyException e) {
					// TODO Auto-generated catch block
					MyLog.Error("StockCache:updateDividendRate:Exception " + e.toString());
				}
			}
	}

	public static void updateDividendRate() {
		for (Stock stock : stock_list) {
			updateDividendRate(stock);
		}
	}

	public static void updateDividend(Stock stock) {
		// 获取股票股息，计算股息率
		try {
			Calendar c = Calendar.getInstance();
			int curr_year = c.get(Calendar.YEAR);
			MyLog.Trace("StockCache:updateDividend:curr year is " + curr_year);
			List<StockDividend> dividends = db.getStockDividends(stock);
			// 找到去年或今年的分红信息
			int dividend_year = 0;
			double total_dividend_num = 0.0;
			for (StockDividend dividend : dividends) {
				c.setTime(dividend.date);
				if (dividend_year == 0) {
					dividend_year = c.get(Calendar.YEAR);
					total_dividend_num = dividend.num;
				} else {
					// 比较年份
					if (c.get(Calendar.YEAR) == dividend_year) {
						total_dividend_num += dividend.num;
					} else {
						break;
					}
				}
			}
			if (dividend_year == curr_year || (dividend_year + 1) == curr_year) {
				MyLog.Trace(stock.name + " " + dividend_year + " year total dividend is " + total_dividend_num);
			} else if (dividend_year > 0) {
				MyLog.Trace(dividend_year + " year total dividend was " + total_dividend_num);
			}
			stock.dividend = total_dividend_num;
		} catch (MyException e) {
			MyLog.Error("StockCache:updateDividend:Exception " + e.toString());
		}

	}

	public static void updateDividend() {
		// 获取股票股息，计算股息率
		for (Stock stock : stock_list) {
			updateDividend(stock);
		}
	}

	public final static int UPDATE_TYPE_STOCK = 1;
	public final static int UPDATE_TYPE_DIVIDEND = 2;
	public final static int UPDATE_TYPE_REMINDER = 3;
	public final static int UPDATE_TYPE_HQ = 4;

	public static void update(int type, Stock stock) {
		switch (type) {
		case UPDATE_TYPE_DIVIDEND:
			// 录入分红后，更新分红信息
			updateDividend(stock);
			updateDividendRate(stock);
			break;
		case UPDATE_TYPE_REMINDER:
			// 设置股票提醒后，更新股票提醒
			updateReminder(stock);
			break;
		default:
			MyLog.Error("StockCache:update:unsupported update type " + type);
			break;
		}
	}
	
	public static void update(int type) {
		switch (type) {
		case UPDATE_TYPE_STOCK:
			// 添加股票后，更新股票列表
			updateStock();
			updateDividend();
			updateReminder();
			updateHq();
			updateDividendRate();
			break;
		case UPDATE_TYPE_DIVIDEND:
			// 录入分红后，更新分红信息
			updateDividend();
			break;
		case UPDATE_TYPE_REMINDER:
			// 设置股票提醒后，更新股票提醒
			updateReminder();
			break;
		case UPDATE_TYPE_HQ:
			// 更新行情时只需更新股票价格和股票分红率
			updateHq();
			updateDividendRate();
			break;
		default:
			MyLog.Error("StockCache:update:unsupported update type " + type);
			break;
		}
	}

	public static List<Stock> getStockList() {
		return stock_list;
	}
}
