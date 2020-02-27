package cn.stock;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
 
public class MyLog {
    
    // 日期格式
    public static final String DATE_PATTERN_FULL = "yyyy-MM-dd HH:mm:ss SSS";
    
    public static String logger_name = "stock";
    public static String log_file = "mystock.log";
    
    private static Logger logger = null;
    
    //日志级别
    private static List<String> log_levels = new ArrayList<String>();
    
    public static String LOG_LEVEL_NONE = "NONE";
    public static String LOG_LEVEL_ERROR = "ERROR";
    public static String LOG_LEVEL_WARN = "WARN";
    public static String LOG_LEVEL_INFO = "INFO";
    public static String LOG_LEVEL_TRACE = "TRACE";
    
    public static List<String> getLevels() {
    	return log_levels;
    }
    
    public static Level getLogLevel(String str) {
    	Level level = Level.OFF;
    	
    	if(str.compareTo(LOG_LEVEL_NONE) == 0) {
    		level = Level.OFF;
    	}else if(str.compareTo(LOG_LEVEL_ERROR) == 0) {
    		level = Level.SEVERE;
    	}else if(str.compareTo(LOG_LEVEL_WARN) == 0) {
    		level = Level.WARNING;
    	}else if(str.compareTo(LOG_LEVEL_INFO) == 0) {
    		level = Level.INFO;
    	}else if(str.compareTo(LOG_LEVEL_TRACE) == 0) {
    		level = Level.FINE;
    	}else {
    		level = Level.SEVERE;
    	}
    	return level;
    }
    
    public static String getLogLevelString(Level level) {
    	String info = "";
    	
    	if(level == Level.SEVERE) {
    		info = LOG_LEVEL_ERROR;
    	}else if(level == Level.WARNING) {
    		info = LOG_LEVEL_WARN;
    	}else if(level == Level.INFO) {
    		info = LOG_LEVEL_INFO;
    	}else if(level == Level.FINE) {
    		info = LOG_LEVEL_TRACE;
    	}
    	
    	return info;
    }
    
    public static void Init(String level) {
    	log_levels.add(LOG_LEVEL_NONE);
    	log_levels.add(LOG_LEVEL_ERROR);
    	log_levels.add(LOG_LEVEL_WARN);
    	log_levels.add(LOG_LEVEL_INFO);
    	log_levels.add(LOG_LEVEL_TRACE);
    	
    	logger = Logger.getLogger(logger_name);
        //创建文件Handler并设置格式Formatter和等级Level
        FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(log_file, true);
			
			fileHandler.setFormatter(new Formatter() {
                @Override
                public String format(LogRecord record) {
 
                    // 设置文件输出格式
//                    return "[ " + getCurrentDateStr(DATE_PATTERN_FULL) + " - "
//                            + getLogLevelString(record.getLevel()) + " ]-" + 
//                            "[" + record.getSourceClassName()
//                            + " -> " + record.getSourceMethodName() + "()] " + 
//                            record.getMessage() + "\n";
                    return "[ " + getCurrentDateStr(DATE_PATTERN_FULL) + " - "
                    + getLogLevelString(record.getLevel()) + " ] - " + 
                    record.getMessage() + "\n";
                }
            });
	        fileHandler.setLevel(getLogLevel(level));
	        logger.setLevel(getLogLevel(level));
	        //添加到logger
	        logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public static void Trace(String msg) {
    	logger.log(Level.FINE, msg);
    }
//    public static void Trace(String src, String method, String msg) {
//    	logger.logp(Level.FINE, src, method, msg);
//    }
    
    public static void Info(String msg) {
    	logger.log(Level.INFO, msg);
    }
//    public static void Info(String src, String method, String msg) {
//    	logger.logp(Level.INFO, src, method, msg);
//    }
    
    public static void Warn(String msg) {
    	logger.log(Level.WARNING, msg);
    }
//    public static void Warn(String src, String method, String msg) {
//    	logger.logp(Level.WARNING, src, method, msg);
//    }

    public static void Error(String msg) {
    	logger.log(Level.SEVERE, msg);
    }
//    public static void Error(String src, String method, String msg) {
//    	logger.logp(Level.SEVERE, src, method, msg);
//    }
 
    private static SimpleDateFormat sdf = null;
    
    public static String getCurrentDateStr(String pattern) {
        Date date = new Date();
        if(sdf == null) {
        	sdf = new SimpleDateFormat(pattern);
        }
        return sdf.format(date);
    }
}


