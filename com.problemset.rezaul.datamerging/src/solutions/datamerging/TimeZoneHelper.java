package solutions.datamerging;

import java.util.HashMap;

/**
 * 
 * @author Muhammad Rezaul Karim
 */
public class TimeZoneHelper {
	public HashMap<String,String> timeZoneMapper = null;
	
	public TimeZoneHelper()
	{
		 timeZoneMapper = new HashMap<String,String>();
	}
	
	public void createTimeZoneMapping()
	{
		// Mapping can be populated from a file. Not done here
		// Only a few timezone mapping has been provided. Before using any new timezone data, it needs to be added here
		
		// This kind of mapping required as not all timezone formats found in various data sets are directly supported by various tools
		// For example, JAVA language does not accept 'ADT' timezone
		
		//supported timezone format 1
		 timeZoneMapper.put("ADT","GMT-3");  //Atlantic Daylight Time, UTC Offset: UTC -3
		 timeZoneMapper.put("AST","GMT-4");  //Atlantic Standard Time, UTC Offset: UTC -4
		 timeZoneMapper.put("America/New_York","America/New_York");
		 timeZoneMapper.put("America/Edmonton","America/Edmonton");
		 
		 //supported timezone format 2 (UTC OFFSET)
		 timeZoneMapper.put("-05:00","GMT-5"); // support another timezone format, not used with the given data set 
		 timeZoneMapper.put("-06:00","GMT-6");  //support another timezone format, not used with the given data set
		 timeZoneMapper.put("+05:00","GMT+5");  //support another timezone format, not used with the given data set
		 timeZoneMapper.put("+06:00","GMT+6");  //support another timezone format, not used with the given data set
		 
	}
	
	public String getSparkCompatibleTimeZone(String timeZone)
	{
		return timeZoneMapper.get(timeZone);
	}
	
	public String getTimeZoneSeperator(String timezone)
	{
		if(timezone.charAt(0)==' ')
			return " ";
		else if(timezone.charAt(0)=='+')
			return "";
		else if(timezone.charAt(0)=='-')
			return "";
		
		return "";
	}

}
