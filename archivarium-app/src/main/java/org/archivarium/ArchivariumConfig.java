package org.archivarium;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.archivarium.data.ScoreSchema;

public class ArchivariumConfig {
	private static final String DATABASE = "archivarium.db";
	private static final String BACKUP_DIR = "archivarium.backup_dir";
	private static final String SCORE_ROOT = "archivarium.scores.root_dir";
	private static final String FIELDS = "archivarium.scores.fields";
	private static final String SELECTORS = "archivarium.scores.selectors";

	private static final ArchivariumConfig instance;

	static {
		try {
			String property = System.getProperty("archivarium.config");
			if (property != null) {
				instance = new ArchivariumConfig(new FileInputStream(property));
			} else {
				instance = null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static ArchivariumConfig getInstance() {
		return instance;
	}

	private String database;
	private List<String> fields, selectors;
	private File scoreRoot, backups;

	private ArchivariumConfig(InputStream stream) throws IOException {
		Properties properties = new Properties();
		properties.load(stream);

		database = properties.getProperty(DATABASE);
		backups = getFile(properties, BACKUP_DIR);
		scoreRoot = getFile(properties, SCORE_ROOT);
		fields = getStringList(properties, FIELDS, ScoreSchema.FIELD_NAMES);
		selectors = getStringList(properties, SELECTORS,
				ScoreSchema.FIELD_NAMES);
	}

	private File getFile(Properties properties, String key) {
		return new File(properties.getProperty(key));
	}

	private List<String> getStringList(Properties properties,
			String propertyName, List<String> validValues) throws IOException {
		List<String> ret = new ArrayList<String>();
		for (String prop : properties.getProperty(propertyName).split(",")) {
			String value = prop.trim();
			if (!validValues.contains(value)) {
				throw new IOException("Invalid value: " + value);
			}
			ret.add(value);
		}
		return ret;
	}

	public String getDatabase() {
		return database;
	}

	public List<String> getFields() {
		return fields;
	}

	public List<String> getSelectors() {
		return selectors;
	}

	public File getScoreRootDir() {
		return scoreRoot;
	}

	public File getBackupDir() {
		return backups;
	}
}
