package cn.stock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Stock implements Comparable<Stock>{
	public static final int STOCK_TYPE_SH = 1;	//�Ϻ���Ʊ
	public static final int STOCK_TYPE_SZ = 2;	//���ڹ�Ʊ
	
	public static int getStockType(String code) {
		int type = 0;
		//����A�ɣ�600��601��602��ͷ
		//����B�ɣ�900��ͷ
		//����A�ɣ�000��ͷ
		//����B�ɣ�200��ͷ
		//��С�壺002��ͷ
		//��ҵ�壺300��ͷ
		if(code.startsWith("60") || code.startsWith("900")) {
			type = STOCK_TYPE_SH;
		}else if(code.startsWith("300") || code.startsWith("00") || 
				code.startsWith("200")) {
			type = STOCK_TYPE_SZ;
		}
		
		return type;
	}
	
	
	public String code;	//��Ʊ����
	public String name;	//��Ʊ����
	public int type;		//����
	public int index;		//����
	public double price = 0.0; //����
	public Date update_time = null;
	public double dividend = 0.0;	//���һ��ķֺ�
	public double dividend_rate = 0.0; //��ǰ��Ϣ��
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
