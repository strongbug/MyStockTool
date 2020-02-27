package cn.stock;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
 
public class HolidayUtil {
 
	/**
	 * @param urlAll
	 *            :����ӿ�
	 * @param httpArg
	 *            :����
	 * @return ���ؽ��
	 */
	public static String request(String httpArg) {      
	    String httpUrl = "http://tool.bitefu.net/jiari/";
	    BufferedReader reader = null;
	    String result = null;
	    StringBuffer sbf = new StringBuffer();
	    httpUrl = httpUrl + "?d=" +httpArg;
	    try {
	        URL url = new URL(httpUrl);
	        HttpURLConnection connection = (HttpURLConnection) url
	                .openConnection();
	        connection.setRequestMethod("GET");
	        connection.connect();
	        InputStream is = connection.getInputStream();
	        reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
	        String strRead = null;
	        while ((strRead = reader.readLine()) != null) {
	            sbf.append(strRead);
	        }
	        reader.close();
	        result = sbf.toString();
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return result;
	}
	
//	public static void main(String[] args) {
//		// ����ڼ��� 
//		String httpArg = "20190720"; 
//		/*SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd"); 
//		httpArg = f.format(new Date());*/
//		String jsonResult = HolidayUtil.request(httpArg); 
//		// 0 �ϰ�  1��ĩ 2�ڼ��� 
//		if ("0".equals(jsonResult)) { 
//			//return resultObject.getFailResult("�ϰ�"); 
//			System.out.println("0�ϰ�");
//		}
//		if ("1".equals(jsonResult)) { 
//			//return resultObject.getFailResult("1��ĩ"); 
//			System.out.println("1��ĩ");
//		} 
//		if ("2".equals(jsonResult)) { 
//			//return resultObject.getFailResult("");
//			System.out.println("2�ڼ���");
//		} 
//	}
 
}
