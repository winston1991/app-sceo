package com.huntkey.software.sceo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Formatter;
import java.util.Locale;

public class TimeUtil {

	/**
	 * 时间处理
	 * @param time 需要处理的时间字符串
	 * @param pattern 格式，如:"yyyy-mm-dd hh:mm:ss"
	 * @return
	 */
	public static String formatDisplayTime(String time, String pattern){
		String display = "";
		int tMin = 60 * 1000;
		int tHour = 60 * tMin;
		int tDay = 24 * tHour;
		
		if (time != null) {
			try {
				Date tDate = new SimpleDateFormat(pattern).parse(time);
				Date today = new Date();
				SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
				SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
				Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
				Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
				Date beforeYes = new Date(yesterday.getTime() - tDay);
				if (tDate != null) {
					SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
					long dTime = today.getTime() - tDate.getTime();
					if (tDate.before(thisYear)) {
						display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
					}else {
						if (dTime < tMin) {
							display = "刚刚";
						}else if (dTime < tHour) {
							display = (int) Math.ceil(dTime / tMin) + "分钟前";
						}else if (dTime < tDay && tDate.after(yesterday)) {
							display = (int) Math.ceil(dTime / tHour) + "小时前";
						}else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
							display = "昨天" + new SimpleDateFormat("HH:mm").format(tDate);
						}else {
							display = halfDf.format(tDate);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return display;		
	}
	
	/**
	 * 获取当前时间
	 */
	public static String getCurrentData(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		//获取当前时间
		Date curDate = new Date(System.currentTimeMillis());
		return format.format(curDate);
	}

	public static String getCurrentTime(){
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
		//获取当前时间
		Date curDate = new Date(System.currentTimeMillis());
		return format.format(curDate);
	}

	public static String getCurrentDay(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		Date curDate = new Date(System.currentTimeMillis());
		return format.format(curDate);
	}
	
    public static String timeFormat(long date) {
        long ssaa = new Date().getTime();
        long delta = ssaa - date;
        final String ONE_SECOND_AGO = "秒前";
        final String ONE_MINUTE_AGO = "分钟前";
        final String ONE_HOUR_AGO = "小时前";
        final String ONE_DAY_AGO = "天前";
        final long ONE_MINUTE = 60000L;
        final long ONE_HOUR = 3600000L;
        final long ONE_DAY = 86400000L;
        if (delta < 1L * ONE_MINUTE) {
            long seconds = toSeconds(delta);
            return (seconds <= 0 ? 1 : seconds) + ONE_SECOND_AGO;
        }
        if (delta < 45L * ONE_MINUTE) {
            long minutes = toMinutes(delta);
            return (minutes <= 0 ? 1 : minutes) + ONE_MINUTE_AGO;
        }
        if (delta < 24L * ONE_HOUR) {
            long hours = toHours(delta);
            return (hours <= 0 ? 1 : hours) + ONE_HOUR_AGO;
        }
        if (delta < 48L * ONE_HOUR) {
            return "昨天";
        }
        if (delta < 30L * ONE_DAY) {
            long days = toDays(delta);
            return (days <= 0 ? 1 : days) + ONE_DAY_AGO;
        } else {
            String time111 = getDateStr(date);
            return time111;
        }
    }
    
    private static long toSeconds(long date) {
        return date / 1000L;
    }

    private static long toMinutes(long date) {
        return toSeconds(date) / 60L;
    }

    private static long toHours(long date) {
        return toMinutes(date) / 60L;
    }

    private static long toDays(long date) {
        return toHours(date) / 24L;
    }

    private static long toMonths(long date) {
        return toDays(date) / 30L;
    }
    
    private static String getDateStr(long millis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        Formatter ft = new Formatter(Locale.CHINA);
        return ft.format("%1$tY年%1$tm月%1$td日", cal).toString();
    }
    
    /**
     * 传入具体日期，返回日期加/减n个月
     * @param data
     * @param n
     * @return
     * @throws ParseException
     */
    public static String subMonth(String data, int n) throws ParseException{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date dt = sdf.parse(data);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(dt);
		
		rightNow.add(Calendar.MONTH, n);
		Date dt1 = rightNow.getTime();
		String reStr = sdf.format(dt1);
    	
    	return reStr;
    }
    
    /**
     * 日期加/减n天
     * @param option pro为减n天，next为加n天
     * @param _date
     * @return
     */
    public static String checkOption(String option, String _date, int n){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cl = Calendar.getInstance();
		Date date = null;
		
		try {
			date = (Date)sdf.parse(_date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		cl.setTime(date);
		if ("pre".equals(option)) {
			//时间减n天
			cl.add(Calendar.DAY_OF_MONTH, -n);
		}else if ("next".equals(option)) {
			//时间加n天
			cl.add(Calendar.DAY_OF_YEAR, n);
		}else {
			
		}
		date = cl.getTime();
    	
    	return sdf.format(date);
    }
    
    /**
     * 获取当前年月日
     */
    public static String getCurrentYMD(){
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date());
	}
    
    /**
     * 判断两段时间的时间间隔是否大于某一值
     */
    public static boolean isClose(String firstTime, String lastTime){
    	Calendar firstCalendar = Calendar.getInstance();
    	Calendar lastCalendar = Calendar.getInstance();
    	try {
			firstCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(firstTime));
			long first = firstCalendar.getTimeInMillis();
			
			lastCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(lastTime));
			long last = lastCalendar.getTimeInMillis();
			
			if ((last - first) < 60000) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
		return false;
    }
    
    /**
     * 判断后一时间是否比前一时间大
     * @param firstTime  yyyy-MM-dd HH:mm
     * @param lastTime  yyyy-MM-dd HH:mm
     * @return
     */
    public static boolean isBigger(String firstTime, String lastTime){
    	Calendar firstCalendar = Calendar.getInstance();
    	Calendar lastCalendar = Calendar.getInstance();
    	try {
			firstCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(firstTime));
			long first = firstCalendar.getTimeInMillis();
			
			lastCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(lastTime));
			long last = lastCalendar.getTimeInMillis();
			
			if (last > first) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
		return false;
    }
    
    /**
     * 判断后一时间是否比前一时间大
     * @param firstTime  yyyy-MM-dd
     * @param lastTime  yyyy-MM-dd
     * @return
     */
    public static boolean isBigger2(String firstTime, String lastTime){
    	Calendar firstCalendar = Calendar.getInstance();
    	Calendar lastCalendar = Calendar.getInstance();
    	try {
			firstCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(firstTime));
			long first = firstCalendar.getTimeInMillis();
			
			lastCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd").parse(lastTime));
			long last = lastCalendar.getTimeInMillis();
			
			if (last > first) {
				return true;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
		return false;
    }
    
    /**
     * 计算两个时间的差值
     * @param firstTime
     * @param lastTime
     * @return
     */
    public static long getSub(String firstTime, String lastTime){
    	Calendar firstCalendar = Calendar.getInstance();
    	Calendar lastCalendar = Calendar.getInstance();
    	try {
			firstCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(firstTime));
			long first = firstCalendar.getTimeInMillis();
			
			lastCalendar.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(lastTime));
			long last = lastCalendar.getTimeInMillis();
			
			return last - first;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
    }

	
}
