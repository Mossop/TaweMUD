package com.esp.tawemud.tawescript;

import com.esp.tawemud.TaweServer;
import com.esp.tawemud.items.Room;
import com.esp.tawemud.Exit;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Returns the current time.
 *
 * @author  Dave Townsend
 * @version 1.1
 */
public class GetTime extends SingleAction
{
	/**
	 * A variable to hold the number of years.
	 */
	public String year = "";
	/**
	 * A variable to hold the number of months.
	 */
	public String month = "";
	/**
	 * A variable to hold the number of days.
	 */
	public String day = "";
	/**
	 * A variable to hold the number of hours.
	 */
	public String hour = "";
	/**
	 * A variable to hold the number of minutes.
	 */
	public String minute = "";
	/**
	 * A variable to hold the number of seconds.
	 */
	public String second = "";

	public boolean doAction(TaweServer server, Variables variables)
	{
		Calendar time = new GregorianCalendar();
		if (year.length()>0)
		{
			variables.setVariable(year,String.valueOf(time.get(Calendar.YEAR)));
		}
		if (month.length()>0)
		{
			variables.setVariable(month,String.valueOf(time.get(Calendar.MONTH)));
		}
		if (day.length()>0)
		{
			variables.setVariable(day,String.valueOf(time.get(Calendar.DAY_OF_MONTH)));
		}
		if (hour.length()>0)
		{
			variables.setVariable(hour,String.valueOf(time.get(Calendar.HOUR_OF_DAY)));
		}
		if (minute.length()>0)
		{
			variables.setVariable(minute,String.valueOf(time.get(Calendar.MINUTE)));
		}
		if (second.length()>0)
		{
			variables.setVariable(second,String.valueOf(time.get(Calendar.SECOND)));
		}
		return false;
	}
}
