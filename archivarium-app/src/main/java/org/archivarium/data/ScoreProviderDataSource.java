package org.archivarium.data;

import geomatico.events.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import org.archivarium.Score;
import org.archivarium.ScoreProvider;
import org.archivarium.ScoreProviderException;
import org.archivarium.impl.DefaultScore;
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

	private ScoreProvider provider;

	@Inject
	public ScoreProviderDataSource(@Assisted ScoreProvider provider)
			throws ScoreProviderException {
		this.provider = provider;
		this.criteria = new String[ScoreRow.COLUMNS.length];
		this.text = null;
		this.uniqueValues = new String[ScoreRow.COLUMNS.length][];

		updateCriteria(-1);
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
	public ScoreRow getRowById(int id) {
		try {
			return new ScoreRow(provider.getScoreById(id));
		} catch (ScoreProviderException e) {
			throw new RuntimeException(e);
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

	public void updateCriteria(int column) throws ScoreProviderException {
		boolean hasCriteria = false;
		for (String criterion : criteria) {
			if (criterion != null) {
				hasCriteria = true;
				break;
			}
		}

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

		updateRows(column);

	}

	private List<Score> filter(List<Score> scores) {
		if (text == null || text.length() == 0) {
			return scores;
		}

		List<Score> ret = new ArrayList<Score>();
		for (Score score : scores) {
			String name = score.getName();
			String author = score.getAuthor();
			String description = score.getDescription();
			String edition = score.getEdition();
			String location = score.getLocation();
			String genre = score.getGenre();
			if (name != null && name.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (author != null && author.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (description != null
					&& description.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (edition != null && edition.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (location != null
					&& location.toLowerCase().contains(text)) {
				ret.add(score);
			} else if (genre != null && genre.toLowerCase().contains(text)) {
				ret.add(score);
			} else {
				List<String> instruments = score.getInstruments();
				for (String instrument : instruments) {
					if (instrument.toLowerCase().contains(text)) {
						ret.add(score);
						break;
					}
				}
			}
		}
		return ret;
	}

	private void updateRows(int column) throws ScoreProviderException {
		this.filtered = filter(this.scores);

		this.rows = new ScoreRow[filtered.size()];
		for (int i = 0; i < rows.length; i++) {
			rows[i] = new ScoreRow(filtered.get(i));
		}

		int numCriteria = 0;
		int index = -1;
		for (int i = 0; i < criteria.length; i++) {
			if (criteria[i] != null) {
				numCriteria++;
				index = i;
			}
		}

		for (int i = 0; i < uniqueValues.length; i++) {
			Set<String> set = new HashSet<String>();
			String value = null;
			List<Score> scores = numCriteria == 1 && index == i ? filter(provider
					.getScores()) : this.filtered;
			for (Score score : scores) {
				if (i == ScoreRow.COLUMN_INDEX_NAME) {
					value = score.getName();
				} else if (i == ScoreRow.COLUMN_INDEX_AUTHOR) {
					value = score.getAuthor();
				} else if (i == ScoreRow.COLUMN_INDEX_DESCRIPTION) {
					value = score.getDescription();
				} else if (i == ScoreRow.COLUMN_INDEX_INSTRUMENTS) {
					List<String> instruments = score.getInstruments();
					value = "";
					for (int j = 0; j < instruments.size(); j++) {
						if (j > 0) {
							value += ", ";
						}
						value += instruments.get(j);
					}
				} else if (i == ScoreRow.COLUMN_INDEX_EDITION) {
					value = score.getEdition();
				} else if (i == ScoreRow.COLUMN_INDEX_LOCATION) {
					value = score.getLocation();
				} else if (i == ScoreRow.COLUMN_INDEX_GENRE) {
					value = score.getGenre();
				}

				if (value != null && value.length() > 0) {
					set.add(value);
				}
			}

			if (i != column) {
				uniqueValues[i] = set.toArray(new String[set.size()]);
			}
		}
	}

	public void setCategory(String category, int column)
			throws ScoreProviderException {
		if (criteria[column] == null && category == null) {
			return;
		} else if (criteria[column] != null && category != null
				&& criteria[column].equals(category)) {
			return;
		} else {
			this.criteria[column] = category;
			updateCriteria(column);
			eventBus.fireEvent(new DataChangeEvent(this));
		}
	}

	public void setText(String text) throws ScoreProviderException {
		this.text = text.toLowerCase();
		updateCriteria(-1);
		eventBus.fireEvent(new DataChangeEvent(this));
	}
}
