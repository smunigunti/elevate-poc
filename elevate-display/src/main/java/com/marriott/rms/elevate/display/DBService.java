package com.marriott.rms.elevate.display;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.LoggerFactory;

import com.mariott.rms.elevate.display.domain.GNRCount;

public class DBService {

	private static final org.slf4j.Logger LOG = LoggerFactory
			.getLogger(DBService.class);

	private BasicDataSource connectionPool;

	public DBService() {
		setupDB();
		LOG.info("DB Service Instantiated ");
	}

	public List<GNRCount> readMessages() {

		List<GNRCount> list = new ArrayList<GNRCount>();
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		try {
			LOG.info("Reading Data.. ");
			connection = connectionPool.getConnection();
			statement = connection.createStatement();
			rs = statement
					.executeQuery("SELECT data -> 'propertyCode' as code , count(*) as count FROM GNR GROUP BY data -> 'propertyCode'");

			LOG.info("Retrieved the data.. ");
			while (rs.next()) {
				list.add(new GNRCount(rs.getString("code"), rs.getInt("count")));
			}

		} catch (Exception e) {
			LOG.info("Error While reading data from DB: " + e.getMessage(), e);
		} finally {
			try {
				if (rs != null)
					rs.close();
				if (statement != null)
					statement.close();
				if (connection != null)
					connection.close();
			} catch (SQLException e) {
				LOG.info("Error While closing connections: " + e.getMessage(),
						e);
			}

		}
		return list;
	}

	private void setupDB() {

		Properties prop = readProperties();

		String dbUrl = prop.getProperty("dbUrl");
		connectionPool = new BasicDataSource();

		connectionPool.setUsername(prop.getProperty("user"));
		connectionPool.setPassword(prop.getProperty("password"));

		connectionPool.setDriverClassName("org.postgresql.Driver");
		connectionPool.setUrl(dbUrl);
		connectionPool.setInitialSize(1);
	}

	private Properties readProperties() {
		Properties prop = new Properties();
		String propFileName = "postgres.properties";

		InputStream inputStream = getClass().getClassLoader()
				.getResourceAsStream(propFileName);

		try {
			if (inputStream != null) {
				prop.load(inputStream);
			} else {
				throw new FileNotFoundException("property file '"
						+ propFileName + "' not found in the classpath");
			}
		} catch (IOException e) {
			e.printStackTrace();
			LOG.info("Error While reading property file: " + e.getMessage(), e);
		}
		return prop;
	}

}
