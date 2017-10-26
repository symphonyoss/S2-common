package org.symphonyoss.s2.common.time;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtils
{
  public static Date getDateOffsetMonths(Date date, int validMonths)
  {
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    
    calendar.setTime(date);
    calendar.add(Calendar.MONTH, validMonths);
    return calendar.getTime();
  }
  
  public static Date getDateOffsetDays(Date date, int validDays)
  {
    GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
    
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_MONTH, validDays);
    return calendar.getTime();
  }
}
