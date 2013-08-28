package org.archivarium.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.h2.Driver;

public class H2ScoreProvider implements ScoreProvider {
	public static final String NAME = "Embedded H2";

	private static final Logger logger = Logger
			.getLogger(H2ScoreProvider.class);

	static final String COLUMN_NAME_ID = "id";
	static final String COLUMN_NAME_NAME = "name";
	static final String COLUMN_NAME_AUTHOR = "author";
	static final String COLUMN_NAME_DESCRIPTION = "description";
	static final String COLUMN_NAME_INSTRUMENTS = "instruments";
	static final String COLUMN_NAME_EDITION = "edition";
	static final String COLUMN_NAME_URL = "url";
	static final String COLUMN_NAME_FORMAT = "format";
	static final String COLUMN_NAME_LOCATION = "location";
	static final String COLUMN_NAME_GENRE = "genre";

	static final String URL_PREFIX = "jdbc:h2:file:";

	private static final String HIBERNATE_PROP_URL = "hibernate.connection.url";
	private static final String PERSISTENCE_UNIT_NAME = "org.archivarium";
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

	private EntityManager entityManager;

	public H2ScoreProvider(String database) throws ScoreProviderException,
			IllegalArgumentException {
		if (database == null) {
			throw new IllegalArgumentException("null file");
		}

		if (!alreadyExistsDB(database)) {
			initializeDB(database);
		}

		Properties properties = new Properties();
		properties.put(HIBERNATE_PROP_URL, URL_PREFIX + database);
		entityManager = Persistence.createEntityManagerFactory(
				PERSISTENCE_UNIT_NAME, properties).createEntityManager();
	}

	static boolean alreadyExistsDB(String database) {
		Connection connection = null;
		try {
			connection = DriverManager.getConnection(URL_PREFIX + database
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

	static void initializeDB(String database) throws ScoreProviderException {
		try {
			executeScript(
					H2ScoreProvider.class.getResourceAsStream(SCRIPT_CREATE_DB),
					URL_PREFIX + database);
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
	public List<Score> getScores() throws ScoreProviderException {
		try {
			entityManager.getTransaction().begin();
			TypedQuery<Score> query = entityManager.createQuery(
					"SELECT s FROM " + DefaultScore.class.getSimpleName()
							+ " s", Score.class);
			List<Score> ret = query.getResultList();
			entityManager.getTransaction().commit();
			return ret;
		} catch (Exception e) {
			String message = "Cannot get scores from H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public boolean readOnly() {
		return false;
	}

	@Override
	public void addScore(Score score) throws ScoreProviderException {
		try {
			entityManager.getTransaction().begin();
			entityManager.persist(score);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot add score to H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public void modifyScore(Score score) throws ScoreProviderException {
		try {
			entityManager.getTransaction().begin();
			entityManager.merge(score);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot add score to H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public void deleteScore(Score score) throws ScoreProviderException {
		try {
			entityManager.getTransaction().begin();
			entityManager.remove(score);
			entityManager.getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot delete score from H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public List<Score> search(Score model, boolean strict)
			throws ScoreProviderException {
		return strict ? doSearchStrict(model) : doSearchNotStrict(model);
	}

	List<Score> doSearchStrict(Score model) throws ScoreProviderException {
		try {
			entityManager.getTransaction().begin();
			CriteriaBuilder builder = entityManager.getCriteriaBuilder();

			CriteriaQuery<Score> criteria = builder.createQuery(Score.class);
			Root<DefaultScore> from = criteria.from(DefaultScore.class);

			criteria = criteria.select(from);
			criteria = criteria.distinct(true);

			List<Predicate> predicates = new ArrayList<Predicate>();
			updatePredicates(model.getName(), COLUMN_NAME_NAME, predicates,
					builder, from);
			updatePredicates(model.getAuthor(), COLUMN_NAME_AUTHOR, predicates,
					builder, from);
			updatePredicates(model.getDescription(), COLUMN_NAME_DESCRIPTION,
					predicates, builder, from);
			updatePredicates(model.getEdition(), COLUMN_NAME_EDITION,
					predicates, builder, from);
			updatePredicates(model.getURL(), COLUMN_NAME_URL, predicates,
					builder, from);
			updatePredicates(model.getFormat(), COLUMN_NAME_FORMAT, predicates,
					builder, from);
			updatePredicates(model.getLocation(), COLUMN_NAME_LOCATION,
					predicates, builder, from);
			updatePredicates(model.getGenre(), COLUMN_NAME_GENRE, predicates,
					builder, from);

			if (model.getInstruments() != null) {
				Path<List<String>> instruments = from
						.<List<String>> get(COLUMN_NAME_INSTRUMENTS);
				for (String instrument : model.getInstruments()) {
					predicates.add(builder.isMember(instrument, instruments));
				}
			}

			criteria.where(predicates.toArray(new Predicate[predicates.size()]));
			List<Score> scores = entityManager.createQuery(criteria)
					.getResultList();
			entityManager.getTransaction().commit();
			return scores;
		} catch (Exception e) {
			String message = "Cannot get scores from H2 database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	private void updatePredicates(String value, String columnName,
			List<Predicate> predicates, CriteriaBuilder builder,
			Root<DefaultScore> from) {
		if (value != null) {
			Path<String> column = from.<String> get(columnName);
			predicates.add(builder.equal(column, value));
		}
	}

	List<Score> doSearchNotStrict(Score model) throws ScoreProviderException {
		List<Score> scores = getScores();

		List<Score> ret = new ArrayList<Score>();
		for (Score score : scores) {
			if (matches(model, score)) {
				ret.add(score);
			}
		}

		return ret;
	}

	private boolean matches(Score model, Score score)
			throws ScoreProviderException {
		try {
			compareStrings(model.getName(), score.getName());
			compareStrings(model.getAuthor(), score.getAuthor());
			compareStrings(model.getDescription(), score.getDescription());
			compareStrings(model.getEdition(), score.getEdition());

			if (model.getInstruments() != null) {
				for (String modelInstrument : model.getInstruments()) {
					boolean found = false;
					for (String scoreInstrument : score.getInstruments()) {
						try {
							compareStrings(modelInstrument, scoreInstrument);
							found = true;
							break;
						} catch (AssertionError e) {
						}
					}

					if (!found) {
						throw new AssertionError();
					}
				}
			}

			compareStrings(model.getURL(), score.getURL());
			compareStrings(model.getFormat(), score.getFormat());
			compareStrings(model.getLocation(), score.getLocation());
			compareStrings(model.getGenre(), score.getGenre());

			return true;
		} catch (AssertionError e) {
			return false;
		}
	}

	private void compareStrings(String model, String score)
			throws AssertionError {
		int threshold = 3;
		if (model != null && score != null) {
			int dist = StringUtils.getLevenshteinDistance(model, score);
			if (dist > threshold) {
				throw new AssertionError();
			}
		}
	}

	static void executeScript(InputStream is, String url) throws SQLException,
			IOException {
		Connection connection = DriverManager.getConnection(url);
		Statement statement = connection.createStatement();

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		while (line != null) {
			statement.execute(line);
			line = reader.readLine();
		}

		statement.close();
		connection.close();
	}

	@Override
	public Score getScore(int i) throws ScoreProviderException {
		return getScores().get(i);
	}

	@Override
	public Score getScoreById(int id) throws ScoreProviderException {
		List<Score> scores = getScores();
		for (Score score : scores) {
			if (score.getId() == id) {
				return score;
			}
		}
		return null;
	}
}
