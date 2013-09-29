package org.archivarium.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;

import javax.swing.ImageIcon;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.impl.DefaultScore;
import org.archivarium.inject.ScoreDataFactory;
import org.archivarium.ui.data.Row;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Inject;

public class ScoreProviderDataSourceTest extends AbstractArchivariumTest {
	@Inject
	private ScoreDataFactory factory;

	private ScoreSchema schema;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		schema = new ScoreSchema(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 });
	}

	@Test
	public void columnNames() throws Exception {
		ScoreProviderDataSource source = factory.createSource(
				mock(ScoreProvider.class), schema);
		int n = source.getColumnCount();

		try {
			source.getColumnName(-1);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}

		for (int i = 0; i < n; i++) {
			source.getColumnName(i);
		}

		try {
			source.getColumnName(n);
			fail();
		} catch (IllegalArgumentException e) {
			// do nothing
		}
	}

	@Test
	public void getRow() throws Exception {
		// Score values
		int id = 42;
		String title = "Score 1";
		String composer = "Composer";
		String description = "Description";
		String edition = "Edition";
		String location = "Location";
		String genre = "Genre";

		// Mock score
		Score score = mock(Score.class);
		when(score.getId()).thenReturn(id);
		when(score.getTitle()).thenReturn(title);
		when(score.getComposer()).thenReturn(composer);
		when(score.getDescription()).thenReturn(description);
		when(score.getEdition()).thenReturn(edition);
		when(score.getLocation()).thenReturn(location);
		when(score.getGenre()).thenReturn(genre);

		// Mock provider
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		// Get row
		ScoreProviderDataSource source = factory.createSource(provider, schema);
		Row row = source.getRowById(id);

		// Test (+1 for icon column)
		assertEquals(title, row.getData(ScoreSchema.TITLE + 1));
		assertEquals(composer, row.getData(ScoreSchema.COMPOSER + 1));
		assertEquals(description, row.getData(ScoreSchema.DESCRIPTION + 1));
		assertEquals(edition, row.getData(ScoreSchema.EDITION + 1));
		assertEquals(location, row.getData(ScoreSchema.LOCATION + 1));
		assertEquals(genre, row.getData(ScoreSchema.GENRE + 1));
		assertEquals(id, row.getId());
	}

	@Test
	public void getRowNullURL() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider(null, "pdf");
		Row row = handler.getRowById(0);
		assertNull(row.getData(0));
	}

	@Test
	public void getRowNullFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score.pdf",
				null);
		Row row = handler.getRowById(0);
		assertNull(row.getData(0));
	}

	@Test
	public void getRowUnrecognizedFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score",
				"unknown_format");
		Row row = handler.getRowById(0);
		assertTrue(row.getData(0) instanceof String);
	}

	@Test
	public void getRowRecognizedFormat() throws Exception {
		ScoreProviderDataSource handler = mockScoreProvider("/tmp/score.sib",
				"sib");
		Row row = handler.getRowById(0);
		assertTrue(row.getData(0) instanceof ImageIcon);
	}

	@Test
	public void getRowAbsoluteUrl() throws Exception {
		File tmp = File.createTempFile("archivarium", ".pdf");
		tmp.delete();

		Score score = mock(Score.class);
		when(score.getURL()).thenReturn(tmp.getAbsolutePath());
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);
		ScoreProviderDataSource source = factory.createSource(provider, schema);

		assertEquals(tmp.getAbsolutePath(), source.getRowById(0).getScore()
				.getURL());
	}

	@Test
	public void getRowRelativeUrl() throws Exception {
		File tmp = File.createTempFile("archivarium", ".pdf");
		tmp.delete();
		when(config.getScoreRootDir()).thenReturn(tmp.getParentFile());

		Score score = new DefaultScore();
		score.setURL(tmp.getName());
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScores()).thenReturn(Collections.singletonList(score));
		when(provider.getScoreById(anyInt())).thenReturn(score);
		ScoreProviderDataSource source = factory.createSource(provider, schema);
		source.update();

		assertEquals(tmp.getAbsolutePath(), source.getRows()[0].getScore()
				.getURL());
	}

	@Test
	public void initializedWithoutData() throws Exception {
		Score score = new DefaultScore();
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScores()).thenReturn(Collections.singletonList(score));
		when(provider.getScoreById(anyInt())).thenReturn(score);
		ScoreProviderDataSource source = factory.createSource(provider, schema);
		assertNull(source.getRows());
		source.update();
		assertEquals(1, source.getRows().length);
	}

	private ScoreProviderDataSource mockScoreProvider(String url, String format)
			throws Exception {
		Score score = mock(Score.class);
		when(score.getId()).thenReturn(42);
		when(score.getURL()).thenReturn(url);
		when(score.getFormat()).thenReturn(format);

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		return factory.createSource(provider, schema);
	}
}
