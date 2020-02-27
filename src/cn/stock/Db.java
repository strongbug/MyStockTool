package cn.stock;

import java.util.Date;
import java.util.List;

public interface Db {
	
	
	//初始化数据库
	public void init() throws MyException;
	
	//股票操作
	public void addStock(Stock stock) throws MyException;
	public void delStock(Stock stock) throws MyException;
	public List<Stock> getStocks() throws MyException;
	
	//股票分红操作
	public void addStockDividend(StockDividend dividend) throws MyException;
	public void modStockDividend(StockDividend dividend) throws MyException;
	public void delStockDividend(StockDividend dividend) throws MyException;
	public List<StockDividend> getStockDividends(Stock stock) throws MyException;
	
	//股息操作
	public void addDividendHistory(DividendRate divident) throws MyException;
	public void modDividendHistory(DividendRate divident) throws MyException;
	public void delDividendHistory(DividendRate divident) throws MyException;
	public List<DividendRate> getStockDividendRates(Stock stock, Date begin, Date end) throws MyException;
	
	//提醒操作
	public void addReminder(Reminder reminder) throws MyException;
	public void modReminder(Reminder reminder) throws MyException;
	public void delReminder(Reminder reminder) throws MyException;
	public List<Reminder> getStockReminders(Stock stock) throws MyException;
	
}
