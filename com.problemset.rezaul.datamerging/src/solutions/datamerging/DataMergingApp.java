package solutions.datamerging;

//import com.databricks.spark.xml.*;

import java.util.HashMap;
import java.util.List;

import org.apache.spark.sql.Column;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConversions;

import static org.apache.spark.sql.functions.from_unixtime;
import static org.apache.spark.sql.functions.from_utc_timestamp;
import static org.apache.spark.sql.functions.to_utc_timestamp;
import static org.apache.spark.sql.functions.date_format;

import static org.apache.spark.sql.functions.concat;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.count;

/**
 * Data merging
 * 
 * @author Muhammad Rezaul Karim
 */
public class DataMergingApp {

	/**
	 * main() is your entry point to the application.
	 * 
	 * @param args
	 */
	private static Logger logger = LoggerFactory.getLogger(DataMergingApp.class);


	// These formats are defined so that we can quite easily read from any data-time
	// format
	public String CSV_FILE_DATETIME_FORMAT = "";
	public String JSON_FILE_DATETIME_FORMAT = "";
	public String XML_FILE_DATETIME_FORMAT = "";

	// Stores timezone information
	public String CSV_FILE_TIMEZONE = ""; // default value
	public String JSON_FILE_TIMEZONE = ""; // default value
	public String XML_FILE_TIMEZONE = ""; // default value

	// Stores time one separator information(Example: " ", "+","-")
	public String CSV_FILE_TIME_ZONE_SEPERATOR = ""; // default value
	public String JSON_FILE_TIME_ZONE_SEPERATOR = ""; // default value
	public String XML_FILE_TIME_ZONE_SEPERATOR = ""; // default value

	Dataset<Row> dfCSV, dfJSON, dfXML, dfCombined, dfServiceGuidAggregate;
	TimeZoneHelper timeZoneHelper;

	SparkSession spark = null;

	public static void main(String[] args) {
		try {
			DataMergingApp app = new DataMergingApp();

			app.initializeEnvironment(args);
			app.readInputFiles();
			app.mergeDataFrames();
			app.transform();
			app.printAggregate();
			app.writeOutPutFile();
		} catch (Exception ex) {
			logger.info("Exception occured while processing data: Details: " + ex.getMessage());
		}
	}

	/**
	 * Initialize environment (e.g. Spark Session)
	 */

	public void initializeEnvironment(String[] args) {
			
		// Initialize timezone mapper
		timeZoneHelper = new TimeZoneHelper();
		timeZoneHelper.createTimeZoneMapping();
		
		// Read date formats from the xml config file
		HashMap<String, String> appConfigMapper = Utility.readXMlAppConfiguration("config" + System.getProperty("file.separator") + "config.xml");
		CSV_FILE_DATETIME_FORMAT = appConfigMapper.get("CsvFileDateTimeFormat");
		JSON_FILE_DATETIME_FORMAT = appConfigMapper.get("JsonFileDateTimeFormat");
		XML_FILE_DATETIME_FORMAT = appConfigMapper.get("XmlFileDateTimeFormat");
						
		// Run on local machine when local is specified as a program argument
		if(args.length > 0 && args[0].equals("local"))
		{
			
			spark = SparkSession.builder().appName("Data Transformation and Merging").master("local[5]")
				.config("spark.sql.session.timeZone", "UTC") // default timezone
				.getOrCreate();
			spark.sparkContext().setLogLevel("WARN");
		}
		else  // (master not specified) when deployed in a cluster with spark-submit command
		{
			spark = SparkSession.builder().appName("Data Transformation and Merging")
					.config("spark.sql.session.timeZone", "UTC") // default timezone
					.getOrCreate();
			spark.sparkContext().setLogLevel("WARN");
		}
	
	}

	/**
	 * Method for merging data from different data sources into a single data frame
	 */

	public void mergeDataFrames() {

		dfCombined = dfCSV.unionByName(dfJSON).unionByName(dfXML);
		dfCombined.cache();
		logger.info("Total records after merging records from all data sets: " + dfCombined.count() + " records.");

	}

	/**
	 * Data transformation and Filtering: 1. All records with packets-serviced equal
	 * to zero are excluded. 2. Records sorted by request-time in ascending order
	 */
	public void transform() {

		String csvSparkCompatibleTimeZone = timeZoneHelper.getSparkCompatibleTimeZone(CSV_FILE_TIMEZONE.trim());

		// Remove records where "packets-serviced" column value is 0
		dfCombined = dfCombined.filter(dfCombined.col("packets-serviced").notEqual(0));

		logger.info("Total records after filtering. " + dfCombined.count());

		// Convert time in UTC time zone to the time zone specified in the CSV file
		dfCombined = dfCombined.withColumn("request-time", date_format(
				from_utc_timestamp(dfCombined.col("request-time"), csvSparkCompatibleTimeZone).cast("timestamp"),
				CSV_FILE_DATETIME_FORMAT));

		// Write the time zone name exactly as used in the original CSV file
		dfCombined = dfCombined.withColumn("request-time",
					concat(dfCombined.col("request-time"), lit(CSV_FILE_TIMEZONE)));

		// Sort the records in ascending order of request-time
		dfCombined = dfCombined.sort(dfCombined.col("request-time"));
		dfCombined.cache();

	}

	/**
	 * Print a summary showing the number of records in the output file associated
	 * with each service-guid
	 */
	public void printAggregate() {

		dfServiceGuidAggregate = dfCombined.groupBy(dfCombined.col("service-guid")).agg(count("service-guid"))
				.orderBy("service-guid");
		dfServiceGuidAggregate=dfServiceGuidAggregate.withColumnRenamed("count(service-guid)", "count");
		dfServiceGuidAggregate.show((int) dfServiceGuidAggregate.count(),false);

	}

	/**
	 * Output a combined CSV file
	 */

	public void writeOutPutFile() {

		dfCombined = dfCombined.repartition(1);
		// Save the output file
		dfCombined.write().format("csv").option("header", true).mode(SaveMode.Overwrite).save("data"+ System.getProperty("file.separator") +"output.csv");
	}

	/**
	 * Method for reading input files.
	 */
	private void readInputFiles() {

		// Read the CSV file in a data frame
		dfCSV = spark.read().format("csv").option("header", "true").load("data" + System.getProperty("file.separator") + "reports.csv");

		// Extract the list of columns and column orders from the CSV file. This order
		// will be used later for json and xml files column ordering
		List<Column> csvColumnList = Utility.getColumnList(dfCSV.schema().fieldNames());

		Row firstRow = dfCSV.first();  // extract time zone information from the first row ("request-time" column)
		int requestTimeColumnIndex = dfCSV.schema().fieldIndex("request-time");		
		CSV_FILE_TIMEZONE = firstRow.getString(2).substring(CSV_FILE_DATETIME_FORMAT.length());
		CSV_FILE_TIME_ZONE_SEPERATOR = timeZoneHelper.getTimeZoneSeperator(CSV_FILE_TIMEZONE);
		String csvSparkCompatibleTimeZone = timeZoneHelper.getSparkCompatibleTimeZone(CSV_FILE_TIMEZONE.trim());

		dfCSV = dfCSV.withColumn("request-time",
				dfCSV.col("request-time").substr(0, CSV_FILE_DATETIME_FORMAT.length()));
		dfCSV = dfCSV.withColumn("request-time",
				to_utc_timestamp(dfCSV.col("request-time"), csvSparkCompatibleTimeZone)); // convert to UTC format
		dfCSV.cache();
		
		// Reads the JSON file in a data frame
		dfJSON = spark.read().format("json").option("multiline", true).load("data"+  System.getProperty("file.separator") + "reports.json");
		dfJSON = dfJSON.withColumn("request-time",
				from_unixtime(dfJSON.col("request-time").divide(1000).cast("BIGINT"), JSON_FILE_DATETIME_FORMAT)); // epoch time needs to be in seconds for this method
		dfJSON = dfJSON.select(JavaConversions.asScalaBuffer(csvColumnList));
		dfJSON.cache();
			
		
		// Read the xml file in a data frame
		dfXML = spark.read().format("com.databricks.spark.xml").option("rowTag", "report").load("data" +  System.getProperty("file.separator") + "reports.xml");
		dfXML = dfXML.select(JavaConversions.asScalaBuffer(csvColumnList));
				
		firstRow = dfXML.first();  // extract time zone information from the first row ("request-time" column)		
		requestTimeColumnIndex = dfXML.schema().fieldIndex("request-time");		
		XML_FILE_TIMEZONE = firstRow.getString(requestTimeColumnIndex).substring(XML_FILE_DATETIME_FORMAT.length()); 
		String xmlSparkCompatibleTimeZone = timeZoneHelper.getSparkCompatibleTimeZone(XML_FILE_TIMEZONE.trim());

		dfXML = dfXML.withColumn("request-time",
				dfXML.col("request-time").substr(0, XML_FILE_DATETIME_FORMAT.length()));
		dfXML = dfXML.withColumn("request-time",
				to_utc_timestamp(dfXML.col("request-time"), xmlSparkCompatibleTimeZone));
		dfXML.cache();
	}
}
