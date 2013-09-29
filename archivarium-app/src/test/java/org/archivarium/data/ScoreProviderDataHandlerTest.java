package org.archivarium.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.events.ScoreAddedEvent;
import org.archivarium.events.ScoreAddedHandler;
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.events.ScoreDeletedHandler;
import org.archivarium.impl.DefaultScore;
import org.archivarium.inject.ScoreDataFactory;
import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandlerException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ScoreProviderDataHandlerTest extends AbstractArchivariumTest {
	@Inject
	private ScoreDataFactory factory;

	@Inject
	private EventBus bus;

	@Inject
	private UIFactory uiFactory;

	private ScoreSchema schema;

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();
		schema = new ScoreSchema(new int[] { 0, 1, 2, 3, 4, 5, 6, 7 });
	}

	@Test
	public void openProviderException() throws Exception {
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenThrow(
				mock(ScoreProviderException.class));

		try {
			factory.createHandler(provider, schema).open(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}
	}

	@Test
	public void openNull() throws Exception {
		try {
			mockHandlerForOpen(null).open(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}
	}

	@Test
	public void openNonExisting() throws Exception {
		ScoreProviderDataHandler handler = mockHandlerForOpen("file:///tmp/non_existing");
		doThrow(IOException.class).when(handler).doOpen(any(URI.class));

		try {
			handler.open(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}
	}

	@Test
	public void openRelativePath() throws Exception {
		File file = new File("non_existing_file");
		file.createNewFile();
		mockHandlerForOpen(file.getPath()).open(0);
		file.delete();
	}

	@Test
	public void openAbsolutePath() throws Exception {
		File file = File.createTempFile("archivarium", ".txt");
		ScoreProviderDataHandler handler = mockHandlerForOpen(file
				.getAbsolutePath());
		handler.open(0);
		file.delete();
	}

	@Test
	public void openValidURI() throws Exception {
		File file = File.createTempFile("archivarium", ".txt");
		ScoreProviderDataHandler handler = mockHandlerForOpen(file.toURI()
				.toASCIIString());
		handler.open(0);
		file.delete();
	}

	@Test
	public void removeNonExisting() throws Exception {
		ScoreDeletedHandler eventHandler = mock(ScoreDeletedHandler.class);
		bus.addHandler(ScoreDeletedEvent.class, eventHandler);

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenThrow(
				mock(ScoreProviderException.class));

		try {
			factory.createHandler(provider, schema).delete(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}

		verify(eventHandler, never()).deleted(any(Score.class));
	}

	@Test
	public void remove() throws Exception {
		ScoreDeletedHandler eventHandler = mock(ScoreDeletedHandler.class);
		bus.addHandler(ScoreDeletedEvent.class, eventHandler);

		Score score = mock(Score.class);
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		factory.createHandler(provider, schema).delete(0);

		verify(eventHandler).deleted(same(score));
		verify(provider).deleteScore(same(score));
	}

	@Test
	public void add() throws Exception {
		ScoreAddedHandler eventHandler = mock(ScoreAddedHandler.class);
		bus.addHandler(ScoreAddedEvent.class, eventHandler);

		Score score = mock(Score.class);
		ScoreProvider provider = mock(ScoreProvider.class);
		factory.createHandler(provider, schema).add(
				new ScoreRow(uiFactory, score, schema));

		verify(eventHandler).added(same(score));
		verify(provider).addScore(same(score));
	}

	@Test
	public void update() throws Exception {
		Score score = mock(Score.class);
		ScoreProvider provider = mock(ScoreProvider.class);
		factory.createHandler(provider, schema).update(
				new ScoreRow(uiFactory, score, schema));
		verify(provider).modifyScore(same(score));
	}

	@Test
	public void addScoreInRootDirectory() throws Exception {
		testScoreInRootDirectory("add");
	}

	@Test
	public void updateScoreInRootDirectory() throws Exception {
		testScoreInRootDirectory("update");
	}

	private void testScoreInRootDirectory(String action) throws Exception {
		File tmp = File.createTempFile("archivarium", "");
		tmp.delete();
		when(config.getScoreRootDir()).thenReturn(tmp.getParentFile());

		String scoreName = "score.pdf";
		Score score = new DefaultScore();
		score.setURL(new File(config.getScoreRootDir(), scoreName)
				.getAbsolutePath());
		ScoreProvider provider = mock(ScoreProvider.class);

		ArgumentCaptor<Score> arg = ArgumentCaptor.forClass(Score.class);

		if (action.equals("add")) {
			factory.createHandler(provider, schema).add(
					new ScoreRow(uiFactory, score, schema));
			verify(provider).addScore(arg.capture());
		} else {
			factory.createHandler(provider, schema).update(
					new ScoreRow(uiFactory, score, schema));
			verify(provider).modifyScore(arg.capture());
		}

		assertEquals(scoreName, arg.getValue().getURL());
	}

	private ScoreProviderDataHandler mockHandlerForOpen(String url)
			throws Exception {
		Score score = mock(Score.class);
		when(score.getURL()).thenReturn(url);

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		ScoreProviderDataHandler handler = spy(factory.createHandler(provider,
				schema));

		// We override the doOpen method to avoid the opening of the empty files
		// while testing
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(handler).doOpen(any(URI.class));
		return handler;
	}
}
