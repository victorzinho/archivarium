package org.archivarium.data;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import junit.framework.TestCase;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.ui.data.Row;
import org.archivarium.ui.data.TableDataException;

public class ScoreProviderDataSourceTest extends TestCase {

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		EventBus.getInstance().removeAllHandlers();
	}

	public void testColumnNames() throws Exception {
		ScoreProvider provider = mock(ScoreProvider.class);

		ScoreProviderDataSource handler = new ScoreProviderDataSource(provider);
		int n = handler.getColumnCount();

		try {
			handler.getColumnName(-1);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}

		for (int i = 0; i < n; i++) {
			handler.getColumnName(i);
		}

		try {
			handler.getColumnName(n);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

	public void testGetRowException() throws Exception {
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenThrow(
				new ScoreProviderException());

		ScoreProviderDataSource handler = new ScoreProviderDataSource(provider);
		try {
			handler.getRowById(0);
			fail();
		} catch (TableDataException e) {
			// do nothing
		}
	}

	public void testGetRow() throws Exception {
		// Score values
		int id = 42;
		String name = "Score 1";
		String author = "Author";
		String description = "Description";
		String edition = "Edition";
		String location = "Location";
		String genre = "Genre";

		// Mock score
		Score score = mock(Score.class);
		when(score.getId()).thenReturn(id);
		when(score.getName()).thenReturn(name);
		when(score.getAuthor()).thenReturn(author);
		when(score.getDescription()).thenReturn(description);
		when(score.getEdition()).thenReturn(edition);
		when(score.getLocation()).thenReturn(location);
		when(score.getGenre()).thenReturn(genre);

		// Mock provider
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		// Get row
		ScoreProviderDataSource handler = new ScoreProviderDataSource(provider);
		Row row = handler.getRowById(id);

		// Test
		assertEquals(name,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_NAME));
		assertEquals(author,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_AUTHOR));
		assertEquals(description,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_DESCRIPTION));
		assertEquals(edition,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_EDITION));
		assertEquals(location,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_LOCATION));
		assertEquals(genre,
				row.getData(ScoreProviderDataSource.COLUMN_INDEX_GENRE));
		assertEquals(id, row.getId());
	}

	public void testGetRowNullURL() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider(null, "pdf");
		Row row = handler.getRowById(0);
		assertNull(row.getData(ScoreProviderDataSource.COLUMN_INDEX_FORMAT));
	}

	public void testGetRowNullFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score.pdf",
				null);
		Row row = handler.getRowById(0);
		assertNull(row.getData(ScoreProviderDataSource.COLUMN_INDEX_FORMAT));
	}

	public void testGetRowUnrecognizedFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score",
				"unknown_format");
		Row row = handler.getRowById(0);
		assertTrue(row.getData(ScoreProviderDataSource.COLUMN_INDEX_FORMAT) instanceof ImageIcon);
	}

	public void testGetRowRecognizedFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score.sib",
				"sib");
		Row row = handler.getRowById(0);
		assertTrue(row.getData(ScoreProviderDataSource.COLUMN_INDEX_FORMAT) instanceof ImageIcon);
	}

	public void testGetRowsNoCriteria() throws Exception {
		List<Score> scores = new ArrayList<Score>();
		scores.add(mockScore(0, "Score 1", "Author"));
		scores.add(mockScore(1, "Score 2", "Author"));
		scores.add(mockScore(2, "Score 3", "Another author"));

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScores()).thenReturn(scores);

		ScoreProviderDataSource handler = new ScoreProviderDataSource(provider);
		checkRows(handler.getRows(null), scores);
	}

	public void testGetRowsInvalidCriteria() throws Exception {
		List<Score> scores = new ArrayList<Score>();
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScores()).thenReturn(scores);

		try {
			new ScoreProviderDataSource(provider)
					.getRows(new String[] { "Score" });
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

	public void testGetRowsWithCriteria() throws Exception {
		List<Score> scores = new ArrayList<Score>();
		scores.add(mockScore(0, "Score 1", "Author"));
		scores.add(mockScore(1, "Score 2", "Author"));
		scores.add(mockScore(2, "Score 3", "Another author"));

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.search(any(Score.class), anyBoolean()))
				.thenReturn(scores);

		ScoreProviderDataSource handler = new ScoreProviderDataSource(provider);
		Row[] rows = handler.getRows(new String[] { "format", "name", "author",
				"description", "instruments", "edition", "location", "genre" });

		checkRows(rows, scores);
	}

	public void testGetRowsException() throws Exception {
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScores()).thenThrow(new ScoreProviderException());

		try {
			new ScoreProviderDataSource(provider).getRows(null);
			fail();
		} catch (TableDataException e) {
			// do nothing
		}
	}

	private ScoreProviderDataSource mockScoreProvider(String url, String format)
			throws Exception {
		Score score = mock(Score.class);
		when(score.getId()).thenReturn(42);
		when(score.getURL()).thenReturn(url);
		when(score.getFormat()).thenReturn(format);

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		return new ScoreProviderDataSource(provider);
	}

	private Score mockScore(int id, String name, String author) {
		Score score = mock(Score.class);
		when(score.getId()).thenReturn(id);
		when(score.getName()).thenReturn(name);
		when(score.getAuthor()).thenReturn(author);

		return score;
	}

	private void checkRows(Row[] rows, List<Score> scores) {
		assertEquals(scores.size(), rows.length);

		for (int i = 0; i < rows.length; i++) {
			int rowId = rows[i].getId();
			Object rowName = rows[i]
					.getData(ScoreProviderDataSource.COLUMN_INDEX_NAME);
			Object rowAuthor = rows[i]
					.getData(ScoreProviderDataSource.COLUMN_INDEX_AUTHOR);
			assertEquals(scores.get(i).getId(), rowId);
			assertEquals(scores.get(i).getName(), rowName);
			assertEquals(scores.get(i).getAuthor(), rowAuthor);
		}
	}
}
