package org.archivarium.impl;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.archivarium.ScoreProviderException;
import org.h2.Driver;

public class H2ScoreProvider extends AbstractDatabaseProvider {
	private static final Logger logger = Logger
			.getLogger(H2ScoreProvider.class);

	private static final String URL_PREFIX = "jdbc:h2:file:";
	private static final String SCRIPT_CREATE_DB = "/create_db.sql";

	static {
		try {
			// Initialize H2 driver
			Class.forName(Driver.class.getName());
		} catch (ClassNotFoundException e) {
			logger.fatal("Cannot initialize H2 driver", e);
			throw new RuntimeException(e);
		}
	}

	private String database;

	public H2ScoreProvider(String database) throws ScoreProviderException,
			IllegalArgumentException {
		super();

		this.database = database;
		if (database == null) {
			throw new IllegalArgumentException("null file");
		}

		if (!alreadyExistsDB(database)) {
			initializeDB(database);
		}
	}

	private boolean alreadyExistsDB(String database) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(getDatabaseUrl()
					+ ";IFEXISTS=TRUE");
			return true;
		} catch (SQLException e) {
			return false;
		} finally {
			try {
				if (connection != null) {
					connection.close();
				}
			} catch (SQLException e) {
				logger.error("Cannot close connection", e);
			}
		}
	}

	private void initializeDB(String database) throws ScoreProviderException {
		try {
			InputStream createDatabaseScript = H2ScoreProvider.class
					.getResourceAsStream(SCRIPT_CREATE_DB);
			executeScript(createDatabaseScript, getDatabaseUrl());
		} catch (SQLException e) {
			String message = "Cannot intialize H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		} catch (IOException e) {
			String message = "Cannot intialize H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public boolean readOnly() {
		return false;
	}

	@Override
	protected String getDatabaseUrl() {
		return URL_PREFIX + database;
	}
}
