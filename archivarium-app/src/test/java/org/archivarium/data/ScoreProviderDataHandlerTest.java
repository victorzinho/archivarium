package org.archivarium.data;

import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import geomatico.events.EventBus;

import java.io.File;
import java.net.URI;

import javax.inject.Inject;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.events.ScoreDeletedHandler;
import org.archivarium.inject.ScoreDataFactory;
import org.archivarium.ui.data.DataHandlerException;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

public class ScoreProviderDataHandlerTest extends AbstractArchivariumTest {
	@Inject
	private ScoreDataFactory factory;

	@Inject
	private EventBus bus;

	@Test
	public void openProviderException() throws Exception {
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenThrow(
				mock(ScoreProviderException.class));

		try {
			factory.createHandler(provider).open(0);
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
		try {
			mockHandlerForOpen("file:///tmp/non_existing").open(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}
	}

	@Test
	public void openRelativePath() throws Exception {
		File file = new File("test_file.archivarium");
		file.createNewFile();

		try {
			mockHandlerForOpen(file.getPath()).open(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}

		file.delete();
	}

	@Test
	public void openAbsolutePath() throws Exception {
		File file = File.createTempFile("archivarium", ".txt");

		ScoreProviderDataHandler handler = mockHandlerForOpen(file
				.getAbsolutePath());

		// We override the doOpen method to avoid the opening of the empty files
		// while testing
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(handler).doOpen(any(URI.class));
		handler.open(0);
		file.delete();
	}

	@Test
	public void openValidURI() throws Exception {
		File file = File.createTempFile("archivarium", ".txt");

		ScoreProviderDataHandler handler = mockHandlerForOpen("file://"
				+ file.getAbsolutePath());

		// We override the doOpen method to avoid the opening of the empty files
		// while testing
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				return null;
			}
		}).when(handler).doOpen(any(URI.class));
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
			factory.createHandler(provider).delete(0);
			fail();
		} catch (DataHandlerException e) {
			// do nothing
		}

		verify(eventHandler, never()).deleted(any(Score.class));
	}

	@Test
	public void testremove() throws Exception {
		ScoreDeletedHandler eventHandler = mock(ScoreDeletedHandler.class);
		bus.addHandler(ScoreDeletedEvent.class, eventHandler);

		Score score = mock(Score.class);
		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		factory.createHandler(provider).delete(0);

		verify(eventHandler).deleted(same(score));
	}

	private ScoreProviderDataHandler mockHandlerForOpen(String url)
			throws Exception {
		Score score = mock(Score.class);
		when(score.getURL()).thenReturn(url);

		ScoreProvider provider = mock(ScoreProvider.class);
		when(provider.getScoreById(anyInt())).thenReturn(score);

		return spy(factory.createHandler(provider));
	}
}
