package org.archivarium.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.archivarium.Score;
import org.archivarium.ScoreProviderException;

public class EmbeddedH2ScoreProviderTest extends TestCase {
	@Override
	protected void setUp() throws Exception {
		super.setUp();

		String url = H2ScoreProvider.URL_PREFIX + getDatabase("scores");
		H2ScoreProvider.executeScript(
				getClass().getResourceAsStream("init_db.sql"), url);
	}

	public void testCreateProvider() throws Exception {
		// null database
		try {
			new H2ScoreProvider(null);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	public void testAlreadyExistsDatabase() throws Exception {
		assertFalse(H2ScoreProvider.alreadyExistsDB(null));
		assertFalse(H2ScoreProvider.alreadyExistsDB(File
				.createTempFile("archivarium", "").getAbsolutePath()));
		assertTrue(H2ScoreProvider
				.alreadyExistsDB(getDatabase("scores")));
		assertTrue(H2ScoreProvider
				.alreadyExistsDB(getDatabase("wrong_schema")));
	}

	public void testInitializeDB() throws Exception {
		try {
			H2ScoreProvider.initializeDB(getDatabase("scores"));
			fail();
		} catch (ScoreProviderException e) {
		}

		String database = File.createTempFile("archivarium", "")
				.getAbsolutePath();
		H2ScoreProvider.initializeDB(database);

		// Check
		H2ScoreProvider provider = new H2ScoreProvider(database);
		DefaultScore score = new DefaultScore();
		score.setName("Name");
		List<String> instruments = new ArrayList<String>();
		instruments.add("Guitar");
		score.setInstruments(instruments);
		provider.addScore(score);
		assertEquals(1, provider.getScores().size());
		assertEquals(1, provider.getScores().get(0).getInstruments().size());
	}

	public void testGetScores() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));
		List<Score> scores = provider.getScores();
		assertEquals(2, scores.size());

		Score score = scores.get(0);
		assertEquals("Score 1", score.getName());
		assertEquals("Author 1", score.getAuthor());
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

	public void testAddScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));
		List<String> instruments = new ArrayList<String>();
		instruments.add("Guitar");
		DefaultScore score = new DefaultScore();
		score.setName("Score 1");
		score.setDescription("Test score");
		score.setAuthor("Author 1");
		score.setInstruments(instruments);
		score.setFormat("pdf");
		score.setURL("score.pdf");
		provider.addScore(score);

		score = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Piano");
		score.setName("Score 1");
		score.setAuthor("Anonymous");
		score.setInstruments(instruments);
		score.setFormat("Paper");
		score.setLocation("Desk drawer");
		provider.addScore(score);
	}

	public void testModifyScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));
		Score score = provider.getScores().get(0);
		score.setDescription(null);
		score.setName("Name changed");

		provider.modifyScore(score);

		assertEquals(score, provider.getScores().get(0));
	}

	public void testDeleteScore() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));
		List<Score> scoresPrev = provider.getScores();
		provider.deleteScore(scoresPrev.get(0));
		List<Score> scoresAfter = provider.getScores();
		assertEquals(scoresPrev.size() - 1, scoresAfter.size());
	}

	public void testSearchStrict() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));

		DefaultScore model = new DefaultScore();
		model.setName("Score 1");
		List<Score> scores = provider.doSearchStrict(model);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getName());

		model = new DefaultScore();
		model.setName("Score ");
		scores = provider.doSearchStrict(model);
		assertEquals(0, scores.size());

		model = new DefaultScore();
		List<String> instruments = new ArrayList<String>();
		instruments.add("Violin");
		model.setInstruments(instruments);
		scores = provider.doSearchStrict(model);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getName());

		model = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Violin");
		instruments.add("Piano");
		model.setInstruments(instruments);
		scores = provider.doSearchStrict(model);
		assertEquals(0, scores.size());
	}

	public void testSearchNotStrict() throws Exception {
		H2ScoreProvider provider = new H2ScoreProvider(
				getDatabase("scores"));

		DefaultScore model = new DefaultScore();
		model.setName("Score 1");
		List<Score> scores = provider.doSearchNotStrict(model);
		assertEquals(2, scores.size());
		assertEquals("Score 1", scores.get(0).getName());
		assertEquals("Score 2", scores.get(1).getName());

		model = new DefaultScore();
		model.setEdition("1992 editn");
		scores = provider.doSearchNotStrict(model);
		assertEquals(1, scores.size());
		assertEquals("Score 2", scores.get(0).getName());

		model = new DefaultScore();
		List<String> instruments = new ArrayList<String>();
		instruments.add("Viol");
		model.setInstruments(instruments);
		scores = provider.doSearchNotStrict(model);
		assertEquals(1, scores.size());
		assertEquals("Score 1", scores.get(0).getName());

		model = new DefaultScore();
		instruments = new ArrayList<String>();
		instruments.add("Violin");
		instruments.add("Piano");
		model.setInstruments(instruments);
		scores = provider.doSearchNotStrict(model);
		assertEquals(0, scores.size());
	}

	private String getDatabase(String name) {
		String database = getClass().getResource(name + ".h2.db").getFile();
		database = database.replaceAll("\\.h2\\.db", "");
		return database;
	}
}
