package cn.stock;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Properties;

public class Config {

	private static final String CONFIG_FILE = "stock.ini";
	private static final String CONFIG_KEY_MIN = "min";
	private static final String CONFIG_KEY_UPDATE = "update";
	private static final String CONFIG_KEY_LOG_LEVEL = "loglevel";
	
	private static final int default_update_time = 2;
	private static final boolean default_min_on_start = false;
	private static final String default_log_level = MyLog.LOG_LEVEL_ERROR;
	
	public static int update_time = 60; //单位：分钟
	public static boolean min_on_start = false;
	public static String log_level = MyLog.LOG_LEVEL_ERROR;
	
	
	public static void saveConfig() {
		Properties setting = new Properties();
		
		setting.setProperty(CONFIG_KEY_MIN, String.valueOf(min_on_start));
		
		setting.setProperty(CONFIG_KEY_UPDATE, String.valueOf(update_time));
		
		setting.setProperty(CONFIG_KEY_LOG_LEVEL, log_level);
		
		try {
			FileOutputStream xie = new FileOutputStream(CONFIG_FILE);
			
			//保存
			setting.store(xie, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public static void getConfig() {
		try {
			File config_file = new File(CONFIG_FILE);
			if(! config_file.exists()) {
				//配置文件不存在，使用默认值
				min_on_start = default_min_on_start;
				update_time = default_update_time;
				log_level = default_log_level;
				return;
			}
			
			Properties setting = new Properties();
			BufferedReader bufferedReader;
			
			bufferedReader = new BufferedReader(new FileReader(CONFIG_FILE));
			setting.load(bufferedReader);
			
			// 获取key对应的value值
			String set_min = setting.getProperty(CONFIG_KEY_MIN);
			if(set_min.isEmpty()) {
				min_on_start = default_min_on_start;
			}else {
				min_on_start = Boolean.valueOf(set_min);
			}
			
			String set_time = setting.getProperty(CONFIG_KEY_UPDATE);
			if(set_time.isEmpty()) {
				update_time = default_update_time;
			}else {
				update_time = Integer.valueOf(set_time);
			}
			
			String level = setting.getProperty(CONFIG_KEY_LOG_LEVEL);
			if(level == null || level.isEmpty()) {
				log_level = default_log_level;
			}else {
				log_level = level;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
}
