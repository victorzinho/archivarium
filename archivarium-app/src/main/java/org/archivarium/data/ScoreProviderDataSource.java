package org.archivarium.data;

import geomatico.events.EventBus;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import org.archivarium.Launcher;
import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.impl.DefaultScore;
import org.archivarium.ui.data.DataHandlerException;
import org.archivarium.ui.data.DataSource;
import org.archivarium.ui.events.DataChangeEvent;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ScoreProviderDataSource implements DataSource<ScoreRow> {
	static final ImageIcon ICON_SIB = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/sib.png"));
	static final ImageIcon ICON_PDF = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/pdf.png"));
	static final ImageIcon ICON_DEFAULT = new ImageIcon(
			ScoreProviderDataSource.class.getResource("/icons/default.png"));

	private String[] criteria;
	private String text;

	private ScoreRow[] rows;
	private List<Score> scores, filtered;

	private String[][] uniqueValues;

	private EventBus eventBus;
	private ScoreProvider provider;

	@Inject
	public ScoreProviderDataSource(@Assisted ScoreProvider provider,
			EventBus eventBus) throws ScoreProviderException {
		this.provider = provider;
		this.criteria = new String[ScoreRow.COLUMNS.length];
		this.text = null;
		this.uniqueValues = new String[ScoreRow.COLUMNS.length][];
		this.eventBus = eventBus;
		update();
	}

	@Override
	public int getColumnCount() {
		return ScoreRow.COLUMNS.length;
	}

	@Override
	public String getColumnName(int column) throws IllegalArgumentException {
		if (column < 0 || column >= ScoreRow.COLUMNS.length) {
			throw new IllegalArgumentException("Invalid column index: "
					+ column);
		}

		return ScoreRow.COLUMNS[column];
	}

	@Override
	public ScoreRow getRowById(int id) throws DataHandlerException {
		try {
			return new ScoreRow(provider.getScoreById(id));
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	@Override
	public String[][] getUniqueValues() {
		return uniqueValues;
	}

	@Override
	public ScoreRow[] getRows() {
		return rows;
	}

	public void update() throws ScoreProviderException {
		update(-1);
	}

	/**
	 * Updates the current scores depending on the criteria and the text.
	 * 
	 * @param column
	 *            The column that fired the score change or -1 if none.
	 * @throws ScoreProviderException
	 *             if the scores cannot be obtained.
	 */
	private void update(int column) throws ScoreProviderException {
		boolean hasCriteria = false;
		for (String criterion : criteria) {
			if (criterion != null) {
				hasCriteria = true;
				break;
			}
		}

		// Update 'scores', which contains all scores matching the criteria
		if (hasCriteria) {
			Score model = new DefaultScore();
			model.setName(criteria[ScoreRow.COLUMN_INDEX_NAME]);
			model.setAuthor(criteria[ScoreRow.COLUMN_INDEX_AUTHOR]);
			model.setDescription(criteria[ScoreRow.COLUMN_INDEX_DESCRIPTION]);
			String instruments = criteria[ScoreRow.COLUMN_INDEX_INSTRUMENTS];
			if (instruments != null) {
				model.setInstruments(Arrays.asList(instruments.split(", ")));
			}
			model.setEdition(criteria[ScoreRow.COLUMN_INDEX_EDITION]);
			model.setLocation(criteria[ScoreRow.COLUMN_INDEX_LOCATION]);
			model.setGenre(criteria[ScoreRow.COLUMN_INDEX_GENRE]);

			this.scores = provider.search(model, true);
		} else {
			this.scores = provider.getScores();
		}

		// Update relative URLs
		for (Score score : this.scores) {
			String url = score.getURL();
			if (!new File(url).isAbsolute()) {
				String newUrl = Launcher.getScoreRootDirectory()
						+ File.separator + url;
				score.setURL(newUrl);
			}
		}

		// Update 'filtered', which contains only the 'scores' that
		// contain the text in any field
		this.filtered = new ScoreFilter(text).filter(this.scores);

		// Update 'rows' from 'filtered'
		this.rows = new ScoreRow[filtered.size()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new ScoreRow(filtered.get(i));
		}

		// Update unique values
		updateUniqueValues(column);

		eventBus.fireEvent(new DataChangeEvent(this));
	}

	private void updateUniqueValues(int column) throws ScoreProviderException {
		int numCriteria = 0;
		int index = -1;
		for (int i = 0; i < criteria.length; i++) {
			if (criteria[i] != null) {
				numCriteria++;
				index = i;
			}
		}

		ScoreFilter filter = new ScoreFilter(text);
		List<Score> allScoresFiltered = filter.filter(provider.getScores());

		for (int i = 0; i < uniqueValues.length; i++) {
			List<Score> scores;
			// If we have only one criterion, we get the unique values for all
			// available scores, not only the ones we filtered
			if (numCriteria == 1 && index == i) {
				scores = allScoresFiltered;
			} else {
				scores = this.filtered;
			}

			Set<String> set = new HashSet<String>();
			for (Score score : scores) {
				String value = ScoreRow.getString(score, i);
				if (value != null && value.length() > 0) {
					set.add(value);
				}
			}

			// We don't update the unique values for the column
			// that fired the change
			if (i != column) {
				uniqueValues[i] = set.toArray(new String[set.size()]);
			}
		}
	}

	public void setCategory(String category, int column)
			throws ScoreProviderException {
		// Previous and new category are null
		if (criteria[column] == null && category == null) {
			return;
		}

		// Previous and new category are the same
		if (criteria[column] != null && category != null
				&& criteria[column].equals(category)) {
			return;
		}

		this.criteria[column] = category;
		update(column);
	}

	public void setText(String text) throws ScoreProviderException {
		this.text = text.toLowerCase();
		update();
	}
}
