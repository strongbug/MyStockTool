package cn.stock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Sqlite implements Db {
	//表字段名称
	final static String DB_STOCK_TABLE = "t_stock";
	final static String DB_STOCK_COL_CODE = "code";
	final static String DB_STOCK_COL_NAME = "name";
	final static String DB_STOCK_COL_TYPE = "type";
	final static String DB_STOCK_COL_INDEX = "sortnum";
	
	final static String DB_DIVIDEND_TABLE = "t_dividend";
	final static String DB_DIVIDEND_COL_ID = "id";
	final static String DB_DIVIDEND_COL_CODE = "code";
	final static String DB_DIVIDEND_COL_DATE = "date";
	final static String DB_DIVIDEND_COL_NUM = "num";
	final static String DB_DIVIDEND_COL_PEIGU = "peigu";
	final static String DB_DIVIDEND_COL_SONGGU = "songgu";
	
	final static String DB_DIVIDENDRATE_TABLE = "t_dividendrate";
	final static String DB_DIVIDENDRATE_COL_CODE = "code";
	final static String DB_DIVIDENDRATE_COL_DATE = "date";
	final static String DB_DIVIDENDRATE_COL_PRICE = "price";
	final static String DB_DIVIDENDRATE_COL_RATE = "rate";
	
	
	final static String DB_REMINDER_TABLE = "t_reminder";
	final static String DB_REMINDER_COL_CODE = "code";
	final static String DB_REMINDER_COL_TYPE = "type";
	final static String DB_REMINDER_COL_VALUE = "value";
	
	final static String DB_REMINDERHIS_TABLE = "t_reminderhis";
	final static String DB_REMINDERHIS_COL_CODE = "code";
	final static String DB_REMINDERHIS_COL_DATE = "date";
	final static String DB_REMINDERHIS_COL_TYPE = "type";
	final static String DB_REMINDERHIS_COL_PRICE = "price";
	final static String DB_REMINDERHIS_COL_RATE = "rate";

	@Override
	public void init() throws MyException {
		// TODO Auto-generated method stub
		try {
			Class.forName("org.sqlite.JDBC");
			
			Connection connection = null;
			
			// create a database connection
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			statement.setQueryTimeout(30);  // set timeout to 30 sec.
			
			String create_header = "CREATE TABLE IF NOT EXISTS ";
			String stock_sql = create_header + DB_STOCK_TABLE + "(" + 
					DB_STOCK_COL_CODE + " TEXT PRIMARY KEY NOT NULL, " + 
					DB_STOCK_COL_NAME + " TEXT NOT NULL, " + 
					DB_STOCK_COL_TYPE + " INTEGER NOT NULL, " + 
					DB_STOCK_COL_INDEX + " INTEGER NOT NULL);";
			String dividend_sql = create_header + DB_DIVIDEND_TABLE + "(" + 
					DB_DIVIDEND_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
					DB_DIVIDEND_COL_CODE + " TEXT NOT NULL, " + 
					DB_DIVIDEND_COL_DATE + " TEXT NOT NULL, " + 
					DB_DIVIDEND_COL_NUM + " REAL, " + 
					DB_DIVIDEND_COL_PEIGU + " REAL, " + 
					DB_DIVIDEND_COL_SONGGU + " REAL);";
			String dividend_index_sql = "CREATE UNIQUE INDEX IF NOT EXISTS dividend_i ON " + 
					DB_DIVIDEND_TABLE + "(" + DB_DIVIDEND_COL_CODE + ","+ 
					DB_DIVIDEND_COL_DATE + ")";
			String dividendrate_sql = create_header + DB_DIVIDENDRATE_TABLE + "(" + 
					DB_DIVIDENDRATE_COL_CODE + " TEXT NOT NULL, " + 
					DB_DIVIDENDRATE_COL_DATE + " TEXT NOT NULL, " + 
					DB_DIVIDENDRATE_COL_PRICE + " REAL NOT NULL, " + 
					DB_DIVIDENDRATE_COL_RATE + " REAL NOT NULL, PRIMARY KEY(" + 
					DB_DIVIDENDRATE_COL_CODE + ", " + DB_DIVIDENDRATE_COL_DATE + "));";
			String reminder_sql = create_header + DB_REMINDER_TABLE + "(" + 
					DB_REMINDER_COL_CODE + " TEXT NOT NULL, " + 
					DB_REMINDER_COL_TYPE + " INTEGER NOT NULL, " + 
					DB_REMINDER_COL_VALUE + " REAL NOT NULL, PRIMARY KEY(" + 
					DB_REMINDER_COL_CODE + ", " + DB_REMINDER_COL_TYPE + "));";
			String reminderhis_sql = create_header + DB_REMINDERHIS_TABLE + "(" + 
					DB_REMINDERHIS_COL_CODE + " TEXT NOT NULL, " + 
					DB_REMINDERHIS_COL_DATE + " TEXT NOT NULL, " + 
					DB_REMINDERHIS_COL_TYPE + " INTEGER NOT NULL, " + 
					DB_REMINDERHIS_COL_PRICE + " REAL NOT NULL, " + 
					DB_REMINDERHIS_COL_RATE + " REAL NOT NULL, PRIMARY KEY(" + 
					DB_REMINDERHIS_COL_CODE + ", " + DB_REMINDERHIS_COL_DATE + ", " + 
					DB_REMINDERHIS_COL_TYPE + "));";
			
			//System.out.println(stock_sql);
			statement.executeUpdate(stock_sql);
			statement.executeUpdate(dividend_sql);
			statement.executeUpdate(dividend_index_sql);
			statement.executeUpdate(dividendrate_sql);
			statement.executeUpdate(reminder_sql);
			statement.executeUpdate(reminderhis_sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("数据库初始化失败！" + e.getMessage());
		}
	}

	@Override
	public void addStock(Stock stock) throws MyException {
		// TODO Auto-generated method stub
		//check
		if(stock.code.isEmpty() || stock.name.isEmpty()) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "INSERT INTO " + DB_STOCK_TABLE + "(" + 
						DB_STOCK_COL_CODE + ", " + DB_STOCK_COL_NAME + ", " + 
						DB_STOCK_COL_TYPE + ", " + DB_STOCK_COL_INDEX + ") VALUES('" + 
						stock.code + "', '" + 
						stock.name + "', " + 
						stock.type + ", " + 
						stock.index + " );";
			//System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("添加股票信息失败！" + e.getMessage());
		}
		
	}

	@Override
	public void delStock(Stock stock) throws MyException {
		// TODO Auto-generated method stub
		if(stock.code.isEmpty() || stock.name.isEmpty()) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "DELETE FROM " + DB_STOCK_TABLE + " WHERE " + 
						DB_STOCK_COL_CODE + " ='" + stock.code + "';";
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("添加股票信息失败！" + e.getMessage());
		}
	}

	@Override
	public List<Stock> getStocks() throws MyException {
		// TODO Auto-generated method stub
		List<Stock> stocks = new ArrayList<Stock>();

		// create a database connection
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM " + DB_STOCK_TABLE + ";");
			while(rs.next())
			{
				Stock stock = new Stock();
				
				stock.code = rs.getString(DB_STOCK_COL_CODE);
				stock.name = rs.getString(DB_STOCK_COL_NAME);
				stock.type = rs.getInt(DB_STOCK_COL_TYPE);
				stock.index = rs.getInt(DB_STOCK_COL_INDEX);
				
				stocks.add(stock);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("查询股票信息失败！" + e.getMessage());
		}
		
		//增加排序
		Collections.sort(stocks);
		
		return stocks;
	}

	@Override
	public void addStockDividend(StockDividend dividend) throws MyException {
		// TODO Auto-generated method stub
		if(dividend.code.isEmpty() ) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "INSERT INTO " + DB_DIVIDEND_TABLE + "(" + 
						DB_DIVIDEND_COL_CODE + ", " + DB_DIVIDEND_COL_DATE + ", " + 
						DB_DIVIDEND_COL_NUM + ", " + DB_DIVIDEND_COL_PEIGU + ", " + 
						DB_DIVIDEND_COL_SONGGU + ") VALUES('" + 
						dividend.code + "', '" + 
						DateUtil.getDateString(dividend.date) + "', " + 
						dividend.num + ", " +
						dividend.peigu + "," +
						dividend.songgu + " );";
			//System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("添加股票分红信息失败！" + e.getMessage());
		}
	}

	@Override
	public void modStockDividend(StockDividend dividend) throws MyException {
		// TODO Auto-generated method stub
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "UPDATE " + DB_DIVIDEND_TABLE + 
					" SET " + DB_DIVIDEND_COL_DATE + " = '" + DateUtil.getDateString(dividend.date) + "', " + 
					DB_DIVIDEND_COL_NUM + " = " + dividend.num + ", " + 
					DB_DIVIDEND_COL_PEIGU + " = " + dividend.peigu + ", " + 
					DB_DIVIDEND_COL_SONGGU + " = " + dividend.songgu + 
					" WHERE " + DB_DIVIDEND_COL_ID + " = " + dividend.id + ";";
			MyLog.Trace("Sqlite:modStockDividend:sql " + sql);
			statement.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("修改股票分红信息失败！" + e.getMessage());
		}
	}

	@Override
	public void delStockDividend(StockDividend dividend) throws MyException {
		// TODO Auto-generated method stub
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "DELETE FROM " + DB_DIVIDEND_TABLE + 
					" WHERE " + DB_DIVIDEND_COL_ID + " = " + dividend.id + ";";
			statement.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("删除股票分红信息失败！" + e.getMessage());
		}
	}
	
	@Override
	public List<StockDividend> getStockDividends(Stock stock) throws MyException {
		// TODO Auto-generated method stub
		// TODO Auto-generated method stub
				List<StockDividend> dividends = new ArrayList<StockDividend>();

				// create a database connection
				try {
					Connection connection = null;
					
					connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
					Statement statement = connection.createStatement();
					
					ResultSet rs = statement.executeQuery("SELECT * FROM " + DB_DIVIDEND_TABLE + 
							" WHERE " + DB_DIVIDEND_COL_CODE + "='" + stock.code + "';");
					while(rs.next())
					{
						StockDividend dividend = new StockDividend();
						
						dividend.id = rs.getInt(DB_DIVIDEND_COL_ID);
						dividend.code = rs.getString(DB_DIVIDEND_COL_CODE);
						//SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
						String dividend_date = rs.getString(DB_DIVIDEND_COL_DATE);
						dividend.date = DateUtil.getDate(dividend_date);
						dividend.num = rs.getDouble(DB_DIVIDEND_COL_NUM);
						dividend.peigu = rs.getDouble(DB_DIVIDEND_COL_PEIGU);
						dividend.songgu = rs.getDouble(DB_DIVIDEND_COL_SONGGU);
						
						dividends.add(dividend);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new MyException("查询股票分红信息失败！" + e.getMessage());
				}
				
				//增加排序
				Collections.sort(dividends);
				
				return dividends;
	}

	@Override
	public void addDividendHistory(DividendRate dividend_rate) throws MyException {
		// TODO Auto-generated method stub
		modDividendHistory(dividend_rate);
	}

	@Override
	public void modDividendHistory(DividendRate dividend_rate) throws MyException {
		// TODO Auto-generated method stub
		if(dividend_rate.code.isEmpty() ) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "REPLACE INTO " + DB_DIVIDENDRATE_TABLE + "(" + 
						DB_DIVIDENDRATE_COL_CODE + ", " + DB_DIVIDENDRATE_COL_DATE + ", " + 
						DB_DIVIDENDRATE_COL_PRICE + ", " + DB_DIVIDENDRATE_COL_RATE + ") VALUES('" + 
						dividend_rate.code + "', '" + 
						DateUtil.getDateString(dividend_rate.date) + "', " +
						dividend_rate.price + "," + 
						dividend_rate.rate + " );";
			//System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("添加股息失败！" + e.getMessage());
		}
	}

	@Override
	public void delDividendHistory(DividendRate dividend_rate) throws MyException {
		// TODO Auto-generated method stub
		if(dividend_rate.code.isEmpty() ) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "DELETE FROM " + DB_DIVIDENDRATE_TABLE + " WHERE " + 
						DB_DIVIDENDRATE_COL_CODE + "='" + dividend_rate.code + "' AND " + 
						DB_DIVIDENDRATE_COL_DATE + "='" + DateUtil.getDateString(dividend_rate.date) + "';";
			//System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("删除股息记录失败！" + e.getMessage());
		}
	}
	
	//获取股息记录
	@Override
	public List<DividendRate> getStockDividendRates(Stock stock, Date begin, Date end) throws MyException{
		List<DividendRate> list = new ArrayList<DividendRate>();
		if(stock.code.isEmpty() ) {
			throw new MyException("股票信息不能为空！");
		}

		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String select_sql = "SELECT * FROM " + DB_DIVIDENDRATE_TABLE + 
					" WHERE " + DB_DIVIDENDRATE_COL_CODE + "='" + stock.code + "';";
			
			ResultSet rs = statement.executeQuery(select_sql);
			while(rs.next())
			{
				DividendRate dividend_rate = new DividendRate();
				
				dividend_rate.code = stock.code;
				String rate_date = rs.getString(DB_DIVIDENDRATE_COL_DATE);
				dividend_rate.date = DateUtil.getDate(rate_date);
				dividend_rate.price = rs.getDouble(DB_DIVIDENDRATE_COL_PRICE);
				dividend_rate.rate = rs.getDouble(DB_DIVIDENDRATE_COL_RATE);
				
				list.add(dividend_rate);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("查询股票分红信息失败！" + e.getMessage());
		}
		
		//增加排序
		Collections.sort(list);
		
		return list;
	}

	@Override
	public void addReminder(Reminder reminder) throws MyException {
		// TODO Auto-generated method stub
		if(reminder.code.isEmpty() ) {
			throw new MyException("股票信息不能为空！");
		}
		
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "INSERT INTO " + DB_REMINDER_TABLE + "(" + 
						DB_REMINDER_COL_CODE + ", " + DB_REMINDER_COL_TYPE + ", " + 
						DB_REMINDER_COL_VALUE + ") VALUES('" + 
						reminder.code + "', '" + 
						reminder.type + ", " +
						reminder.value + " );";
			//System.out.println(sql);
			statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("添加提醒失败！" + e.getMessage());
		}
	}

	@Override
	public void modReminder(Reminder reminder) throws MyException {
		// TODO Auto-generated method stub
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "REPLACE INTO " + DB_REMINDER_TABLE + 
					" ( " + DB_REMINDER_COL_CODE + ", " + DB_REMINDER_COL_TYPE + ", " + 
					DB_REMINDER_COL_VALUE + " ) values ( '" + 
					reminder.code + "', " + reminder.type + ", " + 
					reminder.value + " );";
			statement.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("修改提醒失败！" + e.getMessage());
		}
	}

	@Override
	public void delReminder(Reminder reminder) throws MyException {
		// TODO Auto-generated method stub
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			String sql = "DELETE FROM " + DB_REMINDER_TABLE + 
					" WHERE " + DB_REMINDER_COL_CODE + "='" + reminder.code + "' AND " + 
					DB_REMINDER_COL_TYPE + " =" + reminder.type + ";";
			statement.executeUpdate(sql);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("删除提醒失败！" + e.getMessage());
		}
	}
	
	@Override
	public List<Reminder> getStockReminders(Stock stock) throws MyException{
		List<Reminder> reminders = new ArrayList<Reminder>();
		
		// create a database connection
		try {
			Connection connection = null;
			
			connection = DriverManager.getConnection("jdbc:sqlite:stock.db");
			Statement statement = connection.createStatement();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM " + DB_REMINDER_TABLE + 
					" WHERE " + DB_REMINDER_COL_CODE + "='" + stock.code + "';");
			while(rs.next())
			{
				Reminder reminder = new Reminder();
				
				reminder.code = rs.getString(DB_REMINDER_COL_CODE);
				//SimpleDateFormat formatter = new SimpleDateFormat(DB_DATE_FORMAT);
				reminder.type = rs.getInt(DB_REMINDER_COL_TYPE);
				reminder.value = rs.getDouble(DB_REMINDER_COL_VALUE);
				
				reminders.add(reminder);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new MyException("查询股票分红信息失败！" + e.getMessage());
		}
		
		//增加排序
		Collections.sort(reminders);
		
		return reminders;
	}

	

	

}
