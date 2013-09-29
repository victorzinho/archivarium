package org.archivarium.data;

import geomatico.events.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.ImageIcon;

import org.archivarium.ArchivariumConfig;
import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.ui.UIFactory;
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

	@Inject
	private EventBus eventBus;
	@Inject
	private UIFactory uiFactory;

	@Inject
	private ArchivariumConfig config;

	private ScoreProvider provider;
	private ScoreSchema schema;

	@Inject
	public ScoreProviderDataSource(@Assisted ScoreProvider provider,
			@Assisted ScoreSchema schema) {
		this.provider = provider;
		this.schema = schema;

		this.criteria = new String[schema.getFieldCount()];
		this.uniqueValues = new String[schema.getFieldCount()][];
	}

	@Override
	public int getColumnCount() {
		// +1 for the icon column
		return schema.getFieldCount() + 1;
	}

	@Override
	public String getColumnName(int column) throws IllegalArgumentException {
		return (column == 0) ? "" : schema.getFieldName(column - 1);
	}

	@Override
	public ScoreRow getRowById(int id) throws DataHandlerException {
		try {
			return new ScoreRow(uiFactory, provider.getScoreById(id), schema);
		} catch (ScoreProviderException e) {
			throw new DataHandlerException(e);
		}
	}

	@Override
	public String[] getUniqueValues(int column) {
		// -1 for the icon column
		return uniqueValues[column - 1];
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
			this.scores = provider.search(schema.createScore(criteria), true);
		} else {
			this.scores = provider.getScores();
		}

		// Update relative URLs
		for (Score score : this.scores) {
			String url = score.getURL();
			if (url != null && !new File(url).isAbsolute()) {
				String newUrl = new File(config.getScoreRootDir(), url)
						.getAbsolutePath();
				score.setURL(newUrl);
			}
		}

		// Update 'filtered', which contains only the 'scores' that
		// contain the text in any field
		this.filtered = filter(this.scores);

		// Update 'rows' from 'filtered'
		this.rows = new ScoreRow[filtered.size()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new ScoreRow(uiFactory, filtered.get(i), schema);
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

		List<Score> allScoresFiltered = filter(provider.getScores());
		for (int i = 0; i < uniqueValues.length; i++) {
			// We don't update the unique values for the column
			// that fired the change
			if (i == column) {
				continue;
			}

			List<Score> scores;
			// If we have only one criterion, we get the unique values for all
			// available scores, not only the ones we filtered
			if (numCriteria == 1 && index == i) {
				scores = allScoresFiltered;
			} else {
				scores = this.filtered;
			}

			List<String> list = new ArrayList<String>();
			for (Score score : scores) {
				Object value = schema.getValue(score, i);
				if (value == null) {
					continue;
				}

				String string = value.toString();
				if (string.length() > 0 && !list.contains(string)) {
					list.add(string);
				}
			}

			Collections.sort(list);
			uniqueValues[i] = list.toArray(new String[list.size()]);
		}
	}

	private List<Score> filter(List<Score> scores) {
		if (text == null || text.length() == 0) {
			return scores;
		}

		List<Score> ret = new ArrayList<Score>();
		for (Score score : scores) {
			if (schema.matches(score, text)) {
				ret.add(score);
			}
		}
		return ret;
	}

	public void setCategory(String category, int column)
			throws ScoreProviderException {
		// -1 for the icon column
		int col = column - 1;

		// Previous and new category are null
		if (criteria[col] == null && category == null) {
			return;
		}

		// Previous and new category are the same
		if (criteria[col] != null && category != null
				&& criteria[col].equals(category)) {
			return;
		}

		this.criteria[col] = category;
		update(col);
	}

	public void setText(String text) throws ScoreProviderException {
		this.text = text.toLowerCase();
		update();
	}
}
