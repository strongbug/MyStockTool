package cn.stock;

import java.util.List;

public interface Hq {
	public void update(Stock stock) throws MyException;
	
	public void update(List<Stock> stocks) throws MyException;
}
