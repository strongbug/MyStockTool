package cn.stock;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	private static String DATE_FORMAT = ("yyyy-MM-dd");
	
	private static SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
	
	public static String getDateString(Date date) {
		return formatter.format(date);
	}
	
	public static Date getDate(String str) {
		Date date = null;
		try {
			date = formatter.parse(str);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			MyLog.Error("DateUtil:getDate:str " + str);
			MyLog.Error("DateUtil:getDate:Exception " + e.toString());
		}
		
		return date;
	}
	
	
	public static boolean checkDay() {
		Date date = new Date();
		
		String date1 = getDateString(date);
		
		if(date1.equalsIgnoreCase(Sina.last_update_date)) {
			return true;
		}else {
			return false;
		}
	}
}
