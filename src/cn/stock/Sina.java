package cn.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Sina implements Hq {
	
	//public static Date last_update_date;
	public static String last_update_date;
	
	public String getStockId(Stock stock) {
		if(stock.type == Stock.STOCK_TYPE_SH) {
			return "sh" + stock.code;
		}else if(stock.type == Stock.STOCK_TYPE_SZ) {
			return "sz" + stock.code;
		}else {
			MyLog.Error("Sina:getStockId:unsuppoted stock type! " + stock.code);
			return "unknown";
		}
	}
	
//	public static void updateLastUpdateDate(String update_date) {
//		//format 2019-12-20
//		last_update_date = DateUtil.getDate(update_date);
//	}
	
	//股票行情缓存信息，避免频繁刷新。有效期暂时设置为5分钟。
	private int valid_time = 5*60;	//5分钟
	private Map<String, Stock> stock_cache_map = new HashMap<String, Stock>();

	@Override
	public void update(Stock stock) throws MyException {
		// TODO Auto-generated method stub
		//check cache
		Date curr_time = new Date();
		if(stock_cache_map.containsKey(stock.code)) {
			Stock cache_stock = stock_cache_map.get(stock.code);
			if(cache_stock.update_time != null) {
				if(curr_time.getTime() - cache_stock.update_time.getTime() > valid_time*1000) {
					MyLog.Trace("Sina:update:update " + stock.code);
				}else {
					stock.name = cache_stock.name;
					stock.price = cache_stock.price;
					return;
				}
			}
		}
		String url_str = "http://hq.sinajs.cn/list=" + getStockId(stock);
		
		String hq_str = update(url_str);
		//var hq_str_sh601939="建设银行,7.230,7.210,7.210,7.270,7.190,7.210,7.230,60203458,435232505.000,767468,7.210,3208900,7.200,558700,7.190,1911300,7.180,1100900,7.170,135201,7.230,37000,7.240,33500,7.250,589001,7.260,3761964,7.270,2019-11-26,15:00:00,00,"
		//字段含义：股票名称，今日开盘价，昨日收盘价，当前价格，今日最高价，今日最低价，买一价格，卖一价格，成交的股票数，成交金额，买一数，买一报价，买二股数，买二报价，买三股数，买三报价，买四股数，买四报价，买五股数，买五报价，卖一股数，卖一报价，卖二股数，卖二报价，卖三股数，卖三报价，卖四股数，卖四报价，卖五股数，卖五报价，日期，时间
		String hq_id_str = hq_str.substring(hq_str.indexOf("_", 8) + 1, hq_str.indexOf("="));
		//String stock_type_str = hq_id_str.substring(0, 2);
		String stock_code_str = hq_id_str.substring(2);
		//System.out.println(stock_type_str + " - " + stock_code_str);
		String hq_info_str = hq_str.substring(hq_str.indexOf("\"") + 1, hq_str.lastIndexOf("\""));
		String[] hq_infos = hq_info_str.split(",");
//		System.out.println(hq_infos[0] + ", " + 
//					hq_infos[3] + ", " + hq_infos[30] + ", " + 
//				hq_infos[31]);
		//更新最近更新时间
		//System.out.println("Sina.update.time " + hq_infos[hq_infos.length - 3]);
		last_update_date = hq_infos[hq_infos.length - 3];
		if(stock.code.equals(stock_code_str)) {
			stock.name = hq_infos[0];
			stock.price = Double.valueOf(hq_infos[3]);
			//save cache
			stock.update_time = curr_time;
			stock_cache_map.put(stock.code, stock);
			
			
		}else {
			throw new MyException("获取股票信息失败！");
		}
	}

	@Override
	public void update(List<Stock> stocks) throws MyException {
		// TODO Auto-generated method stub
		//check cache
		List<Stock> update_stocks = new ArrayList<Stock>();
		Date curr_time = new Date();
		for(Stock stock : stocks) {
			if(stock_cache_map.containsKey(stock.code)) {
				Stock cache_stock = stock_cache_map.get(stock.code);
				if(cache_stock.update_time != null) {
					if(curr_time.getTime() - cache_stock.update_time.getTime() > valid_time*1000) {
						MyLog.Trace("Sina:update:update " + stock.code);
						update_stocks.add(stock);
					}else {
						stock.name = cache_stock.name;
						stock.price = cache_stock.price;
					}
				}else {
					update_stocks.add(stock);
				}
			}else {
				update_stocks.add(stock);
			}
		}
		if(update_stocks.size() <= 0) {
			MyLog.Trace("Sina:update:Don't need update. ");
			return;
		}
		
				
		String stocks_id = "";
		for(Stock stock : update_stocks) {
			if(! stocks_id.isEmpty()) {
				stocks_id += ",";
			}
			stocks_id += getStockId(stock);
		}
		String url_str = "http://hq.sinajs.cn/list=" + stocks_id;
		
		String hqs_str = update(url_str);
		//System.out.println("hqs_str : " + hqs_str);
		String[] hq_str_array = hqs_str.split(";");
		for(String hq_str : hq_str_array) {
			//System.out.println("hq_str : " + hq_str);
			//var hq_str_sh601939="建设银行,7.230,7.210,7.210,7.270,7.190,7.210,7.230,60203458,435232505.000,767468,7.210,3208900,7.200,558700,7.190,1911300,7.180,1100900,7.170,135201,7.230,37000,7.240,33500,7.250,589001,7.260,3761964,7.270,2019-11-26,15:00:00,00,"
			//字段含义：股票名称，今日开盘价，昨日收盘价，当前价格，今日最高价，今日最低价，买一价格，卖一价格，成交的股票数，成交金额，买一数，买一报价，买二股数，买二报价，买三股数，买三报价，买四股数，买四报价，买五股数，买五报价，卖一股数，卖一报价，卖二股数，卖二报价，卖三股数，卖三报价，卖四股数，卖四报价，卖五股数，卖五报价，日期，时间
			String hq_id_str = hq_str.substring(hq_str.indexOf("_", 8) + 1, hq_str.indexOf("="));
			//String stock_type_str = hq_id_str.substring(0, 2);
			String stock_code_str = hq_id_str.substring(2);
			//System.out.println(stock_type_str + " - " + stock_code_str);
			String hq_info_str = hq_str.substring(hq_str.indexOf("\"") + 1, hq_str.lastIndexOf("\""));
			String[] hq_infos = hq_info_str.split(",");
//			System.out.println(hq_infos[0] + ", " + 
//						hq_infos[3] + ", " + hq_infos[30] + ", " + 
//					hq_infos[31]);
			//更新最近更新时间
			//System.out.println("Sina.update.time " + hq_infos[hq_infos.length - 3]);
			last_update_date = hq_infos[hq_infos.length - 3];
			for(Stock stock : stocks) {
				if(stock.code.equals(stock_code_str)) {
					stock.name = hq_infos[0];
					stock.price = Double.valueOf(hq_infos[3]);
					stock.update_time = curr_time;
					stock_cache_map.put(stock.code, stock);
					break;
				}
			}
//			for(String hq_info : hq_infos) {
//				System.out.println(hq_info);
//			}
		}
	}
	
	private String update(String stock_url) {
		String info = null;
		try {
			MyLog.Trace("Sina:update:stock_url " + stock_url);
			
			URL url = new URL(stock_url);
			
			InputStream is = url.openStream();
            //InputStreamReader isr = new InputStreamReader(is,"utf-8");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            //String data = br.readLine();//读取数据
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null){//循环读取数据
                //System.out.println(data);//输出数据
                sb.append(line);
            }
            br.close();
            isr.close();
            is.close();
            
            info = sb.toString();
            //System.out.println(info);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			MyLog.Error("Sina:update:Exception " + e.toString());
		}
		
		return info;
	}

}
