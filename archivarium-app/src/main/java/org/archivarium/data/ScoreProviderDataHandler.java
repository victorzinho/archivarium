package org.archivarium.data;

import geomatico.events.EventBus;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.archivarium.ArchivariumConfig;
import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.events.ScoreAddedEvent;
import org.archivarium.events.ScoreDeletedEvent;
import org.archivarium.ui.ScoreEditionPanel;
import org.archivarium.ui.UIFactory;
import org.archivarium.ui.data.DataHandler;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.RowEditionPanel;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ScoreProviderDataHandler implements DataHandler<ScoreRow> {
	private RowEditionPanel<ScoreRow> updatePanel;
	private RowEditionPanel<ScoreRow> addPanel;
	private ScoreProvider provider;
	private ScoreSchema schema;

	@Inject
	private UIFactory factory;

	@Inject
	private ArchivariumConfig config;

	@Inject
	public ScoreProviderDataHandler(@Assisted ScoreProvider provider,
			@Assisted ScoreSchema schema) {
		this.provider = provider;
		this.schema = schema;
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
			Score score = updateScoreUrl(row.getScore());
			provider.modifyScore(score);
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
				uri = new File(url).toURI();
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
			Score score = updateScoreUrl(row.getScore());
			provider.addScore(score);
			eventBus.fireEvent(new ScoreAddedEvent(score));
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	private Score updateScoreUrl(Score score) {
		File rootDir = config.getScoreRootDir();
		if (rootDir == null) {
			return score;
		}

		String root = rootDir.getAbsolutePath();
		if (!root.endsWith(File.separator)) {
			root += File.separator;
		}

		String url = score.getURL();
		if (url != null && url.startsWith(root)) {
			score.setURL(url.replace(root, ""));
		}

		return score;
	}

	@Override
	public RowEditionPanel<ScoreRow> getAddPanel() {
		if (addPanel == null) {
			this.addPanel = new ScoreEditionPanel(schema, factory);
		}
		return addPanel;
	}

	@Override
	public RowEditionPanel<ScoreRow> getUpdatePanel() {
		if (updatePanel == null) {
			this.updatePanel = new ScoreEditionPanel(schema, factory);
		}
		return updatePanel;
	}

	public static void main(String[] args) {
		System.out.println(new File("C:\\Prueba\\una prueba.pdf").toURI());
	}
}
