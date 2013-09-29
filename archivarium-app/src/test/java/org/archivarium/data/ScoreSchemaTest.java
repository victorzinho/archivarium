package org.archivarium.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.archivarium.Score;
import org.archivarium.impl.DefaultScore;
import org.junit.Test;

public class ScoreSchemaTest {
	private static final int[] fieldIndexes = new int[] { ScoreSchema.TITLE,
			ScoreSchema.COMPOSER, ScoreSchema.GENRE, };

	@Test
	public void accepts() throws Exception {
		ScoreSchema schema = mockSchema();

		Score score = new DefaultScore();
		score.setTitle("title");
		assertTrue(schema.matches(score, "ti"));
	}

	@Test
	public void notAccepts() {
		ScoreSchema schema = mockSchema();

		Score score = new DefaultScore();
		score.setTitle("title");
		assertFalse(schema.matches(score, "other"));
	}

	@Test
	public void notAcceptsIgnoredField() {
		ScoreSchema schema = mockSchema();

		Score score = new DefaultScore();
		score.setLanguage("english");
		assertFalse(schema.matches(score, "eng"));
	}

	@Test
	public void getFieldCount() {
		ScoreSchema schema = mockSchema();
		assertEquals(fieldIndexes.length, schema.getFieldCount());
	}

	@Test
	public void getStringValue() {
		ScoreSchema schema = mockSchema();
		Score score = new DefaultScore();
		score.setTitle("Title");
		score.setComposer("Composer");
		score.setGenre("Genre");

		assertEquals("Title", schema.getValue(score, 0));
		assertEquals("Composer", schema.getValue(score, 1));
		assertEquals("Genre", schema.getValue(score, 2));
	}

	@Test
	public void getNullValue() {
		ScoreSchema schema = mockSchema();
		Score score = new DefaultScore();
		score.setTitle("Title");

		assertEquals("Title", schema.getValue(score, 0));
		assertNull(schema.getValue(score, 1));
		assertNull(schema.getValue(score, 2));
	}

	@Test
	public void getInvalidIndex() {
		try {
			mockSchema().getValue(new DefaultScore(), -5);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void createScore() {
		String[] values = { "Title", "Composer", "Genre" };
		Score score = mockSchema().createScore(values);
		assertEquals("Title", score.getTitle());
		assertEquals("Composer", score.getComposer());
		assertEquals("Genre", score.getGenre());
	}

	@Test
	public void updateScore() {
		String[] values = { "Title 2", "Composer 2", "Genre 2" };
		Score score = new DefaultScore();
		score.setTitle("Title");
		score.setComposer("Composer");
		score.setGenre("Genre");

		mockSchema().updateScore(score, values);
		assertEquals("Title 2", score.getTitle());
		assertEquals("Composer 2", score.getComposer());
		assertEquals("Genre 2", score.getGenre());
	}

	@Test
	public void updateScoreWithNullValues() {
		String[] values = { null, "Composer 2", null };
		Score score = new DefaultScore();
		score.setTitle("Title");
		score.setComposer("Composer");
		score.setGenre("Genre");

		mockSchema().updateScore(score, values);
		assertNull(score.getTitle());
		assertEquals("Composer 2", score.getComposer());
		assertNull(score.getGenre());
	}

	@Test
	public void updateScoreInvalidValueArray() {
		Score score = new DefaultScore();

		try {
			mockSchema().updateScore(score, null);
			fail();
		} catch (IllegalArgumentException e) {
		}

		try {
			String[] values = new String[] { "Title", "Composer" };
			mockSchema().updateScore(score, values);
			fail();
		} catch (IllegalArgumentException e) {
		}
	}

	private ScoreSchema mockSchema() {
		return new ScoreSchema(fieldIndexes);
	}
}
