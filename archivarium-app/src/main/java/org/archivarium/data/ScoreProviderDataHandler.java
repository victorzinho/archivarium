package org.archivarium.data;

import geomatico.events.EventBus;

import java.awt.Desktop;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.events.ScoreAddedEvent;
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.ui.ScoreEditionPanel;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.RowEditionPanel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ScoreProviderDataHandler implements DataHandler<ScoreRow> {
	private RowEditionPanel<ScoreRow> editPanel;

	private ScoreProvider provider;

	@Inject
	public ScoreProviderDataHandler(@Assisted ScoreProvider provider) {
		this.provider = provider;
	}

	@Inject
	private EventBus eventBus;

	@Override
	public void delete(int id) throws DataHandlerException {
		try {
			Score score = getScore(id);
			provider.deleteScore(score);
			eventBus.fireEvent(new ScoreDeletedEvent(score));
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	@Override
	public void update(ScoreRow row) throws DataHandlerException {
		try {
			provider.modifyScore(row.getScore());
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	@Override
	public void open(int id) throws DataHandlerException {
		String url;
		try {
			url = getScore(id).getURL();
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}

		if (url == null) {
			throw new DataHandlerException("URL cannot be null");
		}

		URI uri;
		try {
			try {
				uri = new URL(url).toURI();
			} catch (MalformedURLException e) {
				uri = new URI("file://" + url);
			}
		} catch (URISyntaxException e) {
			throw new DataHandlerException(e);
		}

		try {
			doOpen(uri);
		} catch (IOException e) {
			throw new DataHandlerException(e);
		}
	}

	void doOpen(URI uri) throws IOException {
		Desktop.getDesktop().browse(uri);
	}

	private Score getScore(int id) throws ScoreProviderException {
		return provider.getScoreById(id);
	}

	@Override
	public void add(ScoreRow row) throws DataHandlerException {
		try {
			Score score = row.getScore();
			provider.addScore(score);
			eventBus.fireEvent(new ScoreAddedEvent(score));
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	public RowEditionPanel<ScoreRow> getRowEditionPanel() {
		if (editPanel == null) {
			this.editPanel = new ScoreEditionPanel();
		}
		return editPanel;
	}
}
