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
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.ui.data.TableDataException;
import org.archivarium.ui.data.DataHandler;

public class ScoreProviderDataHandler implements DataHandler {
	private ScoreProvider provider;

	public ScoreProviderDataHandler(ScoreProvider provider) {
		this.provider = provider;
	}

	@Override
	public void remove(int id) throws TableDataException {
		try {
			Score score = getScore(id);
			provider.deleteScore(score);
			EventBus.getInstance().fireEvent(new ScoreDeletedEvent(score));
		} catch (ScoreProviderException e) {
			throw new TableDataException(e);
		}
	}

	@Override
	public void update(int id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void open(int id) throws TableDataException {
		String url;
		try {
			url = getScore(id).getURL();
		} catch (ScoreProviderException e) {
			throw new TableDataException(e);
		}

		if (url == null) {
			throw new TableDataException("URL cannot be null");
		}

		URI uri;
		try {
			try {
				uri = new URL(url).toURI();
			} catch (MalformedURLException e) {
				uri = new URI("file://" + url);
			}
		} catch (URISyntaxException e) {
			throw new TableDataException(e);
		}

		try {
			doOpen(uri);
		} catch (IOException e) {
			throw new TableDataException(e);
		}
	}

	void doOpen(URI uri) throws IOException {
		Desktop.getDesktop().browse(uri);
	}

	private Score getScore(int id) throws ScoreProviderException {
		return provider.getScoreById(id);
	}
}
