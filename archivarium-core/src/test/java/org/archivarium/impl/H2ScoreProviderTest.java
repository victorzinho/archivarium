package org.archivarium.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.archivarium.Score;
import org.archivarium.ScoreProviderException;
import org.junit.Before;
import org.junit.Test;

public class H2ScoreProviderTest {
	@Before
	public void setUp() throws Exception {
		Connection connection = DriverManager.getConnection("jdbc:h2:file:"
				+ getDatabase("scores"));
		Statement statement = connection.createStatement();
		execute("/create_db.sql", statement);
		execute("init_db.sql", statement);
		statement.close();
		connection.close();
	}

	private void execute(String resource, Statement statement) throws Exception {
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				getClass().getResourceAsStream(resource)));
		String line = reader.readLine();
		while (line != null) {
			statement.execute(line);
			line = reader.readLine();
		}
	}

	@Test
	public void nullDatabase() throws Exception {
		// null database
		try {
			new H2ScoreProvider(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void invalidDatabase() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("wrong_schema"));
		try {
			provider.getScores();
			fail();
		} catch (ScoreProviderException e) {
			// do nothing
		}
	}

	@Test
	public void nonExistingDatabase() throws Exception {
		File file = File.createTempFile("archivarium", "");
		file.delete();

		String path = file.getAbsolutePath();
		File dbFile = new File(path + ".h2.db");
		assertFalse(dbFile.exists());
		H2ScoreProvider provider = new H2ScoreProvider(path);
		assertTrue(dbFile.exists());
		assertEquals(0, provider.getScores().size());

		file.delete();
		dbFile.delete();
	}

	@Test
	public void getScores() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));
		List<Score> scores = provider.getScores();
		assertEquals(2, scores.size());

		Score score = scores.get(0);
		assertEquals("Score 1", score.getTitle());
		assertEquals("Author 1", score.getComposer());
		assertEquals("Score description", score.getDescription());
		assertEquals("Custom edition", score.getEdition());
		assertEquals(4, score.getInstruments().size());
		assertEquals("Violin", score.getInstruments().get(0));
		assertEquals("Viola", score.getInstruments().get(1));
		assertEquals("Cello", score.getInstruments().get(2));
		assertEquals("Contrabass", score.getInstruments().get(3));
		assertEquals("media/scores/score.pdf", score.getURL());
		assertEquals("pdf", score.getFormat());
		assertNull(score.getLocation());
		assertNull(score.getGenre());
	}

	@Test
	public void getScoreById() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));
		Score score = provider.getScoreById(0);
		assertEquals("Score 1", score.getTitle());
	}

	@Test
	public void addScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));
		List<String> instruments = new ArrayList<String>();
		instruments.add("Guitar");
		DefaultScore score = new DefaultScore();
		score.setTitle("Score 1");
		score.setDescription("Test score");
		score.setComposer("Author 1");
		score.setInstruments(instruments);
		score.setFormat("pdf");
		score.setURL("score.pdf");
		provider.addScore(score);

		score = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Piano");
		score.setTitle("Score 1");
		score.setComposer("Anonymous");
		score.setInstruments(instruments);
		score.setFormat("Paper");
		score.setLocation("Desk drawer");
		provider.addScore(score);
	}

	@Test
	public void modifyScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));
		Score score = provider.getScores().get(0);
		score.setDescription(null);
		score.setTitle("Name changed");

		provider.modifyScore(score);

		assertEquals(score, provider.getScores().get(0));
	}

	@Test
	public void deleteScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));
		List<Score> scoresPrev = provider.getScores();
		provider.deleteScore(scoresPrev.get(0));
		List<Score> scoresAfter = provider.getScores();
		assertEquals(scoresPrev.size() - 1, scoresAfter.size());
	}

	@Test
	public void searchStrict() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));

		DefaultScore model = new DefaultScore();
		model.setTitle("Score 1");
		List<Score> scores = provider.search(model, true);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getTitle());

		model = new DefaultScore();
		model.setTitle("Score ");
		scores = provider.search(model, true);
		assertEquals(0, scores.size());

		model = new DefaultScore();
		List<String> instruments = new ArrayList<String>();
		instruments.add("Violin");
		model.setInstruments(instruments);
		scores = provider.search(model, true);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getTitle());

		model = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Violin");
		instruments.add("Piano");
		model.setInstruments(instruments);
		scores = provider.search(model, true);
		assertEquals(0, scores.size());
	}

	@Test
	public void searchNotStrict() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(getDatabase("scores"));

		DefaultScore model = new DefaultScore();
		model.setTitle("Score 1");
		List<Score> scores = provider.search(model, false);
		assertEquals(2, scores.size());
		assertEquals("Score 1", scores.get(0).getTitle());
		assertEquals("Score 2", scores.get(1).getTitle());

		model = new DefaultScore();
		model.setEdition("1992 editn");
		scores = provider.search(model, false);
		assertEquals(1, scores.size());
		assertEquals("Score 2", scores.get(0).getTitle());

		model = new DefaultScore();
		List<String> instruments = new ArrayList<String>();
		instruments.add("Viol");
		model.setInstruments(instruments);
		scores = provider.search(model, false);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getTitle());

		model = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Violin");
		instruments.add("Piano");
		model.setInstruments(instruments);
		scores = provider.search(model, false);
		assertEquals(0, scores.size());
	}

	private String getDatabase(String name) {
		String database = getClass().getResource(name + ".h2.db").getFile();
		database = database.replaceAll("\\.h2\\.db", "");
		return database;
	}
}
