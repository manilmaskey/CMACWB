package edu.uah.itsc.cmac.glm.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.TimeZone;

public class DataUtil {
	
	public static long dateToMilliseconds(int year, int month, int day, int hour, int minute, int second, int mili)
	{
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month-1);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		cal.set(Calendar.SECOND, second);
		cal.set(Calendar.MILLISECOND, mili);
		
		return cal.getTimeInMillis();
		
	}
	public static long secondsStringToMilliseconds(String fractionalSeconds) 
	{
		// set up milliseconds, use full integer seconds and fraction, otherwise parseFloat
		// converts to exponential notation and looses precision
		String secondTokens[] = fractionalSeconds.split("\\.");
		long second = Long.parseLong(secondTokens[0]);
		return (second * 1000 + Integer.parseInt(secondTokens[1].substring(0, 3)));
	}
	public static String millisecondsToGmtDateString(long milliseconds) {
		// convert seconds since 1970 to date/time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(milliseconds);
		return cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-"+ cal.get(Calendar.DAY_OF_MONTH);
	}

	public static String millisecondsToGmtTimeString(long milliseconds) {
		// convert seconds since 1970 to date/time
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(milliseconds);
		return cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+cal.get(Calendar.MILLISECOND);
	}
	public static String millisecondsToSQLTimeStampString(long milliseconds) {
		// convert seconds since 1970 to date/time + milisec + microsec
		// yyyy-mm-dd hh24:mi:ss.MS.US
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		cal.setTimeInMillis(milliseconds);
		String date = cal.get(Calendar.YEAR) + "-" + (cal.get(Calendar.MONTH)+1) + "-"+ cal.get(Calendar.DAY_OF_MONTH);
		// add +0000 to specify GMT
		return date + "T" + cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND)+"."+cal.get(Calendar.MILLISECOND)+ "+0000";
//		return date + " " + cal.get(Calendar.HOUR_OF_DAY)+":"+cal.get(Calendar.MINUTE)+":"+cal.get(Calendar.SECOND);
	}
    public static Connection establishConnection(String server, String uname, String pw) throws SQLException {
    	Connection con=null;
        try {
//            DataSource dataSource = (DataSource) new InitialContext()
//                    .lookup("jdbc/odr");
//            con = dataSource.getConnection();
    	    Class.forName("org.postgresql.Driver"); 

    		con = DriverManager.getConnection(server, uname, pw); 
    		con.setAutoCommit(false);
        } catch (SQLException ex) {
            throw new SQLException("An exception occured while trying to"
                    + "connect to the database.", ex);
        }
//        } catch (NamingException ex) {
//            throw new SQLException("An exception occured while trying to"
//                    + "connect to the database.", ex);
//        } 
        catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return con;


    }
}
