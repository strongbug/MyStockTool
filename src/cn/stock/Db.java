package cn.stock;

import java.util.Date;
import java.util.List;

public interface Db {
	
	
	//��ʼ�����ݿ�
	public void init() throws MyException;
	
	//��Ʊ����
	public void addStock(Stock stock) throws MyException;
	public void delStock(Stock stock) throws MyException;
	public List<Stock> getStocks() throws MyException;
	
	//��Ʊ�ֺ����
	public void addStockDividend(StockDividend dividend) throws MyException;
	public void modStockDividend(StockDividend dividend) throws MyException;
	public void delStockDividend(StockDividend dividend) throws MyException;
	public List<StockDividend> getStockDividends(Stock stock) throws MyException;
	
	//��Ϣ����
	public void addDividendHistory(DividendRate divident) throws MyException;
	public void modDividendHistory(DividendRate divident) throws MyException;
	public void delDividendHistory(DividendRate divident) throws MyException;
	public List<DividendRate> getStockDividendRates(Stock stock, Date begin, Date end) throws MyException;
	
	//���Ѳ���
	public void addReminder(Reminder reminder) throws MyException;
	public void modReminder(Reminder reminder) throws MyException;
	public void delReminder(Reminder reminder) throws MyException;
	public List<Reminder> getStockReminders(Stock stock) throws MyException;
	
}
