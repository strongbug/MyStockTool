package cn.stock;

public class Reminder implements Comparable<Reminder>{
	final public static int REMINDER_TYPE_DIVIDEND_MAX = 1;
	final public static int REMINDER_TYPE_DIVIDEND_MIN = 2;
	final public static int REMINDER_TYPE_PRICE_MAX = 3;
	final public static int REMINDER_TYPE_PRICE_MIN = 4;
	
	public String code;
	public int type;
	public double value;
	
	@Override
	public int compareTo(Reminder o) {
		// TODO Auto-generated method stub
		return this.type - o.type;
	}
}
