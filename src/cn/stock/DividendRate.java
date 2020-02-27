package cn.stock;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DividendRate implements Comparable<DividendRate>{
	public String code;
	public Date date;
	public double price;
	public double rate;
	
	final static String DATE_FORMAT	= "yyyy-MM-dd";
	
	public String getDateString(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		return formatter.format(date);
	}
	
	public String toString() {
		return "Stock code[" + code + "], date[" + 
					getDateString(date) + "], price[" + 
					price + "], rate[" + 
					rate + "].";
	}
	
	@Override
	public int compareTo(DividendRate arg0) {
		// TODO Auto-generated method stub
		//return this.date.compareTo(arg0.date);
		return arg0.date.compareTo(this.date);
	}
}
