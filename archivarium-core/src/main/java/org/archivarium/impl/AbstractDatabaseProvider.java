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

import org.apache.log4j.Logger;
import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;

public abstract class AbstractDatabaseProvider implements ScoreProvider {
	private static final Logger logger = Logger
			.getLogger(AbstractDatabaseProvider.class);

	private static final String COLUMN_NAME_NAME = "name";
	private static final String COLUMN_NAME_AUTHOR = "author";
	private static final String COLUMN_NAME_DESCRIPTION = "description";
	private static final String COLUMN_NAME_INSTRUMENTS = "instruments";
	private static final String COLUMN_NAME_EDITION = "edition";
	private static final String COLUMN_NAME_URL = "url";
	private static final String COLUMN_NAME_FORMAT = "format";
	private static final String COLUMN_NAME_LOCATION = "location";
	private static final String COLUMN_NAME_GENRE = "genre";

	private static final String PERSISTENCE_UNIT_NAME = "org.archivarium";
	private static final String HIBERNATE_PROP_URL = "hibernate.connection.url";

	private EntityManager entityManager;

	private EntityManager getEntityManager() {
		if (entityManager == null) {
			Properties properties = new Properties();
			properties.put(HIBERNATE_PROP_URL, getDatabaseUrl());
			entityManager = Persistence.createEntityManagerFactory(
					PERSISTENCE_UNIT_NAME, properties).createEntityManager();
		}
		return entityManager;
	}

	@Override
	public List<Score> getScores() throws ScoreProviderException {
		try {
			getEntityManager().getTransaction().begin();
			TypedQuery<Score> query = getEntityManager().createQuery(
					"SELECT s FROM " + DefaultScore.class.getSimpleName()
							+ " s", Score.class);
			List<Score> ret = query.getResultList();
			getEntityManager().getTransaction().commit();
			return ret;
		} catch (Exception e) {
			String message = "Cannot get scores from database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public void addScore(Score score) throws ScoreProviderException {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().persist(score);
			getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot add score to database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public void modifyScore(Score score) throws ScoreProviderException {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().merge(score);
			getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot add score to database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public void deleteScore(Score score) throws ScoreProviderException {
		try {
			getEntityManager().getTransaction().begin();
			getEntityManager().remove(score);
			getEntityManager().getTransaction().commit();
		} catch (Exception e) {
			String message = "Cannot delete score from database";
			logger.error(message, e);
			throw new ScoreProviderException(message, e);
		}
	}

	@Override
	public List<Score> search(Score model, boolean strict)
			throws ScoreProviderException {
		return strict ? doSearchStrict(model) : doSearchNotStrict(model);
	}

	private List<Score> doSearchStrict(Score model)
			throws ScoreProviderException {
		getEntityManager().getTransaction().begin();
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();

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
		updatePredicates(model.getEdition(), COLUMN_NAME_EDITION, predicates,
				builder, from);
		updatePredicates(model.getURL(), COLUMN_NAME_URL, predicates, builder,
				from);
		updatePredicates(model.getFormat(), COLUMN_NAME_FORMAT, predicates,
				builder, from);
		updatePredicates(model.getLocation(), COLUMN_NAME_LOCATION, predicates,
				builder, from);
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
		List<Score> scores = getEntityManager().createQuery(criteria)
				.getResultList();
		getEntityManager().getTransaction().commit();
		return scores;
	}

	private void updatePredicates(String value, String columnName,
			List<Predicate> predicates, CriteriaBuilder builder,
			Root<DefaultScore> from) {
		if (value != null) {
			Path<String> column = from.<String> get(columnName);
			predicates.add(builder.equal(column, value));
		}
	}

	private List<Score> doSearchNotStrict(Score model)
			throws ScoreProviderException {
		List<Score> scores = getScores();

		List<Score> ret = new ArrayList<Score>();
		ScoreComparator comparator = new ScoreComparator();
		for (Score score : scores) {
			if (comparator.matches(model, score)) {
				ret.add(score);
			}
		}

		return ret;
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

	protected void executeScript(InputStream is, String connectionUrl)
			throws SQLException, IOException {
		Connection connection = DriverManager.getConnection(connectionUrl);
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

	protected abstract String getDatabaseUrl();
}
