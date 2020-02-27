package cn.stock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stock implements Comparable<Stock>{
	public static final int STOCK_TYPE_SH = 1;	//上海股票
	public static final int STOCK_TYPE_SZ = 2;	//深圳股票
	
	public static int getStockType(String code) {
		int type = 0;
		//沪市A股：600、601、602开头
		//沪市B股：900开头
		//深市A股：000开头
		//深市B股：200开头
		//中小板：002开头
		//创业板：300开头
		if(code.startsWith("60") || code.startsWith("900")) {
			type = STOCK_TYPE_SH;
		}else if(code.startsWith("300") || code.startsWith("00") || 
				code.startsWith("200")) {
			type = STOCK_TYPE_SZ;
		}
		
		return type;
	}
	
	
	public String code;	//股票代码
	public String name;	//股票名称
	public int type;		//类型
	public int index;		//排序
	public double price = 0.0; //行情
	public Date update_time = null;
	public double dividend = 0.0;	//最近一年的分红
	public double dividend_rate = 0.0; //当前股息率
	//public String dividend_rate_str = null;
	public List<Reminder> reminders = new ArrayList<Reminder>();
	
	
	public String toString() {
		String reminderinfo = "";
		for(Reminder reminder : reminders) {
			if(reminderinfo.length() > 0) {
				reminderinfo += ",";
			}
			reminderinfo += "(" + reminder.type + "-" + reminder.value + ")";
		}
		return "Stock code[" + code + "], name[" + 
					name + "], type[" + 
					type + "], index[" + 
					index + "], price[" + 
					price + "], reminder[" + 
					reminderinfo + "].";
	}

	@Override
	public int compareTo(Stock o) {
		// TODO Auto-generated method stub
		return this.index - o.index;
		//return o.index - this.index;
	}
}
