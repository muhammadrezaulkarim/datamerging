package solutions.datamerging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.spark.sql.Column;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Muhammad Rezaul Karim
 */
public class Utility {
	private static Logger logger = LoggerFactory.getLogger(Utility.class);

	public static List<Column> getColumnList(String[] columnNames) {
		List<Column> filterColumns = new ArrayList<>();
		for (String field : columnNames) {
			filterColumns.add(new Column(field));
		}

		return filterColumns;
	}

	public static HashMap<String, String> readXMlAppConfiguration(String configFilePath) {

		HashMap<String, String> configMapper = new HashMap<String, String>();

		try {

			XMLConfiguration appconfig = null;

			appconfig = new XMLConfiguration(configFilePath);

			Iterator<String> keyIter = appconfig.getKeys();
			String key;

			while (keyIter.hasNext()) {

				key = keyIter.next();
				Object property = appconfig.getProperty(key);

				if (property instanceof Collection) {
					List<String> values = (List<String>) property;
					for (String value : values) {
						configMapper.put(key, value);
					}
				} else {
					configMapper.put(key, property.toString());
				}
			}

		} catch (Exception ex) {
			configMapper = null;
			logger.info("Exception occured while reading configuration file: Details: " + ex.getMessage());
		}
		return configMapper;
	}
}
