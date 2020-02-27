package cn.stock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class StockDividend implements Comparable<StockDividend>{
	public int id;
	public String code;
	public Date date;
	public double num;
	public double peigu;
	public double songgu;
	
	final static String DATE_FORMAT	= "yyyy-MM-dd";
	
	public String getDateString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return formatter.format(date);
	}
	
	public String toString() {
		return "Dividend id[" + id + "], code[" + code + "], date[" + 
				getDateString(date) + "], num[" + 
				num + "], peigu[" + 
				peigu + "], songgu[" + 
				songgu + "].";
	}
	
	@Override
	public int compareTo(StockDividend o) {
		// TODO Auto-generated method stub
		//return this.date.compareTo(o.date);	//’˝–Ú≈≈¡–
		return o.date.compareTo(this.date);	//µπ–Ú≈≈¡–
	}
}
